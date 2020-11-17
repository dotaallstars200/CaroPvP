package com.edu.xogame.views;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.edu.xogame.datastructure.CellPosition;
import com.edu.xogame.GamePlayActivity;

import java.util.HashMap;

public class Board  {

    public static final int NUMBER_COLUMNS = 50;
    public static final int NUMBER_ROWS = 50;
    private final TableLayout tableLayout;
    private final int[][] trackTable;
    private boolean isTurnO = true;
    private final HashMap<Integer, Cell> checkedCells;

    public Board(Context context) {
        trackTable = new int[NUMBER_ROWS][NUMBER_COLUMNS];
        tableLayout = new TableLayout(context);
        checkedCells = new HashMap<>();
        createBoard(context);

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
        if (isTurnO) {
            trackTable[rowPos][colPos] =  1;
            cell.check(Cell.O_IMAGE);
        } else {
            trackTable[rowPos][colPos] =  -1;
            cell.check(Cell.X_IMAGE);
        }
        checkedCells.put(cell.hashCode(), cell);

        // check if the game is draw
        if (checkedCells.size() == getTotalCells())
            Log.e("DRAW", "DRAW");

        boolean gameResult = GamePlayActivity.checkWin(cell.getCellPosition(), trackTable[rowPos][colPos], trackTable);
        if (gameResult)
            Log.e("WIN", "WIN");
        isTurnO = !isTurnO;
    }
}
