package com.llvision.accessory.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import com.llvision.usb.library.LLVisionSdk;
import com.llvision.usb.library.UsbServiceCallback;

public class MainActivity extends AppCompatActivity implements UsbServiceCallback {

    private TextView mShowText;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mShowText = (TextView) findViewById(R.id.id_show_text);
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                LLVisionSdk.getInstance(MainActivity.this).send("host send msg".getBytes());
                return true;
            }
        });
        LLVisionSdk.getInstance(this).setUsbServiceCallback(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void receive(byte[] datas, int length) {
        mShowText.append(new String(datas));
        mShowText.append("len = "+length);
        mShowText.append("\n");
    }

}
