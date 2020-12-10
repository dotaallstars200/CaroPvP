package com.edu.xogame.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.edu.xogame.IFunction;
import com.edu.xogame.R;
import com.edu.xogame.Utilities;
import com.edu.xogame.activities.GamePlayActivity;
import com.edu.xogame.database.DBManager;
import com.edu.xogame.database.DatabaseHelper;
import com.edu.xogame.activities.MultiPlayerActivity;
import com.edu.xogame.datastructure.CellPosition;
import com.edu.xogame.players.Player;
import com.edu.xogame.players.PlayerBot;
import com.edu.xogame.views.Board;
import com.edu.xogame.views.Cell;

import java.util.Arrays;


public class GameFragment extends Fragment {
    public static final int WIN_NUMBERS = 5;
    private final boolean goFirst;
    private Player opponent;
    private final Board board;
    private boolean isTurnO = true; // O always goes first
    private Activity activity;
    ProgressBar progressBar;
    private static final int MAX_PROGRESS = 100;
    private static final int PROGRESS_STEP = 1;
    int sumProgress = 0;
    int maxValue = 10;
    boolean isToPlay;
    public boolean isRunning;
    Thread myBackgroundThread;
    MediaPlayer mediaPlayer;
    private DBManager dbManager;

    public GameFragment(boolean goFirst, boolean isToPlay) {
        this.goFirst = goFirst;
        board = new Board(this);
        isRunning = true;
        this.isToPlay = isToPlay;
    }

    public Player getOpponent() {
        return opponent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Activities containing this fragment must implement interface: MainCallbacks

        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layout_menu = (LinearLayout) inflater.inflate(R.layout.layout_board, null );
        progressBar = activity.findViewById(R.id.progressBar);
        TableLayout tableLayout = layout_menu.findViewById(R.id.tableLayout);
        tableLayout.removeAllViewsInLayout();

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < Board.NUMBER_ROWS; i++) {
            TableRow tr = new TableRow(layout_menu.getContext());
            tr.setLayoutParams(tableParams);

            for (int j = 0; j < Board.NUMBER_COLUMNS; j++) {
                Cell cell = new Cell(layout_menu.getContext(), board, new CellPosition(i, j));
                board.getCells().put(cell.hashCode(), cell);
                cell.setLayoutParams(rowParams);
                tr.addView(cell);
            }
            tableLayout.addView(tr);
        }
        board.setTableLayout(tableLayout);
        if (this.isToPlay)
            start();
        else
            showHistory();
        return layout_menu;
    }

    public void soundWin() {
        mediaPlayer = MediaPlayer.create(this.activity, R.raw.votay);
        mediaPlayer.start();
    }

    public void soundLose() {
        mediaPlayer = MediaPlayer.create(this.activity, R.raw.tiengoh);
        mediaPlayer.start();
    }

    public void showHistory() {
        getBoard().setCell();
        getBoard().checkCellWin();
    }

    public void start() {
        startTimer();
        if (opponent instanceof PlayerBot) {
            if (!goFirst)
                opponent.makeMove();
        }
    }

    public void remake() {
        if (!(activity instanceof GamePlayActivity))
            return;
        GamePlayActivity gamePlayActivity = (GamePlayActivity) (activity);
        gamePlayActivity.removeBoardFromActivity();
        gamePlayActivity.newGame(!goFirst, opponent);
    }

    public void undo() {
        board.uncheckCell();
    }

    public void endGame(String result, boolean showDialog) {
        String resultToStore = "";
        String opponentToStore = "";
        isRunning = false;


        board.checkCellWin(); // To mau cac o chien thang

        if (showDialog) {

            // Lưu kết quả trận đấu
            if (result.equals("Bạn đã thắng.")) {
                soundWin();
                resultToStore = "Thắng";
            } else if (result.equals("Đối thủ đã thắng.")) {
                soundLose();
                resultToStore = "Thua";
            } else if (result.equals("Hoà.")) {
                resultToStore = "Hoà";
            } else {
                resultToStore = "NONE";
            }

            // Lưu opponent
            if (opponent instanceof PlayerBot) {
                opponentToStore = "BOT";
            } else {
                opponentToStore = "PLAYER";
            }

            dbManager = new DBManager(activity.getApplicationContext());
            dbManager.open();
            dbManager.insert(Arrays.deepToString(board.getTrackTable()), resultToStore, opponentToStore);
            dbManager.close();

            if (opponent instanceof PlayerBot) {

                IFunction positiveFunc = () -> {
                    GamePlayActivity gamePlayActivity = (GamePlayActivity) (activity);
                    gamePlayActivity.removeBoardFromActivity();
                    gamePlayActivity.newGame(!goFirst, opponent);
                };

                IFunction negativeFunc = activity::finish;

                Utilities.createDialog(result, "Bạn có muốn bắt đầu game mới không?",
                        "Đồng Ý", "Không", activity, positiveFunc, negativeFunc);
                ((GamePlayActivity) (activity)).updatePoint(result);
            } else {
                IFunction negativeFunc = activity::finish;
                Utilities.createDialog(result, "Bấm ok để thoát!",
                        null, "OK", activity, null, negativeFunc);
                MultiPlayerActivity.disconnect(activity);
            }

        } else {
            GamePlayActivity gamePlayActivity = (GamePlayActivity) (activity);
            gamePlayActivity.removeBoardFromActivity();
        }

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
        sumProgress = 0;
        if (!isMyTurn() && opponent instanceof PlayerBot) {
            opponent.makeMove();
        }

    }
    @SuppressLint("SetTextI18n")
    public void SetTurnPlay(boolean isTurnO){
        ((GamePlayActivity) (activity)).updateTurn(isTurnO);
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


    public void startTimer() {
        if (!isRunning) {
            return;
        }
        sumProgress = 0;
        progressBar.setMax(MAX_PROGRESS);
        progressBar.setVisibility(View.VISIBLE);
        myBackgroundThread = new Thread(backgroundTask, "bgTask");
        myBackgroundThread.start();
    }

    private final Runnable foregroundRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                progressBar.setProgress((int) ((float) sumProgress / maxValue * 100));

                if (sumProgress >= maxValue) {
                    progressBar.setVisibility(View.INVISIBLE);
                    isRunning = false;
                    if (isMyTurn()) {
                        endGame("Đối thủ đã thắng.", true);
                    } else {
                        endGame("Bạn đã thắng.", true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private final Runnable backgroundTask = () -> {
        try {
            for (sumProgress = 0; sumProgress < maxValue; sumProgress += PROGRESS_STEP) {
                if (!isRunning)
                    return;

                Thread.sleep(1000);
                Utilities.HANDLER.post(foregroundRunnable);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

}
