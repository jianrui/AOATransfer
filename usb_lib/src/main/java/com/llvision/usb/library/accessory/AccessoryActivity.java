//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.llvision.usb.library.accessory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.llvision.usb.library.LLVisionSdk;
import com.llvision.usb.library.UsbHandler;

public class AccessoryActivity extends Activity {
    public static final String TAG = AccessoryActivity.class.getSimpleName();
    Context context;
    UsbHandler usbHandler;

    public AccessoryActivity() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.usbHandler = new UsbHandler(this);
        this.context = this;
        LLVisionSdk.getInstance(this).startAccessoryService(this.context);
        this.finish();
    }
}
