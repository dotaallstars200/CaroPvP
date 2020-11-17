package com.edu.xogame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.HorizontalScrollView;

import com.edu.xogame.datastructure.CellPosition;
import com.edu.xogame.views.Board;

public class GamePlayActivity extends AppCompatActivity {

    private Board board;
    private static final int WIN_NUMBERS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        board = new Board(GamePlayActivity.this);
        HorizontalScrollView horizontalScrollView = findViewById(R.id.horizontalSrcollView);
        horizontalScrollView.addView(board.getTableLayout());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static boolean checkWin(CellPosition anchor, int sideChecking, int[][] trackTable) {

        int onSameAxis = 0;
        int point = 1;
        for (Direction direction : Direction.values()) {
            ++onSameAxis;
            point += getPointByDirection(anchor, direction, sideChecking, trackTable);
            if (point >= WIN_NUMBERS)
                return true;
            // on the other axis, then reset point
            if (onSameAxis == 2) {
                point = 1; // reset
                onSameAxis = 0;
            }
        }

        return false;
    }

    private static int getPointByDirection(CellPosition anchor, Direction direction, int sideChecking, int[][] trackTable) {
        int point = 0;
        CellPosition pointer = new CellPosition(anchor);
        CellPosition directionFactor = getCellPositionFactorByDirection(direction);
        do {
            assert directionFactor != null;
            int rowPos = pointer.row += directionFactor.row;
            int colPos = pointer.column += directionFactor.column;
            if (rowPos < 0 || colPos < 0 || rowPos >= Board.NUMBER_ROWS || colPos >= Board.NUMBER_COLUMNS)
                break;

            if (trackTable[rowPos][colPos] == sideChecking)
                ++point;
            else break;
        } while (point < WIN_NUMBERS - 1);

        return point;
    }

    private static CellPosition getCellPositionFactorByDirection(Direction direction) {
        switch (direction) {
            case TOP:
                return new CellPosition(1, 0);

            case BOT:
                return new CellPosition(-1, 0);

            case LEFT:
                return new CellPosition(0, -1);

            case RIGHT:
                return new CellPosition(0, 1);

            case LEFT_BOT:
                return new CellPosition(-1, -1);

            case LEFT_TOP:
                return new CellPosition(1, -1);

            case RIGHT_BOT:
                return new CellPosition(-1, 1);

            case RIGHT_TOP:
                return new CellPosition(1, 1);
        }

        return null;
    }

    private enum Direction {
        TOP, BOT, LEFT, RIGHT, LEFT_TOP, RIGHT_BOT, LEFT_BOT, RIGHT_TOP,
    }
}