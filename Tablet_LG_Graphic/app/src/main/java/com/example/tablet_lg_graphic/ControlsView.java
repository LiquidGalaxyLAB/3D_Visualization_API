package com.example.tablet_lg_graphic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class ControlsView extends AppCompatActivity {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.63:3000/");
        } catch (URISyntaxException e) {}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button up = (Button)  findViewById(R.id.upBut);
        Log.i("APP", "Changed class");
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateUp();
            }
        });

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

    private void disconnectTablet(){
        mSocket.disconnect();
        Log.i("SER", "Disconnected");
    }

    private void attemptLogin() {

        mSocket.connect();
        Log.i("SER", "connected");

        mSocket.emit("signalTablet", "hey" );
        Log.i("SER", "Signal Tablet");

    }
}
