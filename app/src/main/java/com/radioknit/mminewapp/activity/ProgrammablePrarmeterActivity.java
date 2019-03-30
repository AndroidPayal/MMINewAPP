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

public class ProgrammablePrarmeterActivity extends BaseActivity {

    private static final String TAG = "ProgrammableParammeter";
    private Context mContext;
    ArrayList<String> arrCommandValueList;
    private Spinner spinDeviceID;
    private ArrayAdapter<String> adapter;
    private Button btnSetDeviceID;
    private EditText edtDeviceID;
    private Button btnViewProgramableParameter;
    private TextView txtDeviceID;
    private LinearLayout llSetProgmmableParmeter;
    private RelativeLayout rlViewProgrammableParameter;
    private Button btnSetPPValues;
    private Button btnGetPPValues;
    private TextView txtEncoderPPR;
    private TextView txtTravelLenght_1;
    private TextView txtTravelLenght_2;
    private TextView txtTravelLenght_3;
    private TextView txtSlowSpeedDistance_1;
    private TextView txtSlowSpeedDistance_2;
    private TextView txtSlowSpeedDistance_3;
    private TextView txtSlowSpeedDistance_4;
    private TextView txtDiameterOfMainPulley;
    private TextView txtGearBoxRatio;
    private TextView txtCommanUpSlip;
    private TextView txtCommonDnSlip;
    private TextView txtRPMSetting_1;
    private TextView txtRPMSetting_2;
    private TextView txtRPMSetting_3;
    private int counter = 1;

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
    private static ProgrammablePrarmeterActivity.BluetoothResponseHandler mHandler;
    DeviceListActivity deviceListActivity;

    private boolean hexMode, needClean;
    private boolean show_timings, show_direction;
    private String command_ending;
    private String deviceName;

    private ProgressDialog pd;
    private StringBuffer completReceivedString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        PreferenceManager.setDefaultValues(this, R.xml.settings_activity, false);

        if (mHandler == null) mHandler = new ProgrammablePrarmeterActivity.BluetoothResponseHandler(this);
        else mHandler.setTarget(this);

        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);


        setContentView(R.layout.activity_programmable_prarmeter);

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
        edtDeviceID = (EditText)findViewById(R.id.edtDeviceID);
        btnViewProgramableParameter = (Button)findViewById(R.id.btnViewDeviceID);
