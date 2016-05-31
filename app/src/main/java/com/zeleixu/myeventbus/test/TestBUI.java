package com.zeleixu.myeventbus.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zeleixu.myeventbus.EventBaseUI;


/**
 * Created by xuzelei on 2016/5/19.
 * 发送事件，在TestAUI中接收
 */
public class TestBUI extends EventBaseUI {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button btnTest = new Button(this);
        btnTest.setText("发送");
        setContentView(btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event_A event_a = new Event_A();
                event_a.a = 123;
                event_a.b = "abc";
                postEvent(event_a);
            }
        });
    }
}
