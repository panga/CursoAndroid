package com.example.todolist;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class MainActivity extends Activity {

	private DBAdapter dbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dbAdapter = new DBAdapter(this);
		dbAdapter.open();

		final EditText editText = (EditText) findViewById(R.id.text);
		editText.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
							|| (keyCode == KeyEvent.KEYCODE_ENTER)) {
						dbAdapter.addTodo(new TodoItem(editText.getText()
								.toString()));
						refreshList();
						editText.setText("");
						return true;
					}
				}
				return false;

			}
		});

		refreshList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		displayName();
	}

	public void displayName() {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		String displayName = sharedPrefs.getString("pref_listname", "");
		if ("".equals(displayName)) {
			displayName = getText(R.string.app_name).toString();
		}

		setTitle(displayName);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbAdapter.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void refreshList() {
		List<TodoItem> todoItems = dbAdapter.retrieveTodos();
		ArrayAdapter<TodoItem> listAdapter = new TodoAdapter(this,
				R.layout.list_row, todoItems);
		ListView listView = (ListView) findViewById(R.id.list);
		listView.setAdapter(listAdapter);
	}

	private class TodoAdapter extends ArrayAdapter<TodoItem> implements
			OnClickListener {

		public TodoAdapter(Context context, int textViewResourceId,
				List<TodoItem> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public long getItemId(int position) {
			return getItem(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				convertView = layoutInflater.inflate(R.layout.list_row, null);
			}

			TodoItem todoItem = getItem(position);

			ImageView doneView = (ImageView) convertView
					.findViewById(R.id.done);
			doneView.setOnClickListener(this);
			doneView.setTag(todoItem);

			ImageView removeView = (ImageView) convertView
					.findViewById(R.id.remove);
			removeView.setTag(todoItem);
			removeView.setOnClickListener(this);

			TextView rowView = (TextView) convertView.findViewById(R.id.row);
			rowView.setOnClickListener(this);
			rowView.setTag(todoItem);

			if (todoItem.getDone() == 1) {
				doneView.setImageResource(R.drawable.ic_undo);

				SpannableString spanText = new SpannableString(
						todoItem.getText());
				spanText.setSpan(new StrikethroughSpan(), 0, todoItem.getText()
						.length(), 0);
				rowView.setText(spanText, BufferType.SPANNABLE);
			} else {
				doneView.setImageResource(R.drawable.ic_done);

				rowView.setText(todoItem.getText());
			}

			return convertView;
		}

		@Override
		public void onClick(View v) {
			TodoItem todoItem = (TodoItem) v.getTag();

			if (todoItem != null) {
				if (v.getId() == R.id.done) {
					if (todoItem.getDone() == 0) {
						todoItem.setDone(1);
					} else {
						todoItem.setDone(0);
					}
					dbAdapter.updateTodo(todoItem);
					refreshList();

				} else if (v.getId() == R.id.remove) {
					dbAdapter.removeTodo(todoItem.getId());
					refreshList();
				}
			}
		}
	}

}
