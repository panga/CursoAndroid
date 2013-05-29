package com.example.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "todolist";
	public static final String DATABASE_TABLE = "todos";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TODO = "todo";
	public static final String COLUMN_DONE = "done";
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE = "CREATE TABLE "+DATABASE_TABLE+" ("+COLUMN_ID+" integer primary key autoincrement,"+COLUMN_TODO+" text not null,"+COLUMN_DONE+" integer not null);";
	private static final String DATABASE_DROP = "DROP TABLE IF EXISTS "+DATABASE_TABLE;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL(DATABASE_DROP);
		onCreate(db);
	}
}