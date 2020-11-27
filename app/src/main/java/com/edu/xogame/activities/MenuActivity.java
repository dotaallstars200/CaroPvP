package com.edu.xogame.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.edu.xogame.R;
import com.edu.xogame.Utilities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

public class MenuActivity extends AppCompatActivity {

    private Button btnExit, btnPlayWithBot, btnPlayWithFriend, btnHistory;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        MultiPlayerActivity.disconnect(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnExit = findViewById(R.id.btnExit);
        btnPlayWithBot = findViewById(R.id.btnPlayWithBot);
        btnPlayWithFriend = findViewById(R.id.btnPlayWithFriend);
        btnHistory = findViewById(R.id.btnHistory);

        btnPlayWithBot.setOnClickListener(v -> {
            Intent intent = new Intent(this, GamePlayActivity.class);
            dialog = ProgressDialog.show(this, "",
                    "Đang tải...", true);

            intent.putExtra("PlayType", "Bot");
            startActivityForResult(intent, Utilities.CANCEL_DIALOG);
        });

        btnPlayWithFriend.setOnClickListener(v -> {
            Intent intent = new Intent(this, MultiPlayerActivity.class);
            startActivity(intent);
        });

        btnExit.setOnClickListener(v -> {
            System.exit(0);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utilities.CANCEL_DIALOG && resultCode == RESULT_CANCELED) {
            dialog.dismiss();
            dialog = null;
        }
    }
}