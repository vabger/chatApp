package com.example.chatapp.Config;

import android.util.Log;

import com.example.chatapp.GlobalVars;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketIoClient {
    static Socket socket;

    public static Socket getSocket(){
        if(socket==null){
            try {
                socket = IO.socket("http://10.0.2.2:5000");
                socket.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return socket;
    }

}
