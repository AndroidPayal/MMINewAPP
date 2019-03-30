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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.radioknit.mminewapp.CalculateCheckSum;
import com.radioknit.mminewapp.DeviceData;
import com.radioknit.mminewapp.R;
import com.radioknit.mminewapp.Utils;
import com.radioknit.mminewapp.bluetooth.DeviceConnector;
import com.radioknit.mminewapp.bluetooth.DeviceListActivity;
import com.radioknit.mminewapp.sharedpreference.TempSharedPreference;

import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DeviceIDActivity extends BaseActivity {

    private static final String TAG = "DeviceIDActivity";
    private String connectedDeviceName;
    ArrayList<String> arrCommandValueList;
    private Spinner spinDeviceID;
    private ArrayAdapter<String> adapter;
    private Button btnSetDeviceID;
    private EditText edtDeviceID;
    private TextView txtDeviceID;
    private Button btnViewDeviceID;
    private LinearLayout llSetDeveiceID;
    private RelativeLayout rlViewDeviceID;
    private int FLAG_DEVEICE_ID = 1;
    private Button btnSetValues;
    private Button btnGetValues;
    private TextView txtLPB_0;
    private TextView txtLPB_1;
    private TextView txtLPB_2;
    private TextView txtLPB_3;
    private TextView txtLPB_4;
    private TextView txtLPB_5;
    private TextView txtLPB_6;
    private TextView txtLPB_7;
    private TextView txtLPB_8;
    private TextView txtLPB_9;
    private TextView txtLPB_10;
    private TextView txtLPB_11;
    private TextView txtLPB_12;
    private TextView txtLPB_13;
    private TextView txtLPB_14;
    private TextView txtLPB_15;
    private TextView txtLPB_16;
    private TextView txtLPB_17;
    private TextView txtLPB_18;
    private TextView txtLPB_19;
    private TextView txtLPB_20;
    private TextView txtLPB_21;
    private TextView txtLPB_22;
    private TextView txtLPB_23;
    private TextView txtLPB_24;
    private TextView txtLPB_25;
    private TextView txtLPB_26;
    private TextView txtLPB_27;
    private TextView txtLPB_28;
    private TextView txtLPB_29;
    private TextView txtLPB_30;
    private TextView txtLPB_31;
    private TextView txtCOP_1;
    private TextView txtCOP_2;
    private int counter = 1;
    private Context mContext;
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
    private static DeviceIDActivity.BluetoothResponseHandler mHandler;
    DeviceListActivity deviceListActivity;

    private boolean hexMode, needClean;
    private boolean show_timings, show_direction;
    private String command_ending;
    private String deviceName;

    private ProgressDialog pd;
    private StringBuffer completReceivedString;
    private static boolean receiveFlag = false;
    private static String strTemp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.settings_activity, false);

        if (mHandler == null) mHandler = new DeviceIDActivity.BluetoothResponseHandler(this);
        else mHandler.setTarget(this);

        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);

        setContentView(R.layout.activity_device_id);
        completReceivedString = new StringBuffer();
        generateId();
        createObj();
        registerEvent();

        if (isConnected() && (savedInstanceState != null)) {
            setDeviceName(savedInstanceState.getString(DEVICE_NAME));
        } else getSupportActionBar().setSubtitle(MSG_NOT_CONNECTED);

    }

    private void generateId() {
        btnSetDeviceID = (Button) findViewById(R.id.btnSetDeviceID);
        spinDeviceID = (Spinner) findViewById(R.id.spin_deviceID);
        edtDeviceID = (EditText) findViewById(R.id.edtDeviceID);
        txtDeviceID = (TextView) findViewById(R.id.tvDeviceID);
        btnViewDeviceID = (Button) findViewById(R.id.btnViewDeviceID);
        llSetDeveiceID = (LinearLayout) findViewById(R.id.llSetValues);
        rlViewDeviceID = (RelativeLayout) findViewById(R.id.rlViewDeviceIDValues);
        btnSetValues = (Button) findViewById(R.id.btnSetDeviceIDValues);
        btnGetValues = (Button) findViewById(R.id.btnViewDeviceIDValues);
        txtLPB_0 = (TextView) findViewById(R.id.tvLPB_0_floor);
        txtLPB_1 = (TextView) findViewById(R.id.tvLPB_1_floor);
        txtLPB_2 = (TextView) findViewById(R.id.tvLPB_2_floor);
        txtLPB_3 = (TextView) findViewById(R.id.tvLPB_3_floor);
        txtLPB_4 = (TextView) findViewById(R.id.tvLPB_4_floor);
        txtLPB_5 = (TextView) findViewById(R.id.tvLPB_5_floor);
        txtLPB_6 = (TextView) findViewById(R.id.tvLPB_6_floor);
        txtLPB_7 = (TextView) findViewById(R.id.tvLPB_7_floor);
        txtLPB_8 = (TextView) findViewById(R.id.tvLPB_8_floor);
        txtLPB_9 = (TextView) findViewById(R.id.tvLPB_9_floor);
        txtLPB_10 = (TextView) findViewById(R.id.tvLPB_10_floor);
        txtLPB_11 = (TextView) findViewById(R.id.tvLPB_11_floor);
        txtLPB_12 = (TextView) findViewById(R.id.tvLPB_12_floor);
        txtLPB_13 = (TextView) findViewById(R.id.tvLPB_13_floor);
        txtLPB_14 = (TextView) findViewById(R.id.tvLPB_14_floor);
        txtLPB_15 = (TextView) findViewById(R.id.tvLPB_15_floor);
        txtLPB_16 = (TextView) findViewById(R.id.tvLPB_16_floor);
        txtLPB_17 = (TextView) findViewById(R.id.tvLPB_17_floor);
        txtLPB_18 = (TextView) findViewById(R.id.tvLPB_18_floor);
        txtLPB_19 = (TextView) findViewById(R.id.tvLPB_19_floor);
        txtLPB_20 = (TextView) findViewById(R.id.tvLPB_20_floor);
        txtLPB_21 = (TextView) findViewById(R.id.tvLPB_21_floor);
        txtLPB_22 = (TextView) findViewById(R.id.tvLPB_22_floor);
        txtLPB_23 = (TextView) findViewById(R.id.tvLPB_23_floor);
        txtLPB_24 = (TextView) findViewById(R.id.tvLPB_24_floor);
        txtLPB_25 = (TextView) findViewById(R.id.tvLPB_25_floor);
        txtLPB_26 = (TextView) findViewById(R.id.tvLPB_26_floor);
        txtLPB_27 = (TextView) findViewById(R.id.tvLPB_27_floor);
        txtLPB_28 = (TextView) findViewById(R.id.tvLPB_28_floor);
        txtLPB_29 = (TextView) findViewById(R.id.tvLPB_29_floor);
        txtLPB_30 = (TextView) findViewById(R.id.tvLPB_30_floor);
        txtLPB_31 = (TextView) findViewById(R.id.tvLPB_31_floor);
        txtCOP_1 = (TextView) findViewById(R.id.tvCop_1);
        txtCOP_2 = (TextView) findViewById(R.id.tvCop_2);
    }


    private void createObj() {
        mContext = DeviceIDActivity.this;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        arrCommandValueList = new ArrayList<String>();


        for (int i = 0; i <= 31; i++) {
            arrCommandValueList.add(String.valueOf(i));
        }

        adapter = new ArrayAdapter<String>(mContext, R.layout.list_item, getResources().getStringArray(R.array.arr_device_id));

        spinDeviceID.setAdapter(adapter);

    }

    void delay(){
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void registerEvent() {
        final Handler ha = new Handler();
        btnSetDeviceID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSetDeviceID();
            }
        });

        btnViewDeviceID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completReceivedString.setLength(0);
                counter = 0;
                 //pd = ProgressDialog.show(mContext,"","Please wait",true);
                if (isConnected()) {
                    pd = ProgressDialog.show(mContext, "", "Please wait", true);
                    boolean b = ha.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            //call function
                            if (counter == 0) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            }else if (counter == 1) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 2) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 3) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 4) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 5) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 6) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 7) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 8) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 9) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 10) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 11) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 12) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 13) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 14) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 15) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 16) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 17) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 18) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 19) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 20) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 21) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 22) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 23) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 24) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 25) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 26) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 27) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 28) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 29) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 30) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 31) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 32) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            } else if (counter == 33) {
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                            }else if(counter == 34){
                                callViewDeviceId(counter);
                                delay();
                                counter++;
                                if(isConnected()){
                                    pd.dismiss();
                                }
                                showReceivedDataNew();
                            }

                            ha.postDelayed(this, 500);
                        }
                    }, 500);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnSetValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlViewDeviceID.setVisibility(View.GONE);
                llSetDeveiceID.setVisibility(View.VISIBLE);
            }
        });

        btnGetValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlViewDeviceID.setVisibility(View.VISIBLE);
                llSetDeveiceID.setVisibility(View.GONE);
            }
        });
    }


    int a1 = 18;
    int a2 = 241;
    int a3;
    int a4;
    int a5;
    int a6;

    public void callSetDeviceID() {

        String temp = edtDeviceID.getText().toString();
        a3 = Integer.valueOf(String.valueOf(spinDeviceID.getSelectedItemPosition()), 16);
        if (Utils.isStringNotNull(temp)) {
            if (temp.length() == 5) {
                a4 = Integer.valueOf(temp.substring(0, 1), 16);
                a5 = Integer.valueOf(temp.substring(1, 3), 16);
                a6 = Integer.valueOf(temp.substring(3, 5), 16);
            } else {
                Utils.showToastMsg(mContext, "Device Id must be of 5 characters");
            }
        }

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
//            connector.write(br1);
            connector.write(asciiString.getBytes());
        } else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }
    }


    private void callViewDeviceId(int value) {


        int a1 = 18;
        int a2 = 241;
        int a3;
        int a4 = 0;
        int a5 = 0;
        int a6 = 0;

        String temp = edtDeviceID.getText().toString();
        a3 = Integer.valueOf(String.valueOf(value), 16);
        Log.e(TAG, "a3 = "+ String.format("%02x",a3));
        int sum = a1 + a2 + a3 + a4 + a5 + a6;
        String sumHex = String.format("%04x", sum);
        String msb = sumHex.substring(1, 2);
        String lsb = sumHex.substring(2, 4);

        int a7 = Integer.parseInt(lsb, 16);
        int msb2 = (Integer.parseInt(msb) | 50);
        int a8 = Integer.parseInt(String.valueOf(msb2), 16);

        int[] br2 = {0x12, 0xF1, 0x00, 0x00, 0x00, 0x00};
        br2[2]=a3;
        String strCmd=String.format("%02x",br2[0])+String.format("%02x",br2[1])+String.format("%02x",br2[2])+String.format("%02x",br2[3])+String.format("%02x",br2[4])+String.format("%02x",br2[5]);
        Log.e(TAG, "strCmd = "+ strCmd);
        /*int sumCmdString  = 0;
        for(int i = 0; i<strCmd.length(); i++){
            sumCmdString = sumCmdString + strCmd.charAt(i);
        }
        Log.e(TAG, "sumCmdString = "+ Integer.toString(sumCmdString,16).substring(1,3));
        byte[] br1 = {(byte) a1, (byte) a2, (byte) a3, (byte) a4, (byte) a5, (byte) a6, (byte) a7, (byte) a8};*/

        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4) ;
       /* int sumSendString  = 0;
        for(int i = 0; i<asciiString.length(); i++){
            sumSendString = sumSendString + Integer.parseInt(String.format("%04x", (int) asciiString.charAt(i)).substring(2,4));
        }
        Log.e(TAG, "sumSendString = "+ sumSendString);*/
        //asciiString = asciiString +String.valueOf(sumSendString).substring(1,3)+ "\r";

        //asciiString = asciiString +Integer.toString(sumCmdString,16).substring(1,3)+ "\r";
        String strChkSum= CalculateCheckSum.calculateChkSum(br2);
        asciiString = asciiString + strChkSum + "\r";

        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
