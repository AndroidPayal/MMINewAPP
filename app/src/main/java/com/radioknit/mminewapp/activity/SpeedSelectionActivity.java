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


public class SpeedSelectionActivity extends BaseActivity {

    private static final String TAG = "SpeedSelectionActivity";
    private Context mContext;
    ArrayList<String> arrCommandValueList;
    private ArrayAdapter<String> adapter;
    private Button btnSetSpeedCommandSelection;
    private Spinner spinWhenLiftIsStoped;
    private Spinner spinPowerUpMode;
    private Spinner spinPowerUpModeWithClossed;
    private Spinner spinRelevelSpeed;
    private Spinner spinARDMode;
    private Spinner spinHitofSlowDown;
    private Spinner spinDeaccelerationMode;
    private Spinner spinRunSpeedOne;
    private Spinner spinRunSpeedTwo;
    private Spinner spinRunSpeedThree;
    private Spinner spinRunSpeedFour;
    private Button btnViewSpeedCommand;
    private int counter = 0;
    private TextView txtRunSpeedFour;
    private TextView txtWhenLiftIsStoped;
    private TextView txtPowerUpMode;
    private TextView txtPowerUpWithClosed;
    private TextView txtMaintanceSpeed;
    private TextView txtARDMode;
    private TextView txtHitOnSlowDown;
    private TextView txtDesccelarationMode;
    private TextView txtRunSpeedOne;
    private TextView txtRunSpeedTwo;
    private TextView txtRunSpeedThree;
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
    private static SpeedSelectionActivity.BluetoothResponseHandler mHandler;
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

