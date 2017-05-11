package com.llvision.usb.library;

/**
 * Created by jerry on 2017/5/11.
 */

public class AOAConstact {
    /**AOAv1*/
    public static final int PID_ACCESSORY = 0x2D00;//Provides two bulk endpoints for communicating with an Android application.
    public static final int PID_ACCESSORY_ADB = 0x2D01;//For debugging purposes during accessory development. Available only if the user has enabled USB Debugging in the Android device settings.
    /**AOAv2*/
    public static final int PID_AUDIO = 0x2D02;//For streaming audio from an Android device to an accessory.
    public static final int PID_AUDIO_ADB = 0x2D03;
    public static final int PID_ACCESSORY_AUDIO = 0x2D04;
    public static final int PID_ACCESSORY_AUDIO_ADB = 0x2D05;

    /**AOA host profile*/
    public static final int ACCESSORY_STRING_MANUFACTURER = 0;
    public static final int ACCESSORY_STRING_MODEL = 1;
    public static final int ACCESSORY_STRING_DESCRIPTION = 2;
    public static final int ACCESSORY_STRING_VERSION = 3;
    public static final int ACCESSORY_STRING_URI = 4;
    public static final int ACCESSORY_STRING_SERIAL = 5;

}
