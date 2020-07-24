package com.example.tablet_lg_graphic;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;

import java.net.URISyntaxException;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.Properties;
import java.util.Locale;
import java.io.InputStream;

import java.io.IOException;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * A login screen that offers login via email/password.
 */
public class SettingIP extends AppCompatActivity  {


    private int CONNECT=0;
    private int LAUNCH=1;
    private int LAUNCH_INFO=2;
    private int STATE;

    private static int MIN_PORT_NUMBER = 1;
    private static int MAX_PORT_NUMBER = 9999;

    private int visibleColor = 0xFF008577;
    private int offColor = 0xFF4B4B4B;
    private Socket mSocket;

    // All screens menu buttons
    private Button nextButton;
    private Button launch_state_button;
    private Button connect_state_button;

    // Connect buttons
    private EditText ipAddress;
    private EditText port;
    private LinearLayout connect_layout;
    private CheckBox connect_checkbox;

    // Launch buttons
    private LinearLayout launch_layout;
    private CheckBox launch_checkbox;
    private EditText ipAddress_launch;
    private EditText port_launch;
    private EditText noMachines_edit;

    // Launch info buttons
    private Button backButton;
    private LinearLayout launch_layout_machine;
    private EditText ipAddress_launch_machine;
    private EditText hostname_launch_machine;
    private EditText password_launch_machine;
    private EditText path_machine;
    private CheckBox launch_checkbox_info;
    private TextView title_launch_machine;
    private TextView title_ip_launch_machine;

    private Button transUp;
    private Button transDown;
    private Button transLeft;
    private Button transRight;
    private Button transForward;
    private Button transBackwards;

    private Button rotXPos;
    private Button rotXNeg;
    private Button rotYPos;
    private Button rotYNeg;
    private Button rotZPos;
    private Button rotZNeg;

    private Button goBack;
    private Button reset;
    private Button switchRotTrans;
    private ProgressBar loading;


    private String ipAddressCode;
    private String portCode;
    private int noMachines;
    private int howManyMachinesAsked;