        if (mHandler == null) mHandler = new SpeedSelectionActivity.BluetoothResponseHandler(this);
        else mHandler.setTarget(this);

        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);

        setContentView(R.layout.activity_speed_selection);

        completReceivedString = new StringBuffer();

        generateId();
        createObj();
        registerEvent();

        if (isConnected() && (savedInstanceState != null)) {
            setDeviceName(savedInstanceState.getString(DEVICE_NAME));
        } else getSupportActionBar().setSubtitle(MSG_NOT_CONNECTED);


    }


    private void generateId() {
        btnSetSpeedCommandSelection = (Button) findViewById(R.id.btnSetSpeedCommandSelection);
        spinWhenLiftIsStoped = (Spinner)findViewById(R.id.spinWhenLiftIsStopped);
        spinPowerUpMode = (Spinner)findViewById(R.id.spinPowerUpMode);
        spinPowerUpModeWithClossed = (Spinner)findViewById(R.id.spinPowerUpWithClossed);
        spinRelevelSpeed = (Spinner)findViewById(R.id.spinMaintanceSpeed);
        spinARDMode = (Spinner)findViewById(R.id.spinARDMode);
        spinHitofSlowDown = (Spinner)findViewById(R.id.spinHitOnSlowDown);
        spinDeaccelerationMode = (Spinner)findViewById(R.id.spinDeaccelerationMode);
        spinRunSpeedOne = (Spinner)findViewById(R.id.spinRunSpeedOne);
        spinRunSpeedTwo = (Spinner)findViewById(R.id.spinRunSpeedTwo);
        spinRunSpeedThree = (Spinner)findViewById(R.id.spinRunSpeedThree);
        spinRunSpeedFour = (Spinner)findViewById(R.id.spinRunSpeedFour);
        btnViewSpeedCommand = (Button)findViewById(R.id.btnViewSpeedCommandSelection);

        txtWhenLiftIsStoped = (TextView)findViewById(R.id.tvDefalutWhenLiftIsStopped);
        txtPowerUpMode = (TextView)findViewById(R.id.tvDefalutpowerUpMode);
        txtPowerUpWithClosed = (TextView)findViewById(R.id.tvDefalutPowerUpWithClossed);
        txtMaintanceSpeed = (TextView)findViewById(R.id.tvDefalultMaintanceSpeed);
        txtARDMode = (TextView)findViewById(R.id.tvDefaultARDMode);
        txtHitOnSlowDown = (TextView)findViewById(R.id.tvDefaultHitOnSlowDown);
        txtDesccelarationMode = (TextView)findViewById(R.id.tvDefaultDeaccelarationMode);
        txtRunSpeedOne = (TextView)findViewById(R.id.tvDefaultRunSpeedOne);
        txtRunSpeedTwo = (TextView)findViewById(R.id.tvDefaultRunSpeedTwo);
        txtRunSpeedThree = (TextView)findViewById(R.id.tvDefaultRunSpeedThree);
        txtRunSpeedFour = (TextView)findViewById(R.id.tvDefaultRunSpeedFour);

    }


    private void createObj() {
        mContext = SpeedSelectionActivity.this;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        arrCommandValueList = new ArrayList<String>();


        for(int i = 0; i< 8; i++){
            arrCommandValueList.add(String.valueOf(i));
        }

        adapter = new ArrayAdapter<String>(mContext, R.layout.list_item, arrCommandValueList);

        spinWhenLiftIsStoped.setAdapter(adapter);
        spinPowerUpMode.setAdapter(adapter);
        spinPowerUpModeWithClossed.setAdapter(adapter);
        spinRelevelSpeed.setAdapter(adapter);
        spinARDMode.setAdapter(adapter);
        spinHitofSlowDown.setAdapter(adapter);
        spinDeaccelerationMode.setAdapter(adapter);
        spinRunSpeedOne.setAdapter(adapter);
        spinRunSpeedTwo.setAdapter(adapter);
        spinRunSpeedThree.setAdapter(adapter);
        spinRunSpeedFour.setAdapter(adapter);

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
        btnSetSpeedCommandSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()){
                    callWhenLiftIsStoped();
                    delay();
                    callPowerUpMode();
                    delay();
                    callPowerUpModeWithClossed();
                    delay();
                    callMaintanceSpeed();
                    delay();
                    callARDMode();
                    delay();
                    callHotOfSlow();
                    delay();
                    callDeaccelrationMode();
                    delay();
                    callRunSpeedOne();
                    delay();
                    callRunSpeedTwo();
                    delay();
                    callRunSpeedThree();
                    delay();
                    callRunSpeedFour();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnViewSpeedCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completReceivedString.setLength(0);
                counter = 0;
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
                        if (counter == 0) {
                            callViewSpeedSelectionCommand(counter);
                            delay();
                            counter++;
                        } else if (counter == 1) {
                                callViewSpeedSelectionCommand(counter);
                            delay();
                                counter++;
                        } else if (counter == 2) {
                                callViewSpeedSelectionCommand(counter);
                            delay();
                                counter++;
                        } else if (counter == 3) {
                                callViewSpeedSelectionCommand(counter);
                            delay();
                                counter++;
                        } else if (counter == 4) {
                                callViewSpeedSelectionCommand(counter);
                            delay();
                                counter++;
                        } else if (counter == 5) {
                                callViewSpeedSelectionCommand(counter);
                            delay();
                                counter++;
                        } else if (counter == 6) {
                                callViewSpeedSelectionCommand(counter);
                            delay();
                                counter++;
                        } else if (counter == 7) {
                                callViewSpeedSelectionCommand(counter);
                            delay();
                                counter++;
                        } else if (counter == 8) {
                                callViewSpeedSelectionCommand(counter);
                            delay();
                                counter++;
                        } else if (counter == 9) {
                                callViewSpeedSelectionCommand(counter);
                            delay();
                                counter++;
                        }else if (counter == 10) {
                                callViewSpeedSelectionCommand(counter);
                            delay();
                                counter++;
                        }else if(counter == 11){
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
        });
    }



    public void callWhenLiftIsStoped() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 240;
        int a5 = Integer.parseInt(spinWhenLiftIsStoped.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
            connector.write(asciiString.getBytes());
        }
        /*else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }*/
    }

    public void callPowerUpMode() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 241;
        int a5 = Integer.parseInt(spinPowerUpMode.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
            connector.write(asciiString.getBytes());
        }
        /*else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }*/
    }

    public void callPowerUpModeWithClossed() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 242;
        int a5 = Integer.parseInt(spinPowerUpModeWithClossed.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
            connector.write(asciiString.getBytes());
        }
        /*else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }*/
    }

    public void callMaintanceSpeed() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 243;
        int a5 = Integer.parseInt(spinRelevelSpeed.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
            connector.write(asciiString.getBytes());
        }
        /*else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }*/
    }

    public void callARDMode() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 244;
        int a5 = Integer.parseInt(spinARDMode.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
            connector.write(asciiString.getBytes());
        }
        /*else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }*/

    }

    public void callHotOfSlow() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 245;
        int a5 = Integer.parseInt(spinHitofSlowDown.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
            connector.write(asciiString.getBytes());
        }
        /*else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }*/

    }

    public void callDeaccelrationMode() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 246;
        int a5 = Integer.parseInt(spinDeaccelerationMode.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
            connector.write(asciiString.getBytes());
        }
        /*else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }*/

    }

    public void callRunSpeedOne() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 247;
        int a5 = Integer.parseInt(spinRunSpeedOne.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
            connector.write(asciiString.getBytes());
        }
       /* else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }
*/
    }

    public void callRunSpeedTwo() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 248;
        int a5 = Integer.parseInt(spinRunSpeedTwo.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
            connector.write(asciiString.getBytes());
        }
       /* else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }*/
    }

    public void callRunSpeedThree() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 249;
        int a5 = Integer.parseInt(spinRunSpeedThree.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
            connector.write(asciiString.getBytes());
        }
        /*else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }*/

    }
    public void callRunSpeedFour() {

        int a1 = 18;
        int a2 = 17;
        int a3 = 112;
        int a4 = 250;
        int a5 = Integer.parseInt(spinRunSpeedFour.getSelectedItem().toString());
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
        asciiString = asciiString + strChkSum + "\r";
        Log.e(TAG, "asciiString = "+ asciiString);

        if (isConnected()) {
            connector.write(asciiString.getBytes());
        }
        /*else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }*/
    }


    private void callViewSpeedSelectionCommand(int value) {
        int a1 = 18;
        int a2 = 17;
        int a3 = 80;
        int a4 = 240 + value;
        int a5 = 00;
        int a6 = 00;

        int[] sendValChkSum={a1, a2, a3, a4, a5, a6};

        String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);

        int sum = a1 + a2 + a3 + a4 + a5;
        String sumHex = String.format("%04x", sum);

        /*String msb = sumHex.substring(1, 2);
        String lsb = sumHex.substring(2, 4);
        int a7 = Integer.parseInt(lsb, 16);
        int msb2 = (Integer.parseInt(msb) | 50);
        int a8 = Integer.parseInt(String.valueOf(msb2),16);

        byte[] br2 = {(byte) a1, (byte) a2, (byte) a3, (byte) a4, (byte) a5, (byte) a6};
        byte[] br1 = {(byte) a1, (byte) a2, (byte) a3, (byte) a4, (byte) a5, (byte) a6, (byte) a7, (byte) a8};
*/
        String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4) ;
