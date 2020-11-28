package com.edu.xogame.activities;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import android.widget.Toast;

import com.edu.xogame.Game;
import com.edu.xogame.R;
import com.edu.xogame.Utilities;
import com.edu.xogame.players.Player;
import com.edu.xogame.players.PlayerBot;
import com.edu.xogame.players.RealPlayer;

import java.util.Random;

public class GamePlayActivity extends AppCompatActivity {
    private Context context;
    private Game game;
    private Button btnNewGame;
    private Button btnUndo;
    private TextView tvP_Human;
    private TextView tvP_Bot;
    private  int p_Player=0;// point of player
    private int p_Opponet=0;// point of opponet
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        context=this;
        btnNewGame=(Button)findViewById(R.id.btnNewGame);
        btnUndo=(Button)findViewById(R.id.btnUndo);

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

        btnNewGame.setOnClickListener(v -> {
            Toast.makeText(context,"New Game",Toast.LENGTH_SHORT).show();
            game.remake();
        });
        btnUndo.setOnClickListener(v -> {
            Toast.makeText(context,"Undo",Toast.LENGTH_SHORT).show();
            game.undo();
        });
    }
    @SuppressLint("SetTextI18n")
    public void updatePoint(String result){//0 = bot, 1 = human
        tvP_Bot=(TextView)findViewById(R.id.point_bot);
        tvP_Human=(TextView)findViewById(R.id.point_human);

        if (result.equals("YOU")) {
            p_Player+=1;
            tvP_Human.setText(String.valueOf(p_Player));
        }
        else{
             p_Opponet+=1;
            tvP_Bot.setText(String.valueOf(p_Opponet));
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