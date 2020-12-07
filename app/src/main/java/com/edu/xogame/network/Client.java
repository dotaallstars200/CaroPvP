package com.edu.xogame.network;

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

    public void startGame() {
        startActivity.execute();
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(hostAddress, PORT));
            player = new RealPlayer(socket);
            player = new RealPlayer(socket);
            Thread thread = new Thread(player);
            thread.start();
            player.sendInvite();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        try {
            socket.close();
            player.kill();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
