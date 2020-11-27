package com.edu.xogame.players;

import android.app.Activity;

import com.edu.xogame.activities.GamePlayActivity;
import com.edu.xogame.views.Board;

public abstract class Player {

    private Board board;

    public Player(Board board) {
        this.board = board;
    }

    public void makeMove() { }
}
