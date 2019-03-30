package com.radioknit.mminewapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

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
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;

public class IOIndicatorActivity extends BaseActivity {

    private static final String TAG = "IOIndicatorActivity";
    private Context mContext;
    private BluetoothAdapter bluetoothAdapter;
    private Button btnInputSignals;
    private Button btnoutputSignals;
    private LinearLayout llInputSignals;
    private LinearLayout llOutputSignals;
    private static CheckBox chk_OP_RunUp;
    private static CheckBox chk_OP_RunDn;
    private static CheckBox chk_OP_Speed_1_OP;
    private static CheckBox chk_OP_Speed_2_OP;
    private static CheckBox chk_OP_ARD_Relay;
    private static CheckBox chk_OP_CT_Relay;
    private static CheckBox chk_OP_Speed_3_OP;
    private static CheckBox chk_OP_MainContact;
    private static CheckBox chk_OP_DC_OP;
    private static CheckBox chk_OP_DO_OP;
    private static CheckBox chk_OP_Blank_1;
    private static CheckBox chk_OP_Break_Sig_1;
    private static CheckBox chk_OP_Break_Sig_2;
    private static CheckBox chk_OP_Blank_2;
    private static CheckBox chk_OP_Blank_3;
    private static CheckBox chk_OP_Blank_4;
    private static CheckBox chk_io_MC_Room_ins;
    private static CheckBox chk_io_ARDIp;
    private static CheckBox chk_io_SldnSwUp2;
    private static CheckBox chk_io_SldnSwDn2;
    private static CheckBox chk_io_MotorTherm;
    private static CheckBox chk_io_FiremanSw;
    private static CheckBox chk_io_CtIp;
    private static CheckBox chk_io_BreakSwitch;
    private static CheckBox chk_io_Encoder_ch_A;
    private static CheckBox chk_io_Encoder_ch_BB;
    private static CheckBox chk_io_UpStopSw;
    private static CheckBox chk_io_RstDnStop;
    private static CheckBox chk_io_SlowSwDn1;
    private static CheckBox chk_io_SlowSwUp1;
    private static CheckBox chk_io_DoorZoneSw;
    private static CheckBox chk_io_BrkIn;
    private static CheckBox chk_io_AM;
    private static CheckBox chk_io_MntUp;
    private static CheckBox chk_io_MntDn;
    private static CheckBox chk_io_SftEdge;
    private static CheckBox chk_io_IR;
    private static CheckBox chk_io_RunStp;
    private static CheckBox chk_io_Far;
    private static CheckBox chk_io_RRD;
    private static CheckBox chk_io_Blank_2;
    private static CheckBox chk_io_Blank_3;
    private static CheckBox chk_io_Blank_4;
    private static CheckBox chk_io_Blank_5;
    private static CheckBox chk_io_Blank_6;
    private static CheckBox chk_io_Blank_7;
    private static CheckBox chk_io_Blank_8;
    private static CheckBox chk_io_Blank_9;

    // =============

    private OutputStream outputStream;
    private static final String DEVICE_NAME = "DEVICE_NAME";
    private static final String LOG = "LOG";

    private static final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss.SSS");

    private static String MSG_NOT_CONNECTED;
    private static String MSG_CONNECTING;
    private static String MSG_CONNECTED;

    private static DeviceConnector connector;
    private static IOIndicatorActivity.BluetoothResponseHandler mHandler;
    DeviceListActivity deviceListActivity;

