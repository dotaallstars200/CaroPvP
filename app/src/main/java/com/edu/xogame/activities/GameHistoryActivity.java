package com.edu.xogame.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.edu.xogame.ItemAdapter;
import com.edu.xogame.R;
import com.edu.xogame.Utilities;
import com.edu.xogame.database.DBManager;
import com.edu.xogame.database.DatabaseHelper;

public class GameHistoryActivity extends AppCompatActivity {
    Cursor cursor;
    int numberOfGameWin, numberOfGameLose;
    ListView listView;
    TextView gameCounter;
    ItemAdapter cursorAdapter;

    Handler myHandler = new Handler();

    MediaPlayer mediaPlayer;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mediaPlayer = MediaPlayer.create(this.getApplicationContext(),R.raw.ring);

        listView = findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.empty));
        listView.addHeaderView(new View(this), null, true);

        gameCounter = findViewById(R.id.gameCounter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView id = view.findViewById(R.id.id);
                TextView boardGame = view.findViewById(R.id.boardGame);
                Intent intent = new Intent(getApplicationContext(), GamePlayActivity.class);
                intent.putExtra("BoardGame", boardGame.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Thread myBackgroundThread = new Thread( backgroundTask, "backAlias1");
        myBackgroundThread.start();
    }

    private Runnable foregroundRunnable = new Runnable() {
        @Override
        public void run() {
            gameCounter.setText("Số trận thắng/thua: " + numberOfGameWin + "/" + numberOfGameLose);
            cursorAdapter = new ItemAdapter(getApplicationContext(), cursor);
            cursorAdapter.notifyDataSetChanged();
            listView.setAdapter(cursorAdapter);
            Log.e("<<COUNT CURSOR>>", "" + cursorAdapter.getCount());
        }
    };

    private Runnable backgroundTask = new Runnable() {
        @Override
        public void run() {
            DBManager dbManager = new DBManager(getApplicationContext());
            dbManager.open();
            cursor = dbManager.fetch();
            numberOfGameWin = dbManager.getNumberOfGameWin();
            numberOfGameLose = dbManager.getNumberOfGameLose();
            dbManager.close();

            myHandler.post(foregroundRunnable);
        }// run
    };// backgroundTask

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }
}
