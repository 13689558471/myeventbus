package com.zeleixu.myeventbus.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xuzelei on 2016/5/20.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;// 数据库版本
    public static final String DATABASE_NAME = "myevents.db";// 数据库名称

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_COMMENT);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_COMMENT);

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES_COMMENT = "CREATE TABLE "
            + DataBase.EventsEntry.TABLE_NAME + " ("
            + DataBase.EventsEntry._ID + " INTEGER "
            + COMMA_SEP + DataBase.EventsEntry.COLUMN_ACCEPTER + TEXT_TYPE
            + COMMA_SEP + DataBase.EventsEntry.COLUMN_NAME + TEXT_TYPE
            + COMMA_SEP + DataBase.EventsEntry.COLUMN_JSON + TEXT_TYPE
            + COMMA_SEP + " constraint pk_tb_events primary key (event_accepter,event_name))";

    private static final String SQL_DELETE_ENTRIES_COMMENT = "DROP TABLE IF EXISTS "
            + DataBase.EventsEntry.TABLE_NAME;
}
