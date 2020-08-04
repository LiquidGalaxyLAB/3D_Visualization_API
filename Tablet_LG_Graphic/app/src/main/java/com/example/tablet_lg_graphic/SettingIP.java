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

import java.lang.reflect.Array;
import java.net.URISyntaxException;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
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
    private int PROJECT=3;
    private int REGISTER_PROJECT=4;
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

    //Project buttons
    private Button projectPath;
    private LinearLayout project_layout;
    private ArrayList<Button> projects;

    //RegisterProject buttons
    private EditText registerPath;
    private LinearLayout project_register_layout;

    // Launch info buttons
    private Button backButton;
    private LinearLayout launch_layout_machine;
    private EditText ipAddress_launch_machine;
    private EditText hostname_launch_machine;
    private EditText password_launch_machine;
    private EditText noSockets_machine;
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
    private Button kill;
    private Button switchRotTrans;
    private Button switchCamera;
    private ProgressBar loading;


    private ArrayList<String> ipAddressCode;
    private String portCode;
    private ArrayList<String> username;
    private ArrayList<String> password;
    private ArrayList<String> path_projects;
    private ArrayList<Integer> noScreens;
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
            Log.w("APP", "Switching to Launch");
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
            Log.w("APP", "Switching to connect");
            launch_layout.setVisibility(View.GONE);
            connect_layout.setVisibility(View.VISIBLE);
            launch_state_button.setBackgroundColor(offColor);
            connect_state_button.setBackgroundColor(visibleColor);
            STATE=CONNECT;
        }else if(STATE==LAUNCH_INFO){
            Log.w("APP", "Switching to connect");
            launch_layout_machine.setVisibility(View.GONE);
            connect_layout.setVisibility(View.VISIBLE);
            launch_state_button.setBackgroundColor(offColor);
            connect_state_button.setBackgroundColor(visibleColor);
            backButton.setVisibility(View.INVISIBLE);
            STATE=CONNECT;
        }
    }

    private void registerNewPath(boolean result){
        if(STATE==PROJECT){
            STATE = REGISTER_PROJECT;
            project_layout.setVisibility(View.GONE);
            project_register_layout.setVisibility(View.VISIBLE);
            projectPath.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
        }else if(STATE==REGISTER_PROJECT){
            loading.setVisibility(View.GONE);
            if(result == false){
                registerPath.setError("Path not valid");
            }else{
                STATE = PROJECT;
                project_layout.setVisibility(View.VISIBLE);
                project_register_layout.setVisibility(View.GONE);
                projectPath.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.GONE);

                final Button proj = new Button(this);
                proj.setText(registerPath.getText());

                proj.setId(path_projects.size());
                path_projects.add(registerPath.getText().toString());
                registerPath.setText(null);
                projects.add(proj);
                project_layout.addView(proj);

                proj.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        for(int i = 0; i< username.size(); i++){
                            Log.i("APP", "Launching one server " + i);
                            loading.setVisibility(View.VISIBLE);
                            launchServer(username.get(i), password.get(i), ipAddressCode.get(i), path_projects.get(proj.getId()), noScreens.get(i), i==0);
                        }
                    }
                });
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void clickNext(){
        Log.i("APP", "Next in state " + STATE);
        if(STATE==CONNECT){
            String ipAddCode = ipAddress.getText().toString();
            portCode = port.getText().toString();
            loading.setVisibility(View.VISIBLE);
            socketAvailable(ipAddCode, portCode, true);
            //setIP();
        }else if(STATE==LAUNCH){
            if(noMachines_edit.getText().toString().isEmpty()){
                noMachines_edit.setError("Please fill");
            }else if(ipAddress_launch.getText().toString().isEmpty()){
                ipAddress_launch.setError("Please fill");
            }else if(port_launch.getText().toString().isEmpty()){
                port_launch.setError("Please fill");
            }else{
                String ipAddCode = ipAddress_launch.getText().toString();
                portCode = port_launch.getText().toString();
                noMachines = Integer.parseInt(noMachines_edit.getText().toString());
                if(!checkIPValid(ipAddCode)){
                    ipAddress_launch.setError("Not valid");
                }else{
                    loading.setVisibility(View.VISIBLE);
                    socketAvailable(ipAddCode, portCode, false);
                }
            }
        }else if(STATE==LAUNCH_INFO) {
            if (hostname_launch_machine.getText().toString().isEmpty()) {
                hostname_launch_machine.setError("Please fill");
            } else if (password_launch_machine.getText().toString().isEmpty()) {
                password_launch_machine.setError("Please fill");
            } else if (ipAddress_launch_machine.getText().toString().isEmpty()) {
                ipAddress_launch_machine.setError("Please fill");
            } else{
                loading.setVisibility(View.VISIBLE);
                testMachine(hostname_launch_machine.getText().toString(), password_launch_machine.getText().toString(),
                        ipAddress_launch_machine.getText().toString(), "", true);
            }
        }else if(STATE==REGISTER_PROJECT){
            loading.setVisibility(View.VISIBLE);
            testMachine(username.get(0), password.get(0),ipAddressCode.get(0), registerPath.getText().toString(), false);
        }
    }

    private void nextControlsFromConnect(boolean available){
        loading.setVisibility(View.GONE);
        Log.w("APP", "Checking project is open " + available);
        if(!available){
            Log.w("APP", "Going to controls");
            ipAddress.setError(null);
            port.setError(null);
            setIP(false);
        }else{
            Log.w("APP", "Error, no open server");
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
            ipAddress_launch_machine.setText(ipAddressCode.get(0));
            launch_checkbox_info.setVisibility(View.GONE);
            title_ip_launch_machine.setVisibility(View.GONE);
            howManyMachinesAsked = 0;
            STATE=LAUNCH_INFO;
        }
    }

    private void nextMachineInfo(boolean loginWorked){
        Log.w("APP", "Next machine " + loginWorked + " " + howManyMachinesAsked + " " +noMachines);
        loading.setVisibility(View.GONE);
        if(!loginWorked){
            hostname_launch_machine.setError("Login failed");
            password_launch_machine.setError("Login failed");
        }else{


            username.add( hostname_launch_machine.getText().toString()) ;
            password.add(password_launch_machine.getText().toString());
            noScreens.add(Integer.parseInt(noSockets_machine.getText().toString()));
            if(!ipAddressCode.contains(ipAddress_launch_machine.getText().toString())){
                ipAddressCode.add(ipAddress_launch_machine.getText().toString());
            }

            hostname_launch_machine.setError(null);
            password_launch_machine.setError(null);
            ipAddress_launch_machine.setText(null);

            howManyMachinesAsked++;
            if(howManyMachinesAsked < noMachines){
                ipAddress_launch_machine.setVisibility(View.VISIBLE);
                launch_checkbox_info.setVisibility(View.VISIBLE);
                if(howManyMachinesAsked%2 == 0){
                    title_launch_machine.setText("Info " + (howManyMachinesAsked+1)/2 + " machine to the right ");
                }else{
                    title_launch_machine.setText("Info " + (howManyMachinesAsked+1)/2 + " machine to the left ");
                }

            }else {
                Log.w("APP", "Going to projects");
                launch_layout_machine.setVisibility(View.GONE);
                project_layout.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.GONE);
                projectPath.setVisibility(View.VISIBLE);
                launch_state_button.setVisibility(View.INVISIBLE);
                connect_state_button.setVisibility(View.INVISIBLE);
                STATE = PROJECT;

            }
        }
    }

    private void clickBack(){
        if(STATE==LAUNCH_INFO || STATE==PROJECT){
            connect_state_button.setVisibility(View.VISIBLE);
            launch_state_button.setVisibility(View.VISIBLE);
            project_register_layout.setVisibility(View.GONE);
            launch_layout_machine.setVisibility(View.GONE);
            launch_layout.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.GONE);
            STATE=LAUNCH;
        }if(STATE==REGISTER_PROJECT){
            STATE=PROJECT;
            connect_state_button.setVisibility(View.GONE);
            launch_state_button.setVisibility(View.GONE);
            project_register_layout.setVisibility(View.GONE);
            project_layout.setVisibility(View.VISIBLE);
            projectPath.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
        }
    }

    private void wifiClicked(){
        if(STATE==CONNECT){
            if(connect_checkbox.isChecked()){
                String ipDevice = getPublicIPAddress();
                Log.w("MET", getPublicIPAddress());
                ipAddress.setText(ipDevice.substring(0,ipDevice.length()-2));
            }else{
                ipAddress.setText(null);
            }
        }else if(STATE==LAUNCH){
            if(launch_checkbox.isChecked()){
                String ipDevice = getPublicIPAddress();
                Log.w("MET", getPublicIPAddress());
                ipAddress_launch.setText(ipDevice.substring(0,ipDevice.length()-2));
            }else{
                ipAddress_launch.setText(null);
            }
        }else{
            if(launch_checkbox.isChecked()){
                String ipDevice = getPublicIPAddress();
                Log.w("MET", getPublicIPAddress());
                ipAddress_launch_machine.setText(ipDevice.substring(0,ipDevice.length()-2));
            }else{
                ipAddress_launch_machine.setText(null);
            }
        }
    }


    private void setIP(boolean hasLaunched) {
        if(checkIPValid(ipAddressCode.get(0))){
           Log.w("APP","change layout " + ipAddressCode);
           setContentView(R.layout.main);
           setControlButtons(hasLaunched);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void socketAvailable(final String ip, String port, final boolean fromConnect) {
        String url ="http://"+ip+":"+port;
        RequestQueue queue = Volley.newRequestQueue(this);

        final boolean[] result = new boolean[1];
        final boolean[] answered = {false};
        // Request a string response from the provided URL.
        Log.i("SER", "Trying " + url);
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
                    ipAddressCode.add(ip);
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
            Log.i("APP", "Too short " + ip.length() + " " + ip);
            return false;
        }
        int count = 0;
        boolean lastDot = true;
        for(int i = 0; i< ip.length(); i++){
            if(ip.charAt(i) == '.'){
                if(lastDot){
                    Log.i("APP", "Two dots continuous");
                    return false;
                }
                count++;
                lastDot = true;
            }else if(!Character.isDigit(ip.charAt(i))){
                Log.i("APP", "Not a digit");
                return false;
            }else{
                lastDot = false;
            }
        }
        if(count !=3){
            Log.i("APP", "More than three dorts");
            return false;
        }
        return true;
    }

    private void backToSetIP(){
        Log.w("APP","change layout from  " + STATE);
        disconnectTablet();
        setContentView(R.layout.ip_setting);


        if(STATE!=PROJECT){
            setMenuButtons();
        }else{
            setMenuButtons();
            STATE=PROJECT;
            project_layout.setVisibility(View.VISIBLE);
            launch_layout.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
            projectPath.setVisibility(View.VISIBLE);
            launch_state_button.setVisibility(View.GONE);
            connect_state_button.setVisibility(View.GONE);
        }

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
    private void launchServer(final String user, final String password, final String host, final String path, final int noSockets, final boolean isMaster){
        Log.w("LAU", "Start launch");
        final boolean[] resultSSH = new boolean[1];
        new AsyncTask<Integer, Void, String>(){
            @Override
            protected String doInBackground(Integer... params) {
                try {
                    resultSSH[0] = executeSSHcommand( user,  password,  host, path, noSockets, isMaster, false);
                    Log.w("LAU", "Result in " + resultSSH[0]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result){
                Log.w("APP", "Moving to result");
                loading.setVisibility(View.GONE);
                setIP(true);
            }
        }.execute(1);

    }

    @SuppressLint("StaticFieldLeak")
    private void testMachine(final String user, final String password, final String host, final String path, final boolean isLaunchInfo){
        Log.w("LAU", "Start launch");
        final boolean[] resultSSH = new boolean[1];
        new AsyncTask<Integer, Void, String>(){
            @Override
            protected String doInBackground(Integer... params) {
                try {
                    resultSSH[0] = executeTestSSHcommand( user,  password,  host, path);
                    Log.w("LAU", "Result in " + resultSSH[0]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result){
                Log.w("APP", "Moving to result");
                if(isLaunchInfo){
                    nextMachineInfo(resultSSH[0]);
                }else{
                    registerNewPath(resultSSH[0]);
                }

            }
        }.execute(1);

    }

    @SuppressLint("StaticFieldLeak")
    private void killServer(final String user, final String password, final String host, final String path){
        Log.w("LAU", "Start kill");
        final boolean[] resultSSH = new boolean[1];
        new AsyncTask<Integer, Void, String>(){
            @Override
            protected String doInBackground(Integer... params) {
                try {
                    resultSSH[0] = executeSSHcommand( user,  password,  host, path, 0,true, true);
                    Log.w("LAU", "Result in " + resultSSH[0]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result){
                Log.w("APP", "Moving to result");
                loading.setVisibility(View.VISIBLE);
                backToSetIP();
            }
        }.execute(1);

    }

    private boolean executeSSHcommand(String user, String password, String host, String path, int noSockets, boolean isMaster, boolean killServer){
        Log.w("LAU", "Start ssh");
        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession(user, host, 22);
            session.setPassword(password);

            Log.w("LAU", "Just ssh");
            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);
            session.connect();

            Log.w("LAU", "Session connected");

            ChannelExec channel = (ChannelExec)session.openChannel("exec");

            int indexPublic=path.indexOf("public");
            String projectDir = path.substring(indexPublic+6);
            String projectDirToChange = path.substring(0, indexPublic);
            String finalCommand = "cd " + projectDirToChange +"; "+
                    "DIMENSIONS=$(DISPLAY=:0 xdpyinfo | grep dimensions: | awk '{print $2}');" +
                    "WIDTH=$(echo $DIMENSIONS | head -n1 | awk '{print $1;}');" +
                    "HEIGHT=$(echo $DIMENSIONS | head -n1 | awk '{print $2;}')" +
                    ".google-chrome \"data:text/html,<html><body><script>window.moveTo(0,0);" +
                    "window.resizeTo($(($WIDTH)),$HEIGHT);" +
                    "window.location='http://" + ipAddressCode.get(0) + ":" + portCode  + "';" +
                    "</script></body></html>;";
            if(isMaster){
                if(killServer){
                    finalCommand = "cd " + projectDirToChange +"; ./killServer.sh";
                }else{
                    finalCommand = "cd " + projectDirToChange +"; ./launch.sh -m -p " + portCode + " -n "+ noSockets + " -d " + projectDir;
                }
                //channel.setCommand("ls");
            }

            Log.w("SSH", "Command to run "+ finalCommand);
            channel.setCommand(finalCommand);


            InputStream commandOutput = channel.getExtInputStream();

            StringBuilder outputBuffer = new StringBuilder();
            StringBuilder errorBuffer = new StringBuilder();

            InputStream in = channel.getInputStream();
            InputStream err = channel.getExtInputStream();

            channel.connect();
            Log.w("LAU", "Channel connected");

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    outputBuffer.append(new String(tmp, 0, i));
                    Log.w("LAU", "getting output: " + outputBuffer.toString());
                    if(outputBuffer.toString().contains("Screen number "+noMachines + " connected")){
                        Log.w("LAU", "get output: " + outputBuffer.toString());
                        break;
                    }
                }
                while (err.available() > 0) {
                    int i = err.read(tmp, 0, 1024);
                    if (i < 0) break;
                    errorBuffer.append(new String(tmp, 0, i));
                }
                if (channel.isClosed() || outputBuffer.toString().contains("Screen number "+noMachines + " connected" )) {
                    Log.w("LAU", "output state: " + outputBuffer.toString());
                    if ((in.available() > 0) || (err.available() > 0) &&
                            !outputBuffer.toString().contains("Screen number "+noMachines + " connected")){
                        Log.w("LAU","continue: " + channel.getExitStatus());
                        continue;
                    }
                    Log.w("LAU","exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            Log.w("LAU", "output: " + outputBuffer.toString());
            Log.w("LAU", "error: " + errorBuffer.toString());

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

    private boolean executeTestSSHcommand(String user, String password, String host, String path){
        Log.w("LAU", "Start ssh");
        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession(user, host, 22);
            session.setPassword(password);

            Log.w("LAU", "Just ssh");
            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);
            session.connect();

            Log.w("LAU", "Session connected");

            ChannelExec channel = (ChannelExec)session.openChannel("exec");

            String finalCommand = "cd " + path;

            Log.w("SSH", "Command to run "+ finalCommand);
            channel.setCommand(finalCommand);


            InputStream commandOutput = channel.getExtInputStream();

            StringBuilder outputBuffer = new StringBuilder();
            StringBuilder errorBuffer = new StringBuilder();

            InputStream in = channel.getInputStream();
            InputStream err = channel.getExtInputStream();

            channel.connect();
            Log.w("LAU", "Channel connected");

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    outputBuffer.append(new String(tmp, 0, i));
                    Log.w("LAU", "getting output: " + outputBuffer.toString());
                }
                while (err.available() > 0) {
                    int i = err.read(tmp, 0, 1024);
                    if (i < 0) break;
                    errorBuffer.append(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    Log.w("LAU", "output state: " + outputBuffer.toString());
                    if ((in.available() > 0) || (err.available() > 0)){
                        Log.w("LAU","continue: " + channel.getExitStatus());
                        continue;
                    }
                    Log.w("LAU","exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            Log.w("LAU", "output: " + outputBuffer.toString());
            Log.w("LAU", "error: " + errorBuffer.toString());

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
    private void switchCamera(){
        mSocket.emit("serverSwitchCamera");
        Log.i("SER", "Switch camera view");
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
        STATE=LAUNCH;

        ipAddressCode = new ArrayList<String>();
        username = new ArrayList<String>();
        password  = new ArrayList<String>();
        path_projects  = new ArrayList<String>();
        noScreens = new ArrayList<Integer>();
        projects = new ArrayList<Button>();

        nextButton = (Button) findViewById(R.id.next);
        launch_state_button = (Button) findViewById(R.id.launch);
        connect_state_button = (Button) findViewById(R.id.connect);
        loading = (ProgressBar) findViewById(R.id.progressBar);
        launch_state_button.setBackgroundColor(visibleColor);
        connect_state_button.setBackgroundColor(offColor);

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
        port = (EditText) findViewById(R.id.port_connect);
        connect_layout.setVisibility(View.GONE);

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
        launch_layout.setVisibility(View.VISIBLE);

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
        noSockets_machine = (EditText) findViewById(R.id.noSockets);

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

        //Project layout
        project_layout = (LinearLayout) findViewById(R.id.layout_project);
        projectPath = (Button) findViewById(R.id.registerPath);

        projectPath.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewPath(false);
            }
        });

        //Register project
        project_register_layout = (LinearLayout) findViewById(R.id.layout_register_project);
        registerPath = (EditText) findViewById(R.id.path);
        Log.i("APP", "Initial state " + STATE);
    }

    private void setControlButtons( boolean hasLaunched){

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
        kill = (Button)  findViewById(R.id.kill);
        reset = (Button)  findViewById(R.id.reset);
        switchRotTrans = (Button)  findViewById(R.id.switchTrans);
        loading = (ProgressBar) findViewById(R.id.progressBar);
        switchCamera = (Button) findViewById(R.id.camera);
        if(hasLaunched){
            kill.setVisibility(View.VISIBLE);
        }else{
            kill.setVisibility(View.GONE);
        }
        kill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                killServer(username.get(0), password.get(0), ipAddressCode.get(0), path_projects.get(0) );
            }
        });
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
            Log.i("SOC", "Creating socket");
            mSocket = IO.socket("http://" + ipAddressCode.get(0) + ":" + portCode + "/");
        } catch (URISyntaxException e) {Log.i("SOC", "There was an error");}

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

        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });
    }
}