//            connector.write(br1);
            connector.write(asciiString.getBytes());
        } else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext, MainActivity.class));
        finish();
    }

    // ============================================================================

    /**
     * ???????? ?????????? ??????????
     */
    private boolean isConnected() {
        return (connector != null) && (connector.getState() == DeviceConnector.STATE_CONNECTED);
    }
    // ==========================================================================

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

        msg.append(hexMode ? Utils.toHex1(message) : message);
        if (outgoing) msg.append('\n');

        completReceivedString.append(message);

    }
    // =========================================================================

    void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        getSupportActionBar().setSubtitle(deviceName);
        getSupportActionBar().setTitle("Device ID");
    }

    // ==========================================================================

    /**
     * ?????????? ?????? ?????? ?? bluetooth-??????
     */
    private static class BluetoothResponseHandler extends Handler {
        private WeakReference<DeviceIDActivity> mActivity;

        public BluetoothResponseHandler(DeviceIDActivity activity) {
            mActivity = new WeakReference<DeviceIDActivity>(activity);
        }

        public void setTarget(DeviceIDActivity target) {
            mActivity.clear();
            mActivity = new WeakReference<DeviceIDActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            DeviceIDActivity activity = mActivity.get();
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
                        if (readMessage != null) {
                            //Log.e(TAG, " readMessage = "+ readMessage);
                            activity.appendLog1(readMessage, false, false, activity.needClean);
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



    public void showReceivedData(){

        Log.e(TAG , "showReceivedData" );
        String receiviedString = new String(completReceivedString);
        try {

            Utils.calculateChecksumValue(receiviedString);
            Log.e(TAG, " received length = "+ receiviedString.length());
            while (receiviedString.length() >0){

                int index111250 = receiviedString.indexOf("\u0011ñ");
                String temp = receiviedString.substring(index111250, index111250 +7);

                String lsb = String.format("%04x", (int) temp.charAt(index111250 + 2));
                String sb = String.format("%04x", (int) temp.charAt(index111250 + 3));
                String msb = String.format("%04x", (int) temp.charAt(index111250 + 4));
                String deviceId = String.format("%04x", (int) temp.charAt(index111250 + 5));


                Log.e(TAG, "lsb =" + lsb + " sb = " + sb + " msb = " + msb + " deviceId = " + deviceId);
                String id = lsb.substring(2, 4) + " " + sb.substring(2, 4) + " " + msb.substring(2, 4);

                int device = Integer.parseInt(deviceId);
                Log.e(TAG, "Device ID = "+device);
                switch (device) {
                    case 0:
                        txtLPB_0.setText(id);
                        break;
                    case 1:
                        txtLPB_1.setText(id);
                        break;
                    case 2:
                        txtLPB_2.setText(id);
                        break;
                    case 3:
                        txtLPB_3.setText(id);
                        break;
                    case 4:
                        txtLPB_4.setText(id);
                        break;
                    case 5:
                        txtLPB_5.setText(id);
                        break;
                    case 6:
                        txtLPB_6.setText(id);
                        break;
                    case 7:
                        txtLPB_7.setText(id);
                        break;
                    case 8:
                        txtLPB_8.setText(id);
                        break;
                    case 9:
                        txtLPB_9.setText(id);
                        break;
                    case 10:
                        txtLPB_10.setText(id);
                        break;
                    case 11:
                        txtLPB_11.setText(id);
                        break;
                    case 12:
                        txtLPB_12.setText(id);
                        break;
                    case 13:
                        txtLPB_13.setText(id);
                        break;
                    case 14:
                        txtLPB_14.setText(id);
                        break;
                    case 15:
                        txtLPB_15.setText(id);
                        break;
                    case 16:
                        txtLPB_16.setText(id);
                        break;
                    case 17:
                        txtLPB_17.setText(id);
                        break;
                    case 18:
                        txtLPB_18.setText(id);
                        break;
                    case 19:
                        txtLPB_19.setText(id);
                        break;
                    case 20:
                        txtLPB_20.setText(id);
                        break;
                    case 21:
                        txtLPB_21.setText(id);
                        break;
                    case 22:
                        txtLPB_22.setText(id);
                        break;
                    case 23:
                        txtLPB_23.setText(id);
                        break;
                    case 24:
                        txtLPB_24.setText(id);
                        break;
                    case 25:
                        txtLPB_25.setText(id);
                        break;
                    case 26:
                        txtLPB_26.setText(id);
                        break;
                    case 27:
                        txtLPB_27.setText(id);
                        break;
                    case 28:
                        txtLPB_28.setText(id);
                        break;
                    case 29:
                        txtLPB_29.setText(id);
                        break;
                    case 30:
                        txtLPB_30.setText(id);
                        break;
                    case 31:
                        txtLPB_31.setText(id);
                        break;
                    case 32:
                        txtCOP_1.setText(id);
                        break;
                    case 33:
                        txtCOP_2.setText(id);
                        break;
                }
                temp = "";
                receiviedString = receiviedString.substring(index111250 +7, receiviedString.length());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showReceivedDataNew(){
        Log.e(TAG, "ShowReceivedData");

        String receivedString = new String(completReceivedString);
        try {
            Log.e(TAG, "receivedString length = "+ receivedString);

            if(Utils.isStringNotNull(receivedString)) {
                while (receivedString.length() >= 14) {

                    int index0D = receivedString.indexOf("\r");
                    //Log.e(TAG, "index0D = " + index0D);
                    String temp = receivedString.substring(0, index0D);
                    Log.e(TAG, "temp = " + temp);
                    if (temp.startsWith("11f1")) {
                        String sum = Utils.calculateChecksumValueNew(temp);
                       // Log.e(TAG, "" + sum.substring(2, 4) + " -- " + temp.substring(temp.length() - 2, temp.length()) + " temp = " + temp);

                        if (sum.substring(2, 4).equalsIgnoreCase(temp.substring(temp.length() - 2, temp.length()))) {
                            String locationAddress = temp.substring(6, 8);

                            int data = Integer.parseInt(temp.substring(8, 10), 16);

                            //Log.e(TAG, "locationAddress = " + locationAddress + " data = " + data);
//                        Utils.showToastMsg(getActivity(), " Data = "+data +" char =  "+ temp.charAt(index - 1));
                            String lsb = temp.substring(8, 10);
                            String sb = temp.substring(6, 8);
                            String msb = temp.substring(4, 6);
                            String id = lsb + sb + msb;
                            int device = Integer.parseInt(temp.substring(10, 12));
                            Log.e(TAG, "Device  = " + device + " Id :" + id);

                            switch (device) {
                                case 0:
                                    txtLPB_0.setText(id);
                                    break;
                                case 1:
                                    txtLPB_1.setText(id);
                                    break;
                                case 2:
                                    txtLPB_2.setText(id);
                                    break;
                                case 3:
                                    txtLPB_3.setText(id);
                                    break;
                                case 4:
                                    txtLPB_4.setText(id);
                                    break;
                                case 5:
                                    txtLPB_5.setText(id);
                                    break;
                                case 6:
                                    txtLPB_6.setText(id);
                                    break;
                                case 7:
                                    txtLPB_7.setText(id);
                                    break;
                                case 8:
                                    txtLPB_8.setText(id);
                                    break;
                                case 9:
                                    txtLPB_9.setText(id);
                                    break;
                                case 10:
                                    txtLPB_10.setText(id);
                                    break;
                                case 11:
                                    txtLPB_11.setText(id);
                                    break;
                                case 12:
                                    txtLPB_12.setText(id);
                                    break;
                                case 13:
                                    txtLPB_13.setText(id);
                                    break;
                                case 14:
                                    txtLPB_14.setText(id);
                                    break;
                                case 15:
                                    txtLPB_15.setText(id);
                                    break;
                                case 16:
                                    txtLPB_16.setText(id);
                                    break;
                                case 17:
                                    txtLPB_17.setText(id);
                                    break;
                                case 18:
                                    txtLPB_18.setText(id);
                                    break;
                                case 19:
                                    txtLPB_19.setText(id);
                                    break;
                                case 20:
                                    txtLPB_20.setText(id);
                                    break;
                                case 21:
                                    txtLPB_21.setText(id);
                                    break;
                                case 22:
                                    txtLPB_22.setText(id);
                                    break;
                                case 23:
                                    txtLPB_23.setText(id);
                                    break;
                                case 24:
                                    txtLPB_24.setText(id);
                                    break;
                                case 25:
                                    txtLPB_25.setText(id);
                                    break;
                                case 26:
                                    txtLPB_26.setText(id);
                                    break;
                                case 27:
                                    txtLPB_27.setText(id);
                                    break;
                                case 28:
                                    txtLPB_28.setText(id);
                                    break;
                                case 29:
                                    txtLPB_29.setText(id);
                                    break;
                                case 30:
                                    txtLPB_30.setText(id);
                                    break;
                                case 31:
                                    txtLPB_31.setText(id);
                                    break;
                                case 32:
                                    txtCOP_1.setText(id);
                                    break;
                                case 33:
                                    txtCOP_2.setText(id);
                                    break;
                            }
                            temp = "";
                        }
                        receivedString = receivedString.substring(index0D + 1, receivedString.length());
                        //Log.e(TAG, "Sum ===== " + sum);
                    } else {
                        receivedString = receivedString.substring(index0D + 1, receivedString.length());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
