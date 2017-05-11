//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.llvision.usb.library;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class UsbHandler extends Handler {
    public static final int HANDLER_SHOW_MSG = 1;
    Context mContext;
    Toast mToast;
    TextView tv;

    public UsbHandler(Context context) {
        this.mContext = context;
        this.mToast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        this.tv = new TextView(context);
        this.tv.setTextColor(Color.RED);
        this.tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30.0F);
        this.mToast.setView(this.tv);
        this.mToast.setGravity(Gravity.BOTTOM, 0, 0);
    }

    public void handleMessage(Message msg) {
        switch(msg.what) {
            case HANDLER_SHOW_MSG:
                String text = (String)msg.obj;
                this.tv.setText(text);
                this.mToast.show();
                break;
            default:
        }
    }
}
