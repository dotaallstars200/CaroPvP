package com.edu.xogame.network;

import android.os.Handler;

import com.edu.xogame.IFunction;
import com.edu.xogame.players.RealPlayer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client extends Thread {

    private Socket socket;
    private String hostAddress;
    private RealPlayer player;
    private final IFunction startActivity;

    private static final int PORT = 6996;

    public RealPlayer getPlayer() {
        return player;
    }

    public Client(InetAddress address, IFunction startActivity) {
        hostAddress = address.getHostAddress();
        socket = new Socket();
        this.startActivity = startActivity;
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(hostAddress, PORT));
            player = new RealPlayer(socket);
            startActivity.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
