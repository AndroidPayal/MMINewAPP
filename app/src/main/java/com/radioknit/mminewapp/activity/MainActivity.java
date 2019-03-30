package com.radioknit.mminewapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.radioknit.mminewapp.DeviceData;
import com.radioknit.mminewapp.R;
import com.radioknit.mminewapp.Utils;
import com.radioknit.mminewapp.bluetooth.DeviceConnector;
import com.radioknit.mminewapp.bluetooth.DeviceListActivity;
import com.radioknit.mminewapp.sharedpreference.TempSharedPreference;

import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by nishant on 19/5/17.
 */

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private String temp = "";

    private static TextView txtDate;
    private static TextView txtTime;
    private static TextView txtFloorNumber;
//    private static ImageView imgUp;
//    private static ImageView imgDown;
    private static GifImageView imgUp;
    private static GifImageView imgDown;
    private String remaningString = "";
    private String checkSum = "";
    private static TextView tvRunningStatus;

    private ImageView imgMainDisplay;
    private ImageView imgCarCall;
    private ImageView imgIoIndicator;
    private ImageView imgDateAndTime;
    private ImageView imgProgramCode;
    private ImageView imgDisplayPattern;
    private ImageView imgDeviceID;
    private ImageView imgProgrammableParameter;
    private ImageView imgSpeedSelectionCommand;
    private ImageView imgFloorCallBlocking;
    private ImageView imgLevelFunction;
    private ImageView imgErrorLog;
    private String temp1 = "";
    private static TextView mTxtEror;
    private static LinearLayout llError;
    private MainActivity mContext;
    private BluetoothAdapter bluetoothAdapter;
    // =============

    private OutputStream outputStream;
    private static final String DEVICE_NAME = "DEVICE_NAME";
    private static final String LOG = "LOG";

    private static final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss.SSS");

    private static String MSG_NOT_CONNECTED;
    private static String MSG_CONNECTING;
    private static String MSG_CONNECTED;

    private static DeviceConnector connector;
    private static MainActivity.BluetoothResponseHandler mHandler;
    DeviceListActivity deviceListActivity;

    private boolean hexMode, needClean;
    private boolean show_timings, show_direction;
    private String command_ending;
    private String deviceName;
    private int counter = 1;
    private ProgressDialog pd;
    private StringBuffer completReceivedString;
    private String addressConnected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.settings_activity, false);
        Log.e(TAG, "onCreate()");
        if (mHandler == null) mHandler = new MainActivity.BluetoothResponseHandler(this);
        else mHandler.setTarget(this);

        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);

        setContentView(R.layout.activity_main);

        completReceivedString = new StringBuffer();

        generateId();
        createObj();
        registerEvent();

        if (isConnected() && (savedInstanceState != null)) {
            setDeviceName(savedInstanceState.getString(DEVICE_NAME));
        } else getSupportActionBar().setSubtitle(MSG_NOT_CONNECTED);

    }

    private void registerEvent() {

        imgMainDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopConnection();
                startActivity(new Intent(mContext, MainDisplayActivity.class));
                finish();
            }
        });

        imgCarCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopConnection();
                startActivity(new Intent(mContext, CarCallActivity.class));
                finish();
            }
        });

        imgIoIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopConnection();
                startActivity(new Intent(mContext, IOIndicatorActivity.class));
                finish();
            }
        });

        imgDateAndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopConnection();
                startActivity(new Intent(mContext, SetDateTimeActivity.class));
                finish();
            }
        });

        imgProgramCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopConnection();
                startActivity(new Intent(mContext, ProgramCodeActivity.class));
                finish();
            }
        });


        imgDisplayPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopConnection();
                startActivity(new Intent(mContext, DisplayPatternActivity.class));
                finish();
            }
        });

        imgDeviceID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopConnection();
                startActivity(new Intent(mContext, DeviceIDActivity.class));
                finish();
            }
        });

        imgProgrammableParameter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopConnection();
                startActivity(new Intent(mContext, ProgrammablePrarmeterActivity.class));
                finish();
            }
        });

        imgSpeedSelectionCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopConnection();
                startActivity(new Intent(mContext, SpeedSelectionActivity.class));
                finish();
            }
        });

        imgFloorCallBlocking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopConnection();
                startActivity(new Intent(mContext, FloorCallBlockingActivity.class));
                finish();
            }
        });

        imgLevelFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopConnection();
                startActivity(new Intent(mContext, LevelFunctionActivity.class));
                finish();
            }
        });


        imgErrorLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopConnection();
                startActivity(new Intent(mContext, ViewErrorLogActivity.class));
                finish();
            }
        });

    }

    private void createObj() {
        mContext = MainActivity.this;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void generateId() {
        txtDate = (TextView) findViewById(R.id.tvDate);
        txtTime = (TextView) findViewById(R.id.tvTime);
        txtFloorNumber = (TextView)findViewById(R.id.tvFloorNumber);
        imgDown = (GifImageView)findViewById(R.id.imgArrowDown);
        imgUp  = (GifImageView)findViewById(R.id.imgArrowUp);
//        tvRunningStatus = (TextView)findViewById(R.id.tvRunnigstatus);

        imgMainDisplay = (ImageView)findViewById(R.id.imgMainDisplay);
        imgCarCall = (ImageView)findViewById(R.id.imgCarCall);
        imgIoIndicator = (ImageView)findViewById(R.id.imgIoIndicatore);
        imgDateAndTime = (ImageView)findViewById(R.id.imgDateTime);
        imgProgramCode = (ImageView)findViewById(R.id.imgProgramCode);
        imgDisplayPattern = (ImageView)findViewById(R.id.imgDisplayPattern);
        imgDeviceID = (ImageView)findViewById(R.id.imgDeviceID);
        imgProgrammableParameter = (ImageView)findViewById(R.id.imgProgramableParameter);
        imgSpeedSelectionCommand = (ImageView)findViewById(R.id.imgSpeedSelectionCommand);
        imgFloorCallBlocking = (ImageView)findViewById(R.id.imgFloorCallBlocking);
        imgLevelFunction = (ImageView)findViewById(R.id.imgLevelFunction);
        imgErrorLog = (ImageView)findViewById(R.id.imgErrorLog);
        mTxtEror = (TextView)findViewById(R.id.tvError);
        llError = (LinearLayout)findViewById(R.id.llError);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());

       // txtDate.setText(currentDateandTime);
    }

    // ============================================================================
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState");
        outState.putString(DEVICE_NAME, deviceName);
    }

    // ============================================================================

    /**
     * ???????? ?????????? ??????????
     */
    private boolean isConnected() {
        Log.e(TAG, "isConnected()");
        Log.e(TAG, "connector = "+connector);
        return (connector != null) && (connector.getState() == DeviceConnector.STATE_CONNECTED);
    }
    // ==========================================================================

    /**
     * ????????? ??????????
     */
    private void stopConnection() {
        Log.e(TAG, "stopConnection()");
        if (connector != null) {
            connector.stop();
            connector = null;
            deviceName = null;
        }
    }
    // ==========================================================================
    @Override
    public synchronized void onPause() {
        super.onPause();
        Log.e(TAG, "onPause()");
        stopConnection();
    }

    /**
     * ?????? ????????? ??? ???????????
     */
    private void startDeviceListActivity() {
        stopConnection();
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }
    // ============================================================================


    /**
     * ????????? ?????????? ?????? "?????"
     *
     * @return
     */
    @Override
    public boolean onSearchRequested() {
        if (super.isAdapterReady()) startDeviceListActivity();
        return false;
    }
    // ==========================================================================


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.device_control_activity, menu);
        return true;
    }
    // ============================================================================


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_search:
                if (super.isAdapterReady()) {
                    if (isConnected()) stopConnection();
                    else startDeviceListActivity();
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                return true;

            case R.id.wroteModeEnable :
                Intent intent = new Intent(mContext, WriteModeEnableActivity.class);
                startActivityForResult(intent,WRITE_MODE_ENABLE );
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // ============================================================================


    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart()");
        // hex mode
        final String mode = Utils.getPrefence(this, getString(R.string.pref_commands_mode));
        this.hexMode = mode.equals("HEX");

        this.command_ending = getCommandEnding();

        // ?????? ??????????? ???? ??????
        this.show_timings = Utils.getBooleanPrefence(this, getString(R.string.pref_log_timing));
        this.show_direction = Utils.getBooleanPrefence(this, getString(R.string.pref_log_direction));
        this.needClean = Utils.getBooleanPrefence(this, getString(R.string.pref_need_clean));

    }
    // ============================================================================


    @Override
    public synchronized void onResume() {
        super.onResume();

        Log.e(TAG,"onResume");
        String  address = TempSharedPreference.getPairedDeviceAddress(mContext);
        if(Utils.isStringNotNull(address)) {
            BluetoothDevice device = btAdapter.getRemoteDevice(address);
            if (super.isAdapterReady() && (connector == null)) setupConnector(device);

            setDeviceName(device.getName());
        }
    }

    //==========================
    /**
     * ???????? ?? ???????? ??????? ????????? ???????
     */
    private String getCommandEnding() {
        String result = Utils.getPrefence(this, getString(R.string.pref_commands_ending));
        if (result.equals("\\r\\n")) result = "\r\n";
        else if (result.equals("\\n")) result = "\n";
        else if (result.equals("\\r")) result = "\r";
        else result = "";
        return result;
    }
    // ============================================================================


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = btAdapter.getRemoteDevice(address);
                    if (super.isAdapterReady() && (connector == null)) setupConnector(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                super.pendingRequestEnableBt = false;
                if (resultCode != Activity.RESULT_OK) {
                    Utils.log("BT not enabled");
                }
                break;

            case  WRITE_MODE_ENABLE :
                Log.e(TAG, " Enable Write Mode");
        }
    }
    // ==========================================================================


    /**
     * ????????? ?????????? ? ???????????
     */
    private void setupConnector(BluetoothDevice connectedDevice) {
        stopConnection();

        try {
            String emptyName = getString(R.string.empty_device_name);
            DeviceData data = new DeviceData(connectedDevice, emptyName);
            connector = new DeviceConnector(data, mHandler);
            connector.connect();
        } catch (IllegalArgumentException e) {
            Utils.log("setupConnector failed: " + e.getMessage());
        }
    }
    // ==========================================================================

    /**

     * @param message  - ??????????????????
     * @param outgoing - ???????????????????
     */
    public void appendLog(String message, boolean hexMode, boolean outgoing, boolean clean) {

        StringBuffer msg = new StringBuffer();

        msg.append(hexMode ? Utils.toHex1(message) : message);
        if (outgoing) msg.append('\n');
    }


    // =========================================================================
    public void appendLog1(String message, boolean hexMode, boolean outgoing, boolean clean) {

        StringBuffer msg = new StringBuffer();

        msg.append(hexMode ? Utils.toHex1(message) : message);
        if (outgoing) msg.append('\n');

        completReceivedString.append(message);

        showReceivedData(completReceivedString.toString());
        completReceivedString.setLength(0);
    }

    private void showReceivedData(String strReceived) {

        String receivedString = strReceived;
        System.out.println("Received String: "+receivedString);
        if(Utils.isStringNotNull(receivedString)) {

            int indexOd = receivedString.indexOf("\r");
            String temp = receivedString.substring(0, indexOd);

            if (temp.startsWith("05")) {

                int floorNo = Integer.parseInt(temp.substring(4, 6), 16);
                String STAT0 = temp.substring(6, 8);
                String hexFour = String.format("%04x", Integer.parseInt(STAT0, 16));
                String strBinaryFour = Utils.hexToBin(hexFour);

                if (strBinaryFour.charAt(7) == '1') {
                    if (strBinaryFour.charAt(5) == '1') {
                        imgUp.setVisibility(View.VISIBLE);
                        imgDown.setVisibility(View.GONE);
                        imgUp.setImageResource(R.drawable.up_flashing);
//                        tvRunningStatus.setText("Up Running");
//                        Log.e(TAG, "Up Running");
                    } else {
                        imgUp.setVisibility(View.VISIBLE);
                        imgDown.setVisibility(View.GONE);
                        imgUp.setImageResource(R.drawable.up_arraow);
//                        Log.e(TAG, "Up study");
//                        tvRunningStatus.setText("Up");
                    }
                } else if (strBinaryFour.charAt(6) == '1') {
                    if (strBinaryFour.charAt(5) == '1') {
                        imgDown.setVisibility(View.VISIBLE);
                        imgUp.setVisibility(View.GONE);
                        imgDown.setImageResource(R.drawable.down_flashing);
//                        Log.e(TAG, "Down Running");
//                        tvRunningStatus.setText("Down Running");
                    } else {
                        imgDown.setVisibility(View.VISIBLE);
                        imgUp.setVisibility(View.GONE);
                        imgDown.setImageResource(R.drawable.down_arr);
//                        Log.e(TAG, "Down Study");
//                        tvRunningStatus.setText("Down");
                    }
                } else if (strBinaryFour.charAt(7) == '0') {
                    imgUp.setVisibility(View.GONE);
                    imgDown.setVisibility(View.GONE);

                } else if (strBinaryFour.charAt(6) == '0') {
                    imgUp.setVisibility(View.GONE);
                    imgDown.setVisibility(View.GONE);
                }

                if (Integer.parseInt(temp.substring(4, 6), 16) == 80) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 80 - Break Sw Fault ");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else if (Integer.parseInt(temp.substring(4, 6), 16) == 81) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 81 - Encoder dir Error");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else if (Integer.parseInt(temp.substring(4, 6), 16) == 82) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 82 - Encoder no pulse or reset sw stucked up");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else if (Integer.parseInt(temp.substring(4, 6), 16) == 83) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 83 - Motor Thermal ");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else if (Integer.parseInt(temp.substring(4, 6), 16) == 84) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 84 - Door open fault");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else if (Integer.parseInt(temp.substring(4, 6), 16) == 85) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 85 - Door close fault");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else if (Integer.parseInt(temp.substring(4, 6), 16) == 86) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 86 - Power fault");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else if (Integer.parseInt(temp.substring(4, 6), 16) == 87) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 87 - Final limit Cut ");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else if (Integer.parseInt(temp.substring(4, 6), 16) == 88) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 88 - Drive fault ");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else if (Integer.parseInt(temp.substring(4, 6), 16) == 89) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 89 - A.R.D. Mode Detect ");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else if (Integer.parseInt(temp.substring(4, 6), 16) == 90) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 90 - Terminal Switch Error ");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else if (Integer.parseInt(temp.substring(4, 6), 16) == 91) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 91 - Important floor tried to block ");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else if (Integer.parseInt(temp.substring(4, 6), 16) == 92) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 92 - Terminal Error ");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else if (Integer.parseInt(temp.substring(4, 6), 16) == 93) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 93 - Terminal Error ");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else if (Integer.parseInt(temp.substring(4, 6), 16) == 94) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 94 - Reserved ");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else if (Integer.parseInt(temp.substring(4, 6), 16) == 95) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 95 - Reserved ");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else if (Integer.parseInt(temp.substring(4, 6), 16) == 96) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 96 - Encoder Pulse and fin position mismatch ");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else if (Integer.parseInt(temp.substring(4, 6), 16) == 97) {
                    llError.setVisibility(View.VISIBLE);
                    mTxtEror.setText(" 97 - Door zone switch error ");
                    mTxtEror.setSelected(true);
                    mTxtEror.setSingleLine();
                } else {
                    txtFloorNumber.setText("" + floorNo);
                }
                temp = "";
            }

            if (temp.startsWith("11f2")) {
                String hrs = (temp.substring(4, 6));
                String min = (temp.substring(6, 8));
                txtTime.setText("" + hrs + " : " + min);
                temp = "";
            }

            if (temp.startsWith("11f3")) {
                String date = (temp.substring(4, 6));
                String month = (temp.substring(6, 8));
                String year = (temp.substring(8, 10));
//                txtDate.setText("20" + year + " / " + month + " / " + date);
                txtDate.setText(date +" / " + month+ " / " + "20" + year  );
                temp = "";
            }
        }
    }
    // =========================================================================

    void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        getSupportActionBar().setSubtitle(deviceName);
        getSupportActionBar().setTitle("Main Screen");
    }

    // ==========================================================================

    /**
     * ?????????? ?????? ?????? ?? bluetooth-??????
     */
    private static class BluetoothResponseHandler extends Handler {
        private WeakReference<MainActivity> mActivity;
        private String temp1 = "";
        private String temp = "";
        private String remaningString = "";

        public BluetoothResponseHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        public void setTarget(MainActivity target) {
            mActivity.clear();
            mActivity = new WeakReference<MainActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:

                        Utils.log("MESSAGE_STATE_CHANGE: " + msg.arg1);
                        final ActionBar bar = activity.getSupportActionBar();
                        switch (msg.arg1) {
                            case DeviceConnector.STATE_CONNECTED:
                                bar.setSubtitle(MSG_CONNECTED);
                                break;
                            case DeviceConnector.STATE_CONNECTING:
                                bar.setSubtitle(MSG_CONNECTING);
                                break;
                            case DeviceConnector.STATE_NONE:
                                bar.setSubtitle(MSG_NOT_CONNECTED);
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        final String readMessage = (String) msg.obj;
                        temp = temp + readMessage;
                        if(temp.contains("\r")){
                            //Log.e(TAG, " readMessage = "+ readMessage);
                            activity.appendLog1(temp, false, false, activity.needClean);
                            temp = "";
                        }
                        break;

                    case MESSAGE_DEVICE_NAME:
                        activity.setDeviceName((String) msg.obj);
                        break;

                    case MESSAGE_WRITE:

                        break;

                    case MESSAGE_TOAST:
                        break;
                }
            }
        }
    }
    // ==========================================================================

}