/*        int sumSendString  = 0;
        for(int i = 0; i<asciiString.length(); i++){
            sumSendString = sumSendString + Integer.parseInt(String.format("%04x", (int) asciiString.charAt(i)).substring(2,4));
        }
        asciiString = asciiString +String.valueOf(sumSendString).substring(1,3)+ "\r";*/
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
        getSupportActionBar().setTitle("Speed Selection");
    }

    // ==========================================================================

    /**
     * ?????????? ?????? ?????? ?? bluetooth-??????
     */
    private static class BluetoothResponseHandler extends Handler {
        private WeakReference<SpeedSelectionActivity> mActivity;

        public BluetoothResponseHandler(SpeedSelectionActivity activity) {
            mActivity = new WeakReference<SpeedSelectionActivity>(activity);
        }

        public void setTarget(SpeedSelectionActivity target) {
            mActivity.clear();
            mActivity = new WeakReference<SpeedSelectionActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            SpeedSelectionActivity activity = mActivity.get();
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
                    if (temp.startsWith("111250")) {
                        String sum = Utils.calculateChecksumValueNew(temp);
                        Log.e(TAG, "" + sum.substring(2, 4) + " -- " + temp.substring(temp.length() - 2, temp.length()) + " temp = " + temp);

                        if (sum.substring(2, 4).equalsIgnoreCase(temp.substring(temp.length() - 2, temp.length()))) {
                            String locationAddress = temp.substring(6, 8);

                            int data = Integer.parseInt(temp.substring(8, 10), 16);

                            Log.e(TAG, "locationAddress = " + locationAddress + " data = " + data);
//                        Utils.showToastMsg(getActivity(), " Data = "+data +" char =  "+ temp.charAt(index - 1));
                            if (locationAddress.equalsIgnoreCase("F0")) {
                                txtWhenLiftIsStoped.setText(data + " ");
                            } else if (locationAddress.equalsIgnoreCase("F1")) {
                                txtPowerUpMode.setText(data + "");
                            } else if (locationAddress.equalsIgnoreCase("F2")) {
                                txtPowerUpWithClosed.setText(data + " ");
                            } else if (locationAddress.equalsIgnoreCase("F3")) {
                                txtMaintanceSpeed.setText(data + " ");
                            } else if (locationAddress.equalsIgnoreCase("F4")) {
                                txtARDMode.setText(data + "  ");
                            } else if (locationAddress.equalsIgnoreCase("F5")) {
                                txtHitOnSlowDown.setText(data + "  ");
                            } else if (locationAddress.equalsIgnoreCase("F6")) {
                                txtDesccelarationMode.setText(data + "  ");
                            } else if (locationAddress.equalsIgnoreCase("F7")) {
                                txtRunSpeedOne.setText(data + "  ");
                            } else if (locationAddress.equalsIgnoreCase("F8")) {
                                txtRunSpeedTwo.setText(data + "  ");
                            } else if (locationAddress.equalsIgnoreCase("F9")) {
                                txtRunSpeedThree.setText(data + " ");
                            } else if (locationAddress.equalsIgnoreCase("FA")) {
                                txtRunSpeedFour.setText(data + " ");
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
