//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.llvision.usb.library;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Process;

import com.llvision.usb.library.accessory.AccessoryCommunicator;
import com.llvision.usb.library.accessory.AccessoryService;
import com.llvision.usb.library.host.HostCommunicator;
import com.llvision.usb.library.host.HostService;

public class LLVisionSdk {
    public static final String DEVICE_EXTRA_KEY = "device";
    public static Intent accessoryIntent = null;
    AccessoryCommunicator accessoryCommunicator;
    public static Intent hostIntent = null;
    HostCommunicator hostCommunicator;
    static UsbHandler usbHandler;
    UsbServiceCallback usbServiceCallback;
    public static LLVisionSdk mSdk = null;

    private LLVisionSdk() {
    }

    public static synchronized LLVisionSdk getInstance(Context context) {
        if(mSdk == null) {
            mSdk = new LLVisionSdk();
        }

        if(usbHandler == null) {
            usbHandler = new UsbHandler(context);
        }

        return mSdk;
    }

    public void startAccessoryService(Context context) {
        if(accessoryIntent == null) {
            accessoryIntent = new Intent(context, AccessoryService.class);
        }

        context.startService(accessoryIntent);
    }

    public void stopAccessoryService(Context context) {
        if(accessoryIntent != null) {
            context.stopService(accessoryIntent);
        } else {
            Process.killProcess(Process.myPid());
        }

    }

    public AccessoryCommunicator getAccessoryCommunicator() {
        return this.accessoryCommunicator;
    }

    public void setAccessoryCommunicator(AccessoryCommunicator accessoryCommunicator) {
        this.accessoryCommunicator = accessoryCommunicator;
    }

    public void startHostService(Context context, UsbDevice usbDevice) {
        if(hostIntent == null) {
            hostIntent = new Intent(context, HostService.class);
        }

        hostIntent.putExtra("device", usbDevice);
        context.startService(hostIntent);
    }

    public void stopHostService(Context context) {
        if(hostIntent != null) {
            context.stopService(hostIntent);
        } else {
            Process.killProcess(Process.myPid());
        }

    }

    public HostCommunicator getHostCommunicator() {
        return this.hostCommunicator;
    }

    public void setHostCommunicator(HostCommunicator hostCommunicator) {
        this.hostCommunicator = hostCommunicator;
    }

    public void send(byte[] content) {
        if(this.getHostCommunicator() != null) {
            this.getHostCommunicator().sendBytes(content);
        } else if(this.getAccessoryCommunicator() != null) {
            this.getAccessoryCommunicator().send(content);
        } else {
            UsbUtil.doUsbHandler(usbHandler, "host send failed", 1);
        }

    }

    public void setUsbServiceCallback(UsbServiceCallback usbServiceCallback) {
        this.usbServiceCallback = usbServiceCallback;
    }

    public UsbServiceCallback getUsbServiceCallback() {
        return this.usbServiceCallback;
    }
}
