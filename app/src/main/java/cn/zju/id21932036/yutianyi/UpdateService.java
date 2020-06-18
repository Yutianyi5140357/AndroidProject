package cn.zju.id21932036.yutianyi;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.util.Log;

import java.util.List;

import cn.iipc.android.tweetlib.Status;
import cn.iipc.android.tweetlib.YambaClient;
import cn.iipc.android.tweetlib.YambaClientException;

public class UpdateService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener{
    public UpdateService() {
    }
    public static final String TAG = "UpdaterService";

    public static boolean runFlag = false;
    private Updater updater;
    static long DELAY = 10000;
    static String username;
    static String password;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        DELAY = Long.parseLong(pref.getString("interval","60"))*1000;
        username = pref.getString("username", "student");
        password = pref.getString("password", "password");

        pref.registerOnSharedPreferenceChangeListener(this);

        this.updater = new Updater();
        Log.d(TAG, "onCreated");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!runFlag){
            this.runFlag = true;
            this.updater.start();
        }
        Log.d(TAG, "onStarted");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.runFlag = false;
        this.updater.interrupt();
        this.updater = null;
        Log.d(TAG, "onDestroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String s) {
        if(s.equals("username"))
            username = prefs.getString("username", "student");
        if(s.equals("password"))
            password = prefs.getString("password", "password");
        if(s.equals("interval"))
            DELAY = Long.parseLong(prefs.getString("interval","60"))*1000;
    }

    private class Updater extends Thread{
        public Updater(){
            super("UpdaterService-Thread");
        }

        @Override
        public void run() {
            while (runFlag){
                Log.d(TAG, "Running background thread" + DELAY/1000);
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(UpdateService.this);
//                String username = prefs.getString("username", "student");
//                String password = prefs.getString("password", "password");
//                DELAY = Long.parseLong(prefs.getString("interval", "60"))*1000;

                YambaClient yambaCloud = new YambaClient(username, password);
                DBHelper dbHelper = new DBHelper(UpdateService.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                try {
                    List<Status> timeline = yambaCloud.getTimeline(20);
                    int count = 0;
                    for(Status status : timeline) {
                        values.clear();
                        values.put(DBConst.Column.ID, status.getId());
                        values.put(DBConst.Column.USER, status.getUser());
                        values.put(DBConst.Column.MESSAGE, status.getMessage());
                        values.put(DBConst.Column.CREATED_AT, status.getCreatedAt().getTime());
                        values.put(DBConst.Column.AVAT, status.getAvatUrl());
                        long rowID = db.insertWithOnConflict(DBConst.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                        if(rowID != -1){
                            count++;
                            Log.d(TAG, String.format("Insert Success !%s: %s:%s" , status.getUser(),
                                    status.getMessage(), status.getImgUrl()));
                        }
                    }
                    if(count >= 0){
                        Intent bcast=new Intent(DBConst.NEW_STATUSES);
                        bcast.putExtra("count", count);
                        sendBroadcast(bcast);
                    }
                } catch (YambaClientException e){
                    Log.d(TAG, "Failed to fetch the timeline: "+e);
                }
                dbHelper.close();
                try{
                    Thread.sleep(DELAY);
                }catch (InterruptedException e){
                    runFlag = false;
                }
            }
        }
    }
}
