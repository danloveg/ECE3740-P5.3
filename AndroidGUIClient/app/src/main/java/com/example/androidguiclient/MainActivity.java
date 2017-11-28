package com.example.androidguiclient;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity
        implements userinterface.Userinterface, View.OnClickListener,
        android.widget.CompoundButton.OnCheckedChangeListener {

    private client.Client myClient;
    private commandinterface.Command commandHandler;
    private MyHandler handler;
    protected TextView serverMessageTextArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setEventListeners();

        // Set the text area instance
        serverMessageTextArea = findViewById(R.id.messageTextArea);

        // Create a Handler
        handler = new MyHandler(this);

        // Try to get an address for the Client
        InetAddress clientAddress = null;
        try {
            clientAddress = InetAddress.getByName("10.0.2.2");
        } catch (UnknownHostException e) {
            this.update("Could not create emulator address 10.0.2.2");
        }

        // Instantiate a new Client with port number 8765 to start.
        myClient = new client.Client(8765, this, clientAddress);

        // Instantiate a new command handler
        commandHandler = new cmd.UserCommandInvoker(this, this.myClient);
    }


    /**
     * Update the message text area with a new message.
     * @param message The message to display.
     */
    @Override
    public void update(String message) {
        Message msg = Message.obtain();
        msg.obj = message;
        handler.sendMessage(msg);
    }


    /**
     * Set the click listeners of all the Buttons and ToggleButtons to this.
     */
    private void setEventListeners() {
        Button currentButton;
        ToggleButton currentToggleButton;

        // Set up regular buttons
        currentButton = findViewById(R.id.updateIPButton);
        currentButton.setOnClickListener(this); // calling onClick() method
        currentButton = findViewById(R.id.updatePortButton);
        currentButton.setOnClickListener(this);
        currentButton = findViewById(R.id.connectButton);
        currentButton.setOnClickListener(this);
        currentButton = findViewById(R.id.disconnectButton);
        currentButton.setOnClickListener(this);
        currentButton = findViewById(R.id.quitButton);
        currentButton.setOnClickListener(this);
        currentButton = findViewById(R.id.tempButton); // Set up temp button but disable it
        currentButton.setOnClickListener(this);
        currentButton.setEnabled(false);
        currentButton = findViewById(R.id.gpb1Button);
        currentButton.setOnClickListener(this);
        currentButton = findViewById(R.id.gpb2Button);
        currentButton.setOnClickListener(this);
        currentButton = findViewById(R.id.gpb3Button);
        currentButton.setOnClickListener(this);

        // Set up toggle buttons
        currentToggleButton = findViewById(R.id.led1ToggleButton);
        currentToggleButton.setOnCheckedChangeListener(this);
        currentToggleButton = findViewById(R.id.led2ToggleButton);
        currentToggleButton.setOnCheckedChangeListener(this);
        currentToggleButton = findViewById(R.id.led3ToggleButton);
        currentToggleButton.setOnCheckedChangeListener(this);
        currentToggleButton = findViewById(R.id.led4ToggleButton);
        currentToggleButton.setOnCheckedChangeListener(this);
    }


    /**
     * onClick handler that acts on the buttons being clicked.
     * @param view The button being clicked.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.updateIPButton:
                String newIPAddress =
                        ((TextView) findViewById(R.id.ipAddressField)).getText().toString();
                commandHandler.execute("SetIP " + newIPAddress);
                hideKeyboard(this);
                break;
            case R.id.updatePortButton:
                String newPortNumber =
                        ((TextView) findViewById(R.id.portNumberField)).getText().toString();
                commandHandler.execute("SetPort " + newPortNumber);
                hideKeyboard(this);
                break;
            case R.id.connectButton:
                commandHandler.execute("connect");
                break;
            case R.id.disconnectButton:
                commandHandler.execute("d");
                break;
            case R.id.quitButton:
                commandHandler.execute("q");
                break;
            case R.id.tempButton:
                commandHandler.execute("temp");
                break;
            case R.id.gpb1Button:
                commandHandler.execute("gpb1");
                break;
            case R.id.gpb2Button:
                commandHandler.execute("gpb2");
                break;
            case R.id.gpb3Button:
                commandHandler.execute("gpb3");
                break;
        }
    }


    /**
     * onCheckedChanged event handler that acts on any of the toggle buttons being clicked.
     * @param btn The compound button being clicked
     * @param checked true if button is ON, false if OFF
     */
    @Override
    public void onCheckedChanged(CompoundButton btn, boolean checked) {
        switch(btn.getId()) {
            case R.id.led1ToggleButton:
                if (checked) {
                    commandHandler.execute("L1on");
                } else {
                    commandHandler.execute("L1off");
                }
                break;
            case R.id.led2ToggleButton:
                if (checked) {
                    commandHandler.execute("L2on");
                } else {
                    commandHandler.execute("L2off");
                }
                break;
            case R.id.led3ToggleButton:
                if (checked) {
                    commandHandler.execute("L3on");
                } else {
                    commandHandler.execute("L3off");
                }
                break;
            case R.id.led4ToggleButton:
                if (checked) {
                    commandHandler.execute("L4on");
                } else {
                    commandHandler.execute("L4off");
                }
                break;
        }
    }


    /**
     * Hides the keyboard for the activity passed.
     * @param activity The activity for which to hide the keyboard.
     */
    private static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Get the currently focused view
        View view = activity.getCurrentFocus();
        // If no view has focus, create a new one
        if (view == null) {
            view = new View(activity);
        }
        // Hide keyboard
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * static Handler class avoids memory leaks. Handles messages by outputting a message to the
     * server message text area.
     *
     * (Instances of static inner classes do not hold an implicit reference to their outer class.)
     */
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                activity.serverMessageTextArea.setText(msg.obj.toString());
            }
        }
    }
}
