package com.llvision.usb.library.host;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.llvision.usb.library.LLVisionSdk;
import com.llvision.usb.library.UsbHandler;
import com.llvision.usb.library.UsbUtil;

/**
 * Created by jerry on 2017/5/11.
 */

public class HostService extends Service{
    public static final String ACTION_DEVICE_PERMISSION = "com.llvision.USB_PERMISSION";
    private UsbManager mUsbManager;
    UsbDevice mUsbDevice;
    Context context;
    Thread HostCommunicatorThread;
    HostCommunicator hostCommunicator;
    UsbHandler usbHandler;
    PendingIntent mPermissionIntent;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(ACTION_DEVICE_PERMISSION.equals(action)) {
                synchronized(this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra("device");
                    if(intent.getBooleanExtra("permission", false)) {
                        if(device != null) {
                            UsbUtil.doUsbHandler(usbHandler, "start commumicator", 1);
                            mUsbDevice = device;
                            HostCommunicatorThread = new Thread(new HostCommunicator(context, device));
                            HostCommunicatorThread.start();
                        }
                    } else {
                        UsbUtil.doUsbHandler(usbHandler, "usb EXTRA_PERMISSION_GRANTED null!!!", 1);
                    }
                }
            } else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                LLVisionSdk.getInstance(context).stopHostService(context);
            }

        }
    };

    public HostService() {
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        this.context = this;
        this.usbHandler = new UsbHandler(this.context);
        this.mUsbManager = (UsbManager)this.getSystemService(USB_SERVICE);
        this.registerDeAttached();
    }

    private void registerDeAttached() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        this.context.registerReceiver(this.mUsbReceiver, intentFilter);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        UsbDevice device = (UsbDevice)intent.getParcelableExtra("device");
        if(device != null) {
            this.HostCommunicatorThread = new Thread(new HostCommunicator(this.context, device));
            this.HostCommunicatorThread.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.mUsbReceiver);
        this.release();
    }

    private void release() {
        this.hostCommunicator = LLVisionSdk.getInstance(this.context).getHostCommunicator();
        if(this.hostCommunicator != null) {
            this.hostCommunicator.getKeepThreadAlive().set(false);
            LLVisionSdk.getInstance(this.context).setHostCommunicator((HostCommunicator)null);
        }

        this.HostCommunicatorThread = null;
    }
}
