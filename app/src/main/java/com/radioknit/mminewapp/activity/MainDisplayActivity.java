package com.radioknit.mminewapp.activity;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.radioknit.mminewapp.DeviceData;
import com.radioknit.mminewapp.DrawLine;
import com.radioknit.mminewapp.R;
import com.radioknit.mminewapp.Utils;
import com.radioknit.mminewapp.bluetooth.DeviceConnector;
import com.radioknit.mminewapp.bluetooth.DeviceListActivity;
import com.radioknit.mminewapp.sharedpreference.TempSharedPreference;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;

import pl.droidsonroids.gif.GifImageView;

public class MainDisplayActivity extends BaseActivity {

    private static final String TAG = "MainDisplayActivity";
    private BluetoothAdapter bluetoothAdapter;
    private String temp = "";
    private ListView lstFloorsIndicator;
    private Button btnData;
    private Button btnSecurity;
    private LinearLayout llData;
    private LinearLayout llSecurity;
    private static LinearLayout llWireDiagram;
    private static Button btnDoorOpen;
    private static Button btnDoorClose;
    private static Button btnAttnStart;
    private static Button btnNonStop;
    private static Button btnAuto;
    private static Button btnVIP2;
    private static Button btnDIR;
    private static Button btnStop;
    private String remaningString;
//    private static ImageView imgDn;
//    private static ImageView imgUp;
    private static GifImageView imgDn;
    private static GifImageView imgUp;
    private static TextView txtFloorNumber;
    private String connectedDeviceName;
    private StringBuffer outStringBuffer;
    private TextView txtCompulsoruStop;
    private TextView txtParkingFloor;
    private TextView txtHomeLanding;
    private TextView txtFiremanFloor;
    private StringBuffer mOutStringBuffer;
    private static ImageView imgPreUp;
    private static ImageView imgPreDn;
    private static TextView txtPreValue;
    private static Context mContext;

    // =============

    private OutputStream outputStream;
    private static final String DEVICE_NAME = "DEVICE_NAME";
    private static final String LOG = "LOG";

    private static final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss.SSS");

    private static String MSG_NOT_CONNECTED;
    private static String MSG_CONNECTING;
    private static String MSG_CONNECTED;

    private static DeviceConnector connector;
    private static MainDisplayActivity.BluetoothResponseHandler mHandler;
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

