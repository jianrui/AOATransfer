package com.llvision.accessory.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.llvision.usb.library.LLVisionSdk;
import com.llvision.usb.library.UsbServiceCallback;

public class MainActivity extends AppCompatActivity implements UsbServiceCallback {

    private TextView mShowText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View rootView = findViewById(R.id.root_view);
        mShowText = (TextView) findViewById(R.id.id_show_text);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("accessory", "onSingleTapUp");
                LLVisionSdk.getInstance(MainActivity.this).send("host send msg".getBytes());
            }
        });
        LLVisionSdk.getInstance(this).setUsbServiceCallback(this);
    }

    @Override
    public void receive(final byte[] datas, final int length) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mShowText.append(new String(datas));
                mShowText.append("len = "+length);
                mShowText.append("\n");
            }
        });
    }

}
