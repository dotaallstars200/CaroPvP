package com.edu.xogame;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.xogame.database.DatabaseHelper;

public class ItemAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    public ItemAdapter(Context context, Cursor c) {
        super(context, c);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mLayoutInflater.inflate(R.layout.view_record, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper._ID));
        String boardGame = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOARDGAME));
        String opponent = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.OPPONENT));
        String result = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.RESULT));

        TextView id_text =  view.findViewById(R.id.id);
        id_text.setText(id);

        TextView boardGame_text = view.findViewById(R.id.boardGame);
        boardGame_text.setText(boardGame);

        TextView opponent_text = view.findViewById(R.id.opponent);
        opponent_text.setText(opponent);

        TextView result_text = view.findViewById(R.id.result);
        result_text.setText(result);

        ImageView result_icon = view.findViewById(R.id.imgResult);
        if (result.equals("Thắng")) {
            result_icon.setImageResource(R.drawable.win);
        }
        else if (result.equals("Thua")) {
            result_icon.setImageResource(R.drawable.lose);
        }
        else if (result.equals("Hoà")) {
            result_icon.setImageResource(R.drawable.draw);
        }
    }
}
