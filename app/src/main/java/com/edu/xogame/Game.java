package com.edu.xogame;

import android.app.Activity;
import android.util.Log;
import android.widget.HorizontalScrollView;

import androidx.appcompat.app.AlertDialog;

import com.edu.xogame.R;
import com.edu.xogame.activities.GamePlayActivity;
import com.edu.xogame.database.DBManager;
import com.edu.xogame.database.DatabaseHelper;
import com.edu.xogame.datastructure.CellPosition;
import com.edu.xogame.players.Player;
import com.edu.xogame.players.PlayerBot;
import com.edu.xogame.views.Board;

import java.util.Arrays;


public class Game {
    public static final int WIN_NUMBERS = 5;
    private final boolean goFirst;
    private Player opponent;
    private final Board board;
    private boolean isTurnO = true; // O always goes first
    private final Activity activity;

    private DBManager dbManager;

    public Game(Activity activity, boolean goFirst) {
        this.goFirst = goFirst;
        this.activity = activity;
        board = new Board(activity.getApplicationContext(), this);
    }

    public Player getOpponent() {
        return opponent;
    }

    public void start() {
        HorizontalScrollView horizontalScrollView = activity.findViewById(R.id.horizontalSrcollView);
        horizontalScrollView.addView(board.getTableLayout());
        if (opponent instanceof PlayerBot) {
            if (!goFirst)
                opponent.makeMove();
        }
    }

    public void show() {
        HorizontalScrollView horizontalScrollView = activity.findViewById(R.id.horizontalSrcollView);
        horizontalScrollView.addView(board.getTableLayout());
        Log.e("<<BOARDGAMESHOW>>", Arrays.deepToString(board.getTrackTable()));
    }

    public void endGame(String result, boolean showDialog) {
        String resultToStore = "";
        String opponentToStore = "";

        if (showDialog) {

            if (opponent instanceof PlayerBot) {
                IFunction positiveFunc = () -> {
                    removeBoardFromActivity();
                    ((GamePlayActivity) (activity)).newGame(!goFirst, opponent);
                };

                IFunction negativeFunc = activity::finish;

                Utilities.createDialog(result, "Do you want to play a new game?",
                        "YES", "NO", activity, positiveFunc, negativeFunc);
            } else {
                IFunction negativeFunc = activity::finish;
                Utilities.createDialog(result, "Bấm ok để thoát!",
                        null, "OK", activity, null, negativeFunc);
            }

            // Lưu kết quả trận đấu
            if (result.equals("YOU WIN THE GAME!!!")) {
                resultToStore = "Thắng";
            }
            else if (result.equals("OPPONENT WIN THE GAME!!!")) {
                resultToStore = "Thua";
            }
            else if (result.equals("DRAW!!!")) {
                resultToStore = "Hoà";
            }
            else {
                result = "NONE";
            }

            // Lưu opponent
            if (opponent instanceof PlayerBot) {
                opponentToStore = "BOT";
            }
            else {
                opponentToStore = "PLAYER";
            }

            dbManager = new DBManager(activity.getApplicationContext());
            dbManager.open();
            dbManager.insert(Arrays.deepToString(board.getTrackTable()), resultToStore, opponentToStore);
            dbManager.close();

        } else {
            removeBoardFromActivity();
        }
    }
    
    private void removeBoardFromActivity() {
        HorizontalScrollView horizontalScrollView = activity.findViewById(R.id.horizontalSrcollView);
        horizontalScrollView.removeView(board.getTableLayout());
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public Board getBoard() {
        return board;
    }

    public boolean isTurnO() {
        return isTurnO;
    }

    public boolean isMyTurn() {
        return goFirst == isTurnO;
    }

    public void changeTurn() {
        isTurnO = !isTurnO;

        if (!isMyTurn() && opponent instanceof PlayerBot) {
            opponent.makeMove();
        }
    }

    public boolean checkWin(CellPosition anchor, int sideChecking, int[][] trackTable) {

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

    public int getPointByDirection(CellPosition anchor, Direction direction, int sideChecking, int[][] trackTable) {
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

    private CellPosition getCellPositionFactorByDirection(Direction direction) {
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

    public enum Direction {
        TOP, BOT, LEFT, RIGHT, LEFT_TOP, RIGHT_BOT, LEFT_BOT, RIGHT_TOP,
    }
}
