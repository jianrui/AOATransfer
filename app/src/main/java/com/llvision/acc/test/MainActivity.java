package com.llvision.acc.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.llvision.usb.library.LLVisionSdk;
import com.llvision.usb.library.UsbServiceCallback;

public class MainActivity extends AppCompatActivity implements UsbServiceCallback{

    private TextView mShowText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button) findViewById(R.id.id_btn);
        mShowText = (TextView) findViewById(R.id.id_show_text);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LLVisionSdk.getInstance(MainActivity.this).send("host send msg".getBytes());
            }
        });

        LLVisionSdk.getInstance(this).setUsbServiceCallback(this);
    }

    @Override
    public void receive(byte[] datas, int length) {
        mShowText.append(new String(datas));
        mShowText.append("len = "+length);
        mShowText.append("\n");
    }
}
