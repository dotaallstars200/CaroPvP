package com.edu.xogame.datastructure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CellPosition {
    public int row;
    public int column;

    public CellPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public CellPosition(CellPosition cell) {
        this.row = cell.row;
        this.column = cell.column;
    }

    @NonNull
    @Override
    public String toString() {
        return row + " " + column;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        CellPosition another = (CellPosition) obj;
        assert another != null;
        return another.column == column && another.row == row;
    }


    @Override
    public int hashCode() {
        return (row + " " + column).hashCode();
    }
}
