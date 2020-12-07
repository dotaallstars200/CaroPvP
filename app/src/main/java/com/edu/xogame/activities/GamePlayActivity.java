package com.edu.xogame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.edu.xogame.Game;
import com.edu.xogame.R;
import com.edu.xogame.Utilities;
import com.edu.xogame.players.Player;
import com.edu.xogame.players.PlayerBot;
import com.edu.xogame.players.RealPlayer;
import com.edu.xogame.views.Board;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class GamePlayActivity extends AppCompatActivity {

    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);

        String playingType;
        boolean goFirst;
        String boardGame;

        Intent intent = getIntent();

        if (intent.hasExtra("BoardGame")) {
            boardGame = intent.getStringExtra("BoardGame");
            boardGame = boardGame.replace("[","").replace("]","");
            String numbers[] = boardGame.split(", ");

            int boardGameArray[][] = new int[Board.NUMBER_ROWS][Board.NUMBER_COLUMNS];

            for (int i = 0; i < 50; i++) {
                for (int j = 0; j < 50; j++) {
                    boardGameArray[i][j] = Integer.parseInt(numbers[i * 50 + j]);
                }
            }

            showGameHistory(boardGameArray);
        }
        else {
            playingType = intent.getStringExtra("PlayType");
            goFirst = intent.getBooleanExtra("GoFirst", true);
            Player player;
            if (playingType.equals("Bot"))
                player = new PlayerBot();
            else {
                RealPlayer realPlayer;
                if (Utilities.CLIENT != null) {
                    realPlayer = Utilities.CLIENT.getPlayer();
                } else if (Utilities.HOST != null) {
                    realPlayer = Utilities.HOST.getPlayer();
                } else realPlayer = null;

                Thread thread = new Thread(realPlayer);
                thread.start();
                player = realPlayer;
            }

            newGame(goFirst, player);
        }
    }

    public void newGame(boolean goFirst, Player player) {
        if (game != null)
            game.endGame(null, false);

        game = new Game(this, goFirst);
        game.setOpponent(player);
        player.setBoard(game.getBoard());
        game.start();
    }

    public void showGameHistory(int[][] boardGameArray) {
        game = new Game(this, false);
        game.getBoard().setTrackTable(boardGameArray);
        game.getBoard().setCell(this.getApplicationContext());
        game.show();
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
        super.onDestroy();
        MultiPlayerActivity.disconnect(this);
    }
}