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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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

public class FloorCallBlockingActivity extends BaseActivity {

    private static final String TAG = "FloorCallBlocking";
    private String connectedDeviceName;
    ArrayList<String> arrCommandValueList;
    private Spinner spinSelectFloor;
    private ArrayAdapter<String> adapter;
    private Button btnSetFloorCallBlocking;
    private RadioGroup rdoGropFloorCallBlocking;
    private Button btnViewFloorCallBlocking;
    int counter = 0;
    private LinearLayout llFloorBlockingData;
    private LinearLayout llSetFloorCallBlockignData;
    private RelativeLayout rlViewFloorCallBlockingData;
    private Button btnSetValues;
    private Button btnViewValues;
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
    private static BluetoothResponseHandler mHandler;
    DeviceListActivity deviceListActivity;

    private boolean hexMode, needClean;
    private boolean show_timings, show_direction;
    private String command_ending;
    private String deviceName;

    private ProgressDialog pd;
    private StringBuffer completReceivedString;

    private static boolean receiveFlag = false;
    private static String strTemp = "";

    CheckBox checkBoxHu,checkBoxHd,checkBoxCc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.settings_activity, false);

        if (mHandler == null) mHandler = new BluetoothResponseHandler(this);
        else mHandler.setTarget(this);

        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);
        setContentView(R.layout.activity_floor_call_blocking);
        completReceivedString = new StringBuffer();
        generateId();
        createObj();
        registerEvent();

        if (isConnected() && (savedInstanceState != null)) {
            setDeviceName(savedInstanceState.getString(DEVICE_NAME));
        } else getSupportActionBar().setSubtitle(MSG_NOT_CONNECTED);

    }


    private void generateId() {
        btnSetFloorCallBlocking = (Button) findViewById(R.id.btnSetFloorCallBlocking);
        btnViewFloorCallBlocking = (Button)findViewById(R.id.btnViewFloorCallBlocking);
        spinSelectFloor = (Spinner) findViewById(R.id.spinSeclectFloorNumber);
        rdoGropFloorCallBlocking = (RadioGroup)findViewById(R.id.rdogpFloorCallBlocking);
        llFloorBlockingData = (LinearLayout)findViewById(R.id.llFloorBlockingData);
        llSetFloorCallBlockignData = (LinearLayout)findViewById(R.id.llSetFlooorCallBlockingData);
        rlViewFloorCallBlockingData = (RelativeLayout)findViewById(R.id.rlFloorCallBlockingViewData);
        btnSetValues = (Button)findViewById(R.id.btnFloorCallBlockingSetValues);
        btnViewValues = (Button)findViewById(R.id.btnFloorCallBlockingViewValues);
    }


    private void createObj() {
        mContext = FloorCallBlockingActivity.this;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        arrCommandValueList = new ArrayList<String>();


        for (int i = 0; i <=31; i++) {
            arrCommandValueList.add(String.valueOf(i));
        }

        adapter = new ArrayAdapter<String>(mContext, R.layout.list_item, arrCommandValueList);

        spinSelectFloor.setAdapter(adapter);

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
        btnSetFloorCallBlocking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callStopDelay();
            }
        });

        btnSetValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSetFloorCallBlockignData.setVisibility(View.VISIBLE);
                rlViewFloorCallBlockingData.setVisibility(View.GONE);
            }
        });

        btnViewValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSetFloorCallBlockignData.setVisibility(View.GONE);
                rlViewFloorCallBlockingData.setVisibility(View.VISIBLE);
            }
        });


        btnViewFloorCallBlocking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llFloorBlockingData.removeAllViews();
                completReceivedString.setLength(0);
                counter = 0;
                if (isConnected()) {
                pd = ProgressDialog.show(mContext,"","Please wait",true);
                boolean b = ha.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //call function
                        if(counter == 0){
                            callViewCallBlocking(counter);
                            delay();
                            counter++;
                        }else if(counter == 1){
                            callViewCallBlocking(counter);
                            delay();
                            counter++;
                        }else if(counter ==  2 ){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 3){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 4){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 5){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 6){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 7){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 8){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 9){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 10){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 11){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 12){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 13){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 14){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 15){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 16){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 17){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 18){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 19){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 20){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 21){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 22){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 23){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 24){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 25){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 26){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 27){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 28){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 29){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 30){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 31){
                                callViewCallBlocking(counter);
                            delay();
                                counter++;
                        }else if(counter == 32){
                                //counter++;
                                pd.dismiss();
                                if(completReceivedString.toString().contains("11250bf")){
                                    showReceivedDataNew();
                                    counter++;
                                }

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

    }

    int a1 = 18;
    int a2 = 17;
    int a3 = 112;
    int a4;
    int a5;
    int a6 = 00;

    public void callStopDelay() {

        switch (rdoGropFloorCallBlocking.getCheckedRadioButtonId()){
            case R.id.rdoBtnNoCallBlocking:
                a5 = 00;
                break;
            case R.id.rdoBtnOnlyCarCallBlock:
                a5 = 01;
                break;

            case R.id.rdoBtnHallUpCallBlock :
                a5 = 02;
                break;
            case R.id.rdoBtnCarPlusHalllUpCallBlock :
                a5 = 03;
                break;

            case R.id.rdoBtnHallDownCallBlock :
                a5 = 04;
                break;
            case R.id.rdoBtnCarPlusHallDownCallBlock :
                a5 = 05;
                break;
            case R.id.rdoBtnHallUpPlusDownCallsBlock :
                a5 = 06;
                break;

            case R.id.rdoBtnCarPlusHallUpPlusHallDownCallBlock :
                a5 = 07;
                break;
        }

        a4 = 160 + Integer.parseInt(spinSelectFloor.getSelectedItem().toString());
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

    private void callViewCallBlocking(int i) {
        int a1 = 18;
        int a2 = 17;
        int a3 = 80;
        int a4;
        int a5 = 00;
        int a6 = 00;

        a4 = 160 + i;
        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};

        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        /*int sum = a1 + a2 + a3 + a4 + a5 + a6 ;
        String sumHex = String.format("%04x", sum);

        String msb = sumHex.substring(1, 2);
        String lsb = sumHex.substring(2, 4);

        int a7 = Integer.parseInt(lsb, 16);
        int msb2 = (Integer.parseInt(msb) | 50);
        int a8 = Integer.parseInt(String.valueOf(msb2),16);

        byte[] br2 = {(byte) a1, (byte) a2, (byte) a3, (byte) a4, (byte) a5, (byte) a6};
        byte[] br1 = {(byte) a1, (byte) a2, (byte) a3, (byte) a4, (byte) a5, (byte) a6, (byte) a7, (byte) a8};*/

        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4) ;
      /*  int sumSendString  = 0;
        for(int j = 0; j<asciiString.length(); j++){
            sumSendString = sumSendString + Integer.parseInt(String.format("%04x", (int) asciiString.charAt(j)).substring(2,4));
        }
        asciiString = asciiString +String.valueOf(sumSendString).substring(1,3)+ "\r";*/
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
        getSupportActionBar().setTitle("Floor Call Blocking");
    }


    // ==========================================================================

    /**
     * ?????????? ?????? ?????? ?? bluetooth-??????
     */
    private static class BluetoothResponseHandler extends Handler {
        private WeakReference<FloorCallBlockingActivity> mActivity;

        public BluetoothResponseHandler(FloorCallBlockingActivity activity) {
            mActivity = new WeakReference<FloorCallBlockingActivity>(activity);
        }

        public void setTarget(FloorCallBlockingActivity target) {
            mActivity.clear();
            mActivity = new WeakReference<FloorCallBlockingActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            FloorCallBlockingActivity activity = mActivity.get();
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


        String receivedString = new String(completReceivedString);
        Log.e(TAG, "ShowReceivedData"+receivedString);
        try {
            Log.e(TAG, "receivedString lenght = "+ receivedString.length());
            if(receivedString.contains("\r")) {
                Log.e(TAG, "contains true ");
            }

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
                            final View view = inflater.inflate(R.layout.floor_call_blocking_item, null);
                            TextView tvFloorNo = (TextView) view.findViewById(R.id.tvfloor_blocking_FloorNumber);
                            TextView tvFloorData = (TextView) view.findViewById(R.id.tvfloor_blocking_Data);
                             checkBoxHu = (CheckBox) view.findViewById(R.id.checkBoxHU);
                             checkBoxHd = (CheckBox) view.findViewById(R.id.checkBoxHD);
                             checkBoxCc = (CheckBox) view.findViewById(R.id.checkBoxCC);

                            if (locationAddress.equalsIgnoreCase("A0")) {

                                tvFloorNo.setText("" + 0);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("A1")) {

                                tvFloorNo.setText("" + 1);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("A2")) {

                                tvFloorNo.setText("" + 2);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("A3")) {

                                tvFloorNo.setText("" + 3);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("A4")) {

                                tvFloorNo.setText("" + 4);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("A5")) {
                                tvFloorNo.setText("" + 5);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("A6")) {

                                tvFloorNo.setText("" + 6);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("A7")) {

                                tvFloorNo.setText("" + 7);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("A8")) {

                                tvFloorNo.setText("" + 8);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("A9")) {

                                tvFloorNo.setText("" + 9);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("AA")) {

                                tvFloorNo.setText("" + 10);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("AB")) {

                                tvFloorNo.setText("" + 11);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("AC")) {

                                tvFloorNo.setText("" + 12);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("AD")) {

                                tvFloorNo.setText("" + 13);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("AE")) {

                                tvFloorNo.setText("" + 14);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("AF")) {

                                tvFloorNo.setText("" + 15);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("B0")) {

                                tvFloorNo.setText("" + 16);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("B1")) {

                                tvFloorNo.setText("" + 17);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("B2")) {

                                tvFloorNo.setText("" + 18);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);

                            } else if (locationAddress.equalsIgnoreCase("B3")) {

                                tvFloorNo.setText("" + 19);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("B4")) {

                                tvFloorNo.setText("" + 20);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("B5")) {

                                tvFloorNo.setText("" + 21);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("B6")) {

                                tvFloorNo.setText("" + 22);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("B7")) {

                                tvFloorNo.setText("" + 23);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("B8")) {

                                tvFloorNo.setText("" + 24);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("B9")) {

                                tvFloorNo.setText("" + 25);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("BA")) {

                                tvFloorNo.setText("" + 26);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("BB")) {

                                tvFloorNo.setText("" + 27);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("BC")) {

                                tvFloorNo.setText("" + 28);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("BD")) {

                                tvFloorNo.setText("" + 29);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("BE")) {

                                tvFloorNo.setText("" + 30);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
                            } else if (locationAddress.equalsIgnoreCase("BF")) {

                                tvFloorNo.setText("" + 31);
                                getDiscriptionFromCode(data);
                                //tvFloorData.setText("" + getDiscriptionFromCode(data));
                                llFloorBlockingData.addView(view);
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

    public void getDiscriptionFromCode(int code){
        String discription = "";
        if(code == 0){
            checkBoxHu.setChecked(true);
            checkBoxHd.setChecked(true);
            checkBoxCc.setChecked(true);
            discription = "No Call Blocking";
        }else if(code == 1){
            checkBoxHu.setChecked(true);
            checkBoxHd.setChecked(true);
            checkBoxCc.setChecked(false);
            discription = "Only Car Call Block";
        }else if(code == 2){
            checkBoxHu.setChecked(false);
            checkBoxHd.setChecked(true);
            checkBoxCc.setChecked(true);
            discription = "Hall Up Call Block";
        }else if(code == 3){
            checkBoxHu.setChecked(false);
            checkBoxHd.setChecked(true);
            checkBoxCc.setChecked(false);
            discription = "Car + Hall Up Call Block";
        }else if(code == 4){
            checkBoxHu.setChecked(true);
            checkBoxHd.setChecked(false);
            checkBoxCc.setChecked(true);
            discription = "Hall Down Call Block";
        }else if(code == 5){
            checkBoxHu.setChecked(true);
            checkBoxHd.setChecked(false);
            checkBoxCc.setChecked(false);
            discription = "Car + Hall Down Calls Block";
        }else if(code == 6){
            checkBoxHu.setChecked(false);
            checkBoxHd.setChecked(false);
            checkBoxCc.setChecked(true);
            discription = " Hall Up + Down Calls Block";
        }else if(code == 7){
            checkBoxHu.setChecked(false);
            checkBoxHd.setChecked(false);
            checkBoxCc.setChecked(false);
            discription = "Car + Hall Up + Hall Down Calls Block";
        }
        //return discription;
    }


}
