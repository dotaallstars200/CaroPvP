package com.edu.xogame.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.edu.xogame.ItemAdapter;
import com.edu.xogame.R;
import com.edu.xogame.Utilities;
import com.edu.xogame.database.DBManager;
import com.edu.xogame.database.DatabaseHelper;

public class GameHistoryActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ListView listView = findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.empty));
        listView.addHeaderView(new View(this), null, true);

        DBManager dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch();
        int numberOfGameWin = dbManager.getNumberOfGameWin();
        int numberOfGameLose = dbManager.getNumberOfGameLose();
        dbManager.close();

        TextView gameCounter = findViewById(R.id.gameCounter);

        gameCounter.setText("Số trận thắng/thua: " + numberOfGameWin + "/" + numberOfGameLose);


        ItemAdapter cursorAdapter = new ItemAdapter(this, cursor);
        cursorAdapter.notifyDataSetChanged();
        listView.setAdapter(cursorAdapter);

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
}
