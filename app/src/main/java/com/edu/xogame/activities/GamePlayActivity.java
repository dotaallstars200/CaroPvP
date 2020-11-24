package com.edu.xogame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.edu.xogame.Game;
import com.edu.xogame.R;
import com.edu.xogame.Utilities;
import com.edu.xogame.players.Player;
import com.edu.xogame.players.PlayerBot;
import com.edu.xogame.players.RealPlayer;

public class GamePlayActivity extends AppCompatActivity {

    private Game game;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String playingType = intent.getStringExtra("PlayType");
        boolean goFirst = intent.getBooleanExtra("GoFirst", true);
        Player player;
        if (playingType.equals("Bot"))
            player = new PlayerBot(handler);
        else {
            RealPlayer realPlayer;
            if (Utilities.CLIENT != null) {
                realPlayer = Utilities.CLIENT.getPlayer();
            } else if (Utilities.HOST != null) {
                realPlayer = Utilities.HOST.getPlayer();
            } else realPlayer = null;

            realPlayer.setHandler(handler);
            Thread thread = new Thread(realPlayer);
            thread.start();
            player = realPlayer;
        }

        newGame(goFirst, player);
    }

    public void newGame(boolean goFirst, Player player) {
        if (game != null)
            game.endGame(null, false);

        game = new Game(this, goFirst);
        game.setOpponent(player);
        player.setBoard(game.getBoard());
        game.start();
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