package com.edu.xogame.activities;

import androidx.appcompat.app.AppCompatActivity;
import com.edu.xogame.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    private Button btnExit, btnPlayWithBot, btnPlayWithFriend, btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnExit = findViewById(R.id.btnExit);
        btnPlayWithBot = findViewById(R.id.btnPlayWithBot);
        btnPlayWithFriend = findViewById(R.id.btnPlayWithFriend);
        btnHistory = findViewById(R.id.btnHistory);

        btnPlayWithBot.setOnClickListener(v -> {
            Intent intent = new Intent(this, GamePlayActivity.class);

            intent.putExtra("PlayType", "Bot");
            startActivity(intent);
        });

        btnExit.setOnClickListener(v -> {
            System.exit(0);
        });
    }


}