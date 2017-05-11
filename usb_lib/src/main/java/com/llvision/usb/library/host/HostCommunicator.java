package com.llvision.usb.library.host;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.util.Log;

import com.llvision.usb.library.LLVisionSdk;
import com.llvision.usb.library.UsbHandler;
import com.llvision.usb.library.UsbUtil;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class HostCommunicator implements Runnable {
    private final AtomicBoolean keepThreadAlive = new AtomicBoolean(true);
    private final String SAVE_PATH_FOLDER;
    private final String MSG_NO_CALLBACK;
    Context context;
    UsbDevice device;
    private byte[] content;
    byte[] tempArray;
    boolean bigContent;
    UsbHandler hostHandler;
    UsbDeviceConnection connection;
    UsbEndpoint endpointIn;
    UsbEndpoint endpointOut;

    public HostCommunicator(Context context, UsbDevice device) {
        this.SAVE_PATH_FOLDER = Environment.getExternalStorageDirectory().getPath() + File.separator + "usb";
        this.MSG_NO_CALLBACK = "no HostServiceCallback";
        this.content = new byte[0];
        this.tempArray = null;
        this.bigContent = false;
        this.endpointIn = null;
        this.endpointOut = null;
        this.context = context;
        this.device = device;
        this.hostHandler = new UsbHandler(context);
    }

    public void run() {
        UsbManager usbManager = (UsbManager)this.context.getSystemService(Context.USB_SERVICE);
        UsbInterface usbInterface = this.device.getInterface(0);

        for(int claimResult = 0; claimResult < this.device.getInterface(0).getEndpointCount(); ++claimResult) {
            UsbEndpoint buff = this.device.getInterface(0).getEndpoint(claimResult);
            if(buff.getDirection() == UsbConstants.USB_DIR_IN) {
                this.endpointIn = buff;
            }

            if(buff.getDirection() == UsbConstants.USB_DIR_OUT) {
                this.endpointOut = buff;
            }
        }

        if(this.endpointIn == null) {
            UsbUtil.doUsbHandler(this.hostHandler, "Input Endpoint not found", 1);
        } else if(this.endpointOut == null) {
            UsbUtil.doUsbHandler(this.hostHandler, "Output Endpoint not found", 1);
        } else {
            this.connection = usbManager.openDevice(this.device);
            if(this.connection == null) {
                UsbUtil.doUsbHandler(this.hostHandler, "Could not open device", 1);
            } else {
                boolean var7 = this.connection.claimInterface(usbInterface, true);
                if(!var7) {
                    UsbUtil.doUsbHandler(this.hostHandler, "Could not claim device", 1);
                } else {
                    UsbUtil.doUsbHandler(this.hostHandler, " ready to communicate", 1);
                    byte[] revBuffer = new byte[16 * 1024];
                    LLVisionSdk.getInstance(this.context).setHostCommunicator(this);

                    while(this.keepThreadAlive.get()) {
                        int bytesTransferred = this.connection.bulkTransfer(this.endpointIn, revBuffer, revBuffer.length, 100);
                        if(bytesTransferred > 0) {
                            Log.e("bytesTransferred", String.valueOf(bytesTransferred));
                            if(bytesTransferred == (16 * 1024)) {
                                this.bigContent = true;
                                if(this.tempArray == null) {
                                    this.tempArray = new byte[bytesTransferred];
                                    System.arraycopy(revBuffer, 0, this.tempArray, 0, revBuffer.length);
                                } else {
                                    this.tempArray = UsbUtil.concatByteArray(this.tempArray, revBuffer);
                                }
                            } else if(this.bigContent) {
                                byte[] lastArray = new byte[bytesTransferred];
                                System.arraycopy(revBuffer, 0, lastArray, 0, bytesTransferred);
                                this.tempArray = UsbUtil.concatByteArray(this.tempArray, lastArray);
                                this.bigContent = false;
                                this.receive(this.tempArray, this.tempArray.length);
                                this.tempArray = null;
                            } else {
                                this.receive(revBuffer, bytesTransferred);
                                this.tempArray = null;
                                this.bigContent = false;
                            }
                        }
                    }
                }

                this.connection.releaseInterface(usbInterface);
                this.connection.close();
            }
        }
    }

    private void receive(byte[] payload, int len) {
        if(LLVisionSdk.getInstance(this.context).getUsbServiceCallback() != null) {
            LLVisionSdk.getInstance(this.context).getUsbServiceCallback().receive(payload, len);
        } else {
            UsbUtil.doUsbHandler(this.hostHandler, "no HostServiceCallback", 1);
        }

    }

    public void sendBytes(byte[] sendBuff) {
        if(sendBuff.length > (16 * 1024)) {
            int totalSize = sendBuff.length;
            int totalPage = totalSize % (16 * 1024) == 0 ? totalSize / (16 * 1024):totalSize / (16 * 1024) + 1;
            byte[] fullMsg = new byte[(16 * 1024)];

            for(int i = 1; i <= totalPage; ++i) {
                int firstIndex = (i - 1) * (16 * 1024);
                if(i == totalPage) {
                    byte[] lastArray = new byte[totalSize - firstIndex];
                    System.arraycopy(sendBuff, firstIndex, lastArray, 0, totalSize - firstIndex);
                    this.connection.bulkTransfer(this.endpointOut, lastArray, totalSize - firstIndex, 100);
                } else {
                    System.arraycopy(sendBuff, firstIndex, fullMsg, 0, (16 * 1024));
                    this.connection.bulkTransfer(this.endpointOut, fullMsg, (16 * 1024), 100);
                }
            }
        } else {
            this.connection.bulkTransfer(this.endpointOut, sendBuff, sendBuff.length, 100);
        }

    }

    public AtomicBoolean getKeepThreadAlive() {
        return this.keepThreadAlive;
    }
}