        if (mHandler == null) mHandler = new MainDisplayActivity.BluetoothResponseHandler(this);
        else mHandler.setTarget(this);

        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);

        setContentView(R.layout.activity_main_display);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        completReceivedString = new StringBuffer();

        generateId();
        createObj();
        registerEvent();

        setDefaultValues();
        if (isConnected() && (savedInstanceState != null)) {
            setDeviceName(savedInstanceState.getString(DEVICE_NAME));
        } else getSupportActionBar().setSubtitle(MSG_NOT_CONNECTED);

    }

    private void registerEvent() {
        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llData.setVisibility(View.VISIBLE);
                llSecurity.setVisibility(View.GONE);
            }
        });

        btnSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llData.setVisibility(View.GONE);
                llSecurity.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createObj() {
        mContext = MainDisplayActivity.this;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    private void generateId() {
        btnData = (Button)findViewById(R.id.btn_main_dispaly_data);
        btnSecurity = (Button)findViewById(R.id.btn_main_display_security);
        llData = (LinearLayout)findViewById(R.id.llmain_display_data);
        llSecurity = (LinearLayout)findViewById(R.id.llmina_display_security);
        llWireDiagram = (LinearLayout)findViewById(R.id.llWireDiagram);
        btnDoorOpen = (Button)findViewById(R.id.btnMani_display_doorOpen);
        btnDoorClose = (Button)findViewById(R.id.btnMani_display_doorClose);
        btnAttnStart = (Button)findViewById(R.id.btnMani_display_AttnStart);
        btnNonStop = (Button)findViewById(R.id.btnMani_display_NonStop);
        btnDIR = (Button)findViewById(R.id.btnMani_display_Dir);
        btnAuto = (Button)findViewById(R.id.btnMani_display_Auto);
        btnStop = (Button)findViewById(R.id.btnMani_display_stop);
        btnVIP2 = (Button)findViewById(R.id.btnMani_display_VIP2);
//        imgUp = (ImageView)findViewById(R.id.imgUp);
//        imgDn = (ImageView)findViewById(R.id.imgDwn);
        imgUp = (GifImageView)findViewById(R.id.imgUp);
        imgDn = (GifImageView)findViewById(R.id.imgDwn);
        txtFloorNumber = (TextView)findViewById(R.id.tv_main_display_floorNo);
        txtCompulsoruStop = (TextView)findViewById(R.id.tvCompulsoryStopValue);
        txtParkingFloor = (TextView)findViewById(R.id.tvParkingFloorValue);
        txtHomeLanding = (TextView)findViewById(R.id.tvHomeLandingValue);
        txtFiremanFloor = (TextView)findViewById(R.id.tvFireManFloorValue);
        imgPreUp = (ImageView)findViewById(R.id.imgPreUp);
        imgPreDn = (ImageView)findViewById(R.id.imgPreDn);
        txtPreValue = (TextView) findViewById(R.id.tvPreValue);

        DrawLine drawLine = new DrawLine(getApplicationContext(), getResources().getColor(R.color.green),getResources().getColor(R.color.green),getResources().getColor(R.color.green),getResources().getColor(R.color.green),getResources().getColor(R.color.green));
        llWireDiagram.addView(drawLine);
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

            case android.R.id.home:
                this.finish();
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

        // hex mode
        final String mode = Utils.getPrefence(this, getString(R.string.pref_commands_mode));
        this.hexMode = mode.equals("HEX");

        this.command_ending = getCommandEnding();

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

        String receivedString = new String(completReceivedString);

        showReceivedData(completReceivedString.toString());
        completReceivedString.setLength(0);
    }

    private void showReceivedData(String strReceived) {

        String receivedString = strReceived;
        Log.e(TAG, "receivedString = "+ receivedString);

        int indexOd = receivedString.indexOf("\r");
        String temp = receivedString.substring(0, indexOd);

        if (temp.startsWith("1311")) {
            showCarCalls(temp);
            temp ="";
        }

        if(temp.startsWith("05")){
            showFloorNoAndDirection(temp);
            temp ="";
        }

        if(temp.startsWith("06")){
            setPreAnnouncing(temp);
            temp = "";
        }

        if (temp.startsWith("71")){
            setSeftyLoopValues(temp);
            temp ="";
        }
    }

    // =========================================================================

    void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        getSupportActionBar().setSubtitle(deviceName);
        getSupportActionBar().setTitle("Main Display");
    }

    // ==========================================================================

    /**
     * ?????????? ?????? ?????? ?? bluetooth-??????
     */
    private static class BluetoothResponseHandler extends Handler {
        private WeakReference<MainDisplayActivity> mActivity;
        private String temp = "";
        public BluetoothResponseHandler(MainDisplayActivity activity) {
            mActivity = new WeakReference<MainDisplayActivity>(activity);
        }

        public void setTarget(MainDisplayActivity target) {
            mActivity.clear();
            mActivity = new WeakReference<MainDisplayActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            MainDisplayActivity activity = mActivity.get();
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
                            Log.e(TAG, "temp = "+ temp);
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

    private void showCarCalls(String temp) {

        setDefaultColur();
        try {
            String receivedString = temp;
            Log.e(TAG, "-----------------------------------");
            String hexCarCalls = String.format("%04x", Integer.parseInt(receivedString.substring(10,12)));
            String strCarCalls = Utils.hexToBin(hexCarCalls);

            if (strCarCalls.charAt(7) == '1') {
                btnDoorOpen.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            }
            if (strCarCalls.charAt(6) == '1') {
                btnDoorClose.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            }
            if (strCarCalls.charAt(5) == '1') {
                btnDIR.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            }
            if (strCarCalls.charAt(4) == '1') {
                btnNonStop.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            }
            if (strCarCalls.charAt(3) == '1') {
                btnAttnStart.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            }
            if (strCarCalls.charAt(2) == '1') {
                btnVIP2.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            }
            if (strCarCalls.charAt(1) == '1') {
                btnAuto.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            }
            if (strCarCalls.charAt(0) == '1') {
                btnStop.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public void setDefaultColur(){
        btnDoorOpen.setBackground(mContext.getResources().getDrawable(R.drawable.btn_selector));
        btnDoorClose.setBackground(mContext.getResources().getDrawable(R.drawable.btn_selector));
        btnDIR.setBackground(mContext.getResources().getDrawable(R.drawable.btn_selector));
        btnNonStop.setBackground(mContext.getResources().getDrawable(R.drawable.btn_selector));
        btnAttnStart.setBackground(mContext.getResources().getDrawable(R.drawable.btn_selector));
        btnVIP2.setBackground(mContext.getResources().getDrawable(R.drawable.btn_selector));
        btnAuto.setBackground(mContext.getResources().getDrawable(R.drawable.btn_selector));
        btnStop.setBackground(mContext.getResources().getDrawable(R.drawable.btn_selector));
    }

    public void showFloorNoAndDirection(String receivedData){

        if(receivedData.startsWith("05")){

            int floorNo = Integer.parseInt(receivedData.substring(4,6), 16);
            String STAT0 = receivedData.substring(6,8);
            String hexFour = String.format("%04x", Integer.parseInt(STAT0, 16));
            String strBinaryFour = Utils.hexToBin(hexFour);

            txtFloorNumber.setText(""+floorNo);
            if (strBinaryFour.charAt(7) == '1') {
                if (strBinaryFour.charAt(5) == '1') {
                    imgUp.setVisibility(View.VISIBLE);
                    imgDn.setVisibility(View.GONE);
                    imgUp.setImageResource(R.drawable.up_flashing);
//                        tvRunningStatus.setText("Up Running");
//                        Log.e(TAG, "Up Running");
                } else {
                    imgUp.setVisibility(View.VISIBLE);
                    imgDn.setVisibility(View.GONE);
                    imgUp.setImageResource(R.drawable.up_arraow);
//                        Log.e(TAG, "Up study");
//                        tvRunningStatus.setText("Up");
                }
            } else if (strBinaryFour.charAt(6) == '1') {
                if (strBinaryFour.charAt(5) == '1') {
                    imgDn.setVisibility(View.VISIBLE);
                    imgUp.setVisibility(View.GONE);
                    imgDn.setImageResource(R.drawable.down_flashing);
//                        Log.e(TAG, "Down Running");
//                        tvRunningStatus.setText("Down Running");

                } else {
                    imgDn.setVisibility(View.VISIBLE);
                    imgUp.setVisibility(View.GONE);
                    imgDn.setImageResource(R.drawable.down_arr);
//                        Log.e(TAG, "Down Study");
//                        tvRunningStatus.setText("Down");
                }
            }else if(strBinaryFour.charAt(7) == '0'){
                imgUp.setVisibility(View.GONE);
                imgDn.setVisibility(View.GONE);

            }else if(strBinaryFour.charAt(6) == '0'){
                imgUp.setVisibility(View.GONE);
                imgDn.setVisibility(View.GONE);
            }
            temp = "";
        }
    }

    private void setDefaultValues( ){

        try {
            if(Utils.isStringNotNull(TempSharedPreference.getKeyFiremanFloor(mContext))){
                txtFiremanFloor.setText(TempSharedPreference.getKeyFiremanFloor(mContext));
            }
            if(Utils.isStringNotNull(TempSharedPreference.getKeyCompulsoryStop(mContext))){
                txtCompulsoruStop.setText(TempSharedPreference.getKeyCompulsoryStop(mContext));
            }
            if(Utils.isStringNotNull(TempSharedPreference.getKeyParkingFloor(mContext))){
                txtParkingFloor.setText(TempSharedPreference.getKeyParkingFloor(mContext));
            }
            if(Utils.isStringNotNull(TempSharedPreference.getKeyHomeFloor(mContext))){
                txtHomeLanding.setText(TempSharedPreference.getKeyHomeFloor(mContext));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setPreAnnouncing(String temp) {
        String receivedString = temp;
        try {
            String hexFloorNo = String.format("%04x", Integer.parseInt(receivedString.substring(4,6)));
            String hexDirection = String.format("%4x", Integer.parseInt(receivedString.substring(2,4)));

            txtPreValue.setText("" + Integer.parseInt(hexFloorNo, 16));
            int dir = Integer.parseInt(hexDirection.trim(), 16);
            Log.e(TAG, "Floor No = " + hexFloorNo + " fl = " + Integer.parseInt(hexFloorNo, 16));
            Log.e(TAG, "Direction = " + hexDirection + " dir = " + dir);

            if (dir == 0) {
                imgPreDn.setVisibility(View.GONE);
                imgPreUp.setVisibility(View.GONE);
            } else if (dir == 1) {
                imgPreDn.setVisibility(View.VISIBLE);
                imgPreUp.setVisibility(View.GONE);
            } else if (dir == 2) {
                imgPreUp.setVisibility(View.VISIBLE);
                imgPreDn.setVisibility(View.GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setSeftyLoopValues(String strReceive) {

        int color1 = mContext.getResources().getColor(R.color.green) ;
        int color2 = mContext.getResources().getColor(R.color.green);
        int color3 = mContext.getResources().getColor(R.color.green);
        int color4 = mContext.getResources().getColor(R.color.green);
        int color5 = mContext.getResources().getColor(R.color.green);

        String receivedString = strReceive;

        try {
            String hexTwo = String.format("%04x", Integer.parseInt(receivedString.substring(4,6)));

                String binary = Utils.hexToBin(hexTwo);

                if(binary.charAt(4) == '0'){
                    color1 = mContext.getResources().getColor(R.color.red);
                }else {
                    color1 = mContext.getResources().getColor(R.color.green);
                }
                if(binary.charAt(3) == '0'){
                    color2 = mContext.getResources().getColor(R.color.red);
                }else {
                    color2 = mContext.getResources().getColor(R.color.green);
                }

                if(binary.charAt(2) == '0'){
                    color3 = mContext.getResources().getColor(R.color.red);
                }else {
                    color3 = mContext.getResources().getColor(R.color.green);
                }

                if(binary.charAt(1) == '0'){
                    color4 = mContext.getResources().getColor(R.color.red);
                }else {
                    color4 = mContext.getResources().getColor(R.color.green);
                }

                if(binary.charAt(0) == '0'){
                    color5 = mContext.getResources().getColor(R.color.red);
                }else {
                    color5 = mContext.getResources().getColor(R.color.green);
                }

                DrawLine drawLine = new DrawLine(mContext, color1,color2,color3,color4,mContext.getResources().getColor(R.color.green));
                llWireDiagram.removeAllViews();
                llWireDiagram.addView(drawLine);
        }catch (Exception e ){
            e.printStackTrace();
        }

    }

}
