package com.edu.xogame.network;


import com.edu.xogame.IFunction;
import com.edu.xogame.players.RealPlayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Host extends Thread {

    private ServerSocket serverSocket;
    private Socket socket;
    private RealPlayer player;
    private static final int PORT = 6996;
    private final IFunction startActivity;

    public Host(IFunction startActivity) {
        this.startActivity = startActivity;
    }

    public RealPlayer getPlayer() {
        return player;
    }

    public void startGame() {
        startActivity.execute();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            socket = serverSocket.accept();
            player = new RealPlayer(socket);
            Thread thread = new Thread(player);
            thread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        try {
            serverSocket.close();
            socket.close();
            player.kill();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
