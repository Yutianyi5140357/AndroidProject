package cn.zju.id21932036.yutianyi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.SyncStateContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import java.io.FileWriter;

import cn.iipc.android.tweetlib.Status;
import cn.iipc.android.tweetlib.SubmitProgram;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private TextView pkgName;
    private TextView text1;
    SQLiteDatabase db;
    Cursor cursor;
    DBHelper dbhlp;
    SimpleCursorAdapter adapter;
    ListView listStatus;
    private static final String[] FROM = {DBConst.Column.USER,
             DBConst.Column.MESSAGE, DBConst.Column.CREATED_AT, DBConst.Column.AVAT};
    private static final int[] TO = {R.id.txtUser,
             R.id.txtMsg, R.id.txtTime, R.id.imageView3};
    TimelineReceiver receiver;
    IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pkgName=(TextView)findViewById(R.id.pkgName);
        pkgName.setText(this.getPackageName());
//        text1=(TextView)findViewById(R.id.textView1);

        dbhlp=new DBHelper(this);
        db = dbhlp.getReadableDatabase();
        cursor = db.query(DBConst.TABLE, null, null,null,null,null,DBConst.DEFAULT_SORT);
        startManagingCursor(cursor);
        listStatus=(ListView)findViewById(R.id.listStatus);
        adapter=new SimpleCursorAdapter(this,R.layout.row, cursor,FROM, TO);
        adapter.setViewBinder(new TimelineViewBinder());
        listStatus.setAdapter(adapter);
        listStatus.setOnItemClickListener(this);
        receiver = new TimelineReceiver();
        filter = new IntentFilter(DBConst.NEW_STATUSES);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }


    private boolean serviceRunning = false;
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if(menu==null) return true;
        serviceRunning = UpdateService.runFlag;
        MenuItem toggleItem = menu.findItem(R.id.action_start);
        toggleItem.setChecked(serviceRunning);

        if(serviceRunning){
            toggleItem.setTitle(R.string.stopservice);
            toggleItem.setIcon(android.R.drawable.ic_media_pause);
        } else{
            toggleItem.setTitle(R.string.startservice);
            toggleItem.setIcon(android.R.drawable.ic_media_play);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){

            case R.id.action_calc:
                startActivity(new Intent(this, CalcActivity.class));
                return true;
            case R.id.action_filetest:
                startActivity(new Intent(this, MemoActivity.class));
                return true;
            case R.id.action_post:
                startActivity(new Intent(this, StatusActivity.class));
                return true;
            case R.id.action_homework:
                new SubmitProgram().submit(this, "G2");
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_musictest:
                startActivity(new Intent(this, MusicActivity.class));
                return true;
            case R.id.action_start:
                if(serviceRunning){
                    stopService(new Intent(this, UpdateService.class));
//                    serviceRunning=false;
                }else {
                    startService(new Intent(this,UpdateService.class));
//                    serviceRunning=true;
                }
                return true;
            case R.id.action_delete:
                SQLiteDatabase dbw=dbhlp.getWritableDatabase();
                dbw.delete(DBConst.TABLE, null, null);
                cursor.requery();
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "所有数据已经被删除！", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor)listStatus.getItemAtPosition(position);
        String user = cursor.getString(cursor.getColumnIndex(DBConst.Column.USER));
        String msg = cursor.getString(cursor.getColumnIndex(DBConst.Column.MESSAGE));

        new AlertDialog.Builder(this).setTitle(user)
                .setMessage(msg)
                .setNegativeButton("关闭", null)
                .show();
    }

    class TimelineViewBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if (view.getId() == R.id.txtUser) return false;

            if(view.getId() == R.id.txtMsg){
                String msg = cursor.getString(columnIndex);
                if(msg.length()>60){
                    msg = msg.substring(0, 60)+"...";
                }
                ((TextView)view).setText(msg);
                return true;
            }

            if(view.getId() == R.id.txtTime){
                long timestamp = cursor.getLong(columnIndex);
                CharSequence relativeTime =
                        DateUtils.getRelativeTimeSpanString(timestamp);
                ((TextView)view).setText(relativeTime);
                return true;
            }else if(view.getId() == R.id.imageView3){
                String url = cursor.getString(columnIndex);
                if(url.length()>0)
                    Picasso.with(MainActivity.this)
                        .load(url)
                        .placeholder(R.drawable.music)
                        .into((ImageView)view);
                return true;
            }
            return false;
        }
    }

    class TimelineReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TimelineReceiver", "onReceived");
            int count = intent.getIntExtra("count", 0);

            if(count>0){
                cursor.requery();
                adapter.notifyDataSetChanged();
            }
            Toast.makeText(MainActivity.this,
                    "更新了"+count+"条记录。", Toast.LENGTH_LONG).show();
        }
    }

}
