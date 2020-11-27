package com.edu.xogame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    ProgressBar progressBar;
    int MAX_PROGRESS = 100;
    int PROGRESS_STEP = 1;
    int accum = 0;
    int max_value = 60;
    Handler handler1 = new Handler();
    public boolean isRunning;
    Thread myBackgroundThread;
    int count=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isRunning = true;
        progressBar = findViewById(R.id.progressBar);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);

        Intent intent = getIntent();
        String playingType = intent.getStringExtra("PlayType");
        boolean goFirst = intent.getBooleanExtra("GoFirst", true);
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

    public void newGame(boolean goFirst, Player player) {
        if (game != null)
            game.endGame(null, false);

        game = new Game(this, goFirst);
        game.setOpponent(player);
        player.setBoard(game.getBoard());
        isRunning = true;
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

    public void startTime() {
        if (!isRunning) {
            return;
        }
        accum = 0;
        progressBar.setMax(MAX_PROGRESS);
        progressBar.setVisibility(View.VISIBLE);
        Thread myBackgroundThread = new Thread(backgroundTask, "bgTask");
        myBackgroundThread.start();

    }

    private Runnable foregroundRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                progressBar.setProgress((int) ((float) accum / max_value * 100));
                accum += PROGRESS_STEP;


                if (accum >= max_value) {
                    progressBar.setVisibility(View.INVISIBLE);
                    isRunning = false;
                    if(count==0){
                        game.endGame("Opponent",true);
                        count++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable backgroundTask = new Runnable() {
        @Override
        public void run() {
            try {
                for (int i = 0; i < max_value; i++) {
                    Thread.sleep(1000);

                    handler1.post(foregroundRunnable);

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
}