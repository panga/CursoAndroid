package com.example.todolist;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {
	SQLiteDatabase db;
	Context context;
	DBHelper dbHelper;

	public DBAdapter(Context context) {
		this.context = context;
	}

	public DBAdapter open() throws SQLException {
		dbHelper = new DBHelper(context);
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public boolean persistTodo(TodoItem todoItem) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DBHelper.COLUMN_TODO, todoItem.getText());
		initialValues.put(DBHelper.COLUMN_DONE, todoItem.getDone());
		
		if (todoItem.getId() == null) {
			return db.insert(DBHelper.DATABASE_TABLE, null, initialValues) > 0;
		} else {
			return db.update(DBHelper.DATABASE_TABLE, initialValues,
					DBHelper.COLUMN_ID + " = " + todoItem.getId(), null) > 0;
		}
	}

	public boolean removeTodo(long id) {
		return db.delete(DBHelper.DATABASE_TABLE, DBHelper.COLUMN_ID + " = "
				+ id, null) > 0;
	}

	public List<TodoItem> listTodos() {
		List<TodoItem> items = new LinkedList<TodoItem>();

		Cursor c = db.query(DBHelper.DATABASE_TABLE,
				new String[] { DBHelper.COLUMN_ID, DBHelper.COLUMN_TODO,
						DBHelper.COLUMN_DONE }, null, null, null, null,
				DBHelper.COLUMN_ID + " DESC");

		c.moveToFirst();
		while (!c.isAfterLast()) {
			TodoItem item = new TodoItem();
			item.setId(c.getLong(c.getColumnIndex(DBHelper.COLUMN_ID)));
			item.setText(c.getString(c.getColumnIndex(DBHelper.COLUMN_TODO)));
			item.setDone(c.getInt(c.getColumnIndex(DBHelper.COLUMN_DONE)));
			if (item != null) {
				items.add(item);
			}
			
			c.moveToNext();
		}

		if (c != null) {
			c.close();
		}

		return items;
	}
}