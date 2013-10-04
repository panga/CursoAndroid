package com.example.todolist;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class MainActivity extends Activity implements View.OnKeyListener, View.OnFocusChangeListener {

	private EditText textAdd;
	private EditText lastEdit;
	private DBAdapter dbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dbAdapter = new DBAdapter(this);
		dbAdapter.open();

		textAdd = (EditText) findViewById(R.id.text_add);
		textAdd.setOnKeyListener(this);
		textAdd.setOnFocusChangeListener(this);

		refreshList(true);
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

	private void refreshList(boolean newItem) {
		List<TodoItem> todoItems = dbAdapter.listTodos();
		ArrayAdapter<TodoItem> listAdapter = new TodoAdapter(this,
				R.layout.list_row, todoItems);
		ListView listView = (ListView) findViewById(R.id.list);
		
		Parcelable state = null;
		if (!newItem) {
			state = listView.onSaveInstanceState();
		} 
		
		listView.setAdapter(listAdapter);
		
		if (state != null) {
			listView.onRestoreInstanceState(state);
		}
	}

	private class TodoAdapter extends ArrayAdapter<TodoItem> implements
			View.OnClickListener {

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

			CheckBox doneView = (CheckBox) convertView.findViewById(R.id.ic_done);
			doneView.setOnClickListener(this);
			doneView.setTag(todoItem);

			ImageView removeView = (ImageView) convertView
					.findViewById(R.id.ic_remove);
			removeView.setTag(todoItem);
			removeView.setOnClickListener(this);

			TextView textView = (TextView) convertView.findViewById(R.id.text_view);
			textView.setOnClickListener(this);
			textView.setTag(todoItem);

			if (todoItem.getDone() == 1) {
				doneView.setChecked(true);

				SpannableString spanText = new SpannableString(
						todoItem.getText());
				spanText.setSpan(new StrikethroughSpan(), 0, todoItem.getText()
						.length(), 0);
				textView.setText(spanText, BufferType.SPANNABLE);
				textView.setClickable(false);
				textView.setFocusable(false);
			} else {
				doneView.setChecked(false);

				textView.setText(todoItem.getText());
				textView.setClickable(true);
				textView.setFocusable(true);
			}

			return convertView;
		}

		@Override
		public void onClick(View v) {
			TodoItem todoItem = (TodoItem) v.getTag();

			if (todoItem != null) {
				if (v.getId() == R.id.ic_done) {
					if (todoItem.getDone() == 0) {
						todoItem.setDone(1);
					} else {
						todoItem.setDone(0);
					}
					dbAdapter.persistTodo(todoItem);
					refreshList(false);

				} else if (v.getId() == R.id.ic_remove) {
					dbAdapter.removeTodo(todoItem.getId());
					refreshList(false);
					
				} else if (v.getId() == R.id.text_view) {
					EditText textEdit = (EditText) ((View)v.getParent()).findViewById(R.id.text_edit);
					textEdit.setText(((TextView)v).getText());
					textEdit.setTag(v);
					textEdit.setOnKeyListener(MainActivity.this);
					textEdit.setOnFocusChangeListener(MainActivity.this);
					textEdit.setVisibility(EditText.VISIBLE);
					v.setVisibility(TextView.INVISIBLE);
				}
			} 
		}
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {		
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
					|| (keyCode == KeyEvent.KEYCODE_ENTER)) {
				
				editChanged((EditText) v);
				textAdd.requestFocus();
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {	
		if (hasFocus && lastEdit != v) {
			if (lastEdit != null && lastEdit.getId() == R.id.text_edit) {
				editChanged(lastEdit);
			}
			lastEdit = (EditText)v;
		}
	}
	
	public void editChanged(EditText editText) {
		if (editText.length() > 0) {	
			if (editText.getTag() != null) {
				TextView text = (TextView)editText.getTag();
				text.setVisibility(TextView.VISIBLE);
				text.setText(editText.getText());
				editText.setVisibility(EditText.INVISIBLE);
				
				TodoItem item = (TodoItem)text.getTag();
				item.setText(text.getText().toString());
				dbAdapter.persistTodo(item);
			} else {
				dbAdapter.persistTodo(new TodoItem(editText.getText()
						.toString()));
				refreshList(true);
			}
			
			editText.setText("");
			editText.setTag(null);
		}
	}

}
