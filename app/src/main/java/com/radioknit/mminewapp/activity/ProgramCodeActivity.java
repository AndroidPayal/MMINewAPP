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

public class ProgramCodeActivity extends BaseActivity {

    private static final String TAG = "ProgramCodeActivity";
    ArrayList<String> arrCommandValueList;
    ArrayList<String> arrBreakPulseList;
    ArrayList<String> arrNoOfFloorValueList;
    ArrayList<String> arrControlBit;
    ArrayList<String> arrDoorOpenTime;
    ArrayList<String> arrClockDivide;
    ArrayList<String> arrFireFloor;
    private Spinner spinStopDelay;
    private ArrayAdapter<String> adapter;
    private Spinner spinTransitDelay;
    private Spinner spinBreakHipulse;
    private Spinner spinNoOfFloors;
    private Spinner spinDoorOpenTime;
    private Spinner spinDoorCloseTime;
    private Spinner spinDoorKeepOpenTime;
    private Spinner spinClockDivide;
    private Spinner spinControlBit;
    private Spinner spinFireFloor;
    private Spinner spinHomeFloor;
    private Spinner spinCompulsaryStop;
    private Spinner spinParkingFloor;
    private TextView txtStopDelay;
    private TextView txtTransitDelay;
    private TextView txtBreakHiPulse;
    private TextView txtNoOfFloors;
    private TextView txtDoorOpenTime;
    private TextView txtDoorCloseTime;
    private TextView txtDoorDoorKeepOpenTime;
    private TextView txtClockDivide;
    private TextView txtControlBit;
    private TextView txtFireFloor;
    private TextView txtHomeFloor;
    private TextView txtCompulsaryStop;
    private TextView txtParkingFloor;
    private ArrayAdapter<String> adapterNoOfFloor;
    private ArrayAdapter<String> adapterBreakPulse;
    private ArrayAdapter<String> adapterControlBit;
    private ArrayAdapter<String> adapterDoorOpenTime;
    private ArrayAdapter<String> adapterClockDivide;
    private ArrayAdapter<String> adapterFireFloor;
    private Button btnSetProgramCode;
    private Button btnReadPlc;
    private Context mContext;

    private static String strTemp = "";
    // =============

    private OutputStream outputStream;
    private static final String DEVICE_NAME = "DEVICE_NAME";
    private static final String LOG = "LOG";

    private static final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss.SSS");

    private static String MSG_NOT_CONNECTED;
    private static String MSG_CONNECTING;
    private static String MSG_CONNECTED;

    private static DeviceConnector connector;
    private static BluetoothResponseHandler mHandler;
    DeviceListActivity deviceListActivity;

