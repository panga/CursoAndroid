package com.example.todolist.model;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.todolist.model.TodoSQLite.TodoTable;

public class TodoDAO {
	private final SQLiteDatabase db;

	public TodoDAO(final SQLiteDatabase db) {
		this.db = db;
	}

	public boolean persist(TodoItem todoItem) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(TodoTable.COLUMN_TODO, todoItem.getText());
		initialValues.put(TodoTable.COLUMN_DONE, todoItem.getDone());

		if (todoItem.getId() == null) {
			return db.insert(TodoTable.TABLE_NAME, null, initialValues) > 0;
		} else {
			return db.update(TodoTable.TABLE_NAME, initialValues,
					TodoTable.COLUMN_ID + " = " + todoItem.getId(), null) > 0;
		}
	}

	public boolean remove(long id) {
		return db.delete(TodoTable.TABLE_NAME,
				TodoTable.COLUMN_ID + " = " + id, null) > 0;
	}

	public List<TodoItem> list() {
		List<TodoItem> items = new LinkedList<TodoItem>();

		Cursor c = db.query(TodoTable.TABLE_NAME, new String[] {
				TodoTable.COLUMN_ID, TodoTable.COLUMN_TODO,
				TodoTable.COLUMN_DONE }, null, null, null, null,
				TodoTable.COLUMN_ID + " DESC");

		c.moveToFirst();
		while (!c.isAfterLast()) {
			TodoItem item = new TodoItem();
			item.setId(c.getLong(c.getColumnIndex(TodoTable.COLUMN_ID)));
			item.setText(c.getString(c.getColumnIndex(TodoTable.COLUMN_TODO)));
			item.setDone(c.getInt(c.getColumnIndex(TodoTable.COLUMN_DONE)));
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