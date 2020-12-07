package com.edu.xogame.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String boardgame, String result, String opponent) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.BOARDGAME, boardgame);
        contentValue.put(DatabaseHelper.RESULT, result);
        contentValue.put(DatabaseHelper.OPPONENT, opponent);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.BOARDGAME, DatabaseHelper.RESULT, DatabaseHelper.OPPONENT };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, DatabaseHelper._ID + " DESC");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, String boardgame, String result, String opponent) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.BOARDGAME, boardgame);
        contentValues.put(DatabaseHelper.RESULT, result);
        contentValues.put(DatabaseHelper.OPPONENT, opponent);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }
}