    private boolean hexMode, needClean;
    private boolean show_timings, show_direction;
    private String command_ending;
    private String deviceName;
    private int counter = 1;
    private ProgressDialog pd;
    private StringBuffer completReceivedString;
    private static boolean receiveFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "onCreate()");
        PreferenceManager.setDefaultValues(this, R.xml.settings_activity, false);

        if (mHandler == null) mHandler = new BluetoothResponseHandler(this);
        else mHandler.setTarget(this);

        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);

        setContentView(R.layout.activity_program_code);

        completReceivedString = new StringBuffer();

        generateId();
        createObj();
        registerEvent();


        if (isConnected() && (savedInstanceState != null)) {
            setDeviceName(savedInstanceState.getString(DEVICE_NAME));
        } else {
            getSupportActionBar().setSubtitle(MSG_NOT_CONNECTED);
        }

        if(savedInstanceState != null) {
            String address = savedInstanceState.getString(DEVICE_NAME);
            BluetoothDevice device = btAdapter.getRemoteDevice(address);
            if (super.isAdapterReady() && (connector == null)) setupConnector(device);
        }

    }


    void delaySet(){
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void delayRead(){
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerEvent() {
        final Handler ha = new Handler();
        btnSetProgramCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    callStopDelay();
                    delaySet();
                    callTransitDelay();
                    delaySet();
                    callBreakHiPulseDelay();
                    delaySet();
                    callNoOfFloors();
                    delaySet();
                    callDoorOpenTime();
                    delaySet();
                    callDoorCloseTime();
                    delaySet();
                    callDoorKeepOpenTime();
                    delaySet();
                    callClockDivivde();
                    delaySet();
                    callControlBit();
                    delaySet();
                    callFireFloor();
                    delaySet();
                    callHomeFloor();
                    delaySet();
                    callCompulsaryStop();
                    delaySet();
                    callParkingFloor();
                    delaySet();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
                }
            }
        });




        btnReadPlc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completReceivedString.setLength(0);
                counter = 1;
                if (isConnected()) {
                    pd = ProgressDialog.show(mContext, "", "Please wait", true);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
                }
                boolean b = ha.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(counter == 1){
                            callReadPLC(130);
                            delayRead();
                            counter++;
                        }else if(counter ==  2 ){
                                callReadPLC(131);
                                delayRead();
                                counter++;
                        }else if(counter == 3){
                                callReadPLC(132);
                                delayRead();
                                counter++;
                        }else if(counter == 4){
                                callReadPLC(133);
                                delayRead();
                                counter++;
                        }else if(counter == 5){
                                callReadPLC(134);
                            delayRead();
                                counter++;
                        }else if(counter == 6){
                                callReadPLC(135);
                            delayRead();
                                counter++;
                        }else if(counter == 7){
                                callReadPLC(136);
                            delayRead();
                                counter++;
                        }else if(counter == 8){
                                callReadPLC(137);
                            delayRead();
                                counter++;
                        }else if(counter == 9){
                                callReadPLC(138);
                            delayRead();
                                counter++;
                        }else if(counter == 10){
                                callReadPLC(139);
                            delayRead();
                                counter++;
                        }else if(counter == 11){
                                callReadPLC(140);
                            delayRead();
                                counter++;
                        }else if(counter == 12){
                                callReadPLC(141);
                            delayRead();
                                counter++;
                        }else if(counter == 13){
                                callReadPLC(142);
                            delayRead();
                                counter++;
                        }else if(counter == 14){
                            if (isConnected()) {
                                pd.dismiss();
                            }
                                showReceivedDataNew();
                                counter++;
                        }
                        ha.postDelayed(this, 500);
                    }
                }, 500);
            }
        });
    }

    private void createObj() {
        mContext = ProgramCodeActivity.this;

        arrCommandValueList = new ArrayList<String>();

        arrBreakPulseList=new ArrayList<String>();
        arrCommandValueList = new ArrayList<String>();
        arrNoOfFloorValueList = new ArrayList<String>();
        arrControlBit = new ArrayList<String>();
        arrDoorOpenTime = new ArrayList<String>();
        arrFireFloor = new ArrayList<String>();
        arrClockDivide = new ArrayList<String>();
        for (int i = 1; i <= 255; i++) {
            arrCommandValueList.add(String.valueOf(i));
        }

        for (int i = 0; i <= 255; i++) {
            arrBreakPulseList.add(String.valueOf(i));
        }

        for (int i = 1; i <= 31; i++) {
            arrNoOfFloorValueList.add(String.valueOf(i));
        }

        for (int i = 0; i <= 255; i++) {
            arrControlBit.add(String.valueOf(i));
        }

        for (int i = 1; i < 16; i++) {
            arrDoorOpenTime.add(String.valueOf(i));
        }

        for (int i = 0; i < 8; i++) {
            arrClockDivide.add(String.valueOf(i));
        }

        for (int i = 0; i <= 31; i++) {
            arrFireFloor.add(String.valueOf(i));
        }
        arrFireFloor.add(String.valueOf(255));

        adapter = new ArrayAdapter<String>(mContext, R.layout.list_item, arrCommandValueList);
        adapterBreakPulse= new ArrayAdapter<String>(mContext, R.layout.list_item, arrBreakPulseList);
        adapterNoOfFloor = new ArrayAdapter<String>(mContext, R.layout.list_item, arrNoOfFloorValueList);
        adapterControlBit = new ArrayAdapter<String>(mContext, R.layout.list_item, arrControlBit);
        adapterDoorOpenTime = new ArrayAdapter<String>(mContext, R.layout.list_item, arrDoorOpenTime);
        adapterClockDivide = new ArrayAdapter<String>(mContext, R.layout.list_item, arrClockDivide);
        adapterFireFloor = new ArrayAdapter<String>(mContext, R.layout.list_item, arrFireFloor);

        spinStopDelay.setAdapter(adapter);
        spinTransitDelay.setAdapter(adapter);
        spinBreakHipulse.setAdapter(adapter);
        spinNoOfFloors.setAdapter(adapterNoOfFloor);
        spinFireFloor.setAdapter(adapterFireFloor);
        spinHomeFloor.setAdapter(adapterFireFloor);
        spinCompulsaryStop.setAdapter(adapterFireFloor);
        spinParkingFloor.setAdapter(adapterFireFloor);
        spinControlBit.setAdapter(adapterControlBit);
        spinDoorOpenTime.setAdapter(adapterDoorOpenTime);
        spinDoorCloseTime.setAdapter(adapterDoorOpenTime);
        spinDoorKeepOpenTime.setAdapter(adapterDoorOpenTime);
        spinClockDivide.setAdapter(adapterDoorOpenTime);


    }

    private void generateId() {
        btnSetProgramCode = (Button) findViewById(R.id.btnSetProgramCodes);
        btnReadPlc = (Button)findViewById(R.id.btnProgram_code_read_plc);
        spinStopDelay = (Spinner) findViewById(R.id.spinStopDelay);
        spinTransitDelay = (Spinner) findViewById(R.id.spinTransitDelay);
        spinBreakHipulse = (Spinner) findViewById(R.id.spinBreakHiPulse);
        spinNoOfFloors = (Spinner) findViewById(R.id.spinNoOfFloor);
        spinDoorOpenTime = (Spinner) findViewById(R.id.spinDoorOpenTime);
        spinDoorCloseTime = (Spinner) findViewById(R.id.spinDoorCloseTime);
        spinDoorKeepOpenTime = (Spinner) findViewById(R.id.spinDoorKeepOpenTime);
        spinClockDivide = (Spinner) findViewById(R.id.spinClockDivide);
        spinControlBit = (Spinner) findViewById(R.id.spinControlBit);
        spinFireFloor = (Spinner) findViewById(R.id.spinFireFloor);
        spinHomeFloor = (Spinner) findViewById(R.id.spinHomeFloor);
        spinCompulsaryStop = (Spinner) findViewById(R.id.spinCompulsaryStop);
        spinParkingFloor = (Spinner) findViewById(R.id.spinParkingFloor);

        txtStopDelay = (TextView) findViewById(R.id.tvStopDelayDefalut);
        txtTransitDelay = (TextView) findViewById(R.id.tvTransitDelay);
        txtBreakHiPulse = (TextView) findViewById(R.id.tvBreakHiPulse);
        txtNoOfFloors = (TextView) findViewById(R.id.tvNoOfFloor);
        txtDoorOpenTime = (TextView) findViewById(R.id.tvDoorOpenTime);
        txtDoorCloseTime = (TextView) findViewById(R.id.tvDoorCloseTime);
        txtDoorDoorKeepOpenTime = (TextView) findViewById(R.id.tvDoorKeepOpenTime);
        txtClockDivide = (TextView) findViewById(R.id.tvClockDivide);
        txtControlBit = (TextView) findViewById(R.id.tvControlBit);
        txtFireFloor = (TextView) findViewById(R.id.tvFireFloor);
        txtHomeFloor = (TextView) findViewById(R.id.tvHomeFloor);
        txtCompulsaryStop = (TextView) findViewById(R.id.tvCompulsaryStop);
        txtParkingFloor = (TextView) findViewById(R.id.tvParkingFloor);
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

            case android.R.id.home :
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
                return true;

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

        this.show_timings = Utils.getBooleanPrefence(this, getString(R.string.pref_log_timing));
        this.show_direction = Utils.getBooleanPrefence(this, getString(R.string.pref_log_direction));
        this.needClean = Utils.getBooleanPrefence(this, getString(R.string.pref_need_clean));
    }
    // ============================================================================


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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext, MainActivity.class));
        finish();
    }

    //============================
    /**
     * ????????? ?????????? ? ???????????
     */
    private void setupConnector(BluetoothDevice connectedDevice) {
        Log.e(TAG, "setupConnector()");
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
        getSupportActionBar().setTitle("Program Code");
    }


    // ==========================================================================

    /**
     * ?????????? ?????? ?????? ?? bluetooth-??????
     */
    private static class BluetoothResponseHandler extends Handler {
        private WeakReference<ProgramCodeActivity> mActivity;

        public BluetoothResponseHandler(ProgramCodeActivity activity) {
            mActivity = new WeakReference<ProgramCodeActivity>(activity);
        }

        public void setTarget(ProgramCodeActivity target) {
            mActivity.clear();
            mActivity = new WeakReference<ProgramCodeActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            ProgramCodeActivity activity = mActivity.get();
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
                            Log.e(TAG, " readMessage = "+ readMessage);
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

    public void callStopDelay() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 130;
        int a5 = Integer.parseInt(spinStopDelay.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
//            connector.write(br1);
            connector.write(asciiString.getBytes());
        }
        else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }

    }

    public void callTransitDelay() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 131;
        int a5 = Integer.parseInt(spinTransitDelay.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
//            connector.write(br1);
            connector.write(asciiString.getBytes());
        }
        else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }

    }

    public void callBreakHiPulseDelay() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 132;
        int a5 = Integer.parseInt(spinBreakHipulse.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
//            connector.write(br1);
            connector.write(asciiString.getBytes());
        }
        else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }

    }

    public void callNoOfFloors() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 133;
        int a5 = Integer.parseInt(spinNoOfFloors.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
//            connector.write(br1);
            connector.write(asciiString.getBytes());
        }
        else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }
    }

    public void callDoorOpenTime() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 134;
        int a5 = Integer.parseInt(spinDoorOpenTime.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);
        if (isConnected()) {
//            connector.write(br1);
            connector.write(asciiString.getBytes());
        }
        else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }
    }

    public void callDoorCloseTime() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 135;
        int a5 = Integer.parseInt(spinDoorCloseTime.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
//            connector.write(br1);
            connector.write(asciiString.getBytes());
        }
        else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }
    }

    public void callDoorKeepOpenTime() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 136;
        int a5 = Integer.parseInt(spinDoorKeepOpenTime.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);
        if (isConnected()) {
//            connector.write(br1);
            connector.write(asciiString.getBytes());
        }
        else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }

    }

    public void callClockDivivde() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 137;
        int a5 = Integer.parseInt(spinClockDivide.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
//            connector.write(br1);
            connector.write(asciiString.getBytes());
        }
        else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }
    }

    public void callControlBit() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 138;
        int a5 = Integer.parseInt(spinControlBit.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
//            connector.write(br1);
            connector.write(asciiString.getBytes());
        }
        else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }
    }

    public void callFireFloor() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 139;
        int a5 = Integer.parseInt(spinFireFloor.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
//            connector.write(br1);
            connector.write(asciiString.getBytes());
        }
        else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }

    }

    public void callHomeFloor() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 140;
        int a5 = Integer.parseInt(spinHomeFloor.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
//            connector.write(br1);
            connector.write(asciiString.getBytes());
        }
        else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }
    }

    public void callCompulsaryStop() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 141;
        int a5 = Integer.parseInt(spinCompulsaryStop.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
//            connector.write(br1);
            connector.write(asciiString.getBytes());
        }
        else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }
    }

    public void callParkingFloor() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 142;
        int a5 = Integer.parseInt(spinParkingFloor.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
//            connector.write(br1);
            connector.write(asciiString.getBytes());
        }
        else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }
    }

    public void callReadPLC(int locationAddress) {

        int a1 = 18;
        int a2 = 17;
        int a3 = 80;
        int a4 = locationAddress;
        int a5 = 00;
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);

