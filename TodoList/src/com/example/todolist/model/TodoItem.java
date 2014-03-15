package com.example.todolist.model;

public class TodoItem {
	private Long id;
	private String text;
	private int done;

	public TodoItem() {
	}

	public TodoItem(String text) {
		setText(text);
		setDone(0);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
