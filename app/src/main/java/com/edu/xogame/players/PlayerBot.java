package com.edu.xogame.players;

import com.edu.xogame.datastructure.CellPosition;
import com.edu.xogame.views.Board;
import com.edu.xogame.views.Cell;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerBot extends Player {

    private final Board board;
    private final int DEPTH = 3;

    public PlayerBot(Board board) {
        super(board);
        this.board = board;
    }


    private HashMap<Integer, CellPosition> getAvailableCellsPos(int depth) {

        HashMap<Integer, Cell> checkedCells = board.getCheckedCells();
        HashMap<Integer, CellPosition> availableCells = new HashMap<>();

        for (Cell cell : checkedCells.values()) {

            ArrayList<CellPosition> availableCellsPosAround = getAvailableCellsPosAround(cell, depth);
            availableCellsPosAround.forEach(availableCellPos -> {
                if (!availableCells.containsKey(availableCellPos.hashCode()))
                    availableCells.put(availableCellPos.hashCode(), availableCellPos);
            });
        }

        return availableCells;
    }

    private ArrayList<CellPosition> getAvailableCellsPosAround(Cell anchor, int depth) {

        int anchorRowPos = anchor.getCellPosition().row;
        int anchorColPos = anchor.getCellPosition().column;
        int[][] trackTable = board.getTrackTable();

        int leftBoundary = Math.max(anchorColPos - depth, 0);
        int rightBoundary = Math.min(anchorColPos + depth, Board.NUMBER_COLUMNS - 1);
        int topBoundary = Math.max(anchorRowPos - depth, 0);
        int botBoundary = Math.min(anchorRowPos + depth, Board.NUMBER_ROWS - 1);

        ArrayList<CellPosition> availableCellsAround = new ArrayList<>();

        for (int i = topBoundary; i <= botBoundary; ++i) {
            for (int j = leftBoundary; j <= rightBoundary; ++j) {
                if (trackTable[i][j] == 0)
                    availableCellsAround.add(new CellPosition(i, j));
            }
        }

        return availableCellsAround;
    }
}
