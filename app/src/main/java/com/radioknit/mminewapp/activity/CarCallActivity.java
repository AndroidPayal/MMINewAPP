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
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.radioknit.mminewapp.DeviceData;
import com.radioknit.mminewapp.R;
import com.radioknit.mminewapp.Utils;
import com.radioknit.mminewapp.adapter.CarCallAdapter;
import com.radioknit.mminewapp.bluetooth.DeviceConnector;
import com.radioknit.mminewapp.bluetooth.DeviceListActivity;
import com.radioknit.mminewapp.sharedpreference.TempSharedPreference;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;

public class CarCallActivity extends BaseActivity implements CarCallAdapter.CarCallIndicatorSignalListner {


    private static final String TAG = "CarCallActivity";
    private BluetoothAdapter bluetoothAdapter;
    private static Context mContext;
    private static ListView lstFloorsIndicator;
    private OutputStream outputStream;
    private static final String DEVICE_NAME = "DEVICE_NAME";
    private static final String LOG = "LOG";
    private static final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss.SSS");
    private static String MSG_NOT_CONNECTED;
    private static String MSG_CONNECTING;
    private static String MSG_CONNECTED;
    private static DeviceConnector connector;
    private static CarCallActivity.BluetoothResponseHandler mHandler;
    DeviceListActivity deviceListActivity;
    private boolean hexMode, needClean;
    private boolean show_timings, show_direction;
    private String command_ending;
    private String deviceName;
    private int counter = 1;
    private ProgressDialog pd;
    private StringBuffer completReceivedString;
    public static int showState[]=new int[16], showStateUp[] = new int[16], showStateDown[] = new int[16];

    CarCallAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.settings_activity, false);

        if (mHandler == null) mHandler = new CarCallActivity.BluetoothResponseHandler(this);
        else mHandler.setTarget(this);

        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);

        setContentView(R.layout.activity_car_call);
        completReceivedString = new StringBuffer();
        createObj();
        generateId();

        registerEvent();
        
        if (isConnected() && (savedInstanceState != null)) {
            setDeviceName(savedInstanceState.getString(DEVICE_NAME));
        } else getSupportActionBar().setSubtitle(MSG_NOT_CONNECTED);
    }

    private void createObj() {

        mContext = CarCallActivity.this;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    }

    private void registerEvent() {

    }

    private void generateId() {
        lstFloorsIndicator = (ListView)findViewById(R.id.lstFloorIndicator);
        adapter = new CarCallAdapter(getApplicationContext(), "00000000","00000000" , this);
        for(int pos=0;pos<=15;pos++){
            showState[pos]=0;
            showStateUp[pos]=0;
            showStateDown[pos]=0;
        }
        lstFloorsIndicator.setAdapter(adapter);
    }

    @Override
    public void sendCarCallIndicatorSignal(int position) {

    }

    @Override
    public void sendUpCallIndicatorSignal(int position) {

    }

    @Override
    public void sendDnCallIndicatorSignal(int position) {

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
//        logTextView.setText(msg);
//        if (clean) commandEditText.setText("");
    }


    // =========================================================================
    public void appendLog1(String message, boolean hexMode, boolean outgoing, boolean clean) {

        completReceivedString.append(message);

        String receivedString = new String(completReceivedString);
        Log.e(TAG, "receivedString = "+receivedString);

        int indexOD = receivedString.indexOf("\r");
//        String temp1 = receivedString.substring(0, indexOD);
        if(receivedString.contains("131143")&&receivedString.contains("\r")){
            showCarCalls(receivedString);
        }
        if (receivedString.contains("114c50")){
            showUpDnCalls(receivedString);
        }


    }
    // =========================================================================

    void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        getSupportActionBar().setSubtitle(deviceName);
        getSupportActionBar().setTitle("Car Call");
    }


    // ==========================================================================

    /**
     * ?????????? ?????? ?????? ?? bluetooth-??????
     */
    private static class BluetoothResponseHandler extends Handler {
        private WeakReference<CarCallActivity> mActivity;
        private String temp = "";

        public BluetoothResponseHandler(CarCallActivity activity) {
            mActivity = new WeakReference<CarCallActivity>(activity);
        }

        public void setTarget(CarCallActivity target) {
            mActivity.clear();
            mActivity = new WeakReference<CarCallActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            CarCallActivity activity = mActivity.get();
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
                       /* final String readMessage = (String) msg.obj;
                        temp = temp + readMessage;
                        if (temp.contains("\r")) {
                            activity.appendLog1(readMessage, false, false, activity.needClean);
                            temp = "";
                        }*/
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
     void showCarCalls(String strCarCall) {

       /* CarCallAdapter adapter1 = new CarCallAdapter(mContext, "00000000","00000000",(CarCallAdapter.CarCallIndicatorSignalListner) this);
        lstFloorsIndicator.setAdapter(adapter1);*/


        try {
            int index = strCarCall.lastIndexOf("131143");
            String hexCarCallsCop1 = strCarCall.substring(index+6,index+8);
            String hexCarCallsCop2 = strCarCall.substring(index+8,index+10);
            String strcallCop1 = Utils.hexToBin(hexCarCallsCop1);
            String strcallCop2 = Utils.hexToBin(hexCarCallsCop2);
            Log.e(TAG, "hexCarCallsCop1 = "+hexCarCallsCop1);
            Log.e(TAG, "strcallCop1 = "+strcallCop1);
            Log.e(TAG, "hexCarCallsCop2 = "+hexCarCallsCop2);
            Log.e(TAG, "strcallCop2 = "+strcallCop2);
            String  strCallCopCombine = strcallCop2 + strcallCop1;
            for(int indexCop=0; indexCop <=15; indexCop++) {
                if (strCallCopCombine.charAt(indexCop) == '0') {
                    showState[indexCop] = 0;
                } else if(strCallCopCombine.charAt(indexCop) == '1') {
                    showState[indexCop] = 1;
                }
            }
            adapter.notifyDataSetChanged();
            /*CarCallAdapter adapter = new CarCallAdapter(mContext, strcallCop1, strcallCop2, (CarCallAdapter.CarCallIndicatorSignalListner) this);
            lstFloorsIndicator.setAdapter(adapter);*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private   void showUpDnCalls(String strUpDn) {

       /* CarCallAdapter adapter1 = new CarCallAdapter(mContext, "00000000","00000000", (CarCallAdapter.CarCallIndicatorSignalListner)this);
        lstFloorsIndicator.setAdapter(adapter1);
*/
        try {
            int index = strUpDn.lastIndexOf("114c50");
            String strChkFlr=strUpDn.substring(index-2,index);
            Log.e(TAG, "strChkFlr = "+ strChkFlr);
            String hexSwitchData = strUpDn.substring(index+8,index+10);
            String binSwitchData = Utils.hexToBin(hexSwitchData);
            Log.e(TAG, "hexSwitchData = "+ hexSwitchData);
            if(strChkFlr.equals("30")){
                if(binSwitchData.charAt(0)=='0'){
                    showStateDown[15]=0;
                }
                else if(binSwitchData.charAt(0)=='1'){
                    showStateDown[15]=1;
                }
                if(binSwitchData.charAt(1)=='0'){
                    showStateUp[15]=0;
                }
                else if(binSwitchData.charAt(1)=='1'){
                    showStateUp[15]=1;
                }
            }
            if(strChkFlr.equals("31")){
                if(binSwitchData.charAt(0)=='0'){
                    showStateDown[14]=0;
                }
                else if(binSwitchData.charAt(0)=='1'){
                    showStateDown[14]=1;
                }
                if(binSwitchData.charAt(1)=='0'){
                    showStateUp[14]=0;
                }
                else if(binSwitchData.charAt(1)=='1'){
                    showStateUp[14]=1;
                }
            }
            if(strChkFlr.equals("32")){
                if(binSwitchData.charAt(0)=='0'){
                    showStateDown[13]=0;
                }
                else if(binSwitchData.charAt(0)=='1'){
                    showStateDown[13]=1;
                }
                if(binSwitchData.charAt(1)=='0'){
                    showStateUp[13]=0;
                }
                else if(binSwitchData.charAt(1)=='1'){
                    showStateUp[13]=1;
                }
            }
            if(strChkFlr.equals("33")){
                if(binSwitchData.charAt(0)=='0'){
                    showStateDown[12]=0;
                }
                else if(binSwitchData.charAt(0)=='1'){
                    showStateDown[12]=1;
                }
                if(binSwitchData.charAt(1)=='0'){
                    showStateUp[12]=0;
                }
                else if(binSwitchData.charAt(1)=='1'){
                    showStateUp[12]=1;
                }
            }
            if(strChkFlr.equals("34")){
                if(binSwitchData.charAt(0)=='0'){
                    showStateDown[11]=0;
                }
                else if(binSwitchData.charAt(0)=='1'){
                    showStateDown[11]=1;
                }
                if(binSwitchData.charAt(1)=='0'){
                    showStateUp[11]=0;
                }
                else if(binSwitchData.charAt(1)=='1'){
                    showStateUp[11]=1;
                }
            }
            if(strChkFlr.equals("35")){
                if(binSwitchData.charAt(0)=='0'){
                    showStateDown[10]=0;
                }
                else if(binSwitchData.charAt(0)=='1'){
                    showStateDown[10]=1;
                }
                if(binSwitchData.charAt(1)=='0'){
                    showStateUp[10]=0;
                }
                else if(binSwitchData.charAt(1)=='1'){
                    showStateUp[10]=1;
                }
            }
            if(strChkFlr.equals("36")){
                if(binSwitchData.charAt(0)=='0'){
                    showStateDown[9]=0;
                }
                else if(binSwitchData.charAt(0)=='1'){
                    showStateDown[9]=1;
                }
                if(binSwitchData.charAt(1)=='0'){
                    showStateUp[9]=0;
                }
                else if(binSwitchData.charAt(1)=='1'){
                    showStateUp[9]=1;
                }
            }
            if(strChkFlr.equals("37")){
                if(binSwitchData.charAt(0)=='0'){
                    showStateDown[8]=0;
                }
                else if(binSwitchData.charAt(0)=='1'){
                    showStateDown[8]=1;
                }
                if(binSwitchData.charAt(1)=='0'){
                    showStateUp[8]=0;
                }
                else if(binSwitchData.charAt(1)=='1'){
                    showStateUp[8]=1;
                }
            }
            if(strChkFlr.equals("38")){
                if(binSwitchData.charAt(0)=='0'){
                    showStateDown[7]=0;
                }
                else if(binSwitchData.charAt(0)=='1'){
                    showStateDown[7]=1;
                }
                if(binSwitchData.charAt(1)=='0'){
                    showStateUp[7]=0;
                }
                else if(binSwitchData.charAt(1)=='1'){
                    showStateUp[7]=1;
                }
            }
            if(strChkFlr.equals("39")){
                if(binSwitchData.charAt(0)=='0'){
                    showStateDown[6]=0;
                }
                else if(binSwitchData.charAt(0)=='1'){
                    showStateDown[6]=1;
                }
                if(binSwitchData.charAt(1)=='0'){
                    showStateUp[6]=0;
                }
                else if(binSwitchData.charAt(1)=='1'){
                    showStateUp[6]=1;
                }
            }
            if(strChkFlr.equals("3a")){
                if(binSwitchData.charAt(0)=='0'){
                    showStateDown[5]=0;
                }
                else if(binSwitchData.charAt(0)=='1'){
                    showStateDown[5]=1;
                }
                if(binSwitchData.charAt(1)=='0'){
                    showStateUp[5]=0;
                }
                else if(binSwitchData.charAt(1)=='1'){
                    showStateUp[5]=1;
                }
            }
            if(strChkFlr.equals("3b")){
                if(binSwitchData.charAt(0)=='0'){
                    showStateDown[4]=0;
                }
                else if(binSwitchData.charAt(0)=='1'){
                    showStateDown[4]=1;
                }
                if(binSwitchData.charAt(1)=='0'){
                    showStateUp[4]=0;
                }
                else if(binSwitchData.charAt(1)=='1'){
                    showStateUp[4]=1;
                }
            }
            if(strChkFlr.equals("3c")){
                if(binSwitchData.charAt(0)=='0'){
                    showStateDown[3]=0;
                }
                else if(binSwitchData.charAt(0)=='1'){
                    showStateDown[3]=1;
                }
                if(binSwitchData.charAt(1)=='0'){
                    showStateUp[3]=0;
                }
                else if(binSwitchData.charAt(1)=='1'){
                    showStateUp[3]=1;
                }
            }
            if(strChkFlr.equals("3d")){
                if(binSwitchData.charAt(0)=='0'){
                    showStateDown[2]=0;
                }
                else if(binSwitchData.charAt(0)=='1'){
                    showStateDown[2]=1;
                }
                if(binSwitchData.charAt(1)=='0'){
                    showStateUp[2]=0;
                }
                else if(binSwitchData.charAt(1)=='1'){
                    showStateUp[2]=1;
                }
            }
            if(strChkFlr.equals("3e")){
                if(binSwitchData.charAt(0)=='0'){
                    showStateDown[1]=0;
                }
                else if(binSwitchData.charAt(0)=='1'){
                    showStateDown[1]=1;
                }
                if(binSwitchData.charAt(1)=='0'){
                    showStateUp[1]=0;
                }
                else if(binSwitchData.charAt(1)=='1'){
                    showStateUp[1]=1;
                }
            }
            if(strChkFlr.equals("3f")){
                if(binSwitchData.charAt(0)=='0'){
                    showStateDown[0]=0;
                }
                else if(binSwitchData.charAt(0)=='1'){
                    showStateDown[0]=1;
                }
                if(binSwitchData.charAt(1)=='0'){
                    showStateUp[0]=0;
                }
                else if(binSwitchData.charAt(1)=='1'){
                    showStateUp[0]=1;
                }
            }
            adapter.notifyDataSetChanged();

            /*String hexUpDnCalls = String.format("%04x", (int) strUpDn.charAt(index + 3));
            String strUpDnCalls = Utils.hexToBin(hexUpDnCalls);
            String floorNo = String.format("%04x", (int) strUpDn.charAt(index - 2));
            int flrNo = Integer.parseInt(floorNo) - 30;*/

            /*CarCallAdapter adapter = new CarCallAdapter(mContext, strUpDnCalls, flrNo,(CarCallAdapter.CarCallIndicatorSignalListner) this);
            lstFloorsIndicator.setAdapter(adapter);*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    // ==========================================================================
}
