package com.edu.xogame.players;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.edu.xogame.Utilities;
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

    private static final String MOVE = "MOVE";
    private static final String INVITE = "INVITE";
    private static final String ACCEPT = "ACCEPT";
    private static final String DENY = "DENY";

    private boolean isRunning = true;

    public RealPlayer(Socket socket) {
        this.socket = socket;

        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kill() throws IOException {
        isRunning = false;
        socket.close();
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

        while (isRunning) {
            try {

                String[] receiveMessage = inputStream.readUTF().split("/");
                String message = receiveMessage[0];
                Log.e("Connect", message);
                switch (message) {

                    case MOVE:
                        String[] position = receiveMessage[1].split(",");
                        moveToMake = new CellPosition(Integer.parseInt(position[0]), Integer.parseInt(position[1]));
                        makeMove();
                        break;
                    case INVITE:
                        if (Utilities.IS_AVAILABLE) {
                            outputStream.writeUTF(ACCEPT);
                            Utilities.IS_AVAILABLE = false;
                            if (Utilities.HOST != null)
                                Utilities.HOST.startGame();
                            else
                                Utilities.CLIENT.startGame();
                        } else {
                            outputStream.writeUTF(DENY);
                        }
                        outputStream.flush();
                        break;
                    case ACCEPT:
                        if (Utilities.IS_AVAILABLE) {
                            if (Utilities.HOST != null)
                                Utilities.HOST.startGame();
                            else
                                Utilities.CLIENT.startGame();
                            Utilities.IS_AVAILABLE = false;
                        } else
                            throw new SocketException("Connection error");
                    case DENY:
                        break;

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

    public void sendInvite() {
        try {

            outputStream.writeUTF(INVITE);
            outputStream.flush();
            Log.e("Connect", "ASFASF");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void makeMove() {
        handler.post(() -> board.checkCell(board.getCell(moveToMake)));

    }
}
