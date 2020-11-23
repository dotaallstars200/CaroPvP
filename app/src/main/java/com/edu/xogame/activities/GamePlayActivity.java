package com.edu.xogame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.edu.xogame.Game;
import com.edu.xogame.R;
import com.edu.xogame.players.PlayerBot;

public class GamePlayActivity extends AppCompatActivity {

    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String playingType = intent.getStringExtra("PlayType");
        if (playingType.equals("Bot"))
            newGame(true, true);
    }

    public void newGame(boolean isPlayingWithBot, boolean goFirst) {
        if (game != null)
            game.endGame(null, false);

        game = new Game(this, goFirst);
        if (isPlayingWithBot)
            game.setOpponent(new PlayerBot(game.getBoard()));
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


}