    private boolean translationOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ip_setting);

        setMenuButtons();
    }

    private void switchStateToLaunch(){
        if(STATE==CONNECT){
            Log.i("APP", "Switching to Launch");
            connect_layout.setVisibility(View.GONE);
            launch_layout_machine.setVisibility(View.GONE);
            launch_layout.setVisibility(View.VISIBLE);
            launch_state_button.setBackgroundColor(visibleColor);
            connect_state_button.setBackgroundColor(offColor);
            backButton.setVisibility(View.INVISIBLE);
            STATE=LAUNCH;
        }
    }

    private void switchStateToConnect(){
        if(STATE==LAUNCH){
            Log.i("APP", "Switching to connect");
            launch_layout.setVisibility(View.GONE);
            connect_layout.setVisibility(View.VISIBLE);
            launch_state_button.setBackgroundColor(offColor);
            connect_state_button.setBackgroundColor(visibleColor);
            STATE=CONNECT;
        }else if(STATE==LAUNCH_INFO){
            Log.i("APP", "Switching to connect");
            launch_layout_machine.setVisibility(View.GONE);
            connect_layout.setVisibility(View.VISIBLE);
            launch_state_button.setBackgroundColor(offColor);
            connect_state_button.setBackgroundColor(visibleColor);
            backButton.setVisibility(View.INVISIBLE);
            STATE=CONNECT;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void clickNext(){
        if(STATE==CONNECT){
            ipAddressCode = ipAddress.getText().toString();
            portCode = port.getText().toString();
            loading.setVisibility(View.VISIBLE);
            socketAvailable(ipAddressCode, portCode, true);
            //setIP();
        }else if(STATE==LAUNCH){
            if(noMachines_edit.getText().toString().isEmpty()){
                noMachines_edit.setError("Please fill");
            }else if(ipAddress_launch.getText().toString().isEmpty()){
                ipAddress_launch.setError("Please fill");
            }else if(port_launch.getText().toString().isEmpty()){
                port_launch.setError("Please fill");
            }else{
                ipAddressCode = ipAddress_launch.getText().toString();
                portCode = port_launch.getText().toString();
                noMachines = Integer.parseInt(noMachines_edit.getText().toString());
                if(!checkIPValid(ipAddressCode)){
                    ipAddress_launch.setError("Not valid");
                }else{
                    loading.setVisibility(View.VISIBLE);
                    socketAvailable(ipAddressCode, portCode, false);
                }
            }
        }else{
            loading.setVisibility(View.VISIBLE);
            launchServer(hostname_launch_machine.getText().toString(),
                    password_launch_machine.getText().toString(),
                    ipAddress_launch_machine.getText().toString(),
                    path_machine.getText().toString(),
                    howManyMachinesAsked==0);
            //setIP();
        }
    }

    private void nextControlsFromConnect(boolean available){
        loading.setVisibility(View.GONE);
        Log.i("APP", "Checking project is open " + available);
        if(!available){
            Log.i("APP", "Going to controls");
            ipAddress.setError(null);
            port.setError(null);
            setIP();
        }else{
            Log.i("APP", "Error, no open server");
            ipAddress.setError("No open project");
            port.setError("No open project");
        }
    }

    private void nextMachineInfoFromLaunch(boolean available){
        loading.setVisibility(View.GONE);
        if(!available){
            port_launch.setError("Port not available");
        }else{
            port_launch.setError(null);
            launch_layout.setVisibility(View.GONE);
            launch_layout_machine.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
            ipAddress_launch_machine.setVisibility(View.GONE);
            ipAddress_launch_machine.setText(ipAddressCode);
            launch_checkbox_info.setVisibility(View.GONE);
            title_ip_launch_machine.setVisibility(View.GONE);
            howManyMachinesAsked = 0;
            STATE=LAUNCH_INFO;
        }
    }

    private void nextMachineInfo(boolean loginWorked){
        Log.i("APP", "Next machine " + loginWorked + " " + howManyMachinesAsked + " " +noMachines);
        loading.setVisibility(View.GONE);
        if(!loginWorked){
            hostname_launch_machine.setError("Login failed");
            password_launch_machine.setError("Login failed");
        }else{
            hostname_launch_machine.setError(null);
            password_launch_machine.setError(null);
            howManyMachinesAsked++;
            if(howManyMachinesAsked < noMachines){
                ipAddress_launch_machine.setVisibility(View.VISIBLE);
                ipAddress_launch_machine.setText(null);
                launch_checkbox_info.setVisibility(View.VISIBLE);
                hostname_launch_machine.setText(null);
                password_launch_machine.setText(null);
                if(howManyMachinesAsked%2 == 0){
                    title_launch_machine.setText("Info " + (howManyMachinesAsked+1)/2 + " machine to the right ");
                }else{
                    title_launch_machine.setText("Info " + (howManyMachinesAsked+1)/2 + " machine to the left ");
                }

            }else {
                Log.i("APP", "Going to set IP");
                setIP();
            }
        }
    }

    private void clickBack(){
        if(STATE==LAUNCH_INFO){
            launch_layout_machine.setVisibility(View.GONE);
            launch_layout.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.GONE);
            STATE=LAUNCH;
        }
    }

    private void wifiClicked(){
        if(STATE==CONNECT){
            if(connect_checkbox.isChecked()){
                String ipDevice = getPublicIPAddress();
                Log.i("MET", getPublicIPAddress());
                ipAddress.setText(ipDevice.substring(0,ipDevice.length()-2));
            }else{
                ipAddress.setText(null);
            }
        }else if(STATE==LAUNCH){
            if(launch_checkbox.isChecked()){
                String ipDevice = getPublicIPAddress();
                Log.i("MET", getPublicIPAddress());
                ipAddress_launch.setText(ipDevice.substring(0,ipDevice.length()-2));
            }else{
                ipAddress_launch.setText(null);
            }
        }else{
            if(launch_checkbox.isChecked()){
                String ipDevice = getPublicIPAddress();
                Log.i("MET", getPublicIPAddress());
                ipAddress_launch_machine.setText(ipDevice.substring(0,ipDevice.length()-2));
            }else{
                ipAddress_launch_machine.setText(null);
            }
        }
    }


    private void setIP() {
        if(checkIPValid(ipAddressCode)){
           Log.i("APP","change layout " + ipAddressCode);
           setContentView(R.layout.main);
           setControlButtons();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void socketAvailable(String ip, String port, final boolean fromConnect) {
        String url ="http://"+ip+":"+port;
        RequestQueue queue = Volley.newRequestQueue(this);

        final boolean[] result = new boolean[1];
        final boolean[] answered = {false};
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
        new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the first 500 characters of the response string.
                Log.i("SER", "A response");
                if(!fromConnect){
                    nextMachineInfoFromLaunch(false);
                }else{
                    nextControlsFromConnect(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("SER", "No response");
                if(!fromConnect){
                    nextMachineInfoFromLaunch(true);
                }else{
                    nextControlsFromConnect(true);
                }
            }
        });
        queue.add(stringRequest);
    }

    private boolean checkIPValid(String ip){
        if(ip.length() < 7){
            return false;
        }
        int count = 0;
        boolean lastDot = true;
        for(int i = 0; i< ip.length(); i++){
            if(ip.charAt(i) == '.'){
                if(lastDot){
                    return false;
                }
                count++;
                lastDot = true;
            }else if(!Character.isDigit(ip.charAt(i))){
                return false;
            }else{
                lastDot = false;
            }
        }
        if(count !=3){
            return false;
        }
        return true;
    }

    private void backToSetIP(){
        Log.i("APP","change layout");
        disconnectTablet();
        setContentView(R.layout.ip_setting);
        STATE=CONNECT;
        setMenuButtons();
    }

    private void setSwitchRotTrans(){
        if(translationOn){
            transUp.setVisibility(View.INVISIBLE);
            transDown.setVisibility(View.INVISIBLE);
            transLeft.setVisibility(View.INVISIBLE);
            transRight.setVisibility(View.INVISIBLE);
            transForward.setVisibility(View.INVISIBLE);
            transBackwards.setVisibility(View.INVISIBLE);

            rotXPos.setVisibility(View.VISIBLE);
            rotXNeg.setVisibility(View.VISIBLE);
            rotYPos.setVisibility(View.VISIBLE);
            rotYNeg.setVisibility(View.VISIBLE);
            rotZPos.setVisibility(View.VISIBLE);
            rotZNeg.setVisibility(View.VISIBLE);

            switchRotTrans.setText("Rotate");
        }else{
            transUp.setVisibility(View.VISIBLE);
            transDown.setVisibility(View.VISIBLE);
            transLeft.setVisibility(View.VISIBLE);
            transRight.setVisibility(View.VISIBLE);
            transForward.setVisibility(View.VISIBLE);
            transBackwards.setVisibility(View.VISIBLE);

            rotXPos.setVisibility(View.INVISIBLE);
            rotXNeg.setVisibility(View.INVISIBLE);
            rotYPos.setVisibility(View.INVISIBLE);
            rotYNeg.setVisibility(View.INVISIBLE);
            rotZPos.setVisibility(View.INVISIBLE);
            rotZNeg.setVisibility(View.INVISIBLE);

            switchRotTrans.setText("Translate");
        }
        translationOn = !translationOn;
    }

    @SuppressLint("StaticFieldLeak")
    private void launchServer(final String user, final String password, final String host, final String path, final boolean isMaster){
        Log.i("LAU", "Start launch");
        final boolean[] resultSSH = new boolean[1];
        new AsyncTask<Integer, Void, String>(){
            @Override
            protected String doInBackground(Integer... params) {
                try {
                    resultSSH[0] = executeSSHcommand( user,  password,  host, path, isMaster);
                    Log.i("LAU", "Result in " + resultSSH[0]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result){
                Log.i("APP", "Moving to result");
                nextMachineInfo(resultSSH[0]);
            }
        }.execute(1);

    }

    private boolean executeSSHcommand(String user, String password, String host, String path, boolean isMaster){
        Log.i("LAU", "Start ssh");
        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession(user, host, 22);
            session.setPassword(password);

            Log.i("LAU", "Just ssh");
            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);
            session.connect();

            Log.i("LAU", "Session connected");

            ChannelExec channel = (ChannelExec)session.openChannel("exec");

            if(isMaster){
                channel.setCommand("cd " + path +"; ./launch.sh -m -i " + ipAddressCode + " -p " + portCode);
                //channel.setCommand("ls");
            }else{
                channel.setCommand("cd " + path +"; ./launch.sh -i " + ipAddressCode + " -p " + portCode);
            }


            InputStream commandOutput = channel.getExtInputStream();

            StringBuilder outputBuffer = new StringBuilder();
            StringBuilder errorBuffer = new StringBuilder();

            InputStream in = channel.getInputStream();
            InputStream err = channel.getExtInputStream();

            channel.connect();
            Log.i("LAU", "Channel connected");

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    outputBuffer.append(new String(tmp, 0, i));
                    Log.i("LAU", "getting output: " + outputBuffer.toString());
                    if(outputBuffer.toString().contains("Screen number "+noMachines + " connected")){
                        Log.i("LAU", "get output: " + outputBuffer.toString());
                        break;
                    }
                }
                while (err.available() > 0) {
                    int i = err.read(tmp, 0, 1024);
                    if (i < 0) break;
                    errorBuffer.append(new String(tmp, 0, i));
                }
                if (channel.isClosed() || outputBuffer.toString().contains("Screen number "+noMachines + " connected")) {
                    Log.i("LAU", "output state: " + outputBuffer.toString());
                    if ((in.available() > 0) || (err.available() > 0) &&
                            !outputBuffer.toString().contains("Screen number "+noMachines + " connected")){
                        Log.i("LAU","continue: " + channel.getExitStatus());
                        continue;
                    }
                    Log.i("LAU","exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            Log.i("LAU", "output: " + outputBuffer.toString());
            Log.i("LAU", "error: " + errorBuffer.toString());

            channel.disconnect();
            if(!errorBuffer.toString().isEmpty()){
                return false;
            }
        } catch (JSchException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void translateUp(){
        mSocket.emit("serverMoveUp");
        Log.i("SER", "move up");
    }
    private void translateDown(){
        mSocket.emit("serverMoveDown");
        Log.i("SER", "move down");
    }
    private void translateLeft(){
        mSocket.emit("serverMoveLeft");
        Log.i("SER", "move left");
    }
    private void translateRight(){
        mSocket.emit("serverMoveRight");
        Log.i("SER", "move right");
    }
    private void translateForward(){
        mSocket.emit("serverMoveForward");
        Log.i("SER", "move forward");
    }
    private void translateBackwards(){
        mSocket.emit("serverMoveBackwards");
        Log.i("SER", "move backwards");
    }
    private void resetCamera(){
        mSocket.emit("serverResetCamera");
        Log.i("SER", "reset camera");
    }

    private void rotateXPos(){
        mSocket.emit("serverRotateXPos");
        Log.i("SER", "rotate X Pos");
    }
    private void rotateXNeg(){
        mSocket.emit("serverRotateXNeg");
        Log.i("SER", "rotate X Neg");
    }
    private void rotateYPos(){
        mSocket.emit("serverRotateYPos");
        Log.i("SER", "rotate Y Pos");
    }
    private void rotateYNeg(){
        mSocket.emit("serverRotateYNeg");
        Log.i("SER", "rotate Y Neg");
    }
    private void rotateZPos(){
        mSocket.emit("serverRotateZPos");
        Log.i("SER", "rotate Z Pos");
    }
    private void rotateZNeg(){
        mSocket.emit("serverRotateZNeg");
        Log.i("SER", "rotate Z Neg");
    }

    private void disconnectTablet(){
        mSocket.disconnect();
        Log.i("SER", "Disconnected");
    }



    public String getPublicIPAddress(){
        WifiManager wifiMgr = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return String.format(Locale.US, "%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));

    }

    private void setMenuButtons(){
        STATE=CONNECT;

        nextButton = (Button) findViewById(R.id.next);
        launch_state_button = (Button) findViewById(R.id.launch);
        connect_state_button = (Button) findViewById(R.id.connect);
        loading = (ProgressBar) findViewById(R.id.progressBar);
        launch_state_button.setBackgroundColor(offColor);
        connect_state_button.setBackgroundColor(visibleColor);

        nextButton.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                clickNext();
            }
        });
        connect_state_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switchStateToConnect();
            }
        });
        launch_state_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switchStateToLaunch();
            }
        });

        //Connect layout
        connect_layout = (LinearLayout) findViewById(R.id.connect_layout);
        connect_checkbox = (CheckBox) findViewById(R.id.connected_Wifi_connect);
        ipAddress = (EditText) findViewById(R.id.ip);
        port = (EditText) findViewById(R.id.port);
        connect_layout.setVisibility(View.VISIBLE);

        connect_checkbox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiClicked();
            }
        });

        //Launch layout
        launch_layout = (LinearLayout) findViewById(R.id.launch_layout);
        launch_checkbox = (CheckBox) findViewById(R.id.connected_Wifi_launch);
        ipAddress_launch = (EditText) findViewById(R.id.ip_launch);
        port_launch = (EditText) findViewById(R.id.port);
        noMachines_edit = (EditText) findViewById(R.id.noMachines);
        path_machine = (EditText) findViewById(R.id.path);
        launch_layout.setVisibility(View.GONE);

        launch_checkbox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiClicked();
            }
        });

        //Launch info layout
        launch_layout_machine = (LinearLayout) findViewById(R.id.launch_layout_machine);
        launch_checkbox_info = (CheckBox) findViewById(R.id.connected_Wifi);
        hostname_launch_machine = (EditText) findViewById(R.id.host_name_machine);
        ipAddress_launch_machine = (EditText) findViewById(R.id.ip_address_machine);
        password_launch_machine = (EditText) findViewById(R.id.password_machine);
        backButton = (Button) findViewById(R.id.back);
        title_launch_machine = (TextView) findViewById(R.id.machine_title);
        title_ip_launch_machine = (TextView) findViewById(R.id.machine_ip_title);
        launch_layout_machine.setVisibility(View.GONE);

        launch_checkbox_info.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiClicked();
            }
        });
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickBack();
            }
        });
    }

    private void setControlButtons(){

        translationOn = true;

        transUp = (Button)  findViewById(R.id.upBut);
        transDown = (Button)  findViewById(R.id.downBut);
        transLeft = (Button)  findViewById(R.id.leftBut);
        transRight = (Button)  findViewById(R.id.rightBut);
        transForward = (Button)  findViewById(R.id.forwardBut);
        transBackwards = (Button)  findViewById(R.id.backwardBut);

        rotXPos = (Button)  findViewById(R.id.rotXPosBut);
        rotXNeg = (Button)  findViewById(R.id.rotXNegBut);
        rotYPos = (Button)  findViewById(R.id.rotYPosBut);
        rotYNeg = (Button)  findViewById(R.id.rotYNegBut);
        rotZPos = (Button)  findViewById(R.id.rotZPosBut);
        rotZNeg = (Button)  findViewById(R.id.rotZNegBut);

        goBack = (Button)  findViewById(R.id.back);
        reset = (Button)  findViewById(R.id.reset);
        switchRotTrans = (Button)  findViewById(R.id.switchTrans);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToSetIP();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetCamera();
            }
        });
        switchRotTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSwitchRotTrans();
            }
        });

        try {
            mSocket = IO.socket("http://" + ipAddressCode + ":" + portCode + "/");
        } catch (URISyntaxException e) {}

        mSocket.connect();
        mSocket.emit("signalTablet");
        transUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateUp();
            }
        });
        transDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateDown();
            }
        });
        transLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateLeft();
            }
        });
        transRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateRight();
            }
        });
        transForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateForward();
            }
        });
        transBackwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateBackwards();
            }
        });

        rotXPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateXPos();
            }
        });
        rotXNeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateXNeg();
            }
        });
        rotYPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateYPos();
            }
        });
        rotYNeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateYNeg();
            }
        });
        rotZPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateZPos();
            }
        });
        rotZNeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateZNeg();
            }
        });
    }
}

