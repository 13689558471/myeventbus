package com.zeleixu.myeventbus.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by xuzelei on 2016/5/20.
 */
public class EventDao {
    private DataBaseHelper helper;

    private static EventDao instance;

    private Gson gson;

    private EventDao(Context ctx)
    {
        helper = new DataBaseHelper(ctx);
        gson = new Gson();
    }

    public static synchronized EventDao getInstance(Context ctx)
    {
        if (instance == null)
        {
            instance = new EventDao(ctx);
        }
        return instance;
    }

    // ------------------------------------------------------

    public void insert(String accepter, String eventName) {
        //直接插入也可以，已经设置了主键，不会出现重复的记录，SQLite本身也做了catch处理，不会造成crash，但是有错误日志输出，为了美观，加上insert前的判断
        String count = "";
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select count(1) from "+DataBase.EventsEntry.TABLE_NAME+
                " where "+DataBase.EventsEntry.COLUMN_ACCEPTER+"=? and "+DataBase.EventsEntry.COLUMN_NAME+"=?",new String[]{accepter, eventName});
        if(cursor!=null && cursor.moveToNext()) {
            count = cursor.getString(0);
            cursor.close();
        }
        if(TextUtils.isEmpty(count) || count.equals("0")) {
            ContentValues values = new ContentValues();
            values.put(DataBase.EventsEntry.COLUMN_ACCEPTER, accepter);
            values.put(DataBase.EventsEntry.COLUMN_NAME, eventName);
            helper.getWritableDatabase().insert(DataBase.EventsEntry.TABLE_NAME, null, values);
        }

        //replace into 语法这里不适合，会造成其他字段，比如json字段的自动清空
//        String execSql = "replace into "+DataBase.EventsEntry.TABLE_NAME+"("+DataBase.EventsEntry.COLUMN_ACCEPTER+","+DataBase.EventsEntry.COLUMN_NAME+") values('"+accepter+"','"+eventName+"')";
//        helper.getWritableDatabase().execSQL(execSql);

        Log.d("events","-> insert 数据库");
        logCount();
    }

    public void update(String eventName, Object obj) {
        String strJson = gson.toJson(obj);
        ContentValues values = new ContentValues();
        values.put(DataBase.EventsEntry.COLUMN_JSON, strJson);
//        values.put(DataBase.EventsEntry.COLUMN_NAME, eventName);

        helper.getWritableDatabase().update(DataBase.EventsEntry.TABLE_NAME, values, DataBase.EventsEntry.COLUMN_NAME+"=?", new String[]{eventName});

        logCount();
    }

    public ArrayList<Object>  getEvents(String accepter) {
        ArrayList<Object> mSerializable = new ArrayList<Object>();
        Cursor cursor = helper.getReadableDatabase().rawQuery("select  * from "+DataBase.EventsEntry.TABLE_NAME +" where "+DataBase.EventsEntry.COLUMN_ACCEPTER+"=?",new String[]{accepter});
        if(cursor!=null) {
            while (cursor.moveToNext()) {
                String json = cursor.getString(cursor.getColumnIndex(DataBase.EventsEntry.COLUMN_JSON));
                String action = cursor.getString(cursor.getColumnIndex(DataBase.EventsEntry.COLUMN_NAME));
                Log.d("events", "事件类型：" + action);
                if (!TextUtils.isEmpty(json)) {
                    try {
                        Object obj = gson.fromJson(json, Class.forName(action));
                        mSerializable.add(obj);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            cursor.close();
        }

        return mSerializable;
    }

    public void clear(String accepter, String eventName) {
        helper.getWritableDatabase().delete(DataBase.EventsEntry.TABLE_NAME,  DataBase.EventsEntry.COLUMN_ACCEPTER+"=? and "+DataBase.EventsEntry.COLUMN_NAME+"=?", new String[]{accepter, eventName});

        logCount();
    }
    public void clear(String accepter) {
        helper.getWritableDatabase().delete(DataBase.EventsEntry.TABLE_NAME,  DataBase.EventsEntry.COLUMN_ACCEPTER+"=?", new String[]{accepter});

        logCount();
    }

    public void logCount(){
//        Cursor cursor = helper.getReadableDatabase().rawQuery("select count(1) from "+DataBase.EventsEntry.TABLE_NAME,new String[]{});
//        if(cursor!=null && cursor.moveToNext()) {
//            String count = cursor.getString(0);
//            LogUtil.d("events","============事件个数===============>"+count);
//            cursor.close();
//        }
    }
}
