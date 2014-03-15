package com.example.todolist.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoSQLite extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "todolist";

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ TodoTable.TABLE_NAME + " (" + TodoTable.COLUMN_ID
			+ " integer primary key autoincrement," + TodoTable.COLUMN_TODO
			+ " text not null," + TodoTable.COLUMN_DONE + " integer not null);";
	private static final String DATABASE_DROP = "DROP TABLE IF EXISTS "
			+ TodoTable.TABLE_NAME;

	public TodoSQLite(Context context) {
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

	public static final class TodoTable {
		public static final String TABLE_NAME = "todos";
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_TODO = "todo";
		public static final String COLUMN_DONE = "done";
	}
}