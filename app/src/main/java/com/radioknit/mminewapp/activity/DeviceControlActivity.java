package com.radioknit.mminewapp.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.radioknit.mminewapp.CustomKeyboard;
import com.radioknit.mminewapp.DeviceData;
import com.radioknit.mminewapp.R;
import com.radioknit.mminewapp.Utils;
import com.radioknit.mminewapp.bluetooth.DeviceConnector;
import com.radioknit.mminewapp.bluetooth.DeviceListActivity;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;



public final class DeviceControlActivity extends BaseActivity {

    private static final String DEVICE_NAME = "DEVICE_NAME";
    private static final String LOG = "LOG";
    private static final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss.SSS");
    private static String MSG_NOT_CONNECTED;
    private static String MSG_CONNECTING;
    private static String MSG_CONNECTED;
    private static DeviceConnector connector;
    private static BluetoothResponseHandler mHandler;
    private TextView logTextView,logTextView1;
    private EditText commandEditText;
    int maxLength = 2;
    CustomKeyboard mCustomKeyboard;
    // ????????? ??????????
    private boolean hexMode, needClean;
    private boolean show_timings, show_direction;
    private String command_ending;
    private String deviceName;
    int len;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.settings_activity, false);

        if (mHandler == null) mHandler = new BluetoothResponseHandler(this);
        else mHandler.setTarget(this);

        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);

        setContentView(R.layout.activity_terminal);
        if (isConnected() && (savedInstanceState != null)) {
            setDeviceName(savedInstanceState.getString(DEVICE_NAME));
        } else getSupportActionBar().setSubtitle(MSG_NOT_CONNECTED);

        this.logTextView = (TextView) findViewById(R.id.log_textview);

        this.logTextView1 = (TextView) findViewById(R.id.log_textview_R);
        this.logTextView1.setMovementMethod(new ScrollingMovementMethod());
        if (savedInstanceState != null)
            logTextView1.setText(savedInstanceState.getString(LOG));

        this.commandEditText = (EditText) findViewById(R.id.command_edittext);
        mCustomKeyboard= new CustomKeyboard(this, R.id.keyboardview, R.xml.hexkbd );
        mCustomKeyboard.registerEditText(R.id.command_edittext);

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        commandEditText.setFilters(FilterArray);

        this.commandEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    return true;
                }
                return false;
            }
        });

        this.commandEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

    }
    // ==========================================================================
    @Override
    public void onBackPressed() {

        if( mCustomKeyboard.isCustomKeyboardVisible() )
            mCustomKeyboard.hideCustomKeyboard();
        else this.finish();
    }
    // ============================================================================
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DEVICE_NAME, deviceName);
        if (logTextView != null) {
            final String log = logTextView.getText().toString();
            outState.putString(LOG, log);
        }
    }
    // ============================================================================


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

        new MenuInflater(this).inflate(R.menu.device_control_activity, menu);

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

            case R.id.menu_clear:
                if (logTextView != null) logTextView.setText("");
                if (logTextView1 != null) logTextView1.setText("");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // ============================================================================


    @Override
    public void onStart() {
        super.onStart();

        final String mode = Utils.getPrefence(this, getString(R.string.pref_commands_mode));
        this.hexMode = mode.equals("HEX");
//
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

    public void viewCommand(View view) {

    String commandString = (commandEditText.getText().toString());
        if (commandString.equals("")){
            Toast.makeText(getApplicationContext(), "Enter Data", Toast.LENGTH_SHORT).show();
            return;
        }
        if (commandString.length()!=2){
            Toast.makeText(getApplicationContext(), "Enter 2 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        int i1 = 73;
        int i2 = 73;
        int i3 = Integer.parseInt(commandString, 16);
        int cmd = i3 | 128;
        int i4 = 0;
        int i5 = 0;
        int i8 = 13;

        int add = i1 + i2 + cmd + i4 + i5;

        int i6 = add & 15;
        String i6_dec = decimal_value(i6);

        char s = i6_dec.charAt(0);
        int digit5 = (int) s;

        int i7 = (add & 240) >> 4;
        String i7_dec = decimal_value(i7);

        char s1 = i7_dec.charAt(0);
        int digit6 = (int) s1;


        byte[] myList = {73, 73, (byte) cmd, 0, 0, (byte) digit5, (byte) digit6, 13};

        byte[] command = myList;

        StringBuffer buffer = new StringBuffer();
        buffer.append(i1).append(i2).append(cmd).append(i4).append(i5).
                append(digit5).append(digit6).append(i8);

        if (isConnected()) {
            connector.write(command);

            appendLog(commandString, hexMode, true, needClean);
        }
        else {
            Toast.makeText(getApplicationContext(), "Connect to the device", Toast.LENGTH_SHORT).show();
        }

    }
    public static String decimal_value(int val) {
        String va = null;
        if (val == 10) {
            va = "A";
        } else if (val == 11) {
            va = "B";
        } else if (val ==12 ) {
            va = "C";
        } else if (val ==13 ) {
            va ="D";
        } else if (val ==14 ) {
            va = "E";
        } else if (val == 15) {
            va = "F";
        } else if (val == 0) {
            va = "0";
        } else if (val == 1) {
            va = "1";
        } else if (val == 2) {
            va = "2";
        } else if (val == 3) {
            va = "3";
        } else if (val == 4) {
            va = "4";
        } else if (val == 5) {
            va = "5";
        } else if (val == 6) {
            va = "6";
        } else if (val == 7) {
            va = "7";
        } else if (val == 8) {
            va = "8";
        } else if (val == 9) {
            va = "9";
        }
        return va;
    }
    // ==========================================================================

    // ==========================================================================

    /**

     * @param message  - ????? ??? ???????????
     * @param outgoing - ??????????? ????????
     */
    public void appendLog(String message, boolean hexMode, boolean outgoing, boolean clean) {

        StringBuffer msg = new StringBuffer();
        /*if (show_timings) msg.append("[").append(timeformat.format(new Date())).append("]");
        if (show_direction) {
            final String arrow = (outgoing ? " << " : " >> ");
            msg.append(arrow);
        } else msg.append(" ");*/

        //msg.append(hexMode ? Utils.printHex(message) : message);
        msg.append(hexMode ? Utils.toHex1(message) : message);
        if (outgoing) msg.append('\n');

        //logTextView.append(msg);
        logTextView.setText(msg);

       /* final int scrollAmount = logTextView.getLayout().getLineTop(logTextView.getLineCount()) - logTextView.getHeight();
        if (scrollAmount > 0)
            logTextView.scrollTo(0, scrollAmount);
        else logTextView.scrollTo(0, 0);
       */
        if (clean) commandEditText.setText("");
    }


    // =========================================================================
    public void appendLog1(String message, boolean hexMode, boolean outgoing, boolean clean) {

        StringBuffer msg = new StringBuffer();
        /*if (show_timings) msg.append("[").append(timeformat.format(new Date())).append("]");
        if (show_direction) {
            final String arrow = (outgoing ? " << " : " >> ");
            msg.append(arrow);
        } else msg.append(" ");*/

        //msg.append(hexMode ? Utils.printHex(message) : message);
        msg.append(hexMode ? Utils.toHex1(message) : message);
        if (outgoing) msg.append("\n");

        logTextView1.append(msg);
        //logTextView1.setText(msg);

       /* final int scrollAmount = logTextView.getLayout().getLineTop(logTextView.getLineCount()) - logTextView.getHeight();
        if (scrollAmount > 0)
            logTextView.scrollTo(0, scrollAmount);
        else logTextView.scrollTo(0, 0);
       */
        //if (clean) commandEditText.setText("");
    }
    // =========================================================================
    void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        getSupportActionBar().setSubtitle(deviceName);
    }
    // =========================================================================
    //FILE:
    // Act on a validated [positive] button click or a [negative] button
    // click. On [negative] click path and name are both null.
   /* @Override
    public void onConfirmSelect(String absolutePath, String fileName) {
        if (absolutePath != null && fileName != null) {
            // Recommend that long/intensive file processes be handled by an
            // Async task.
            myFileProcess(absolutePath, fileName);
        }

    }

    @Override
    public boolean isValid(String absolutePath, String fileName) {
        return fileHeaderCheck(absolutePath, fileName);
    }


    @Override
    public boolean onCanSave(String absolutePath, String fileName) {

        boolean canSave = true;

// Catch the really stupid case.
        if (absolutePath == null || absolutePath.length() ==0 ||
                fileName == null || fileName.length() == 0) {
            canSave = false;
            showToast(R.string.alert_supply_filename, Toast.LENGTH_SHORT);
        }

// Do we have a filename if the extension is thrown away?
        if (canSave) {
            String copyName = FileSaveFragment.NameNoExtension(fileName);
            if (copyName == null || copyName.length() == 0 ) {
                canSave = false;
                showToast(R.string.alert_supply_filename, Toast.LENGTH_SHORT);
            }
        }

// Allow only alpha-numeric names. Simplify dealing with reserved path
// characters.
        if (canSave) {
            if (!FileSaveFragment.IsAlphaNumeric(fileName)) {
                canSave = false;
                showToast(R.string.alert_bad_filename_chars, Toast.LENGTH_SHORT);
            }
        }

// No overwrite of an existing file.
        if (canSave) {
            if (FileSaveFragment.FileExists(absolutePath, fileName)) {
                canSave = false;
                showToast(R.string.alert_file_exists, Toast.LENGTH_SHORT);
            }
        }

        return canSave;

    }

    @Override
    public void onConfirmSave(String absolutePath, String fileName) {
        if (absolutePath != null && fileName != null) {
            // Recommend that file save for large amounts of data is handled
            // by an AsyncTask.
            mySaveMethod(absolutePath, fileName);
        }

    }
*/

    // ==========================================================================

    /**
     * ?????????? ?????? ?????? ?? bluetooth-??????
     */
    private static class BluetoothResponseHandler extends Handler {
        private WeakReference<DeviceControlActivity> mActivity;

        public BluetoothResponseHandler(DeviceControlActivity activity) {
            mActivity = new WeakReference<DeviceControlActivity>(activity);
        }

        public void setTarget(DeviceControlActivity target) {
            mActivity.clear();
            mActivity = new WeakReference<DeviceControlActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            DeviceControlActivity activity = mActivity.get();
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