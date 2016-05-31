package com.zeleixu.myeventbus.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zeleixu.myeventbus.EventBaseUI;

import java.io.Serializable;

/**
 * Created by xuzelei on 2016/5/19.
 * 注册事件并响应事件
 */
public class TestAUI extends EventBaseUI {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button btnTest = new Button(this);
        btnTest.setText("打开B");
        setContentView(btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestAUI.this, TestBUI.class));
            }
        });
    }

    @Override
    protected void onRegisterEvent() {
        registerEvent(Event_A.class);
    }

    @Override
    protected void onEvent(Serializable event) {
        if(event instanceof Event_A) {
            Event_A event_a = (Event_A)event;
            Toast.makeText(this,event_a.b,Toast.LENGTH_SHORT).show();
        }
        //必须执行基类的方法
        super.onEvent(event);
    }

}