    private boolean hexMode, needClean;
    private boolean show_timings, show_direction;
    private String command_ending;
    private String deviceName;
    private int counter = 1;
    private ProgressDialog pd;
    private StringBuffer completReceivedString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.settings_activity, false);

        if (mHandler == null) mHandler = new IOIndicatorActivity.BluetoothResponseHandler(this);
        else mHandler.setTarget(this);

        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);
        setContentView(R.layout.activity_ioindicator);
        completReceivedString = new StringBuffer();

        generateId();
        createObj();
        registerEvent();

        if (isConnected() && (savedInstanceState != null)) {
            setDeviceName(savedInstanceState.getString(DEVICE_NAME));
        } else getSupportActionBar().setSubtitle(MSG_NOT_CONNECTED);

    }

    private void generateId() {

        btnInputSignals = (Button) findViewById(R.id.btnInputSignals);
        btnoutputSignals = (Button)findViewById(R.id.btnOutputSignals);
        llInputSignals = (LinearLayout) findViewById(R.id.llInout_Signals);
        llOutputSignals = (LinearLayout)findViewById(R.id.llOutputSignals);
        chk_OP_RunUp = (CheckBox)findViewById(R.id.rdo_OP_Run_up);
        chk_OP_RunDn = (CheckBox)findViewById(R.id.rdo_OP_Run_dn);
        chk_OP_Speed_1_OP = (CheckBox)findViewById(R.id.rdo_OP_Speed_1_op);
        chk_OP_Speed_2_OP = (CheckBox)findViewById(R.id.rdo_OP_Speed_2_op);
        chk_OP_ARD_Relay = (CheckBox)findViewById(R.id.rdo_OP_ARD_Relay);
        chk_OP_CT_Relay = (CheckBox)findViewById(R.id.rdo_OP_CT_Realy);
        chk_OP_Speed_3_OP = (CheckBox)findViewById(R.id.rdo_OP_Speed_3_op);
        chk_OP_MainContact = (CheckBox)findViewById(R.id.rdo_OP_Main_contact);
        chk_OP_DC_OP = (CheckBox)findViewById(R.id.rdo_OP_DC_op);
        chk_OP_DO_OP = (CheckBox)findViewById(R.id.rdo_OP_DO_op);
        chk_OP_Break_Sig_1 = (CheckBox)findViewById(R.id.rdo_OP_Break_Sig_1);
        chk_OP_Break_Sig_2 = (CheckBox)findViewById(R.id.rdo_OP_Break_Sig_2);
        chk_OP_Blank_1 = (CheckBox)findViewById(R.id.rdo_OP_Blank_1);
        chk_OP_Blank_2 = (CheckBox)findViewById(R.id.rdo_OP_Blank_2);
        chk_OP_Blank_3 = (CheckBox)findViewById(R.id.rdo_OP_Blank_3);
        chk_OP_Blank_4 = (CheckBox)findViewById(R.id.rdo_OP_Blank_4);

        chk_io_MC_Room_ins = (CheckBox) findViewById(R.id.chMCRoomIns);
        chk_io_ARDIp = (CheckBox) findViewById(R.id.chArdIP);
        chk_io_SldnSwUp2 = (CheckBox) findViewById(R.id.chSldnSwUp2);
        chk_io_SldnSwDn2 = (CheckBox) findViewById(R.id.chSldnSwDn2);
        chk_io_MotorTherm = (CheckBox) findViewById(R.id.chMotorTherm);
        chk_io_FiremanSw = (CheckBox) findViewById(R.id.chFiremanSw);
        chk_io_CtIp = (CheckBox)findViewById(R.id.chCtIP);
        chk_io_BreakSwitch = (CheckBox)findViewById(R.id.chBreakSwitch);
        chk_io_Encoder_ch_A = (CheckBox)findViewById(R.id.chEncoderChA);
        chk_io_Encoder_ch_BB = (CheckBox)findViewById(R.id.chEnccoderChB);
        chk_io_UpStopSw = (CheckBox)findViewById(R.id.chUpStopSw);
        chk_io_RstDnStop = (CheckBox)findViewById(R.id.chRstDnStop);
        chk_io_SlowSwDn1 = (CheckBox)findViewById(R.id.chSlowSwDN1);
        chk_io_SlowSwUp1 = (CheckBox)findViewById(R.id.chSlowSwUP1);
        chk_io_DoorZoneSw = (CheckBox)findViewById(R.id.chDoorZoneSw);
        chk_io_BrkIn = (CheckBox)findViewById(R.id.chBrkIn);
        chk_io_AM = (CheckBox)findViewById(R.id.chAm);
        chk_io_MntUp = (CheckBox)findViewById(R.id.chMntUp);
        chk_io_MntDn = (CheckBox)findViewById(R.id.chMntDn);
        chk_io_SftEdge = (CheckBox)findViewById(R.id.chSftEdge);
        chk_io_IR= (CheckBox)findViewById(R.id.chIR);
        chk_io_RunStp = (CheckBox)findViewById(R.id.chRunSft);
        chk_io_Far = (CheckBox)findViewById(R.id.chFAR);
        chk_io_RRD = (CheckBox)findViewById(R.id.chRRD);
        chk_io_Blank_2 = (CheckBox)findViewById(R.id.rdoBlankTwo);
        chk_io_Blank_3 = (CheckBox)findViewById(R.id.rdoBlank_three);
        chk_io_Blank_4 = (CheckBox)findViewById(R.id.rdoBlank_four);
        chk_io_Blank_5 = (CheckBox)findViewById(R.id.rdoBlank_five);
        chk_io_Blank_6 = (CheckBox)findViewById(R.id.rdoBlank_six);
        chk_io_Blank_7 = (CheckBox)findViewById(R.id.rdoBlank_seven);
        chk_io_Blank_8 = (CheckBox)findViewById(R.id.rdoBlankEight);
        chk_io_Blank_9 = (CheckBox)findViewById(R.id.rdoBlankNine);

    }

    private void registerEvent() {
        btnInputSignals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                llInputSignals.setVisibility(View.VISIBLE);
                llOutputSignals.setVisibility(View.GONE);
            }
        });

        btnoutputSignals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llInputSignals.setVisibility(View.GONE);
                llOutputSignals.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createObj() {
        mContext = getApplicationContext();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext, MainActivity.class));
        finish();
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
    //========================================================================


    /**
     * ???????? ?????????? ??????????
     */
    private boolean isConnected() {
        return (connector != null) && (connector.getState() == DeviceConnector.STATE_CONNECTED);
    }
    // ==========================================================================


    /**
     * ????????? ??????????
     */
    private void stopConnection() {
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

     * @param message  - ????? ??? ???????????
     * @param outgoing - ??????????? ????????
     */
    public void appendLog(String message, boolean hexMode, boolean outgoing, boolean clean) {

        StringBuffer msg = new StringBuffer();

        msg.append(hexMode ? Utils.toHex1(message) : message);
        if (outgoing) msg.append('\n');
    }


    // =========================================================================
    public void appendLog1(String message, boolean hexMode, boolean outgoing, boolean clean) {

        StringBuffer msg = new StringBuffer();

        completReceivedString.append(message);

        String receivedString = completReceivedString.toString();
        Log.e(TAG, "receivedString = "+receivedString);

        int indexOD = receivedString.indexOf("\r");
        String temp1 = receivedString.substring(0, indexOD);
        receivedString = receivedString.substring(indexOD, receivedString.length());

        if (temp1.startsWith("71")) {
            setIOValues(temp1);
            temp1 = "";
        }

        if(temp1.startsWith("77")){
            setIOValuesTwo(temp1);
            temp1 = "";
        }
        completReceivedString.setLength(0);

    }
    // =========================================================================

    void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        getSupportActionBar().setSubtitle(deviceName);
        getSupportActionBar().setTitle("IO Indicator");
    }


    // ==========================================================================

    /**
     * ?????????? ?????? ?????? ?? bluetooth-??????
     */
    private static class BluetoothResponseHandler extends Handler {
        private WeakReference<IOIndicatorActivity> mActivity;
        private String temp = "";
        public BluetoothResponseHandler(IOIndicatorActivity activity) {
            mActivity = new WeakReference<IOIndicatorActivity>(activity);
        }

        public void setTarget(IOIndicatorActivity target) {
            mActivity.clear();
            mActivity = new WeakReference<IOIndicatorActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            IOIndicatorActivity activity = mActivity.get();
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
                        /**
                         *
                         *  /dev/ttyUSB0
                         *
                         */

                        final String readMessage = (String) msg.obj;
                        Log.e(TAG, "readMessage = "+ readMessage);
                        temp = temp + readMessage;
                        if(temp.contains("\r")){
                            Log.e(TAG, "temp = "+temp);
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
                        // stub
                        break;
                }
            }
        }
    }
    // ==========================================================================

    private static void setIOValues(String tempstr) {

//        if(Utils.checkRecivedStringValid(temp)) {
        try {
            int in = tempstr.indexOf("71");
//            Utils.calculateChecksumValue(tempstr);
            //Log.e(TAG, " -----------------------------------");
            String hexTwo = String.format("%04x", Integer.parseInt(tempstr.substring(2,4),16));
            String hexThree = String.format("%04x", Integer.parseInt(tempstr.substring(4,6),16));
            String hexFour = String.format("%04x", Integer.parseInt(tempstr.substring(6,8),16));
            String hexFive = String.format("%04x", Integer.parseInt(tempstr.substring(8,10),16));
//            String hexSix = String.format("%04x", Integer.parseInt(tempstr.substring(12,14),16));
            String hexSix = String.format("%04x", Integer.parseInt(tempstr.substring(10,12),16));

//                Log.e(TAG, "hex Two = " + hexTwo + " bin = " + Utils.hexToBin(hexTwo));
//                Log.e(TAG, "hex Three = " + hexThree + " bin = " + Utils.hexToBin(hexThree));
//                Log.e(TAG, "hex Four = " + hexFour + " bin = " + Utils.hexToBin(hexFour));
//                Log.e(TAG, "hex Five = " + hexFive + " bin = " + Utils.hexToBin(hexFive));
//                Log.e(TAG, "hex Six = " + hexSix + " bin = " + Utils.hexToBin(hexSix));

            String two = Utils.hexToBin(hexTwo);
            String three = Utils.hexToBin(hexThree);
            String four = Utils.hexToBin(hexFour);
            String five = Utils.hexToBin(hexFive);
            String six = Utils.hexToBin(hexSix);

            if (five.charAt(7) == '0') {
                chk_OP_RunUp.setChecked(true);
            } else {
                chk_OP_RunUp.setChecked(false);
            }
            if (five.charAt(6) == '0') {
                chk_OP_RunDn.setChecked(true);
            } else {
                chk_OP_RunDn.setChecked(false);
            }
            if (five.charAt(5) == '0') {
                chk_OP_Speed_1_OP.setChecked(true);
            } else {
                chk_OP_Speed_1_OP.setChecked(false);
            }
            if (five.charAt(4) == '0') {
                chk_OP_Speed_2_OP.setChecked(true);
            } else {
                chk_OP_Speed_2_OP.setChecked(false);
            }
            if (five.charAt(3) == '0') {
                chk_OP_ARD_Relay.setChecked(true);
            } else {
                chk_OP_ARD_Relay.setChecked(false);
            }
            if (five.charAt(2) == '0') {
                chk_OP_CT_Relay.setChecked(true);
            } else {
                chk_OP_CT_Relay.setChecked(false);
            }
            if (five.charAt(1) == '0') {
                chk_OP_Speed_3_OP.setChecked(true);
            } else {
                chk_OP_Speed_3_OP.setChecked(false);
            }
            if (five.charAt(0) == '0') {
                chk_OP_MainContact.setChecked(true);
            } else {
                chk_OP_MainContact.setChecked(false);
            }

            if (six.charAt(7) == '0') {
                chk_OP_DC_OP.setChecked(true);
            } else {
                chk_OP_DC_OP.setChecked(false);
            }
            if (six.charAt(6) == '0') {
                chk_OP_DO_OP.setChecked(true);
            } else {
                chk_OP_DO_OP.setChecked(false);
            }
            if (six.charAt(5) == '0') {
                chk_OP_Blank_1.setChecked(true);
            } else {
                chk_OP_Blank_1.setChecked(false);
            }
            if (six.charAt(4) == '0') {
                chk_OP_Break_Sig_1.setChecked(true);
            } else {
                chk_OP_Break_Sig_1.setChecked(false);
            }
            if (six.charAt(3) == '0') {
                chk_OP_Break_Sig_2.setChecked(true);
            } else {
                chk_OP_Break_Sig_2.setChecked(false);
            }
            if (six.charAt(2) == '0') {
                chk_OP_Blank_2.setChecked(true);
            } else {
                chk_OP_Blank_2.setChecked(false);
            }
            if (six.charAt(1) == '0') {
                chk_OP_Blank_3.setChecked(true);
            } else {
                chk_OP_Blank_3.setChecked(false);
            }
            if (six.charAt(0) == '0') {
                chk_OP_Blank_4.setChecked(true);
            } else {
                chk_OP_Blank_4.setChecked(false);
            }

            // For Input Signals

            if (two.charAt(0) == '1') {
                chk_io_MC_Room_ins.setChecked(true);
            } else {
                chk_io_MC_Room_ins.setChecked(false);
            }
            if (two.charAt(1) == '1') {
                chk_io_ARDIp.setChecked(true);
            } else {
                chk_io_ARDIp.setChecked(false);
            }
            if (two.charAt(2) == '1') {
                chk_io_SldnSwUp2.setChecked(true);
            } else {
                chk_io_SldnSwUp2.setChecked(false);
            }
            if (two.charAt(3) == '1') {
                chk_io_SldnSwDn2.setChecked(true);
            } else {
                chk_io_SldnSwDn2.setChecked(false);
            }
            if (two.charAt(4) == '1') {
                chk_io_MotorTherm.setChecked(true);
            } else {
                chk_io_MotorTherm.setChecked(false);
            }
            if (two.charAt(5) == '1') {
                chk_io_FiremanSw.setChecked(true);
            } else {
                chk_io_FiremanSw.setChecked(false);
            }
            if (two.charAt(6) == '1') {
                chk_io_CtIp.setChecked(true);
            } else {
                chk_io_CtIp.setChecked(false);
            }
            if (two.charAt(7) == '1') {
                chk_io_BreakSwitch.setChecked(true);
            } else {
                chk_io_BreakSwitch.setChecked(false);
            }

            /*if (four.charAt(7) == '1') {
                chk_io_Encoder_ch_BB.setChecked(true);
            } else {
                chk_io_Encoder_ch_BB.setChecked(false);
            }
            if (four.charAt(6) == '1') {
                chk_io_Encoder_ch_A.setChecked(true);
            } else {
                chk_io_Encoder_ch_A.setChecked(false);
            }*/

            if (four.charAt(5) == '1') {
                chk_io_UpStopSw.setChecked(true);
            } else {
                chk_io_UpStopSw.setChecked(false);
            }
            if (four.charAt(4) == '1') {
                chk_io_RstDnStop.setChecked(true);
            } else {
                chk_io_RstDnStop.setChecked(false);
            }
            if (four.charAt(3) == '1') {
                chk_io_SlowSwDn1.setChecked(true);
            } else {
                chk_io_SlowSwDn1.setChecked(false);
            }
            if (four.charAt(2) == '1') {
                chk_io_SlowSwUp1.setChecked(true);
            } else {
                chk_io_SlowSwUp1.setChecked(false);
            }
            if (four.charAt(1) == '1') {
                chk_io_DoorZoneSw.setChecked(true);
            } else {
                chk_io_DoorZoneSw.setChecked(false);
            }
            if (four.charAt(0) == '1') {
                chk_io_BrkIn.setChecked(true);
            } else {
                chk_io_BrkIn.setChecked(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
    }

    private static void setIOValuesTwo(String tempStr) {

        try {
            String receivedString = tempStr;
            Log.e(TAG, " -----------------------------------");

//            if(Utils.checkRecivedStringValid(temp)) {

            String hexTwo = String.format("%04x", Integer.parseInt(receivedString.substring(2,4),16));
                Log.e(TAG, "hex Two = " + hexTwo + " bin = " + Utils.hexToBin(hexTwo));
            String two = Utils.hexToBin(hexTwo);

            if (two.charAt(7) == '0') {
                chk_io_AM.setChecked(true);
            } else {
                chk_io_AM.setChecked(false);
            }
            if (two.charAt(6) == '0') {
                chk_io_MntUp.setChecked(true);
            } else {
                chk_io_MntUp.setChecked(false);
            }
            if (two.charAt(5) == '0') {
                chk_io_MntDn.setChecked(true);
            } else {
                chk_io_MntDn.setChecked(false);
            }
            if (two.charAt(4) == '0') {
                chk_io_SftEdge.setChecked(true);
            } else {
                chk_io_SftEdge.setChecked(false);
            }
            if (two.charAt(3) == '0') {
                chk_io_IR.setChecked(true);
            } else {
                chk_io_IR.setChecked(false);
            }
            if (two.charAt(2) == '0') {
                chk_io_RunStp.setChecked(true);
            } else {
                chk_io_RunStp.setChecked(false);
            }
            if (two.charAt(1) == '0') {
                chk_io_Far.setChecked(true);
            } else {
                chk_io_Far.setChecked(false);
            }
            if (two.charAt(0) == '0') {
                chk_io_RRD.setChecked(true);
            } else {
                chk_io_RRD.setChecked(false);
            }
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
