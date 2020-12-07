package com.edu.xogame.activities;

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
    private DBManager dbManager;
    private ListView listView;
    private ItemAdapter cursorAdapter;
    private final String[] from = {DatabaseHelper._ID, DatabaseHelper.BOARDGAME, DatabaseHelper.RESULT, DatabaseHelper.OPPONENT};
    private final int[] to = {R.id.id, R.id.boardGame, R.id.result, R.id.opponent };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.listView);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View headerView = inflater.inflate(R.layout.listview_header, null, false);
        listView.addHeaderView(headerView);//Add view to list view as header view

        listView.setEmptyView(findViewById(R.id.empty));

        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch();
        dbManager.close();

        Log.e("<<H>>", "onCreate call ...");

        cursorAdapter = new ItemAdapter(this, cursor);
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
