package com.zeleixu.myeventbus;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.zeleixu.myeventbus.db.EventDao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by xuzelei on 2016/5/19.
 * 已验证通过
 */
public class EventBaseFragmentUI extends FragmentActivity {
    private LocalBroadcastManager mLocalBroadcastManager;
    private CommandReceiver cmdReceiver;
    private IntentFilter intentFilter;
    private ArrayList<String> eventColl = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取标识
        getIdent();
        //注册事件的准备
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        cmdReceiver = new CommandReceiver();
        intentFilter = new IntentFilter();
        onRegisterEvent();
        mLocalBroadcastManager.registerReceiver(cmdReceiver, intentFilter);

        //重现事件
        ArrayList<Object> events = EventDao.getInstance(this).getEvents(accepter);
        if(events!=null && !events.isEmpty()) {
            for(Object event : events) {
                if(event!=null) {
                    postEvent((Serializable)event);
                }
            }
        }
    }
    protected void onRegisterEvent(){

    }

    protected <T> void registerEvent(Class<T> event) {
        //注册事件
        String action = event.getName();
        intentFilter.addAction(action);
        eventColl.add(action);

        //记录注册，实现重现
        Log.d("events","-> 注册到 数据库");
        EventDao.getInstance(this).insert(accepter, action);
    }

    protected void onEvent(Serializable event) {
        String action = event.getClass().getName();
        EventDao.getInstance(this).clear(accepter, action);
    }

    /**
     * 发出事件
     * @param event
     */
    protected void postEvent(Serializable event) {
        if(event !=null) {
            //发出事件
            String action = event.getClass().getName();
            Bundle extra = new Bundle();
            extra.putSerializable(action, (Serializable) event);
            mLocalBroadcastManager.sendBroadcast(new Intent(action).putExtras(extra));

            //记录事件，实现重现
            EventDao.getInstance(this).update(action, event);
        }
    }

    private class CommandReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if(eventColl != null && !eventColl.isEmpty()) {
//                for(String eventName : eventColl) {
//                    if (intent.getAction().equals(eventName)) {
//                        onEvent((Serializable)(intent.getExtras().getSerializable(eventName)));
//                    }
//                }

                new Thread(){
                    public void run() {
                        int eventCount = eventColl.size();
                        String action = intent.getAction();
                        for(int i=0;i<eventCount;i++) {
                            String eventName = eventColl.get(i);
                            if (action.equals(eventName)) {
                                Message msg = Message.obtain();
                                msg.setData(intent.getExtras());
                                msg.arg1 = i;
                                eventHandler.sendMessage(msg);
                            }
                        }
                    }
                }.start();
            }
        }
    }

    private Handler eventHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String eventName = eventColl.get(msg.arg1);
            onEvent((Serializable)(bundle.getSerializable(eventName)));
        }
    };

    private String accepter;
    private void getIdent() {
        if(!TextUtils.isEmpty(accepter)) return;

        try {
//            Field field = Activity.class.getDeclaredField("mMainThread");
//            field.setAccessible(true);
//            Object mMainThreadField = field.get(this);
//            if(mMainThreadField != null) {
//                LogUtil.d("events","反射系统线程MainThread成功");
//            }

            Field fieldmIdent = Activity.class.getDeclaredField("mIdent");
            fieldmIdent.setAccessible(true);
            Object mMainThreadDent = fieldmIdent.get(this);
            if(mMainThreadDent != null) {
                Log.d("events","反射标识成功->"+mMainThreadDent);
//                DialogUtil.alter(this, "反射标识成功->"+mMainThreadDent);
            }
            accepter = String.valueOf(mMainThreadDent);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        /**onActivityResult会被Activity的dispathActivityResult方法调用，而dispathActivityResult会被
         * MainThread的deliverResult方法调用，每个activity在MainThread中对应一个ActivityClientRecord对象保存在集合中：
         * ArrayMap<IBinder, ActivityClientRecord> mActivities = new ArrayMap<>()，ActivityClientRecord中定义了标识Ident，token等信息
         *
         * IBinder token;
         int ident;
         String referrer;
         Activity activity;
         Activity parent;*/
    }
    private boolean deleteEventOnDestroy = true;
    @Override
    public void onResume() {
        super.onResume();
        deleteEventOnDestroy = true;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        deleteEventOnDestroy = false;
        Log.d("events","保存状态");
    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState){
        super.onSaveInstanceState(outState, outPersistentState);
        deleteEventOnDestroy = false;
        Log.d("events","保存状态");
    }
    @Override
    public void onDestroy() {
        //注销广播
        mLocalBroadcastManager.unregisterReceiver(cmdReceiver);

        //如果未有发出的事件
        if(deleteEventOnDestroy){
            EventDao.getInstance(this).clear(accepter);
        }
        super.onDestroy();
    }
}

