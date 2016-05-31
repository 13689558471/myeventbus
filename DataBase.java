package com.zeleixu.myeventbus.db;

import android.provider.BaseColumns;

/**
 * Created by xuzelei on 2016/5/20.
 */
public class DataBase {
    private DataBase() {
    }// 防止被实例化

    /**
     * 事件  表
     * @author xuzelei
     *
     */
    public static abstract class EventsEntry implements BaseColumns {

        // 定义表名
        public static final String TABLE_NAME = "tb_events";

        // 定义字段

        public static final String COLUMN_ACCEPTER = "event_accepter";//接收者
        public static final String COLUMN_NAME = "event_name";//事件名称
        public static final String COLUMN_JSON = "event_json";//载体

    }
}
