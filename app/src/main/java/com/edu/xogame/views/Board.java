package com.edu.xogame.views;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.edu.xogame.activities.GameFragment;
import com.edu.xogame.datastructure.CellPosition;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Stack;

public class Board {

    public static final int NUMBER_COLUMNS = 25;
    public static final int NUMBER_ROWS = 25;

    private final int[][] trackTable;
    private final HashMap<Integer, Cell> checkedCells;
    private final HashMap<Integer, Cell> cells;
    private final GameFragment game;
    private Stack<Integer> num_order; // Thu tu xuat hien cac check tren ban co
    private TableLayout tableLayout;

    public Board(GameFragment game) {
        trackTable = new int[NUMBER_ROWS][NUMBER_COLUMNS];

        checkedCells = new HashMap<>();
        cells = new HashMap<>();
        num_order = new Stack<>();
        initTrackTable();
        this.game = game;
    }

    private void initTrackTable() {
        for (int i = 0; i < Board.NUMBER_ROWS; ++i) {
            for (int j = 0; j < Board.NUMBER_COLUMNS; ++j) {
                trackTable[i][j] = 0;
            }
        }
    }

    public int[][] getTrackTable() {
        return trackTable;
    }

    public void setTrackTable(int[][] boardGameArray) {
        for (int i = 0; i < NUMBER_ROWS; i++) {
            for (int j = 0; j < NUMBER_COLUMNS; j++) {
                trackTable[i][j] = boardGameArray[i][j];
            }
        }
    }

    public void setCell() {
        Cell cell;
        CellPosition cellPosition;
        for (int i = 0; i < NUMBER_ROWS; i++) {
            for (int j = 0; j < NUMBER_COLUMNS; j++) {
                cellPosition = new CellPosition(i, j);

                cell = cells.get(cellPosition.hashCode());
                if (trackTable[i][j] == 1) {
                    cell.check(Cell.O_IMAGE);

                } else if (trackTable[i][j] == -1) {
                    cell.check(Cell.X_IMAGE);

                }
                checkedCells.put(cell.hashCode(), cell);
                num_order.push(cell.hashCode());
            }
        }

    }

    public HashMap<Integer, Cell> getCheckedCells() {
        return checkedCells;
    }

    public Cell getCell(CellPosition cellPosition) {
        return cells.get(cellPosition.hashCode());
    }

    public GameFragment getGame() {
        return game;
    }

    public int getTotalCells() {
        return NUMBER_COLUMNS * NUMBER_ROWS;
    }

    public void checkCell(Cell cell) {

        int rowPos = cell.getCellPosition().row;
        int colPos = cell.getCellPosition().column;

        // if cell is checked, then ignore it
        if (trackTable[rowPos][colPos] != 0)
            return;

        // handle turn
        if (game.isTurnO()) {
            trackTable[rowPos][colPos] = 1;
            cell.check(Cell.O_IMAGE);
            game.SetTurnPlay(false);
        } else {
            trackTable[rowPos][colPos] = -1;
            cell.check(Cell.X_IMAGE);
            game.SetTurnPlay(true);
        }
        checkedCells.put(cell.hashCode(), cell);
        num_order.push(cell.hashCode());

        // check if the game is draw
        if (checkedCells.size() == getTotalCells())
            getGame().endGame("Hoà.", true);

        boolean gameResult = game.checkWin(cell.getCellPosition(), trackTable[rowPos][colPos], trackTable);
        if (gameResult) {
            String side = game.isMyTurn() ? "Bạn" : "Đối thủ";
            getGame().endGame(side + " đã thắng.", true);
        }

        game.changeTurn();
    }


    public void checkCellWin() {
        if (num_order.empty() || num_order.size() == 1)//Board empty
            return;
        Cell tempCell;
        boolean testResult;
        int rowPos;
        int colPos;
        for (; !num_order.empty(); ) {
            tempCell = checkedCells.get(num_order.pop());
            rowPos = tempCell.getCellPosition().row;
            colPos = tempCell.getCellPosition().column;
            testResult = game.checkWin(tempCell.getCellPosition(), trackTable[rowPos][colPos], trackTable);
            if (testResult) {
                if (trackTable[rowPos][colPos] == 1) {
                    tempCell.check(Cell.O_Win_IMAGE);
                } else if (trackTable[rowPos][colPos] == -1) {
                    tempCell.check(Cell.X_Win_IMAGE);
                }
            }
        }


    }

    public void uncheckCell() {
        if (num_order.empty() || num_order.size() == 1)//Board empty
            return;

        Cell checkofBot = checkedCells.get(num_order.pop());
        Cell checkofPlayer = checkedCells.get(num_order.pop());
        checkofBot.uncheck();
        checkofPlayer.uncheck();
        // update trackTable
        trackTable[checkofBot.getCellPosition().row][checkofBot.getCellPosition().column] = 0;
        trackTable[checkofPlayer.getCellPosition().row][checkofPlayer.getCellPosition().column] = 0;

    }

    public View getTableLayout() {
        return tableLayout;
    }

    public HashMap<Integer, Cell> getCells() {
        return cells;
    }

    public void setTableLayout(TableLayout tableLayout) {
        this.tableLayout = tableLayout;
    }
}