/*        int sum = a1 + a2 + a3 + a4 + a5;
        String sumHex = String.format("%04x", sum);
        String hex = Integer.toHexString(locationAddress);
        String s1 = Integer.toHexString(locationAddress);
        int s2 = Integer.parseInt(s1, 16);

        String msb = sumHex.substring(1, 2);
        String lsb = sumHex.substring(2, 4);

        int a7 = Integer.parseInt(lsb, 16);
        int msb2 = (Integer.parseInt(msb) | 50);
        int a8 = Integer.parseInt(String.valueOf(msb2),16);

        byte[] br2 = {(byte) a1, (byte) a2, (byte) a3, (byte) locationAddress, (byte) a5, (byte) a6,(byte) '\r'};
        byte[] br1 = {(byte) a1, (byte) a2, (byte) a3, (byte) locationAddress, (byte) a5, (byte) a6, (byte) a7, (byte) a8};*/

        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4) ;
        /*int sumSendString  = 0;
        for(int i = 0; i<asciiString.length(); i++){
            sumSendString = sumSendString + Integer.parseInt(String.format("%04x", (int) asciiString.charAt(i)).substring(2,4));
        }
        asciiString = asciiString +String.valueOf(sumSendString).substring(1,3)+ "\r";*/

        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);
        if(isConnected()) {
            connector.write(asciiString.getBytes());
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
                    Log.e(TAG, "index0D = " + index0D);
                    String temp = receivedString.substring(0, index0D);
                    Log.e(TAG, "temp = " + temp);
                    if (temp.startsWith("111250")) {
                        String sum = Utils.calculateChecksumValueNew(temp);
                        Log.e(TAG, "" + sum.substring(2, 4) + " -- " + temp.substring(temp.length() - 2, temp.length()) + " temp = " + temp);

                        if (sum.substring(2, 4).equalsIgnoreCase(temp.substring(temp.length() - 2, temp.length()))) {
                            String locationAddress = temp.substring(6, 8);

                            int data = Integer.parseInt(temp.substring(8, 10), 16);

                            Log.e(TAG, "locationAddress = " + locationAddress + " data = " + data);
                            if (locationAddress.equalsIgnoreCase("82")) {
                                txtStopDelay.setText(data + " ");
                            } else if (locationAddress.equalsIgnoreCase("83")) {
                                txtTransitDelay.setText(data + " ");
                            } else if (locationAddress.equalsIgnoreCase("84")) {
                                txtBreakHiPulse.setText(data + " ");
                            } else if (locationAddress.equalsIgnoreCase("85")) {
                                txtNoOfFloors.setText(data + " ");
                            } else if (locationAddress.equalsIgnoreCase("86")) {
                                txtDoorOpenTime.setText(data + " ");
                            } else if (locationAddress.equalsIgnoreCase("87")) {
                                txtDoorCloseTime.setText(data + " ");
                            } else if (locationAddress.equalsIgnoreCase("88")) {
                                txtDoorDoorKeepOpenTime.setText(data + " ");
                            } else if (locationAddress.equalsIgnoreCase("89")) {
                                txtClockDivide.setText(data + " ");
                            } else if (locationAddress.equalsIgnoreCase("8A")) {
                                txtControlBit.setText(data + " ");
                            } else if (locationAddress.equalsIgnoreCase("8B")) {
                                txtFireFloor.setText(data + " ");
                                TempSharedPreference.setKeyFiremanFloor(mContext, "" + data);
                            } else if (locationAddress.equalsIgnoreCase("8C")) {
                                txtHomeFloor.setText(data + " ");
                                TempSharedPreference.setKeyHomeFloor(mContext, "" + data);
                            } else if (locationAddress.equalsIgnoreCase("8D")) {
                                txtCompulsaryStop.setText(data + " ");
                                TempSharedPreference.setKeyCompulsoryStop(mContext, "" + data);
                            } else if (locationAddress.equalsIgnoreCase("8E")) {
                                txtParkingFloor.setText(data + " ");
                                TempSharedPreference.setKeyParkingFloor(mContext, "" + data);
                            }
                            temp = "";
                        }
                        receivedString = receivedString.substring(index0D + 1, receivedString.length());
                        Log.e(TAG, "Sum ===== " + sum);
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