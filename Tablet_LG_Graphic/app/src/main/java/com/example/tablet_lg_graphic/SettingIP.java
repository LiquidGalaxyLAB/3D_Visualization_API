package com.example.tablet_lg_graphic;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;

import java.net.URISyntaxException;
import java.util.Formatter;
import java.util.Locale;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.net.URL;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.IOException;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import com.jcraft.jsch.Channel;
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

    private int visibleColor = 0xFF008577;
    private int offColor = 0xFF4B4B4B;
    private Socket mSocket;

    // UI references.
    private EditText ipAddress;
    private EditText port;
    private Button nextButton;
    private Button backButton;
    private Button launch_state_button;
    private Button connect_state_button;
    private LinearLayout connect_layout;
    private LinearLayout launch_layout;
    private LinearLayout launch_layout_machine;
    private CheckBox connect_checkbox;
    private TextView title_ip_connect;

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


    private String ipAddressCode;
    private String portCode;

    private boolean translationOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ip_setting);
        STATE=CONNECT;

        setMenuButtons();
    }

    private void switchStateToLaunch(){
        if(STATE==CONNECT){
            Log.i("APP", "Switching to Launch");
            connect_layout.setVisibility(View.GONE);
            launch_layout.setVisibility(View.VISIBLE);
            launch_state_button.setBackgroundColor(visibleColor);
            connect_state_button.setBackgroundColor(offColor);
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

    private void clickNext(){
        if(STATE==CONNECT){
            setIP();
        }else if(STATE==LAUNCH){
            launch_layout.setVisibility(View.GONE);
            launch_layout_machine.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
        }else{
            launchServer();
            setIP();
        }
    }

    private void wifiClicked(){
        if(STATE==CONNECT){
            if(ipAddress.getText().toString().isEmpty()){
                Log.i("MET", getPublicIPAddress());
                ipAddress.setText(getPublicIPAddress());
            }else{
                ipAddress.setText("");
            }
        }
    }


    private void setIP() {
        ipAddressCode = ipAddress.getText().toString();
        portCode = port.getText().toString();
       if(checkIPValid(ipAddressCode)){
           Log.i("APP","change layout " + ipAddressCode);
           setContentView(R.layout.main);
           setControlButtons();
       }

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

    private void launchServer(){

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

    @SuppressLint("StaticFieldLeak")
    private void onClick11 (View v) {
        new AsyncTask<Integer, Void, Void>(){
            @Override
            protected Void doInBackground(Integer... params) {
                try {
                    executeSSHcommand();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(1);
    }

    private void executeSSHcommand(){
        String user = "pi";
        String password = "raspberry";
        String host = "192.168.0.1";
        int port=22;
        try{

            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(10000);
            session.connect();
            ChannelExec channel = (ChannelExec)session.openChannel("exec");
            channel.setCommand("omxd n");
            channel.connect();
            channel.disconnect();

        }
        catch(JSchException e){

        }
    }

    public String getPublicIPAddress(){
        /*String value = null;
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<String> result = es.submit(new Callable<String>() {
            public String call() throws Exception {
                try {
                    URL url = new URL("http://whatismyip.akamai.com/");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader r = new BufferedReader(new InputStreamReader(in));
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            total.append(line).append('\n');
                        }
                        urlConnection.disconnect();
                        return total.toString();
                    }finally {
                        urlConnection.disconnect();
                    }
                }catch (IOException e){
                    Log.e("Public IP: ",e.getMessage());
                }
                return null;
            }
        });
        try {
            value = result.get();
        } catch (Exception e) {
            // failed
        }
        es.shutdown();
        return value;*/
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
        nextButton = (Button) findViewById(R.id.next);
        backButton = (Button) findViewById(R.id.back);
        launch_state_button = (Button) findViewById(R.id.launch);
        connect_state_button = (Button) findViewById(R.id.connect);
        connect_layout = (LinearLayout) findViewById(R.id.connect_layout);
        launch_layout = (LinearLayout) findViewById(R.id.launch_layout);
        launch_layout_machine = (LinearLayout) findViewById(R.id.launch_layout_machine);
        connect_checkbox = (CheckBox) findViewById(R.id.connected_Wifi_connect);
        title_ip_connect = (TextView) findViewById(R.id.ip_connect);

        ipAddress = (EditText) findViewById(R.id.ip);
        port = (EditText) findViewById(R.id.port);

        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickNext();
            }
        });

        launch_state_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switchStateToLaunch();
            }
        });
        connect_state_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switchStateToConnect();
            }
        });
        connect_checkbox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiClicked();
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

