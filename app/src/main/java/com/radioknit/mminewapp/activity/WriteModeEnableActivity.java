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
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class WriteModeEnableActivity extends BaseActivity {

    private static final String TAG = "WriteModeEnabled";
    private Context mContext;
    private BluetoothAdapter bluetoothAdapter;
    private String connectedDeviceName;
    ArrayList<String> arrCommandValueList;
    private Button btnWriteModeEnable;

    // =============
    private OutputStream outputStream;
    private static final String DEVICE_NAME = "DEVICE_NAME";
    private static final String LOG = "LOG";
    private static final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss.SSS");
    private static String MSG_NOT_CONNECTED;
    private static String MSG_CONNECTING;
    private static String MSG_CONNECTED;
    private static DeviceConnector connector;
    private static WriteModeEnableActivity.BluetoothResponseHandler mHandler;
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

        Log.e(TAG, "onCreate()");
        PreferenceManager.setDefaultValues(this, R.xml.settings_activity, false);

        if (mHandler == null) mHandler = new WriteModeEnableActivity.BluetoothResponseHandler(this);
        else mHandler.setTarget(this);

        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);

        setContentView(R.layout.activity_write_mode_enable);
        completReceivedString = new StringBuffer();

        generateId();
        createObj();
        registerEvent();

        if (isConnected() && (savedInstanceState != null)) {
            Log.e(TAG, " if isConnected() = "+isConnected());
            setDeviceName(savedInstanceState.getString(DEVICE_NAME));
        } else {
            Log.e(TAG, " else isConnected() = "+isConnected());
            getSupportActionBar().setSubtitle(MSG_NOT_CONNECTED);
        }

        if(savedInstanceState != null) {
            String address = savedInstanceState.getString(DEVICE_NAME);
            BluetoothDevice device = btAdapter.getRemoteDevice(address);
            if (super.isAdapterReady() && (connector == null)) setupConnector(device);
        }

    }


    private void registerEvent() {
        btnWriteModeEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completReceivedString.setLength(0);
                String str1 = Integer.toHexString(18);
                String str2 = Integer.toHexString(17);
                String str3 = Integer.toHexString(112);
                String str4 = Integer.toHexString(0);
                String str5 = Integer.toHexString(5);
                String str6 = Integer.toHexString(87);
                String str7 = Integer.toHexString(239);
                String str8 = Integer.toHexString(80);

                int a1 = Integer.parseInt(str1,16);
                int a2 = Integer.parseInt(str2,16);
                int a3 = Integer.parseInt(str3,16);
                int a4 = Integer.parseInt(str4,16);
                int a5 = Integer.parseInt(str5,16);
                int a6 = Integer.parseInt(str6,16);
                int a7 = Integer.parseInt(str7,16);
                int a8 = Integer.parseInt(str8,16);

                int[] sendValChkSum={a1, a2, a3, a4, a5, a6};

                String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);

                byte[] br1 = {(byte)a1,(byte) a2 , (byte) a3,(byte) a4,(byte)a5,(byte) a6,(byte) a7, (byte) a8};

                String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4) ;
               /* int sumSendString  = 0;
                for(int i = 0; i<asciiString.length(); i++){
                    sumSendString = sumSendString + Integer.parseInt(String.format("%04x", (int) asciiString.charAt(i)).substring(2,4));
                }
                asciiString = asciiString +String.valueOf(sumSendString).substring(1,3)+ "\r";*/
                asciiString = asciiString + strChkSum + "\r";
                Log.e(TAG, "asciiString = "+ asciiString);

                if (isConnected()) {
                    connector.write(asciiString.getBytes());
                }
                else {
                    Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String str12 = Integer.toHexString(18);
                String str22 = Integer.toHexString(17);
                String str32 = Integer.toHexString(112);
                String str42 = Integer.toHexString(1);
                String str52 = Integer.toHexString(9);
                String str62 = Integer.toHexString(87);
                String str72 = Integer.toHexString(244);
                String str82 = Integer.toHexString(80);

                int a12 = Integer.parseInt(str12,16);
                int a22 = Integer.parseInt(str22,16);
                int a32 = Integer.parseInt(str32,16);
                int a42 = Integer.parseInt(str42,16);
                int a52 = Integer.parseInt(str52,16);
                int a62 = Integer.parseInt(str62,16);
                int a72 = Integer.parseInt(str72,16);
                int a82 = Integer.parseInt(str82,16);

                int[] sendValChkSum1={a12, a22, a32, a42, a52, a62};

                String strChkSum1= CalculateCheckSum.calculateChkSum(sendValChkSum1);

                byte[] br2 = {(byte)a12,(byte) a22 , (byte) a32,(byte) a42,(byte)a52,(byte) a62,(byte) a72, (byte) a82};

                String asciiString1  = String.format("%04x", a12).substring(2,4)+String.format("%04x", a22).substring(2,4)+String.format("%04x", a32).substring(2,4)+String.format("%04x", a42).substring(2,4)+String.format("%04x", a52).substring(2,4)+String.format("%04x", a62).substring(2,4) ;
                /*int sumSendString1  = 0;
                for(int i = 0; i<asciiString1.length(); i++){
                    sumSendString1 = sumSendString1 + Integer.parseInt(String.format("%04x", (int) asciiString1.charAt(i)).substring(2,4));
                }
                asciiString1 = asciiString1 +String.valueOf(sumSendString1).substring(1,3)+ "\r";*/
                asciiString1 = asciiString1 + strChkSum1 + "\r";
                Log.e(TAG, "asciiString1 = "+ asciiString1);

                if (isConnected()) {
                    connector.write(asciiString1.getBytes());
                }
                else {
                    Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Write mode enabled", Toast.LENGTH_SHORT).show();
                /*int[] sendChkSumRead1={0x12, 0x11, 0x50, 0x00, 0x00, 0x00};
                String strChkSumRead1=CalculateCheckSum.calculateChkSum(sendChkSumRead1);
                String sendStrRead1="121150000000"+strChkSumRead1+ "\r";
                Log.e(TAG, "sendStrRead1 = "+ sendStrRead1);
                if (isConnected()) {
                    connector.write(sendStrRead1.getBytes());
                }
                else {
                    Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
                }

                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int[] sendChkSumRead2={0x12, 0x11, 0x50, 0x01, 0x00, 0x00};
                String strChkSumRead2=CalculateCheckSum.calculateChkSum(sendChkSumRead2);
                String sendStrRead2="121150010000"+strChkSumRead2+ "\r";
                Log.e(TAG, "sendStrRead2 = "+ sendStrRead2);
                if (isConnected()) {
                    connector.write(sendStrRead2.getBytes());
                }
                else {
                    Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
                }*/

            }
        });

    }

    private void createObj() {
        mContext = WriteModeEnableActivity.this;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        arrCommandValueList = new ArrayList<String>();

    }

    private void generateId() {
        btnWriteModeEnable = (Button)findViewById(R.id.btnEnableWriteMode);
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

            case R.id.menu_search:
                if (super.isAdapterReady()) {
                    if (isConnected()) stopConnection();
                    else startDeviceListActivity();
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
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

        completReceivedString.append(message);
        Log.e(TAG, " received msg = "+ completReceivedString);
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
        private WeakReference<WriteModeEnableActivity> mActivity;

        public BluetoothResponseHandler(WriteModeEnableActivity activity) {
            mActivity = new WeakReference<WriteModeEnableActivity>(activity);
        }

        public void setTarget(WriteModeEnableActivity target) {
            mActivity.clear();
            mActivity = new WeakReference<WriteModeEnableActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            WriteModeEnableActivity activity = mActivity.get();
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
                        final String readMessageStr = (String) msg.obj;

                        //Log.e(TAG, " received msg = "+ readMessageStr);
                       /* String readMessage = null;
                        try {
                            byte[] readBuf = (byte[]) msg.obj;
                            readMessage = new String(readBuf, 0, msg.arg1,"ISO-8859-1");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                        //Log.e(TAG, " received msg = "+ readMessage);
                        if (readMessageStr != null) {
                            activity.appendLog1(readMessageStr, false, false, activity.needClean);
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

}
