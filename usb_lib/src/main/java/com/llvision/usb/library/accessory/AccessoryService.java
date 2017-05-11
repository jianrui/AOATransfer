//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.llvision.usb.library.accessory;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.llvision.usb.library.LLVisionSdk;
import com.llvision.usb.library.UsbHandler;
import com.llvision.usb.library.UsbUtil;

import java.io.File;

public class AccessoryService extends Service {
    private final String SAVE_PATH_FOLDER;
    public static final String MSG_ERROR = "onError";
    public static final String MSG_CONNECTED = "connected";
    public static final String MSG_DISCONNECTED = "disconnected";
    public static final String MSG_NO_CALLBACK = "no AccessoryServiceCallback";
    Context context;
    AccessoryCommunicator communicator;
    UsbHandler accessoryHandler;
    Message message;

    public AccessoryService() {
        this.SAVE_PATH_FOLDER = Environment.getExternalStorageDirectory().getPath() + File.separator + "usb";
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        this.context = this.getApplicationContext();
        this.accessoryHandler = new UsbHandler(this.context);
        this.initAccessoryCommunicator();
    }

    public void onDestroy() {
        super.onDestroy();
        this.communicator = null;
        this.accessoryHandler = null;
    }

    private void initAccessoryCommunicator() {
        this.communicator = new AccessoryCommunicator(this.context) {
            public void onReceive(byte[] payload, int len) {
                if(LLVisionSdk.getInstance(AccessoryService.this.context).getUsbServiceCallback() != null) {
                    LLVisionSdk.getInstance(AccessoryService.this.context).getUsbServiceCallback().receive(payload, len);
                } else {
                    UsbUtil.doUsbHandler(AccessoryService.this.accessoryHandler, MSG_NO_CALLBACK, 1);
                }

            }

            public void onError(String msg) {
                UsbUtil.doUsbHandler(AccessoryService.this.accessoryHandler, msg, 1);
                LLVisionSdk.getInstance(AccessoryService.this.context).stopAccessoryService(AccessoryService.this.context);
            }

            public void onConnected() {
                UsbUtil.doUsbHandler(AccessoryService.this.accessoryHandler, MSG_CONNECTED, 1);
            }

            public void onDisconnected() {
                UsbUtil.doUsbHandler(AccessoryService.this.accessoryHandler, MSG_DISCONNECTED, 1);
                LLVisionSdk.getInstance(AccessoryService.this.context).stopAccessoryService(AccessoryService.this.context);
            }
        };
        LLVisionSdk.getInstance(this.context).setAccessoryCommunicator(this.communicator);
    }
}
