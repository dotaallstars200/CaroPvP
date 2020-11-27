package com.edu.xogame.players;

import android.os.Handler;
import android.util.Log;

import com.edu.xogame.datastructure.CellPosition;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;


public class RealPlayer extends Player implements Runnable {

    private final Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private CellPosition moveToMake;

    public RealPlayer(Socket socket) {
        this.socket = socket;

        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMove(CellPosition cellPosition) {
        try {

            outputStream.writeUTF("MOVE/" + cellPosition.row + "," + cellPosition.column);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        while (true) {
            try {

                String[] receiveMessage = inputStream.readUTF().split("/");

                if (receiveMessage[0].equals("MOVE")) {
                    String[] position = receiveMessage[1].split(",");
                    moveToMake = new CellPosition(Integer.parseInt(position[0]), Integer.parseInt(position[1]));
                    makeMove();
                }

            } catch (SocketException socketException) {

                try {
                    inputStream.close();
                    outputStream.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void makeMove() {
        handler.post(() -> board.checkCell(board.getCell(moveToMake)));

    }
}
