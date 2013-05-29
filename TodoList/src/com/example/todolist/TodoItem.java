package com.example.todolist;

public class TodoItem {
	private long id;
	private String text;
	private int done;

	public TodoItem() {
	}
	
	public TodoItem(String text) {
		setText(text);
		setDone(0);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getDone() {
		return done;
	}

	public void setDone(int done) {
		this.done = done;
	}

}
