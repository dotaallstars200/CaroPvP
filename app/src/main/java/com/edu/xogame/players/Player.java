package com.edu.xogame.players;


import android.os.Handler;

import com.edu.xogame.Utilities;

import com.edu.xogame.views.Board;

public abstract class Player {

    protected Board board;
    protected Handler handler = Utilities.HANDLER;


    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }


    public void makeMove() { }
}
