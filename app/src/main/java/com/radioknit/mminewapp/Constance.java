package com.radioknit.mminewapp;

import android.os.Environment;

import java.io.File;

/**
 * Created by nishant on 27/1/17.
 */

public class Constance {

    public static final String VALUE_APP_FOLDER_NAME = "NetworkDemo";
    public static final String VALUE_CSV_FILE_NAME = "RecivedDataDetails";

    public static final String VALUE_CSV_PATH = Environment.getExternalStorageDirectory() + File.separator
            + VALUE_APP_FOLDER_NAME + File.separator;


    public static final int NO_OF_FLOORS = 16;


    public static final String CSV_ID ="ID";
    public static final String CSV_DATE = "Date";
    public static final String CSV_TIME = "Time";
    public static final String CSV_MSG = "Message";
    public static final String CSV_SERVER_EPOCH = "ServerEpoch";

    public static final String VALUE_CSV_EXTENTION = ".csv";

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    public static String DEVICE_ADDRESS = "deviceAddress";

    public static final String ERROR_CODE_80 ="Break sw fault";
    public static final String ERROR_CODE_81 = "Encoder dir Error";
    public static final String ERROR_CODE_82 = "Encoder no pulse or reset sw stucked up";
    public static final String ERROR_CODE_83 = "Motor Thermal";
    public static final String ERROR_CODE_84 = "Door Open Fault";
    public static final String ERROR_CODE_85 = "Door Close Fault";
    public static final String ERROR_CODE_86 = "power Fault";
    public static final String ERROR_CODE_87 = "final Limit cut";
    public static final String ERROR_CODE_88 = "Drive Fault";
    public static final String ERROR_CODE_89 = "A.R.D. Mode Detect";
    public static final String ERROR_CODE_90 = "Terminal Switch Error";
    public static final String ERROR_CODE_91 = "Important Floor Tried to Block";
    public static final String ERROR_CODE_92 = "Terminal Error";
    public static final String ERROR_CODE_93 = "Terminal Error";
    public static final String ERROR_CODE_94 = "Reserved";
    public static final String ERROR_CODE_95 = "Reserved";
    public static final String ERROR_CODE_96 = "Encoder Pulses and Fin position mismatch";
    public static final String ERROR_CODE_97 = "Door zone Switch Error";





}
