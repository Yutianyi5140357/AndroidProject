package cn.zju.id21932036.yutianyi;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 82307 on 2020/6/8.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG= DBHelper.class.getSimpleName();

    public DBHelper(Context context) {
        super(context, DBConst.DB_NAME, null, DBConst.DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format(
                "create table %s (%s int primary key, %s text, %s text, %s int, %s text)",
                DBConst.TABLE,
                DBConst.Column.ID,
                DBConst.Column.USER,
                DBConst.Column.MESSAGE,
                DBConst.Column.CREATED_AT,
                DBConst.Column.AVAT);
        Log.d(TAG, "onCreate with SQL: "+sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + DBConst.TABLE);
        onCreate(db);
    }
}
