//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.llvision.usb.library;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.os.Message;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public class UsbUtil {
    public UsbUtil() {
    }

    public static File createFileWithByte(byte[] bytes, String folder, String fileName) {
        File file = new File(folder, fileName);
        FileOutputStream outputStream = null;
        BufferedOutputStream bufferedOutputStream = null;

        try {
            if(file.exists()) {
                file.delete();
            }

            file.createNewFile();
            outputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(bytes);
            bufferedOutputStream.flush();
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException err) {
                    err.printStackTrace();
                }
            }

            if(bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

        }

        return file;
    }

    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;

        try {
            File e = new File(filePath);
            FileInputStream fis = new FileInputStream(e);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];

            int n;
            while((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }

            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException var7) {
            var7.printStackTrace();
        } catch (IOException var8) {
            var8.printStackTrace();
        }

        return buffer;
    }

    public static byte[] concatByteArray(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static boolean isGoodJson(String json) {
        try {
            (new JsonParser()).parse(json);
            return true;
        } catch (JsonParseException var2) {
            System.out.println("bad json: " + json);
            return false;
        }
    }

    public static void killProcess(String packageName, Context context, boolean isForce) {
        ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        if(isForce) {
            try {
                Method runningAppProcesses = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", new Class[]{String.class});
                runningAppProcesses.invoke(mActivityManager, new Object[]{packageName});
            } catch (Exception var8) {
                var8.printStackTrace();
            }
        } else {
            List runningAppProcesses1 = mActivityManager.getRunningAppProcesses();
            Iterator i$ = runningAppProcesses1.iterator();

            while(i$.hasNext()) {
                RunningAppProcessInfo runningAppProcessInfo = (RunningAppProcessInfo)i$.next();
                String processName = runningAppProcessInfo.processName;
                if(processName.equals(packageName)) {
                    packageName = processName;
                    mActivityManager.killBackgroundProcesses(processName);
                }
            }
        }

    }

    public static boolean isServiceRun(Context context, String serviceName) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List list = am.getRunningServices(30);
        Iterator i$ = list.iterator();

        RunningServiceInfo info;
        do {
            if(!i$.hasNext()) {
                return false;
            }

            info = (RunningServiceInfo)i$.next();
        } while(!info.service.getClassName().contains(serviceName));

        return true;
    }

    public static void doUsbHandler(UsbHandler usbHandler, Object obj, int what) {
        if(usbHandler != null) {
            Message message = new Message();
            message.obj = obj;
            message.what = what;
            usbHandler.sendMessage(message);
        }

    }
}
