package com.example.tablet_lg_graphic;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.net.URISyntaxException;

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



    // UI references.
    private EditText ipAddress;
    private Socket mSocket;
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
    private Button mEmailSignInButton;
    private boolean translationOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ip_setting);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        ipAddress = (EditText) findViewById(R.id.ipAddress);

        ipAddress.setText("192.168.1.63");
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setIP();
            }
        });


    }

    private void setIP() {
        ipAddressCode = ipAddress.getText().toString();
       if(checkIPValid(ipAddressCode)){
           Log.i("APP","change layout " + ipAddressCode);
           setContentView(R.layout.main);

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
               mSocket = IO.socket("http://" + ipAddressCode + ":3000/");
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

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        ipAddress = (EditText) findViewById(R.id.ipAddress);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setIP();
            }
        });
        ipAddress.setText(ipAddressCode);
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
    public void onClick11 (View v) {
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

    public void executeSSHcommand(){
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
}

