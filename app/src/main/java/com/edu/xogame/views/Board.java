package com.edu.xogame.views;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.edu.xogame.Game;
import com.edu.xogame.datastructure.CellPosition;

import java.util.HashMap;
import java.util.Stack;

public class Board  {

    public static final int NUMBER_COLUMNS = 25;
    public static final int NUMBER_ROWS = 25;

    private final TableLayout tableLayout;
    private final int[][] trackTable;
    private final HashMap<Integer, Cell> checkedCells;
    private final HashMap<Integer, Cell> cells;
    private final Game game;
    private Stack<Integer> num_order; // Thu tu xuat hien cac check tren ban co
    public Board(Context context, Game game) {
        trackTable = new int[NUMBER_ROWS][NUMBER_COLUMNS];
        tableLayout = new TableLayout(context);
        checkedCells = new HashMap<>();
        cells = new HashMap<>();
        num_order = new Stack<>();
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
        num_order.push(cell.hashCode());

        // check if the game is draw
        if (checkedCells.size() == getTotalCells())
            getGame().endGame("DRAW!!!", true);

        boolean gameResult = game.checkWin(cell.getCellPosition(), trackTable[rowPos][colPos], trackTable);
        if (gameResult) {
            String side = game.isMyTurn() ? "YOU" : "OPPONENT";
            getGame().endGame(side , true);
        }

        game.changeTurn();
    }
    public void uncheckCell()
    {
        if (num_order.empty() || num_order.size() == 1)//Board empty
            return;

        Cell checkofBot = checkedCells.get(num_order.pop());
        Cell checkofPlayer = checkedCells.get(num_order.pop());
        checkofBot.uncheck();
        checkofPlayer.uncheck();
        // update trackTable
        trackTable[checkofBot.getCellPosition().row][checkofBot.getCellPosition().column]=0;
        trackTable[checkofPlayer.getCellPosition().row][checkofPlayer.getCellPosition().column]=0;

    }

}
