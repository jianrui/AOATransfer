package com.llvision.usb.library.host;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.llvision.usb.library.AOAConstact;
import com.llvision.usb.library.R;
import com.llvision.usb.library.LLVisionSdk;

import java.util.HashMap;
import java.util.Iterator;

public class HostActivity extends AppCompatActivity {
    public static final String DEVICE_EXTRA_KEY = "device";


    private UsbManager mUsbManager;

    public HostActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mUsbManager = (UsbManager)this.getSystemService(USB_SERVICE);
    }

    protected void onResume() {
        super.onResume();
        HashMap deviceList = this.mUsbManager.getDeviceList();
        if(deviceList != null && deviceList.size() != 0) {
            if(!this.searchForUsbAccessory(deviceList)) {
                Iterator iterator = deviceList.values().iterator();

                while(iterator.hasNext()) {
                    UsbDevice device = (UsbDevice)iterator.next();
                    initAccessory(device);
                }

                this.finish();
            }
        } else {
            this.finish();
        }
    }

    private boolean searchForUsbAccessory(HashMap<String, UsbDevice> deviceList) {
        Iterator iterator = deviceList.values().iterator();

        UsbDevice device;
        do {
            if(!iterator.hasNext()) {
                return false;
            }

            device = (UsbDevice)iterator.next();
        } while(!this.isUsbAccessory(device));

        LLVisionSdk.getInstance(this).startHostService(this, device);
        this.finish();
        return true;
    }

    private boolean isUsbAccessory(UsbDevice device) {
        return device.getProductId() == AOAConstact.PID_ACCESSORY ||
                device.getProductId() == AOAConstact.PID_ACCESSORY_ADB;
    }

    private boolean initAccessory(UsbDevice device) {
        UsbDeviceConnection connection = this.mUsbManager.openDevice(device);
        if(connection == null) {
            return false;
        } else {
            byte[] protocol = new byte[2];
            int ret = connection.controlTransfer(UsbConstants.USB_DIR_IN | UsbConstants.USB_TYPE_VENDOR,
                    51, 0, 0, protocol, protocol.length, 100);
            if (ret > 0) {
                Log.d(DEVICE_EXTRA_KEY, "get protocol ver = "+ret+" protocol=0x"+protocol[0]+" 0x"+protocol[1]);
                this.initStringControlTransfer(connection, AOAConstact.ACCESSORY_STRING_MANUFACTURER, "llvision");
                this.initStringControlTransfer(connection, AOAConstact.ACCESSORY_STRING_MODEL, "GLXSS ONE");
                this.initStringControlTransfer(connection, AOAConstact.ACCESSORY_STRING_DESCRIPTION, "请确认配件是否安装应用");
                this.initStringControlTransfer(connection, AOAConstact.ACCESSORY_STRING_VERSION, "0.1");
                this.initStringControlTransfer(connection, AOAConstact.ACCESSORY_STRING_URI, "http://llvision.com");
                this.initStringControlTransfer(connection, AOAConstact.ACCESSORY_STRING_SERIAL, "66");
                connection.controlTransfer(UsbConstants.USB_DIR_OUT | UsbConstants.USB_TYPE_VENDOR,
                        53, 0, 0, new byte[0], 0, 100);
            }else {
                Toast.makeText(this, R.string.prompt_msg1, Toast.LENGTH_LONG).show();
            }
            connection.close();
            return true;
        }
    }

    private void initStringControlTransfer(UsbDeviceConnection deviceConnection, int index, String string) {
        deviceConnection.controlTransfer((UsbConstants.USB_DIR_OUT | UsbConstants.USB_TYPE_VENDOR),
                52, 0, index, string.getBytes(), string.length(), 100);
    }
}
