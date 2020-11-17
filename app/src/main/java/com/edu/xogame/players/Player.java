package com.edu.xogame.players;

import com.edu.xogame.views.Board;

public abstract class Player {

    private Board board;

    public Player(Board board) {
        this.board = board;
    }
}
