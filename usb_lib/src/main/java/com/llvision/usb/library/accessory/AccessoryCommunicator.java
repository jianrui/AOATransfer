//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.llvision.usb.library.accessory;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.llvision.usb.library.UsbUtil;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class AccessoryCommunicator {
    private final String TAG = AccessoryCommunicator.class.getSimpleName();
    private UsbManager usbManager;
    private Context context;
    private Handler sendHandler;
    private ParcelFileDescriptor fileDescriptor;
    private FileInputStream inStream;
    private FileOutputStream outStream;
    private boolean running;
    byte[] tempArray = null;
    boolean bigContent = false;

    public AccessoryCommunicator(Context context) {
        this.context = context;
        this.usbManager = (UsbManager)this.context.getSystemService(Context.USB_SERVICE);
        UsbAccessory[] accessoryList = this.usbManager.getAccessoryList();
        if(accessoryList != null && accessoryList.length != 0) {
            UsbAccessory accessory = accessoryList[0];
            this.openAccessory(accessory);
        } else {
            this.onError("no accessory found");
        }

    }

    public void send(byte[] payload) {
        if(this.sendHandler != null) {
            Message msg = this.sendHandler.obtainMessage();
            msg.obj = payload;
            this.sendHandler.sendMessage(msg);
        }

    }

    private void receive(byte[] payload, int length) {
        this.onReceive(payload, length);
    }

    public abstract void onReceive(byte[] var1, int var2);

    public abstract void onError(String var1);

    public abstract void onConnected();

    public abstract void onDisconnected();

    private void openAccessory(UsbAccessory accessory) {
        this.fileDescriptor = this.usbManager.openAccessory(accessory);
        Log.d(TAG, "openAccessory");
        if(this.fileDescriptor != null) {
            Log.d(TAG, "fileDescriptor!=null");
            FileDescriptor fd = this.fileDescriptor.getFileDescriptor();
            this.inStream = new FileInputStream(fd);
            this.outStream = new FileOutputStream(fd);
            (new AccessoryCommunicator.CommunicationThread()).start();
            this.sendHandler = new Handler() {
                public void handleMessage(Message msg) {
                    try {
                        AccessoryCommunicator.this.outStream.write((byte[])((byte[])msg.obj));
                    } catch (Exception var3) {
                        AccessoryCommunicator.this.onError("USB Send Failed " + var3.toString() + "\n");
                    }

                }
            };
            this.onConnected();
        } else {
            this.onError("could not connect");
        }

    }

    public void closeAccessory() {
        this.running = false;

        try {
            if(this.fileDescriptor != null) {
                this.fileDescriptor.close();
            }
        } catch (IOException var5) {
            ;
        } finally {
            this.fileDescriptor = null;
        }

        this.onDisconnected();
    }

    private class CommunicationThread extends Thread {
        private CommunicationThread() {
        }

        public void run() {
            AccessoryCommunicator.this.running = true;

            while(AccessoryCommunicator.this.running) {
                byte[] msg = new byte[16 * 1024];

                try {
                    for(int e = AccessoryCommunicator.this.inStream.read(msg); AccessoryCommunicator.this.inStream != null && AccessoryCommunicator.this.running; e = AccessoryCommunicator.this.inStream.read(msg)) {
                        if(e == (16 * 1024)) {
                            AccessoryCommunicator.this.bigContent = true;
                            if(AccessoryCommunicator.this.tempArray == null) {
                                AccessoryCommunicator.this.tempArray = new byte[e];
                                System.arraycopy(msg, 0, AccessoryCommunicator.this.tempArray, 0, msg.length);
                            } else {
                                AccessoryCommunicator.this.tempArray = UsbUtil.concatByteArray(AccessoryCommunicator.this.tempArray, msg);
                            }
                        } else if(AccessoryCommunicator.this.bigContent) {
                            byte[] lastArray = new byte[e];
                            System.arraycopy(msg, 0, lastArray, 0, e);
                            AccessoryCommunicator.this.tempArray = UsbUtil.concatByteArray(AccessoryCommunicator.this.tempArray, lastArray);
                            AccessoryCommunicator.this.bigContent = false;
                            AccessoryCommunicator.this.receive(AccessoryCommunicator.this.tempArray, e);
                            AccessoryCommunicator.this.tempArray = null;
                        } else {
                            AccessoryCommunicator.this.receive(msg, e);
                            AccessoryCommunicator.this.tempArray = null;
                            AccessoryCommunicator.this.bigContent = false;
                        }

                        Thread.sleep(10L);
                    }
                } catch (Exception var4) {
                    AccessoryCommunicator.this.onError("USB Receive Failed " + var4.toString() + "\n");
                    AccessoryCommunicator.this.closeAccessory();
                }
            }

        }
    }
}
