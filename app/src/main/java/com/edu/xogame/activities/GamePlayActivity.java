package com.edu.xogame.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;


import android.app.Activity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Button;

import com.edu.xogame.R;
import com.edu.xogame.Utilities;
import com.edu.xogame.players.Player;
import com.edu.xogame.players.PlayerBot;
import com.edu.xogame.players.RealPlayer;
import com.edu.xogame.views.Board;

import java.util.Objects;

public class GamePlayActivity extends AppCompatActivity {
    private GameFragment game;
    private Button btnNewGame;
    private Button btnUndo;
    private TextView tvP_Human;
    private TextView tvP_Bot;
    private  int p_Player=0;// point of player
    private int p_Opponet=0;// point of opponet

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);

        String playingType;
        boolean goFirst;
        String boardGame;

        Intent intent = getIntent();

        if (intent.hasExtra("BoardGame")) {
            setContentView(R.layout.activity_show_history);
            boardGame = intent.getStringExtra("BoardGame");
            boardGame = boardGame.replace("[","").replace("]","");
            String[] numbers = boardGame.split(", ");

            int[][] boardGameArray = new int[Board.NUMBER_ROWS][Board.NUMBER_COLUMNS];

            for (int i = 0; i < Board.NUMBER_ROWS; i++) {
                for (int j = 0; j < Board.NUMBER_COLUMNS; j++) {
                    boardGameArray[i][j] = Integer.parseInt(numbers[i * Board.NUMBER_COLUMNS + j]);
                }
            }
            showGameHistory(boardGameArray);
        }
        else {
            playingType = intent.getStringExtra("PlayType");
            goFirst = intent.getBooleanExtra("GoFirst", true);
            Player player;
            if (playingType.equals("Bot")) {
                player = new PlayerBot();
                setContentView(R.layout.activity_main_bot);
                btnNewGame= findViewById(R.id.btnNewGame);
                btnUndo= findViewById(R.id.btnUndo);
                btnNewGame.setOnClickListener(v -> {
                    game.remake();
                });
                btnUndo.setOnClickListener(v -> {
                    game.undo();
                });
            }
            else {
                setContentView(R.layout.activity_main_player);
                RealPlayer realPlayer;
                if (Utilities.CLIENT != null) {
                    realPlayer = Utilities.CLIENT.getPlayer();
                } else if (Utilities.HOST != null) {
                    realPlayer = Utilities.HOST.getPlayer();
                } else realPlayer = null;

                player = realPlayer;
            }
            newGame(goFirst, player);
        }

    }
    @SuppressLint("SetTextI18n")
    public void updatePoint(String result){//0 = bot, 1 = human
        tvP_Bot= findViewById(R.id.point_bot);
        tvP_Human= findViewById(R.id.point_human);

        if (result.equals("Bạn đã thắng.")) {
            p_Player+=1;
            tvP_Human.setText(String.valueOf(p_Player));
        }
        else if (result.equals("Đối thủ đã thắng.")){
            p_Opponet+=1;
            tvP_Bot.setText(String.valueOf(p_Opponet));
        }
    }


    public void newGame(boolean goFirst, Player player) {
        if (game != null)
            game.endGame(null, false);

        game = new GameFragment(goFirst, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.boardGame, game).commit();
        game.setOpponent(player);
        player.setBoard(game.getBoard());

    }

    public void removeBoardFromActivity() {
        getSupportFragmentManager().beginTransaction().remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.boardGame))).commit();
    }

    public void showGameHistory(int[][] boardGameArray) {
        if (game != null)
            game.endGame(null, false);
        game = new GameFragment(false, false);
        game.getBoard().setTrackTable(boardGameArray);
        getSupportFragmentManager().beginTransaction().replace(R.id.boardGame, game).commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override

    protected void onDestroy() {
        MultiPlayerActivity.disconnect(this);
        Utilities.IS_AVAILABLE = true;
        super.onDestroy();

    }
}