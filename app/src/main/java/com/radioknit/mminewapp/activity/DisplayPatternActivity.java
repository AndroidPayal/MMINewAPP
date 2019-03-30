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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.Arrays;

public class DisplayPatternActivity extends BaseActivity {

    private static final String TAG = "DisplayPatternActivity";
    ArrayList<String> arrCommandValueList;
    private Spinner spinSelectFloor;
    private Spinner spinDisplayPattern;
    private ArrayAdapter<String> adapter;
    private Button btnSetDisplayPattern;
    private Button btnViewDisplayPattern;
    private LinearLayout llViewDisplayPattern;
    private int counter = 0;
    private RelativeLayout rlViewDisplayPatternData;
    private LinearLayout llSetDispplayPatternData;
    private Button btnSetValues;
    private Button btnGetValues;
    private ArrayList<String> arrPattern;
    private Context mContext;
    private BluetoothAdapter bluetoothAdapter;

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

    private ProgressDialog pd;
    private StringBuffer completReceivedString;
    private static String strTemp = "";
    private static boolean receiveFlag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.settings_activity, false);

        if (mHandler == null) mHandler = new BluetoothResponseHandler(this);
        else mHandler.setTarget(this);

        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);

        setContentView(R.layout.activity_display_pattern);

        completReceivedString = new StringBuffer();

        generateId();
        createObj();
        registerEvent();

    }

    private void generateId() {
        btnSetDisplayPattern = (Button) findViewById(R.id.btnSetDisplayPattern);
        spinSelectFloor = (Spinner) findViewById(R.id.spin_fragdisplay_pattern_floor_no);
        spinDisplayPattern = (Spinner) findViewById(R.id.spin_fragdiaplay_pattern_display);
        btnViewDisplayPattern = (Button)findViewById(R.id.btnViewDisplayPattern);
        llViewDisplayPattern = (LinearLayout)findViewById(R.id.llDisplayPattern);
        llSetDispplayPatternData = (LinearLayout)findViewById(R.id.llSetDisplayPatternValue);
        rlViewDisplayPatternData = (RelativeLayout)findViewById(R.id.rlViewDisplayPatternValue);
        btnSetValues = (Button) findViewById(R.id.btnDisplayPatternSetValues);
        btnGetValues = (Button)findViewById(R.id.btnDisplayPatternViewValues);

    }

    private void createObj() {
        mContext = DisplayPatternActivity.this;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        arrCommandValueList = new ArrayList<String>();

        for (int i = 0; i <= 31; i++) {
            arrCommandValueList.add(String.valueOf(i));
        }

        adapter = new ArrayAdapter<String>(mContext, R.layout.list_item, arrCommandValueList);

        spinSelectFloor.setAdapter(adapter);

        String[] tempPattern = getResources().getStringArray(R.array.arr_display_pattern);
        arrPattern = new ArrayList<>(Arrays.asList(tempPattern));
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
        btnSetDisplayPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callStopDelay();
            }
        });

        btnSetValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSetDispplayPatternData.setVisibility(View.VISIBLE);
                rlViewDisplayPatternData.setVisibility(View.GONE);
            }
        });

        btnGetValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSetDispplayPatternData.setVisibility(View.GONE);
                rlViewDisplayPatternData.setVisibility(View.VISIBLE);
            }
        });

        btnViewDisplayPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llViewDisplayPattern.removeAllViews();
                counter = 0;
                completReceivedString.setLength(0);
                if (isConnected()) {
                    pd = ProgressDialog.show(mContext, "", "Please wait", true);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
                }
                boolean b = ha.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //call function

                        if(counter == 0){
                            callViewDisplayPattern(counter);
                            delay();
                            counter++;
                        }else if(counter == 1){
                                callViewDisplayPattern(counter);
                                delay();
                                counter++;
                        }else if(counter ==  2 ){
                                callViewDisplayPattern(counter);
                                delay();
                                counter++;
                        }else if(counter == 3){
                                callViewDisplayPattern(counter);
                                delay();
                                counter++;
                        }else if(counter == 4){
                                callViewDisplayPattern(counter);
                                delay();
                                counter++;
                        }else if(counter == 5){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 6){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 7){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 8){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 9){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 10){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 11){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 12){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 13){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 14){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 15){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 16){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 17){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 18){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 19){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 20){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 21){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 22){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 23){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 24){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 25){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 26){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 27){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 28){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 29){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 30){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 31){
                                callViewDisplayPattern(counter);
                            delay();
                                counter++;
                        }else if(counter == 32){
                                counter++;
                                Log.e(TAG, "counter = "+counter);
                            if(isConnected()){
                                pd.dismiss();
                            }
                            delay();
                                showReceivedDataNew();

                        }
                        ha.postDelayed(this, 500);
                    }
                }, 500);
            }

        });
    }


    int a1 = 18;
    int a2 = 17;
    int a3 = 112;
    int a4;
    int a5;
    int a6 = 00;

    public void callStopDelay() {

        a4 = 192 + Integer.parseInt(spinSelectFloor.getSelectedItem().toString());
        if (spinDisplayPattern.getSelectedItemPosition() == 55) {
            a5 = 55;
        } else if (spinDisplayPattern.getSelectedItemPosition() == 56) {
            a5 = 251;
        } else if (spinDisplayPattern.getSelectedItemPosition() == 57) {
            a5 = 252;
        } else if (spinDisplayPattern.getSelectedItemPosition() == 58) {
            a5 = 253;
        } else if (spinDisplayPattern.getSelectedItemPosition() == 59) {
            a5 = 254;
        } else if (spinDisplayPattern.getSelectedItemPosition() == 60) {
            a5 = 255;
        } else {
            a5 = spinDisplayPattern.getSelectedItemPosition();
        }

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
            connector.write(asciiString.getBytes());
        }
        else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }
    }

    private void callViewDisplayPattern(int floorNo) {
        int a1 = 18;
        int a2 = 17;
        int a3 = 80;
        int a4 = 192+ floorNo;
        int a5 = 00;
        int a6 = 00;


        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};

        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);

        /*int sum = a1 + a2 + a3 + a4 + a5 + a6;
        String sumHex = String.format("%04x", sum);

        String msb = sumHex.substring(1, 2);
        String lsb = sumHex.substring(2, 4);

        int a7 = Integer.parseInt(lsb, 16);
        int msb2 = (Integer.parseInt(msb) | 50);
        int a8 = Integer.parseInt(String.valueOf(msb2), 16);

        byte[] br2 = {(byte) a1, (byte) a2, (byte) a3, (byte) a4, (byte) a5, (byte) a6,(byte) '\r'};
        byte[] br1 = {(byte) a1, (byte) a2, (byte) a3, (byte) a4, (byte) a5, (byte) a6, (byte) a7, (byte) a8};*/

        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4) ;
       /* int sumSendString  = 0;
        for(int i = 0; i<asciiString.length(); i++){
            sumSendString = sumSendString + Integer.parseInt(String.format("%04x", (int) asciiString.charAt(i)).substring(2,4));
        }*/
        //asciiString = asciiString +String.valueOf(sumSendString).substring(1,3)+ "\r";
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
//            connector.write(br1);
            connector.write(asciiString.getBytes());
        }
        /*else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }*/
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

        msg.append(hexMode ? Utils.toHex1(message) : message);
        if (outgoing) msg.append('\n');

        completReceivedString.append(message);

    }
    // =========================================================================

    void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        getSupportActionBar().setSubtitle(deviceName);
        getSupportActionBar().setTitle("Display Pattern");
    }


    // ==========================================================================

    /**
     * ?????????? ?????? ?????? ?? bluetooth-??????
     */
    private static class BluetoothResponseHandler extends Handler {
        private WeakReference<DisplayPatternActivity> mActivity;

        public BluetoothResponseHandler(DisplayPatternActivity activity) {
            mActivity = new WeakReference<DisplayPatternActivity>(activity);
        }

        public void setTarget(DisplayPatternActivity target) {
            mActivity.clear();
            mActivity = new WeakReference<DisplayPatternActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            DisplayPatternActivity activity = mActivity.get();
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


    public void showReceivedDataNew(){
        Log.e(TAG, "ShowReceivedData");

        String receivedString = new String(completReceivedString);
        try {
            Log.e(TAG, "receivedString lenght = "+ receivedString );

            if(Utils.isStringNotNull(receivedString)) {
                while (receivedString.length() >= 14) {
                    int index0D = receivedString.indexOf("\r");

                    Log.e(TAG, "indexOD = " + index0D);

                    String temp = receivedString.substring(index0D - 14, index0D);
                    Log.e(TAG, "temp = " + temp);
                    if (temp.startsWith("111250")) {
                        String sum = Utils.calculateChecksumValueNew(temp);
                        Log.e(TAG, "" + sum.substring(2, 4) + " -- " + temp.substring(temp.length() - 2, temp.length()) + " temp = " + temp);

                        if (sum.substring(2, 4).equalsIgnoreCase(temp.substring(temp.length() - 2, temp.length()))) {
                            String locationAddress = temp.substring(6, 8);

                            int data = Integer.parseInt(temp.substring(8, 10), 16);

                            Log.e(TAG, "locationAddress = " + locationAddress + " data = " + data);
//                        Utils.showToastMsg(getActivity(), " Data = "+data +" char =  "+ temp.charAt(index - 1));
                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View view = inflater.inflate(R.layout.display_pattern_item, null);
                            TextView tvFloorNo = (TextView) view.findViewById(R.id.tvfloor_blocking_FloorNumber);
                            TextView tvFloorData = (TextView) view.findViewById(R.id.tvfloor_blocking_Data);

                            if (locationAddress.equalsIgnoreCase("C0")) {

                                tvFloorNo.setText("" + 0);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("C1")) {

                                tvFloorNo.setText("" + 1);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("C2")) {

                                tvFloorNo.setText("" + 2);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("C3")) {

                                tvFloorNo.setText("" + 3);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("C4")) {

                                tvFloorNo.setText("" + 4);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("C5")) {

                                tvFloorNo.setText("" + 5);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("C6")) {

                                tvFloorNo.setText("" + 6);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("C7")) {

                                tvFloorNo.setText("" + 7);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("C8")) {

                                tvFloorNo.setText("" + 8);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("C9")) {

                                tvFloorNo.setText("" + 9);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("CA")) {

                                tvFloorNo.setText("" + 10);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("CB")) {

                                tvFloorNo.setText("" + 11);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("CC")) {

                                tvFloorNo.setText("" + 12);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("CD")) {

                                tvFloorNo.setText("" + 13);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("CE")) {

                                tvFloorNo.setText("" + 14);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("CF")) {

                                tvFloorNo.setText("" + 15);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("D0")) {

                                tvFloorNo.setText("" + 16);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("D1")) {

                                tvFloorNo.setText("" + 17);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("D2")) {

                                tvFloorNo.setText("" + 18);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("D3")) {

                                tvFloorNo.setText("" + 19);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("D4")) {

                                tvFloorNo.setText("" + 20);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("D5")) {

                                tvFloorNo.setText("" + 21);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("D6")) {

                                tvFloorNo.setText("" + 22);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("D7")) {

                                tvFloorNo.setText("" + 23);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("D8")) {

                                tvFloorNo.setText("" + 24);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("D9")) {

                                tvFloorNo.setText("" + 25);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("DA")) {

                                tvFloorNo.setText("" + 26);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("DB")) {

                                tvFloorNo.setText("" + 27);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("DC")) {

                                tvFloorNo.setText("" + 28);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("DD")) {

                                tvFloorNo.setText("" + 29);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("DE")) {

                                tvFloorNo.setText("" + 30);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("DF")) {

                                tvFloorNo.setText("" + 31);
                                tvFloorData.setText("" + arrPattern.get(data));
                                llViewDisplayPattern.addView(view);
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
