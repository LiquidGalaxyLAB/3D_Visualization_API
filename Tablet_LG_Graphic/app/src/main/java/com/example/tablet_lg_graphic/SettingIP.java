package com.example.tablet_lg_graphic;

import android.annotation.SuppressLint;
import android.os.Build;
import android.preference.PreferenceManager;
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
import android.content.SharedPreferences;

import java.lang.reflect.Array;
import java.net.URISyntaxException;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Locale;
import java.io.InputStream;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;



public class SettingIP extends AppCompatActivity  {

    private boolean DEMO_ON=false;
    private int projectRunning = 0;

    private int CONNECT=0;
    private int LAUNCH=1;
    private int LAUNCH_INFO=2;
    private int PROJECT=3;
    private int REGISTER_PROJECT=4;
    private int REGISTER_REPOSITORY=5;
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
    private CheckBox sameRepository;

    //RegisterREPOSITORY buttons
    private EditText registerRepository;
    private LinearLayout repository_register_layout;

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
    private int lastProjectUsed;
    private int noMachines;
    private int howManyMachinesAsked;
    private boolean portBusyFromUs;

    private boolean translationOn;


    private SharedPreferences prefs;
    private int progress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ip_setting);

        setMenuButtons(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        restoreForCreate(prefs);
    }

    @Override
    protected void onRestart(){
        super.onRestart();

        Log.i("APP", "restore projects " + projects);
        Log.i("APP", "restore paths " + path_projects);



        Set<String> ipAddressCodeSet = prefs.getStringSet("ipAddressCode", new HashSet<String>());
        ipAddressCode = new ArrayList<String>(ipAddressCodeSet) ;

        Set<String> usernameSet = prefs.getStringSet("username", new HashSet<String>());
        username = new ArrayList<String>(usernameSet) ;
        Set<String> passwordSet = prefs.getStringSet("password", new HashSet<String>());
        password = new ArrayList<String>(passwordSet) ;
        Set<String> path_projectsSet = prefs.getStringSet("path_projects", new HashSet<String>());
        path_projects = new ArrayList<String>(path_projectsSet) ;

        Set<String> noScreensSet = prefs.getStringSet("noScreens", new HashSet<String>());
        noScreens = new ArrayList<Integer>(noScreensSet.size()) ;
        for (String myInt : noScreensSet)
        {
            noScreens.add(Integer.valueOf(myInt));
        }

        portBusyFromUs = prefs.getBoolean("portBusyFromUs", false);

        Set<String> buttonID = prefs.getStringSet("buttonID", new HashSet<String>());

        projects = new ArrayList<Button>();
        int i=0;
        for (Iterator<String> it = buttonID.iterator(); it.hasNext(); ) {
            String f = it.next();

            if(i==0){
                final Button proj = new Button(this);
                proj.setText("Demo");

                proj.setId(Integer.parseInt(f));
                projects.add(proj);
                project_layout.addView(proj);

                proj.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        for(int i = 0; i< username.size(); i++){
                            Log.i("APP", "Launching demo ");
                            loading.setVisibility(View.VISIBLE);

                            lastProjectUsed = proj.getId();
                            DEMO_ON=true;
                            projectRunning=1;
                            try {
                                launchDemo(projectRunning);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                i++;
            }else{
                final Button proj = new Button(this);
                String path=path_projects.get(i);
                int indexPublic=path.indexOf(path_projects.get(0) + "public");
                String projectDir = path;
                if(indexPublic ==-1){
                    projectDir = path.substring(indexPublic+path_projects.get(0).length()+6);
                }
                proj.setText(projectDir);

                proj.setId(Integer.parseInt(f));
                projects.add(proj);
                project_layout.addView(proj);

                proj.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        for(int i = 0; i< username.size(); i++){
                            Log.i("APP", "Launching one server " + i + " from button " + path_projects.get(proj.getId()));
                            loading.setVisibility(View.VISIBLE);

                            lastProjectUsed = proj.getId();
                            launchServer(username.get(i), password.get(i), ipAddressCode.get(i), path_projects.get(proj.getId()), noScreens.get(i), i==0, false);
                            try {
                                TimeUnit.SECONDS.sleep(3);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

        }

        Log.i("APP", "restore final  projects " + projects);
        Log.i("APP", "restore final paths " + path_projects);
        //boolean myBoolean = savedState.getBoolean("MyBoolean");
        //double myDouble = savedState.getDouble("myDouble");
        STATE = prefs.getInt("STATE", 1);
        howManyMachinesAsked = prefs.getInt("howManyMachinesAsked", 0);
        lastProjectUsed = prefs.getInt("lastProjectUsed", 0);
        noMachines = prefs.getInt("noMachines", 1);

        portCode = prefs.getString("portCode", "");

        Log.i("APP", "restore State " + STATE);
        Log.i("APP", "restore howManyMachinesAsked " + howManyMachinesAsked);
        Log.i("APP", "restore lastProjectUsed " + lastProjectUsed);
        Log.i("APP", "restore noMachines " + noMachines);
        Log.i("APP", "restore portCode " + portCode);
        if(STATE==CONNECT){
            Log.i("APP", "restore State connect");
            connect_state_button.setVisibility(View.VISIBLE);
            launch_state_button.setVisibility(View.VISIBLE);
            launch_state_button.setBackgroundColor(offColor);
            connect_state_button.setBackgroundColor(visibleColor);
            connect_layout.setVisibility(View.VISIBLE);
            launch_layout.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
            launch_layout_machine.setVisibility(View.GONE);
            project_layout.setVisibility(View.GONE);
            projectPath.setVisibility(View.GONE);
            project_register_layout.setVisibility(View.GONE);
        }else if(STATE==LAUNCH){
            Log.i("APP", "restore State launch");
            connect_state_button.setVisibility(View.VISIBLE);
            launch_state_button.setVisibility(View.VISIBLE);
            launch_state_button.setBackgroundColor(visibleColor);
            connect_state_button.setBackgroundColor(offColor);
            connect_layout.setVisibility(View.GONE);
            launch_layout.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.INVISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            launch_layout_machine.setVisibility(View.GONE);
            project_layout.setVisibility(View.GONE);
            projectPath.setVisibility(View.GONE);
            project_register_layout.setVisibility(View.GONE);
            noMachines_edit.setText(String.valueOf(noMachines));
            port_launch.setText(String.valueOf(portCode));
        }else if(STATE==LAUNCH_INFO){
            Log.i("APP", "restore State launch info");
            connect_state_button.setVisibility(View.VISIBLE);
            launch_state_button.setVisibility(View.VISIBLE);
            launch_state_button.setBackgroundColor(visibleColor);
            connect_state_button.setBackgroundColor(offColor);
            connect_layout.setVisibility(View.GONE);
            launch_layout.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            launch_layout_machine.setVisibility(View.VISIBLE);

            if(howManyMachinesAsked==1){
                launch_checkbox_info.setVisibility(View.GONE);
                title_ip_launch_machine.setVisibility(View.GONE);
                ipAddress_launch_machine.setVisibility(View.GONE);
            }else{
                launch_checkbox_info.setVisibility(View.VISIBLE);
                title_ip_launch_machine.setVisibility(View.VISIBLE);
                ipAddress_launch_machine.setVisibility(View.VISIBLE);
                if(howManyMachinesAsked%2 == 0){
                    title_launch_machine.setText("Info " + (howManyMachinesAsked)/2 + " machine to the right ");
                }else{
                    title_launch_machine.setText("Info " + (howManyMachinesAsked)/2 + " machine to the left ");
                }
            }

            project_layout.setVisibility(View.GONE);
            projectPath.setVisibility(View.GONE);
            project_register_layout.setVisibility(View.GONE);

        }else if(STATE==PROJECT){
            Log.i("APP", "restore State project");
            connect_state_button.setVisibility(View.GONE);
            launch_state_button.setVisibility(View.GONE);
            connect_layout.setVisibility(View.GONE);
            launch_layout.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
            launch_layout_machine.setVisibility(View.GONE);
            project_layout.setVisibility(View.VISIBLE);
            projectPath.setVisibility(View.VISIBLE);
            project_register_layout.setVisibility(View.GONE);
        }else if(STATE==REGISTER_PROJECT){
            Log.i("APP", "restore State register");
            connect_state_button.setVisibility(View.GONE);
            launch_state_button.setVisibility(View.GONE);
            connect_layout.setVisibility(View.GONE);
            launch_layout.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            launch_layout_machine.setVisibility(View.GONE);
            project_layout.setVisibility(View.GONE);
            projectPath.setVisibility(View.GONE);
            project_register_layout.setVisibility(View.VISIBLE);
        }else if(STATE==REGISTER_REPOSITORY){
            Log.i("APP", "restore State repository");
            connect_state_button.setVisibility(View.GONE);
            launch_state_button.setVisibility(View.GONE);
            connect_layout.setVisibility(View.GONE);
            launch_layout.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            launch_layout_machine.setVisibility(View.GONE);
            project_layout.setVisibility(View.GONE);
            projectPath.setVisibility(View.GONE);
            project_register_layout.setVisibility(View.GONE);
            repository_register_layout.setVisibility(View.VISIBLE);
        }
        //String myString = savedState.getString("MyString");

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editPrefs = prefs.edit();


        Log.i("APP", "restore before projects " + projects);
        Log.i("APP", "restore before paths " + path_projects);
        Log.i("APP", "restore State before" + STATE);

        editPrefs.putInt("STATE", STATE);
        editPrefs.putInt("howManyMachinesAsked", howManyMachinesAsked);
        editPrefs.putInt("lastProjectUsed", lastProjectUsed);
        editPrefs.putInt("noMachines", noMachines);
        editPrefs.putString("portCode", portCode);
        Set<String> ipAddressCodeSet = new HashSet<String>(ipAddressCode);
        editPrefs.putStringSet("ipAddressCode", ipAddressCodeSet);
        Set<String> usernameSet = new HashSet<String>(username);
        editPrefs.putStringSet("username", usernameSet);
        Set<String> passwordSet = new HashSet<String>(password);
        editPrefs.putStringSet("password", passwordSet);
        Set<String> path_projectsSet = new HashSet<String>(path_projects);
        editPrefs.putStringSet("path_projects", path_projectsSet);

        List<String> noScreensString = new ArrayList<String>();
        for (Integer i: noScreens) {
            noScreensString.add(String.valueOf(i));
        }
        Set<String> noScreensSet = new HashSet<String>(noScreensString);
        editPrefs.putStringSet("noScreens", noScreensSet);

        editPrefs.putBoolean("portBusyFromUs", portBusyFromUs);

        Set<String> buttonID = new HashSet<String>();
        for(int i=0; i<projects.size(); i++){
            buttonID.add(String.valueOf(projects.get(i).getId()));
            project_layout.removeView( projects.get(i));
        }
        editPrefs.putStringSet("buttonID", buttonID);

        editPrefs.commit();
    }

    protected void restoreForCreate(SharedPreferences prefs) {
        Log.i("APP", "restore projects " + projects);
        Log.i("APP", "restore paths " + path_projects);



        Set<String> ipAddressCodeSet = prefs.getStringSet("ipAddressCode", new HashSet<String>());
        ipAddressCode = new ArrayList<String>(ipAddressCodeSet) ;

        Set<String> usernameSet = prefs.getStringSet("username", new HashSet<String>());
        username = new ArrayList<String>(usernameSet) ;
        Set<String> passwordSet = prefs.getStringSet("password", new HashSet<String>());
        password = new ArrayList<String>(passwordSet) ;
        Set<String> path_projectsSet = prefs.getStringSet("path_projects", new HashSet<String>());
        path_projects = new ArrayList<String>(path_projectsSet) ;

        Set<String> noScreensSet = prefs.getStringSet("noScreens", new HashSet<String>());
        noScreens = new ArrayList<Integer>(noScreensSet.size()) ;
        for (String myInt : noScreensSet)
        {
            noScreens.add(Integer.valueOf(myInt));
        }

        portBusyFromUs = prefs.getBoolean("portBusyFromUs", false);

        Set<String> buttonID = prefs.getStringSet("buttonID", new HashSet<String>());


        projects = new ArrayList<Button>();
        int i=0;
        for (Iterator<String> it = buttonID.iterator(); it.hasNext(); ) {
            String f = it.next();
            if(i==0){
                final Button proj = new Button(this);
                proj.setText("Demo");

                proj.setId(Integer.parseInt(f));
                projects.add(proj);
                project_layout.addView(proj);

                proj.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        for(int i = 0; i< username.size(); i++){
                            Log.i("APP", "Launching demo ");
                            loading.setVisibility(View.VISIBLE);

                            lastProjectUsed = proj.getId();
                            DEMO_ON=true;
                            projectRunning=1;
                            try {
                                launchDemo(projectRunning);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                i++;
            }else{
                final Button proj = new Button(this);
                String path=path_projects.get(i);
                int indexPublic=path.indexOf(path_projects.get(0) + "public");
                String projectDir = path;
                if(indexPublic ==-1){
                    projectDir = path.substring(indexPublic+path_projects.get(0).length()+6);
                }
                proj.setText(projectDir);

                proj.setId(Integer.parseInt(f));
                projects.add(proj);
                project_layout.addView(proj);

                proj.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        for(int i = 0; i< username.size(); i++){
                            Log.i("APP", "Launching one server " + i + " from button " + path_projects.get(proj.getId()));
                            loading.setVisibility(View.VISIBLE);

                            lastProjectUsed = proj.getId();
                            launchServer(username.get(i), password.get(i), ipAddressCode.get(i), path_projects.get(proj.getId()), noScreens.get(i), i==0, false);
                            try {
                                TimeUnit.SECONDS.sleep(3);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }

        Log.i("APP", "restore final  projects " + projects);
        Log.i("APP", "restore final paths " + path_projects);
        //boolean myBoolean = savedState.getBoolean("MyBoolean");
        //double myDouble = savedState.getDouble("myDouble");
        STATE = prefs.getInt("STATE", 1);
        howManyMachinesAsked = prefs.getInt("howManyMachinesAsked", 0);
        lastProjectUsed = prefs.getInt("lastProjectUsed", 0);
        noMachines = prefs.getInt("noMachines", 1);
        portCode = prefs.getString("portCode", "");
        Log.i("APP", "restore State " + STATE);
        Log.i("APP", "restore howManyMachinesAsked " + howManyMachinesAsked);
        Log.i("APP", "restore lastProjectUsed " + lastProjectUsed);
        Log.i("APP", "restore noMachines " + noMachines);
        Log.i("APP", "restore portCode " + portCode);
        if(STATE==CONNECT){
            Log.i("APP", "restore State connect");
            connect_state_button.setVisibility(View.VISIBLE);
            launch_state_button.setVisibility(View.VISIBLE);
            launch_state_button.setBackgroundColor(offColor);
            connect_state_button.setBackgroundColor(visibleColor);
            connect_layout.setVisibility(View.VISIBLE);
            launch_layout.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
            launch_layout_machine.setVisibility(View.GONE);
            project_layout.setVisibility(View.GONE);
            projectPath.setVisibility(View.GONE);
            project_register_layout.setVisibility(View.GONE);
        }else if(STATE==LAUNCH){
            Log.i("APP", "restore State launch");
            connect_state_button.setVisibility(View.VISIBLE);
            launch_state_button.setVisibility(View.VISIBLE);
            launch_state_button.setBackgroundColor(visibleColor);
            connect_state_button.setBackgroundColor(offColor);
            connect_layout.setVisibility(View.GONE);
            launch_layout.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.INVISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            launch_layout_machine.setVisibility(View.GONE);
            project_layout.setVisibility(View.GONE);
            projectPath.setVisibility(View.GONE);
            project_register_layout.setVisibility(View.GONE);
            noMachines_edit.setText(String.valueOf(noMachines));
            port_launch.setText(String.valueOf(portCode));
        }else if(STATE==LAUNCH_INFO){
            Log.i("APP", "restore State launch info");
            connect_state_button.setVisibility(View.VISIBLE);
            launch_state_button.setVisibility(View.VISIBLE);
            launch_state_button.setBackgroundColor(visibleColor);
            connect_state_button.setBackgroundColor(offColor);
            connect_layout.setVisibility(View.GONE);
            launch_layout.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            launch_layout_machine.setVisibility(View.VISIBLE);

            if(howManyMachinesAsked==1){
                launch_checkbox_info.setVisibility(View.GONE);
                title_ip_launch_machine.setVisibility(View.GONE);
                ipAddress_launch_machine.setVisibility(View.GONE);
            }else{
                launch_checkbox_info.setVisibility(View.VISIBLE);
                title_ip_launch_machine.setVisibility(View.VISIBLE);
                ipAddress_launch_machine.setVisibility(View.VISIBLE);
                if(howManyMachinesAsked%2 == 0){
                    title_launch_machine.setText("Info " + (howManyMachinesAsked)/2 + " machine to the right ");
                }else{
                    title_launch_machine.setText("Info " + (howManyMachinesAsked)/2 + " machine to the left ");
                }
            }

            project_layout.setVisibility(View.GONE);
            projectPath.setVisibility(View.GONE);
            project_register_layout.setVisibility(View.GONE);
        }else if(STATE==PROJECT){
            Log.i("APP", "restore State project");
            connect_state_button.setVisibility(View.GONE);
            launch_state_button.setVisibility(View.GONE);
            connect_layout.setVisibility(View.GONE);
            launch_layout.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
            launch_layout_machine.setVisibility(View.GONE);
            project_layout.setVisibility(View.VISIBLE);
            projectPath.setVisibility(View.VISIBLE);
            project_register_layout.setVisibility(View.GONE);
        }else if(STATE==REGISTER_PROJECT){
            Log.i("APP", "restore State register");
            connect_state_button.setVisibility(View.GONE);
            launch_state_button.setVisibility(View.GONE);
            connect_layout.setVisibility(View.GONE);
            launch_layout.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            launch_layout_machine.setVisibility(View.GONE);
            project_layout.setVisibility(View.GONE);
            projectPath.setVisibility(View.GONE);
            project_register_layout.setVisibility(View.VISIBLE);
        }else if(STATE==REGISTER_REPOSITORY){
            Log.i("APP", "restore State repository");
            connect_state_button.setVisibility(View.GONE);
            launch_state_button.setVisibility(View.GONE);
            connect_layout.setVisibility(View.GONE);
            launch_layout.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            launch_layout_machine.setVisibility(View.GONE);
            project_layout.setVisibility(View.GONE);
            projectPath.setVisibility(View.GONE);
            project_register_layout.setVisibility(View.GONE);
            repository_register_layout.setVisibility(View.VISIBLE);
        }
        //String myString = savedState.getString("MyString");

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
                if(path_projects.contains(registerPath.getText())){
                    registerPath.setError("Path already registered");
                }else{
                    STATE = PROJECT;
                    project_layout.setVisibility(View.VISIBLE);
                    project_register_layout.setVisibility(View.GONE);
                    projectPath.setVisibility(View.VISIBLE);
                    backButton.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.GONE);

                    final Button proj = new Button(this);
                    String path=registerPath.getText().toString();
                    int indexPublic=path.indexOf(path_projects.get(0) + "public");
                    String projectDir = path;
                    if(indexPublic ==-1){
                        projectDir = path.substring(indexPublic+path_projects.get(0).length()+6);
                    }

                    proj.setText(projectDir);

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
                                Log.i("APP", "Launching one server " + i + " from button " + path_projects.get(proj.getId()));
                                loading.setVisibility(View.VISIBLE);

                                lastProjectUsed = proj.getId();
                                launchServer(username.get(i), password.get(i), ipAddressCode.get(i), path_projects.get(proj.getId()), noScreens.get(i), i==0, false);
                                try {
                                    TimeUnit.SECONDS.sleep(3);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }

            }
        }
    }

    private void sameRepositoryFill(){
        if(STATE==REGISTER_PROJECT){
            Log.i("APP", "Same reposiory to fill");
            if(sameRepository.isChecked()){
                ipAddress.setText(path_projects.get(0));
            }else{
                ipAddress.setText(null);
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
                        ipAddress_launch_machine.getText().toString(), "", STATE);
            }
        }else if(STATE==REGISTER_PROJECT){
            loading.setVisibility(View.VISIBLE);
            testMachine(username.get(0), password.get(0),ipAddressCode.get(0), registerPath.getText().toString(), STATE);
        }else if(STATE==REGISTER_REPOSITORY){
            Log.i("APP", "Registering base repository");
            loading.setVisibility(View.VISIBLE);
            testMachine(username.get(0), password.get(0),ipAddressCode.get(0), registerRepository.getText().toString(), STATE);
        }
    }

    private void registerBasePath(boolean available){
        Log.i("APP", "Registering new repository");
        if(STATE==REGISTER_REPOSITORY){
            loading.setVisibility(View.GONE);
            if(!available){
                Log.i("APP", "Repository not found");
                registerRepository.setError("Path not found");
            }else{
                Log.i("APP", "Setting layout");
                nextButton.setVisibility(View.GONE);
                projectPath.setVisibility(View.VISIBLE);
                repository_register_layout.setVisibility(View.GONE);
                project_layout.setVisibility(View.VISIBLE);
                STATE=PROJECT;

                Log.i("APP", "Creating demo button");
                final Button demo = new Button(this);
                demo.setText("Demo");
                String path=registerRepository.getText().toString();

                demo.setId(path_projects.size());
                path_projects.add(path);
                projects.add(demo);
                project_layout.addView(demo);

                demo.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        Log.i("APP", "Launching demo ");
                        loading.setVisibility(View.VISIBLE);

                        lastProjectUsed = demo.getId();
                        DEMO_ON=true;
                        projectRunning=1;
                        try {
                            launchDemo(projectRunning);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Log.i("APP", "Creating cylinder button");
                final Button cylinder = new Button(this);
                cylinder.setText("examples/cylinder");

                cylinder.setId(path_projects.size());
                if (path.endsWith("/")) {
                    path_projects.add(path+"public/examples/cylinder");
                }else{
                    path_projects.add(path+"/public/examples/cylinder");
                }
                projects.add(cylinder);
                project_layout.addView(cylinder);

                cylinder.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        for(int i = 0; i< username.size(); i++){
                            Log.i("APP", "Launching one server " + i + " from button " + path_projects.get(cylinder.getId()));
                            loading.setVisibility(View.VISIBLE);

                            lastProjectUsed = cylinder.getId();
                            launchServer(username.get(i), password.get(i), ipAddressCode.get(i), path_projects.get(cylinder.getId()), noScreens.get(i), i==0, false);
                        }
                    }
                });

                Log.i("APP", "Creating particles button");

                final Button particles = new Button(this);
                particles.setText("examples/particles");

                particles.setId(path_projects.size());
                if (path.endsWith("/")) {
                    path_projects.add(path+"public/examples/particles");
                }else{
                    path_projects.add(path+"/public/examples/particles");
                }
                projects.add(particles);
                project_layout.addView(particles);

                particles.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        for(int i = 0; i< username.size(); i++){
                            Log.i("APP", "Launching one server " + i + " from button " + path_projects.get(particles.getId()));
                            loading.setVisibility(View.VISIBLE);

                            lastProjectUsed = particles.getId();
                            launchServer(username.get(i), password.get(i), ipAddressCode.get(i), path_projects.get(particles.getId()), noScreens.get(i), i==0, false);
                        }
                    }
                });

                Log.i("APP", "Creating objects button");

                final Button objects = new Button(this);
                objects.setText("examples/objects");

                objects.setId(path_projects.size());
                if (path.endsWith("/")) {
                    path_projects.add(path+"public/examples/objects");
                }else{
                    path_projects.add(path+"/public/examples/objects");
                }
                projects.add(objects);
                project_layout.addView(objects);

                objects.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        for(int i = 0; i< username.size(); i++){
                            Log.i("APP", "Launching one server " + i + " from button " + path_projects.get(objects.getId()));
                            loading.setVisibility(View.VISIBLE);

                            lastProjectUsed = objects.getId();
                            launchServer(username.get(i), password.get(i), ipAddressCode.get(i), path_projects.get(objects.getId()), noScreens.get(i), i==0, false);
                        }
                    }
                });

                Log.i("APP", "Finishing setting buttons " + path_projects);
            }
        }
    }

    private void nextControlsFromConnect(boolean available) throws InterruptedException {
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
            howManyMachinesAsked = 1;
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
            hostname_launch_machine.setText(null);
            password_launch_machine.setText(null);
            ipAddress_launch_machine.setText(null);

            howManyMachinesAsked++;
            if(howManyMachinesAsked <= noMachines){
                ipAddress_launch_machine.setVisibility(View.VISIBLE);
                launch_checkbox_info.setVisibility(View.VISIBLE);
                ipAddress_launch_machine.setVisibility(View.VISIBLE);
                if(howManyMachinesAsked%2 == 0){
                    title_launch_machine.setText("Info " + (howManyMachinesAsked)/2 + " machine to the right ");
                }else{
                    title_launch_machine.setText("Info " + (howManyMachinesAsked)/2 + " machine to the left ");
                }

            }else {
                Log.w("APP", "Going to find repository");
                launch_layout_machine.setVisibility(View.GONE);
                repository_register_layout.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
                launch_state_button.setVisibility(View.INVISIBLE);
                connect_state_button.setVisibility(View.INVISIBLE);
                STATE = REGISTER_REPOSITORY;

            }
            Log.i("APP", "Added new username "+ username);
            Log.i("APP", "Added new ip "+ ipAddressCode);
        }
    }

    private void clickBack(){
        Log.i("APP", "clicked back " + STATE);
        if(STATE==LAUNCH_INFO){
            howManyMachinesAsked--;
            Log.i("APP", "how many machines asked going back "+howManyMachinesAsked);
            if(howManyMachinesAsked<=0){
                noMachines_edit.setText(String.valueOf(noMachines));
                port_launch.setText(String.valueOf(portCode));
                portCode=null;
                hostname_launch_machine.setError(null);
                password_launch_machine.setError(null);
                hostname_launch_machine.setText(null);
                password_launch_machine.setText(null);
                ipAddress_launch_machine.setText(null);
                launch_layout_machine.setVisibility(View.GONE);
                launch_layout.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.GONE);
                nextButton.setVisibility(View.VISIBLE);
                ipAddress_launch.setText(ipAddressCode.get(0));
                ipAddressCode = new ArrayList<String>();
                username = new ArrayList<String>();
                password = new ArrayList<String>();
                path_projects = new ArrayList<String>();
                noScreens = new ArrayList<Integer>();
                for(int i=0; i<projects.size(); i++){
                    project_layout.removeView(projects.get(i));
                    projects.get(i).setOnClickListener(null);
                }
                projects = new ArrayList<Button>();
                //password_launch_machine.setText(null);
                STATE=LAUNCH;
            }else{
                hostname_launch_machine.setError(null);
                password_launch_machine.setError(null);
                hostname_launch_machine.setText(username.get(username.size()-1));
                password_launch_machine.setText(password.get(password.size()-1));
                ipAddress_launch_machine.setText(ipAddressCode.get(ipAddressCode.size()-1));

                username.remove(username.size()-1);
                password.remove(password.size()-1);

                if(howManyMachinesAsked > 1){
                    ipAddressCode.remove(ipAddressCode.size()-1);
                    ipAddress_launch_machine.setVisibility(View.VISIBLE);
                    launch_checkbox_info.setVisibility(View.VISIBLE);
                    title_ip_launch_machine.setVisibility(View.VISIBLE);
                    if(howManyMachinesAsked%2 == 0){
                        title_launch_machine.setText("Info " + (howManyMachinesAsked)/2 + " machine to the right ");
                    }else{
                        title_launch_machine.setText("Info " + (howManyMachinesAsked)/2 + " machine to the left ");
                    }

                }else {
                    ipAddress_launch_machine.setVisibility(View.GONE);
                    launch_checkbox_info.setVisibility(View.GONE);
                    title_ip_launch_machine.setVisibility(View.GONE);
                    title_launch_machine.setText("Info central machine");

                }
            }

        }else if(STATE==PROJECT){
            STATE=REGISTER_REPOSITORY;
            for(int i=0; i<projects.size(); i++){
                project_layout.removeView( projects.get(i));
            }
            projects=new ArrayList<Button>();
            projectPath.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
        }else if(STATE==REGISTER_PROJECT){
            STATE=PROJECT;
            connect_state_button.setVisibility(View.GONE);
            launch_state_button.setVisibility(View.GONE);
            project_register_layout.setVisibility(View.GONE);
            project_layout.setVisibility(View.VISIBLE);
            projectPath.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);

        }else if(STATE==REGISTER_REPOSITORY){
            Log.i("APP", "Going back from repository");
            noMachines_edit.setText(String.valueOf(noMachines));
            port_launch.setText(String.valueOf(portCode));
            portCode=null;
            title_launch_machine.setText("Info central machine");
            connect_state_button.setVisibility(View.VISIBLE);
            launch_state_button.setVisibility(View.VISIBLE);
            project_register_layout.setVisibility(View.GONE);
            launch_layout.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.GONE);
            project_layout.setVisibility(View.GONE);
            projectPath.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
            ipAddressCode = new ArrayList<String>();
            username = new ArrayList<String>();
            password = new ArrayList<String>();
            path_projects = new ArrayList<String>();
            noScreens = new ArrayList<Integer>();
            for(int i=0; i<projects.size(); i++){
                project_layout.removeView(projects.get(i));
                projects.get(i).setOnClickListener(null);
            }
            projects = new ArrayList<Button>();
            //password_launch_machine.setText(null);
            STATE=LAUNCH;
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
            if(launch_checkbox_info.isChecked()){
                String ipDevice = getPublicIPAddress();
                Log.w("MET", getPublicIPAddress());
                ipAddress_launch_machine.setText(ipDevice.substring(0,ipDevice.length()-2));
            }else{
                ipAddress_launch_machine.setText(null);
            }
        }
    }


    private void setIP(boolean hasLaunched) throws InterruptedException {
        if(checkIPValid(ipAddressCode.get(0))){

           Log.i("APP", "checking list " + projects);
            for(int i=0; i<projects.size(); i++){
                project_layout.removeView(projects.get(i));

                projects.get(i).setOnClickListener(null);
            }
            Log.w("APP","change layout " + ipAddressCode);
           setContentView(R.layout.main);
           Log.i("APP", "checking list " + projects);
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
                    try {
                        nextControlsFromConnect(false);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
                    try {
                        nextControlsFromConnect(true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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

        if(STATE!=PROJECT){
            Log.w("APP","change layout from  " + STATE);
            disconnectTablet();
            setContentView(R.layout.ip_setting);

            setMenuButtons(true);
        }else {
            Log.w("APP", "change layout from  Project " + STATE + " demo on " + DEMO_ON);
            disconnectTablet();

            loading.setVisibility(View.VISIBLE);
            launchServer(username.get(0), password.get(0), ipAddressCode.get(0), path_projects.get(lastProjectUsed), 0, false, true);

        }

    }

    private void backToSetIPProject() throws InterruptedException {
        setContentView(R.layout.ip_setting);

        setMenuButtons(false);
        STATE=PROJECT;
        project_layout.setVisibility(View.VISIBLE);
        launch_layout.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);
        projectPath.setVisibility(View.VISIBLE);
        launch_state_button.setVisibility(View.GONE);
        connect_state_button.setVisibility(View.GONE);
        Log.i("APP", "moving to list " + projects);

        project_layout.addView(projects.get(0));
        projects.get(0).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                for(int i = 0; i< username.size(); i++){
                    Log.i("APP", "Launching demo ");
                    loading.setVisibility(View.VISIBLE);

                    lastProjectUsed = projects.get(0).getId();
                    DEMO_ON=true;
                    projectRunning=1;
                    try {
                        launchDemo(projectRunning);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        for(int j=1; j<projects.size(); j++){
            project_layout.addView(projects.get(j));

            final int finalJ = j;
            projects.get(j).setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    for(int i = 0; i< username.size(); i++){
                        Log.i("APP", "Launching one server resetting" + i + " from button " + path_projects.get(projects.get(finalJ).getId()));
                        loading.setVisibility(View.VISIBLE);
                        lastProjectUsed=projects.get(finalJ).getId();
                        launchServer(username.get(i), password.get(i), ipAddressCode.get(i), path_projects.get(projects.get(finalJ).getId()), noScreens.get(i), i==0, false);
                        try {
                            TimeUnit.SECONDS.sleep(3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        Log.i("APP", "Finishing setting buttons " + path_projects + " and demo on " + DEMO_ON);

        if(DEMO_ON){
            Log.i("APP", "Going to next demo ");
            projectRunning++;
            if(projectRunning>3){
                Log.i("APP", "finishing demo ");
                DEMO_ON = false;
                projectRunning=0;
            }else{
                TimeUnit.SECONDS.sleep(2);
                launchDemo(projectRunning);
            }
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

    private void launchDemo(int projectToLaunch) throws InterruptedException {
        Log.i("APP", "Demo launching project: " + projectToLaunch);
        DEMO_ON = true;

        if(projectToLaunch>1){
            Log.i("APP", "waiting  for " + projects.get(projectToLaunch) + " path " + path_projects.get(projects.get(projectToLaunch).getId()));
            TimeUnit.SECONDS.sleep(5);
        }
        projects.get(projectToLaunch).performClick();
    }

    @SuppressLint("StaticFieldLeak")
    private void launchServer(final String user, final String password, final String host, final String path, final int noSockets, final boolean isMaster, final boolean goingBack){
        Log.w("LAU", "Start launch is MAster " + isMaster + " goingBack " + goingBack );
        final boolean[] resultSSH = new boolean[1];
        new AsyncTask<Integer, Void, String>(){
            @Override
            protected String doInBackground(Integer... params) {
                try {
                    Log.i("APP", "It has launched " + portBusyFromUs);
                    resultSSH[0] = executeSSHcommand( user,  password,  host, path, noSockets, isMaster, portBusyFromUs);
                    Log.w("LAU", "Result in " + resultSSH[0]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result){
                Log.w("APP", "Moving to result");

                if(!goingBack){
                    loading.setVisibility(View.GONE);
                    portBusyFromUs=true;
                    try {
                        setIP(true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        backToSetIPProject();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.execute(1);

    }

    @SuppressLint("StaticFieldLeak")
    private void testMachine(final String user, final String password, final String host, final String path, final int whatState){
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
                Log.w("APP", "Moving to result " + whatState);
                if(whatState==LAUNCH_INFO){
                    nextMachineInfo(resultSSH[0]);
                }else if(whatState==REGISTER_PROJECT){
                    registerNewPath(resultSSH[0]);
                }else if(whatState==REGISTER_REPOSITORY){
                    registerBasePath(resultSSH[0]);
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
                    resultSSH[0] = executeSSHcommand( user,  password,  host, path, 0,false, true);
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
                STATE=LAUNCH;
                    backToSetIP();
            }
        }.execute(1);

    }

    private boolean executeSSHcommand(String user, String password, String host, String path, int noSockets, boolean isMaster, boolean killServer){
        Log.w("LAU", "Start ssh with user " + user + " host " + host + " path " + path + " noSockets " + noSockets +
                " isMaster " + isMaster + " killServer " + killServer);
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

            channel.setXForwarding(true);

            int indexPublic=path.indexOf("public");
            String projectDirToChange = path;
            String projectDir = path;
            if(indexPublic!=-1){
                projectDir = path.substring(indexPublic+6);
                projectDirToChange = path.substring(0, indexPublic);
            }



            String finalCommand = "cd " + projectDirToChange +"; "+
                    "DIMENSIONS=$(DISPLAY=:0 xdpyinfo | grep dimensions: | awk '{print $2}');" +
                    "WIDTH=$(echo $DIMENSIONS | head -n1 | awk '{print $1;}');" +
                    "HEIGHT=$(echo $DIMENSIONS | head -n1 | awk '{print $2;}')" +
                    ".google-chrome \"data:text/html,<html><body><script>window.moveTo(0,0);" +
                    "window.resizeTo($(($WIDTH)),$HEIGHT);" +
                    "window.location='http://" + ipAddressCode.get(0) + ":" + portCode  + "';" +
                    "</script></body></html>;";
            if(isMaster && killServer) {
                finalCommand = "cd " + projectDirToChange +"; ./launch.sh -m -p " + portCode + " -d " + projectDir + " -b";
            }else if(killServer && !isMaster) {
                finalCommand = "cd " + projectDirToChange + "; ./killServer.sh";
            } else if (!killServer && isMaster) {
                finalCommand = "cd " + projectDirToChange +"; ./launch.sh -m -p " + portCode + " -n "+ noSockets + " -d " + projectDir;
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
        Log.w("LAU", "Start ssh with user " + user + " host " + host + " path " + path);
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


    @SuppressLint("StaticFieldLeak")
    private void demoAnimation(){
        Log.w("LAU", "Start kill");
        final boolean[] resultSSH = new boolean[1];
        new AsyncTask<Integer, Void, String>(){
            @Override
            protected String doInBackground(Integer... params) {
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                actualDemoAnimation();
            }
        }.execute(1);

    }

    private void actualDemoAnimation(){
        Log.i("APP", "Start animation");
        try {
            if(projectRunning == 1){
                Log.i("APP", "Running project 1 ");
                TimeUnit.SECONDS.sleep(10);
                transRight.performClick();
                TimeUnit.SECONDS.sleep(2);
                transRight.performClick();
                TimeUnit.SECONDS.sleep(2);
                transRight.performClick();
                TimeUnit.SECONDS.sleep(5);
                transUp.performClick();
                TimeUnit.SECONDS.sleep(1);
                transBackwards.performClick();
                TimeUnit.SECONDS.sleep(2);
                switchRotTrans.performClick();
                TimeUnit.SECONDS.sleep(5);
                rotYNeg.performClick();
                TimeUnit.SECONDS.sleep(2);
                rotYNeg.performClick();
                TimeUnit.SECONDS.sleep(2);
                rotZNeg.performClick();
                TimeUnit.SECONDS.sleep(10);
                reset.performClick();
                TimeUnit.SECONDS.sleep(10);
                switchCamera.performClick();
                TimeUnit.SECONDS.sleep(10);

            }else if(projectRunning == 2){
                Log.i("APP", "Running project 2 ");
                TimeUnit.SECONDS.sleep(10);
                switchCamera.performClick();
                TimeUnit.SECONDS.sleep(10);
                switchRotTrans.performClick();
                TimeUnit.SECONDS.sleep(2);
                rotXPos.performClick();
                TimeUnit.SECONDS.sleep(2);
                rotXPos.performClick();
                TimeUnit.SECONDS.sleep(5);
                rotZPos.performClick();
                TimeUnit.SECONDS.sleep(1);
                rotZPos.performClick();
                TimeUnit.SECONDS.sleep(2);
                switchRotTrans.performClick();
                TimeUnit.SECONDS.sleep(10);
                reset.performClick();
                TimeUnit.SECONDS.sleep(5);
                transRight.performClick();
                TimeUnit.SECONDS.sleep(2);
                transDown.performClick();
                TimeUnit.SECONDS.sleep(2);
                transForward.performClick();
                TimeUnit.SECONDS.sleep(10);
                switchCamera.performClick();
                TimeUnit.SECONDS.sleep(10);

            }else if(projectRunning == 3){
                Log.i("APP", "Running project 3 ");
                TimeUnit.SECONDS.sleep(10);
                transRight.performClick();
                TimeUnit.SECONDS.sleep(2);
                transRight.performClick();
                TimeUnit.SECONDS.sleep(2);
                transRight.performClick();
                TimeUnit.SECONDS.sleep(5);
                transUp.performClick();
                TimeUnit.SECONDS.sleep(1);
                transBackwards.performClick();
                TimeUnit.SECONDS.sleep(2);
                switchRotTrans.performClick();
                TimeUnit.SECONDS.sleep(5);
                rotYNeg.performClick();
                TimeUnit.SECONDS.sleep(2);
                rotYNeg.performClick();
                TimeUnit.SECONDS.sleep(2);
                rotZNeg.performClick();
                TimeUnit.SECONDS.sleep(10);
                reset.performClick();
                TimeUnit.SECONDS.sleep(10);
                switchCamera.performClick();
                TimeUnit.SECONDS.sleep(10);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.w("APP", "Finishing demo animation");
        if(projectRunning ==3){
            kill.performClick();
        }else{
            goBack.performClick();
        }
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



    private void setMenuButtons(boolean empty){
        STATE=LAUNCH;

        if(DEMO_ON!= true){
            DEMO_ON=false;
        }

        if(ipAddressCode == null || empty ) {
            ipAddressCode = new ArrayList<String>();
        }
        if(username == null || empty ) {
            username = new ArrayList<String>();
        }
        if(password == null || empty ) {
            password = new ArrayList<String>();
        }
        if(path_projects == null || empty ) {
            path_projects = new ArrayList<String>();
        }
        if(noScreens == null|| empty ) {
            noScreens = new ArrayList<Integer>();
        }
        if(projects == null|| empty ){
            projects = new ArrayList<Button>();
        }
        if(portBusyFromUs != true){
            portBusyFromUs = false;
        }


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
        sameRepository = (CheckBox) findViewById(R.id.same_repository);
        sameRepository.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sameRepositoryFill();
            }
        });

        //Register project
        repository_register_layout = (LinearLayout) findViewById(R.id.layout_find_repository);
        registerRepository = (EditText) findViewById(R.id.repository);

        Log.i("APP", "Initial state " + STATE);
    }

    private void setControlButtons( boolean hasLaunched) throws InterruptedException {

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
                portBusyFromUs = false;
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


        if(DEMO_ON){
            demoAnimation();
        }
    }
}

