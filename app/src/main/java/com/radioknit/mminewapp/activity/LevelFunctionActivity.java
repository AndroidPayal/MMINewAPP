package com.radioknit.mminewapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LevelFunctionActivity extends BaseActivity {

    private static final String TAG = "LevelFunctionActivity";
    private Context mContext;
    ArrayList<String> arrCommandValueList;
    private Spinner spinFloorNumber;
    private ArrayAdapter<String> adapter;
    private Button btnSetSlipValue;
    private Button btnViewSlipValue;
    private TextView txtViewValue;
    private EditText edtSetSlipValue;
    private RadioGroup rdogpUpDownSlip;
    private Button btnAutoLevelFunction;
    private Button btnSetLevelFunction;
    private Button btnViewLevelFunction;
    private LinearLayout llViewLevelFunction;
    //private int counter = 0;
    private RelativeLayout rlViewLevelFunctionData;
    private ScrollView svSetLevelFunctionData;
    private Button btnSetValues;
    private Button btnGetValues;
    private StringBuffer mOutStringBuffer;
    public static final int FALGUPSLIP = 1;
    public static final int FLAGDOWNSLIP = 2;
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
    private static LevelFunctionActivity.BluetoothResponseHandler mHandler;
    DeviceListActivity deviceListActivity;

    private boolean hexMode, needClean;
    private boolean show_timings, show_direction;
    private String command_ending;
    private String deviceName;
    private int counter = 1;
    private ProgressDialog pd;
    private StringBuffer completReceivedString, posDataString;
    int cntViewCmdAll=0, cntViewCmd=1;
    int upSlipValue, dnSlipValue, flrValue=0;
    String chkViewLocAdd="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.settings_activity, false);

        if (mHandler == null) mHandler = new LevelFunctionActivity.BluetoothResponseHandler(this);
        else mHandler.setTarget(this);

        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);

        setContentView(R.layout.activity_level_function);

        completReceivedString = new StringBuffer();
        posDataString = new StringBuffer();

        generateId();
        createObj();
        registerEvent();


        if (isConnected() && (savedInstanceState != null)) {
            setDeviceName(savedInstanceState.getString(DEVICE_NAME));
        } else getSupportActionBar().setSubtitle(MSG_NOT_CONNECTED);

    }


    private void registerEvent() {
        final Handler ha = new Handler();
        btnSetSlipValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSetLevelFunction();
            }
        });


        btnAutoLevelFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAutoLevelFunction();
            }
        });

        btnViewSlipValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callViewLevelFunction();
            }
        });

        btnSetValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svSetLevelFunctionData.setVisibility(View.VISIBLE);
                rlViewLevelFunctionData.setVisibility(View.GONE);
            }
        });

        btnGetValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svSetLevelFunctionData.setVisibility(View.GONE);
                rlViewLevelFunctionData.setVisibility(View.VISIBLE);
            }
        });

        btnViewLevelFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    cntViewCmdAll=0;
                    posDataString.setLength(0);
                    cntViewCmd=1;
                    flrValue=0;
                    pd = ProgressDialog.show(mContext, "", "Please wait", true);
                    llViewLevelFunction.removeAllViews();
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View view = inflater.inflate(R.layout.level_function_item, null);
                    TextView tvFloorNo = (TextView) view.findViewById(R.id.tvLevelFunctionFlrNum);
                    TextView tvFloorPos = (TextView) view.findViewById(R.id.tvLevelFunctionFlrPos);
                    TextView tvFloorUpSlip = (TextView) view.findViewById(R.id.tvLevelFunctionUpSlip);
                    TextView tvFloorDnSlip = (TextView) view.findViewById(R.id.tvLevelFunctionDnSlip);
                    tvFloorNo.setText("Flr");
                    tvFloorPos.setText("Pos");
                    tvFloorUpSlip.setText("Up");
                    tvFloorDnSlip.setText("Down");
                    llViewLevelFunction.addView(view);
                    callViewAllLevelFunction();
                 }
                else {
                    Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void createObj() {
        mContext = LevelFunctionActivity.this;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        arrCommandValueList = new ArrayList<String>();

        for (int i = 0; i <= 31; i++) {
            arrCommandValueList.add(String.valueOf(i));
        }

        adapter = new ArrayAdapter<String>(mContext, R.layout.list_item, arrCommandValueList);

        spinFloorNumber.setAdapter(adapter);
    }

    private void generateId() {
        btnSetSlipValue = (Button)  findViewById(R.id.btnSetSlipValue);
        spinFloorNumber = (Spinner) findViewById(R.id.levelFrag_floorNumber);
        edtSetSlipValue = (EditText)findViewById(R.id.edtLevelFunctionSlipValue);
        rdogpUpDownSlip = (RadioGroup)findViewById(R.id.rdogpUpDownSlip);
        btnAutoLevelFunction = (Button)findViewById(R.id.btnAutoLevelFunction);
        btnViewSlipValue = (Button) findViewById(R.id.btnViewValue);
        txtViewValue = (TextView) findViewById(R.id.textViewValue);

        btnSetValues = (Button) findViewById(R.id.btnLevelFunctionSetValues);
        btnGetValues = (Button)findViewById(R.id.btnLevelFunctionViewValues);
        llViewLevelFunction = (LinearLayout)findViewById(R.id.llLevelFunction);
        svSetLevelFunctionData = (ScrollView) findViewById(R.id.svSetLevelFunctionValue);
        rlViewLevelFunctionData = (RelativeLayout)findViewById(R.id.rlViewLevelFunctionValue);
        btnViewLevelFunction = (Button)findViewById(R.id.btnViewLevelFunction);
    }

    int a1 = 18;
    int a2 = 17;
    int a3 = 112;
    int a4;
    int a5;
    int a6 = 00;
    int flag;

    public void callSetLevelFunction() {

        switch (rdogpUpDownSlip.getCheckedRadioButtonId()){
            case R.id.rdoUpSlip :
                flag = FALGUPSLIP;
                break;
            case R.id.rdoDownSlip :
                flag = FLAGDOWNSLIP;
                break;
        }

        int t = spinFloorNumber.getSelectedItemPosition();
//        int t = Integer.valueOf(String.valueOf(spinFloorNumber.getSelectedItemPosition()) , 16);
        Log.e(TAG, "t = "+ t);
        if(t == 0){
            if(flag == FALGUPSLIP){
                a4 = 02;
            }else {
                a4 = 03;
            }
        }else if(t == 1){
            if(flag == FALGUPSLIP){
                a4 = 06;
            }else {
                a4 = 07;
            }
        }else if(t == 2){
            if(flag == FALGUPSLIP){
                a4 = 10;
            }else {
                a4 = 11;
            }
        }else if(t == 3){
            if(flag == FALGUPSLIP){
                a4 = 14;
            }else {
                a4 = 15;
            }
        }else if(t == 4){
            if(flag == FALGUPSLIP){
                a4 = 18;
            }else {
                a4 = 19;
            }
        }else if(t == 5){
            if(flag == FALGUPSLIP){
                a4 = 22;
            }else {
                a4 = 23;
            }
        }else if(t == 6){
            if(flag == FALGUPSLIP){
                a4 = 26;
            }else {
                a4 = 27;
            }
        }else if(t == 7){
            if(flag == FALGUPSLIP){
                a4 = 30;
            }else {
                a4 = 31;
            }
        }else if(t == 8){
            if(flag == FALGUPSLIP){
                a4 = 34;
            }else {
                a4 = 35;
            }
        }else if(t == 9){
            if(flag == FALGUPSLIP){
                a4 = 38;
            }else {
                a4 = 39;
            }
        }else if(t == 10){
            if(flag == FALGUPSLIP){
                a4 = 42;
            }else {
                a4 = 43;
            }
        }else if(t == 11){
            if(flag == FALGUPSLIP){
                a4 = 46;
            }else {
                a4 = 47;
            }
        }else if(t == 12){
            if(flag == FALGUPSLIP){
                a4 = 50;
            }else {
                a4 = 51;
            }
        }else if(t == 13){
            if(flag == FALGUPSLIP){
                a4 = 54;
            }else {
                a4 = 55;
            }
        }else if(t == 14){
            if(flag == FALGUPSLIP){
                a4 = 58;
            }else {
                a4 = 59;
            }
        }else if(t == 15){
            if(flag == FALGUPSLIP){
                a4 = 62;
            }else {
                a4 = 63;
            }
        }else if(t == 16){
            if(flag == FALGUPSLIP){
                a4 = 66;
            }else {
                a4 = 67;
            }
        }else if(t == 17){
            if(flag == FALGUPSLIP){
                a4 = 70;
            }else {
                a4 = 71;
            }
        }else if(t == 18){
            if(flag == FALGUPSLIP){
                a4 = 74;
            }else {
                a4 = 75;
            }
        }else if(t == 19){
            if(flag == FALGUPSLIP){
                a4 = 78;
            }else {
                a4 = 79;
            }
        }else if(t == 20){
            if(flag == FALGUPSLIP){
                a4 = 82;
            }else {
                a4 = 83;
            }
        }else if(t == 21){
            if(flag == FALGUPSLIP){
                a4 = 86;
            }else {
                a4 = 87;
            }
        }else if(t == 22){
            if(flag == FALGUPSLIP){
                a4 = 90;
            }else {
                a4 = 91;
            }
        }else if(t == 23){
            if(flag == FALGUPSLIP){
                a4 = 94;
            }else {
                a4 = 95;
            }
        }else if(t == 24){
            if(flag == FALGUPSLIP){
                a4 = 98;
            }else {
                a4 = 99;
            }
        }else if(t == 25){
            if(flag == FALGUPSLIP){
                a4 = 102;
            }else {
                a4 = 103;
            }
        }else if(t == 26){
            if(flag == FALGUPSLIP){
                a4 = 106;
            }else {
                a4 = 107;
            }
        }else if(t == 27){
            if(flag == FALGUPSLIP){
                a4 = 110;
            }else {
                a4 = 111;
            }
        }else if(t == 28){
            if(flag == FALGUPSLIP){
                a4 = 114;
            }else {
                a4 = 115;
            }
        }else if(t == 29){
            if(flag == FALGUPSLIP){
                a4 = 118;
            }else {
                a4 = 119;
            }
        }else if(t == 30){
            if(flag == FALGUPSLIP){
                a4 = 122;
            }else {
                a4 = 123;
            }
        }else if(t == 31){
            if(flag == FALGUPSLIP){
                a4 = 126;
            }else {
                a4 = 127;
            }
        }

        if(Utils.isStringNotNull(edtSetSlipValue.getText().toString())) {
            if (Integer.parseInt(edtSetSlipValue.getText().toString()) > 255) {
                Utils.showToastMsg(mContext, "Slip value must be in range of 0-255");
            } else {
                a5 = Integer.parseInt(edtSetSlipValue.getText().toString());
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
        }else {
            Utils.showToastMsg(mContext,"Please Enter The Level Value");
        }
    }

    private void callAutoLevelFunction() {
        if (!isConnected()) {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LevelFunctionActivity.this);
        alertDialogBuilder.setTitle("Confirmation");
        //alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setMessage("Are you sure, you want to auto level?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                int a1 = 18;
                int a2 = 17;
                int a3 = 76;
                int a4 = 00;
                int a5 = 00;
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

        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();





    }

    String chkLocAdd="";
    private void callViewLevelFunction(){
        completReceivedString.setLength(0);
        int a1 = 18;
        int a2 = 17;
        int a3 = 80;
        int a4 = 00;
        int a5 = 00;
        int a6 = 00;

        switch (rdogpUpDownSlip.getCheckedRadioButtonId()){
            case R.id.rdoUpSlip :
                flag = FALGUPSLIP;
                break;
            case R.id.rdoDownSlip :
                flag = FLAGDOWNSLIP;
                break;
        }

        int t = spinFloorNumber.getSelectedItemPosition();
//        int t = Integer.valueOf(String.valueOf(spinFloorNumber.getSelectedItemPosition()) , 16);
        Log.e(TAG, "t = "+ t);
        if(t == 0){
            if(flag == FALGUPSLIP){
                a4 = 02;
            }else {
                a4 = 03;
            }
        }else if(t == 1){
            if(flag == FALGUPSLIP){
                a4 = 06;
            }else {
                a4 = 07;
            }
        }else if(t == 2){
            if(flag == FALGUPSLIP){
                a4 = 10;
            }else {
                a4 = 11;
            }
        }else if(t == 3){
            if(flag == FALGUPSLIP){
                a4 = 14;
            }else {
                a4 = 15;
            }
        }else if(t == 4){
            if(flag == FALGUPSLIP){
                a4 = 18;
            }else {
                a4 = 19;
            }
        }else if(t == 5){
            if(flag == FALGUPSLIP){
                a4 = 22;
            }else {
                a4 = 23;
            }
        }else if(t == 6){
            if(flag == FALGUPSLIP){
                a4 = 26;
            }else {
                a4 = 27;
            }
        }else if(t == 7){
            if(flag == FALGUPSLIP){
                a4 = 30;
            }else {
                a4 = 31;
            }
        }else if(t == 8){
            if(flag == FALGUPSLIP){
                a4 = 34;
            }else {
                a4 = 35;
            }
        }else if(t == 9){
            if(flag == FALGUPSLIP){
                a4 = 38;
            }else {
                a4 = 39;
            }
        }else if(t == 10){
            if(flag == FALGUPSLIP){
                a4 = 42;
            }else {
                a4 = 43;
            }
        }else if(t == 11){
            if(flag == FALGUPSLIP){
                a4 = 46;
            }else {
                a4 = 47;
            }
        }else if(t == 12){
            if(flag == FALGUPSLIP){
                a4 = 50;
            }else {
                a4 = 51;
            }
        }else if(t == 13){
            if(flag == FALGUPSLIP){
                a4 = 54;
            }else {
                a4 = 55;
            }
        }else if(t == 14){
            if(flag == FALGUPSLIP){
                a4 = 58;
            }else {
                a4 = 59;
            }
        }else if(t == 15){
            if(flag == FALGUPSLIP){
                a4 = 62;
            }else {
                a4 = 63;
            }
        }else if(t == 16){
            if(flag == FALGUPSLIP){
                a4 = 66;
            }else {
                a4 = 67;
            }
        }else if(t == 17){
            if(flag == FALGUPSLIP){
                a4 = 70;
            }else {
                a4 = 71;
            }
        }else if(t == 18){
            if(flag == FALGUPSLIP){
                a4 = 74;
            }else {
                a4 = 75;
            }
        }else if(t == 19){
            if(flag == FALGUPSLIP){
                a4 = 78;
            }else {
                a4 = 79;
            }
        }else if(t == 20){
            if(flag == FALGUPSLIP){
                a4 = 82;
            }else {
                a4 = 83;
            }
        }else if(t == 21){
            if(flag == FALGUPSLIP){
                a4 = 86;
            }else {
                a4 = 87;
            }
        }else if(t == 22){
            if(flag == FALGUPSLIP){
                a4 = 90;
            }else {
                a4 = 91;
            }
        }else if(t == 23){
            if(flag == FALGUPSLIP){
                a4 = 94;
            }else {
                a4 = 95;
            }
        }else if(t == 24){
            if(flag == FALGUPSLIP){
                a4 = 98;
            }else {
                a4 = 99;
            }
        }else if(t == 25){
            if(flag == FALGUPSLIP){
                a4 = 102;
            }else {
                a4 = 103;
            }
        }else if(t == 26){
            if(flag == FALGUPSLIP){
                a4 = 106;
            }else {
                a4 = 107;
            }
        }else if(t == 27){
            if(flag == FALGUPSLIP){
                a4 = 110;
            }else {
                a4 = 111;
            }
        }else if(t == 28){
            if(flag == FALGUPSLIP){
                a4 = 114;
            }else {
                a4 = 115;
            }
        }else if(t == 29){
            if(flag == FALGUPSLIP){
                a4 = 118;
            }else {
                a4 = 119;
            }
        }else if(t == 30){
            if(flag == FALGUPSLIP){
                a4 = 122;
            }else {
                a4 = 123;
            }
        }else if(t == 31){
            if(flag == FALGUPSLIP){
                a4 = 126;
            }else {
                a4 = 127;
            }
        }


        //a5 = Integer.parseInt(edtSetSlipValue.getText().toString());
                int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
                String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
                String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
                asciiString = asciiString + strChkSum + "\r";
                chkLocAdd = String.format("%04x", a4).substring(2,4);
                Log.e(TAG, "asciiString = "+ asciiString);
                Log.e(TAG, "chkLocAdd = "+ chkLocAdd);

                if (isConnected()) {
                    connector.write(asciiString.getBytes());
                }
                else {
                    Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
                }




    }


    private void callViewAllLevelFunction() {

        completReceivedString.setLength(0);
        int a1 = 18;
        int a2 = 17;
        int a3 = 80;
        int a4;
        int a5 = 00;
        int a6 = 00;
        if(cntViewCmdAll<128){
            a4 = cntViewCmdAll;
            int[] sendValChkSum={a1, a2, a3, a4, a5, a6};
            String strChkSum= CalculateCheckSum.calculateChkSum(sendValChkSum);
            String asciiString  = String.format("%04x", a1).substring(2,4)+String.format("%04x", a2).substring(2,4)+String.format("%04x", a3).substring(2,4)+String.format("%04x", a4).substring(2,4)+String.format("%04x", a5).substring(2,4)+String.format("%04x", a6).substring(2,4);
            asciiString = asciiString + strChkSum + "\r";
            chkViewLocAdd = String.format("%04x", a4).substring(2,4);
            Log.e(TAG, "asciiString = "+ asciiString);
            Log.e(TAG, "chkViewLocAdd = "+ chkViewLocAdd);

            if (isConnected()) {
                connector.write(asciiString.getBytes());
            }
            else {
                Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

        }
        else {
            pd.dismiss();
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
//        logTextView.setText(msg);
//        if (clean) commandEditText.setText("");
    }


    // =========================================================================
    public void appendLog1(String message, boolean hexMode, boolean outgoing, boolean clean) {

        StringBuffer msg = new StringBuffer();

        completReceivedString.append(message);
        String receivedString = completReceivedString.toString();
        Log.e(TAG, "receivedString = "+ receivedString);
        if(receivedString.contains("111250")){
            try {
                int index=receivedString.lastIndexOf("111250");
                //Log.e(TAG, "Loacation data = "+ receivedString.substring(index+10,index+12));
                if(receivedString.substring(index+6,index+8).equals(chkLocAdd) && receivedString.substring(index+10,index+12).equals("52")){
                    String strDecData = receivedString.substring(index+8,index+10);
                    int decimal = Integer.parseInt(strDecData, 16);
                    txtViewValue.setText(String.format("%s",decimal));
                    Log.e(TAG, "decimal = "+ decimal);
                }
                if(receivedString.substring(index+6,index+8).equals(chkViewLocAdd) && receivedString.substring(index+10,index+12).equals("52")){
                    String strDecData = receivedString.substring(index+8,index+10);
                    if(cntViewCmd<3){
                        posDataString.append(strDecData);
                        cntViewCmdAll=cntViewCmdAll+1;
                        cntViewCmd=cntViewCmd+1;
                        callViewAllLevelFunction();
                    }
                    else if(cntViewCmd==3){
                        upSlipValue = Integer.parseInt(strDecData, 16);
                        Log.e(TAG, "upSlipValue = "+ upSlipValue);
                        cntViewCmdAll=cntViewCmdAll+1;
                        cntViewCmd=cntViewCmd+1;
                        callViewAllLevelFunction();
                    }
                    else if(cntViewCmd==4){
                        dnSlipValue = Integer.parseInt(strDecData, 16);
                        Log.e(TAG, "dnSlipValue = "+ dnSlipValue);
                        int posData = Integer.parseInt(posDataString.toString(), 16);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View view = inflater.inflate(R.layout.level_function_item, null);
                        TextView tvFloorNo = (TextView) view.findViewById(R.id.tvLevelFunctionFlrNum);
                        TextView tvFloorPos = (TextView) view.findViewById(R.id.tvLevelFunctionFlrPos);
                        TextView tvFloorUpSlip = (TextView) view.findViewById(R.id.tvLevelFunctionUpSlip);
                        TextView tvFloorDnSlip = (TextView) view.findViewById(R.id.tvLevelFunctionDnSlip);
                        tvFloorNo.setText(String.format("%s",flrValue));
                        tvFloorPos.setText(String.format("%s",posData));
                        tvFloorUpSlip.setText(String.format("%s",upSlipValue));
                        tvFloorDnSlip.setText(String.format("%s",dnSlipValue));
                        llViewLevelFunction.addView(view);
                        cntViewCmdAll=cntViewCmdAll+1;
                        cntViewCmd=1;
                        flrValue=flrValue+1;
                        posDataString.setLength(0);
                        callViewAllLevelFunction();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }
    // =========================================================================

    void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        getSupportActionBar().setSubtitle(deviceName);
        getSupportActionBar().setTitle("Level Function");
    }


    // ==========================================================================

    /**
     * ?????????? ?????? ?????? ?? bluetooth-??????
     */
    private static class BluetoothResponseHandler extends Handler {
        private WeakReference<LevelFunctionActivity> mActivity;

        public BluetoothResponseHandler(LevelFunctionActivity activity) {
            mActivity = new WeakReference<LevelFunctionActivity>(activity);
        }

        public void setTarget(LevelFunctionActivity target) {
            mActivity.clear();
            mActivity = new WeakReference<LevelFunctionActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            LevelFunctionActivity activity = mActivity.get();
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
//                        final String readMessage = (String) msg.obj;
//                        Log.e(TAG, " received msg = "+ readMessage);

                        /*String readMessage = null;
                        try {
                            byte[] readBuf = (byte[]) msg.obj;
                            readMessage = new String(readBuf, 0, msg.arg1,"ISO-8859-1");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, " received msg = "+ readMessage);
                        if (readMessage != null) {
                            activity.appendLog1(readMessage, false, false, activity.needClean);
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
    // ==========================================================================

}