//        txtDeviceID = (TextView)view.findViewById(R.id.tvDeviceID);

        llSetProgmmableParmeter = (LinearLayout) findViewById(R.id.llProgrammable_parameter_SetValues);
        rlViewProgrammableParameter = (RelativeLayout) findViewById(R.id.rlViewProgrammable_parameter_Values);
        btnSetPPValues = (Button) findViewById(R.id.btnSetProgrammable_parameter_Values);
        btnGetPPValues = (Button) findViewById(R.id.btnViewProgrammable_parameter_Values);
        txtEncoderPPR = (TextView)findViewById(R.id.tvEncoderPPR);
        txtTravelLenght_1 = (TextView)findViewById(R.id.tvTravelLength_1);
        txtTravelLenght_2 = (TextView)findViewById(R.id.tvTravel_length_2);
        txtTravelLenght_3 = (TextView)findViewById(R.id.tvTravel_length_3);
        txtSlowSpeedDistance_1 = (TextView)findViewById(R.id.tvSlowSpeedDistanceOne);
        txtSlowSpeedDistance_2 = (TextView)findViewById(R.id.tvSlowSpeedDistance_2);
        txtSlowSpeedDistance_3 = (TextView)findViewById(R.id.tvSlowSpeedDistance_3);
        txtSlowSpeedDistance_4 = (TextView)findViewById(R.id.tvSlowSpeedDistance_4);
        txtDiameterOfMainPulley = (TextView)findViewById(R.id.tvDiameterOfMainPulley);
        txtGearBoxRatio = (TextView)findViewById(R.id.tvGareBoxRatio);
        txtCommanUpSlip = (TextView)findViewById(R.id.tvCommonUpSlip);
        txtCommonDnSlip = (TextView)findViewById(R.id.tvCommonDnSlip);
        txtRPMSetting_1 = (TextView)findViewById(R.id.tvRPMSetting_1);
        txtRPMSetting_2 = (TextView)findViewById(R.id.tvRPMSetting_2);
        txtRPMSetting_3 = (TextView)findViewById(R.id.tvRPMSetting_3);
    }

    private void createObj() {
        mContext = ProgrammablePrarmeterActivity.this;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        arrCommandValueList = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(mContext, R.layout.list_item, getResources().getStringArray(R.array.arr_deviceid_programable_parameter));

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

        btnViewProgramableParameter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completReceivedString.setLength(0);
                if (isConnected()) {
                   // pd = ProgressDialog.show(mContext, "", "Please wait", true);
                pd = ProgressDialog.show(mContext,"","Please wait",true);
                counter = 0;
                boolean b = ha.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //call function
                        if (counter == 0) {
                            callViewProgramableparametter(counter);
                            delay();
                            counter++;
                        }else if (counter == 1) {
                            callViewProgramableparametter(counter);
                            delay();
                            counter++;
                        } else if (counter == 2) {
                            callViewProgramableparametter(counter);
                            delay();
                            counter++;
                        } else if (counter == 3) {
                            callViewProgramableparametter(counter);
                            delay();
                            counter++;
                        } else if (counter == 4) {
                            callViewProgramableparametter(counter);
                            delay();
                            counter++;
                        } else if (counter == 5) {
                            callViewProgramableparametter(counter);
                            delay();
                            counter++;
                        } else if (counter == 6) {
                            callViewProgramableparametter(counter);
                            delay();
                            counter++;
                        } else if (counter == 7) {
                            callViewProgramableparametter(counter);
                            delay();
                            counter++;
                        } else if (counter == 8) {
                            callViewProgramableparametter(counter);
                            delay();
                            counter++;
                        } else if (counter == 9) {
                            callViewProgramableparametter(counter);
                            delay();
                            counter++;
                        } else if (counter == 10) {
                            callViewProgramableparametter(counter);
                            delay();
                            counter++;
                        } else if (counter == 11) {
                            callViewProgramableparametter(counter);
                            delay();
                            counter++;
                        } else if (counter == 12) {
                            callViewProgramableparametter(counter);
                            delay();
                            counter++;
                        } else if (counter == 13) {
                            callViewProgramableparametter(counter);
                            delay();
                            counter++;
                        } else if (counter == 14) {
                            callViewProgramableparametter(counter);
                            delay();
                            counter++;

                        }else if(counter == 15){
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


        btnSetPPValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSetProgmmableParmeter.setVisibility(View.VISIBLE);
                rlViewProgrammableParameter.setVisibility(View.GONE);
            }
        });

        btnGetPPValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSetProgmmableParmeter.setVisibility(View.GONE);
                rlViewProgrammableParameter.setVisibility(View.VISIBLE);
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
        if(spinDeviceID.getSelectedItemPosition() == 9){
            a3 = 80;
        }else {
            a3 = 65 + Integer.valueOf(String.valueOf(spinDeviceID.getSelectedItemPosition()), 16);
        }
        if(Utils.isStringNotNull(temp)){
            if(temp.length() == 5) {
                a4 = Integer.valueOf(temp.substring(0, 1), 16);
                a5 = Integer.valueOf(temp.substring(1, 3), 16);
                a6 = Integer.valueOf(temp.substring(3, 5), 16);
            }else {
                Utils.showToastMsg(mContext, "Device Id must be of 5 characters");
            }
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

    private void callViewProgramableparametter(int position ) {
        int a1 = 18;
        int a2 = 241;
        int a3 ;
        int a4 = 00;
        int a5 = 00;
        int a6 = 00;

        if(position == 9){
            a3 = 80;
        }else {
            a3 = 65 + Integer.valueOf(String.valueOf(position), 16);
        }

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};

        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);


       /* int sum = a1 + a2 + a3 + a4 + a5 + a6;
        String sumHex = String.format("%04x", sum);

        String msb = sumHex.substring(1, 2);
        String lsb = sumHex.substring(2, 4);

        int a7 = Integer.parseInt(lsb, 16);
        int msb2 = (Integer.parseInt(msb) | 50);
        int a8 = Integer.parseInt(String.valueOf(msb2), 16);

        byte[] br2 = {(byte) a1, (byte) a2, (byte) a3, (byte) a4, (byte) a5, (byte) a6};
        byte[] br1 = {(byte) a1, (byte) a2, (byte) a3, (byte) a4, (byte) a5, (byte) a6, (byte) a7, (byte) a8};
*/
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
/*        int sumSendString  = 0;
        for(int j = 0; j<asciiString.length();j++){
            sumSendString = sumSendString + Integer.parseInt(String.format("%04x", (int) asciiString.charAt(j)).substring(2,4));
        }

        asciiString = asciiString +String.valueOf(sumSendString).substring(1,3)+ "\r";*/

        asciiString = asciiString + strChkSum + "\r";

        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
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
        getSupportActionBar().setTitle("Programmable Parameter");
    }


    // ==========================================================================

    /**
     * ?????????? ?????? ?????? ?? bluetooth-??????
     */
    private static class BluetoothResponseHandler extends Handler {
        private WeakReference<ProgrammablePrarmeterActivity> mActivity;

        public BluetoothResponseHandler(ProgrammablePrarmeterActivity activity) {
            mActivity = new WeakReference<ProgrammablePrarmeterActivity>(activity);
        }

        public void setTarget(ProgrammablePrarmeterActivity target) {
            mActivity.clear();
            mActivity = new WeakReference<ProgrammablePrarmeterActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            ProgrammablePrarmeterActivity activity = mActivity.get();
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

            Log.e(TAG, "receivedString length = "+ receivedString);
            if(Utils.isStringNotNull(receivedString)) {
                while (receivedString.length() >= 14) {
                    if (receivedString.contains("\r")) {
                        Log.e(TAG, "True contains");
                    }
                    int index0D = receivedString.indexOf("\r");
                    Log.e(TAG, "index0D = " + index0D);
                    String temp = receivedString.substring(0, index0D);
                    Log.e(TAG, "temp = " + temp);
                    if (temp.startsWith("11f1")) {
                        String sum = Utils.calculateChecksumValueNew(temp);
                        Log.e(TAG, "" + sum.substring(2, 4) + " -- " + temp.substring(temp.length() - 2, temp.length()) + " temp = " + temp);

                        if (sum.substring(2, 4).equalsIgnoreCase(temp.substring(temp.length() - 2, temp.length()))) {
                            String locationAddress = temp.substring(6, 8);

                            int data = Integer.parseInt(temp.substring(8, 10), 16);

                            Log.e(TAG, "locationAddress = " + locationAddress + " data = " + data);
//                        Utils.showToastMsg(getActivity(), " Data = "+data +" char =  "+ temp.charAt(index - 1));
                            String lsb = temp.substring(8, 10);
                            String sb = temp.substring(6, 8);
                            String msb = temp.substring(4, 6);
                            String id = lsb + sb + msb;
                            int device = Integer.parseInt(temp.substring(10, 12));
                            Log.e(TAG, "Device ID = " + device);

                            Log.e(TAG, "device = " + device);
                            switch (device) {
                                case 41:
                                    txtEncoderPPR.setText(id);
                                    break;
                                case 42:
                                    txtTravelLenght_1.setText(id);
                                    break;
                                case 43:
                                    txtTravelLenght_2.setText(id);
                                    break;
                                case 44:
                                    txtTravelLenght_3.setText(id);
                                    break;
                                case 45:
                                    txtSlowSpeedDistance_1.setText(id);
                                    break;
                                case 46:
                                    txtSlowSpeedDistance_2.setText(id);
                                    break;
                                case 47:
                                    txtSlowSpeedDistance_3.setText(id);
                                    break;
                                case 48:
                                    txtSlowSpeedDistance_4.setText(id);
                                    break;
                                case 49:
                                    txtDiameterOfMainPulley.setText(id);
                                    break;
                                case 50:
                                    txtGearBoxRatio.setText(id);
                                    break;
                                case 51:
                                    txtCommanUpSlip.setText(id);
                                    break;
                                case 52:
                                    txtCommonDnSlip.setText(id);
                                    break;
                                case 53:
                                    txtRPMSetting_1.setText(id);
                                    break;
                                case 54:
                                    txtRPMSetting_2.setText(id);
                                    break;
                                case 55:
                                    txtRPMSetting_3.setText(id);
                                    break;

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
