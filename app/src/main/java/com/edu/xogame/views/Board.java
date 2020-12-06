package com.edu.xogame.views;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.edu.xogame.Game;
import com.edu.xogame.datastructure.CellPosition;

import java.util.HashMap;

public class Board  {

    public static final int NUMBER_COLUMNS = 50;
    public static final int NUMBER_ROWS = 50;

    private final TableLayout tableLayout;
    private final int[][] trackTable;
    private final HashMap<Integer, Cell> checkedCells;
    private final HashMap<Integer, Cell> cells;
    private final Game game;

    public Board(Context context, Game game) {
        trackTable = new int[NUMBER_ROWS][NUMBER_COLUMNS];
        tableLayout = new TableLayout(context);
        checkedCells = new HashMap<>();
        cells = new HashMap<>();
        createBoard(context);
        this.game = game;
    }

    private void createBoard(Context context) {
        tableLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));// assuming the parent view is a LinearLayout
        tableLayout.removeAllViewsInLayout();

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < NUMBER_ROWS; i++) {
            TableRow tr = new TableRow(context);
            tr.setLayoutParams(tableParams);

            for (int j = 0; j < NUMBER_COLUMNS; j++) {
                Cell cell = new Cell(context, this, new CellPosition(i, j));
                cells.put(cell.hashCode(), cell);
                cell.setLayoutParams(rowParams);
                tr.addView(cell);
                trackTable[i][j] = 0;
            }

            tableLayout.addView(tr);
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
        return;
    }

    public void setCell(Context context) {
        Cell cell;
        CellPosition cellPosition;
        for (int i = 0; i < NUMBER_ROWS; i++) {
            for (int j = 0; j < NUMBER_COLUMNS; j++) {
                cellPosition = new CellPosition(i, j);
                cell = cells.get(cellPosition.hashCode());
                if (trackTable[i][j] == 1) {
                    cell.check(Cell.O_IMAGE);
                }
                else if (trackTable[i][j] == -1){
                    cell.check(Cell.X_IMAGE);
                }
            }
        }
    }

    public HashMap<Integer, Cell> getCheckedCells() {
        return checkedCells;
    }

    public Cell getCell(CellPosition cellPosition) { return cells.get(cellPosition.hashCode()); }

    public Game getGame() {
        return game;
    }

    public int getTotalCells() { return NUMBER_COLUMNS * NUMBER_ROWS; }

    public TableLayout getTableLayout() {
        return tableLayout;
    }

    public void checkCell(Cell cell) {

        int rowPos = cell.getCellPosition().row;
        int colPos = cell.getCellPosition().column;

        // if cell is checked, then ignore it
        if (trackTable[rowPos][colPos] != 0)
            return;

        // handle turn
        if (game.isTurnO()) {
            trackTable[rowPos][colPos] =  1;
            cell.check(Cell.O_IMAGE);
        } else {
            trackTable[rowPos][colPos] =  -1;
            cell.check(Cell.X_IMAGE);
        }
        checkedCells.put(cell.hashCode(), cell);

        // check if the game is draw
        if (checkedCells.size() == getTotalCells())
            getGame().endGame("DRAW!!!", true);

        boolean gameResult = game.checkWin(cell.getCellPosition(), trackTable[rowPos][colPos], trackTable);
        if (gameResult) {
            String side = game.isMyTurn() ? "YOU" : "OPPONENT";
            getGame().endGame(side + " WIN THE GAME!!!", true);

        }

        game.changeTurn();
    }
}
