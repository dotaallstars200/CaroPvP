package com.edu.xogame.players;

import android.os.Handler;

import com.edu.xogame.views.Board;

public abstract class Player {

    protected Board board;
    protected Handler handler;

    public Player(Handler handler) {
        this.handler = handler;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void makeMove() { }
}
