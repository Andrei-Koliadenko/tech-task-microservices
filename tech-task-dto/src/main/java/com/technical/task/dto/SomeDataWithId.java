package com.technical.task.dto;

public class SomeDataWithId extends SomeData {
	int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "SomeDataWithId [id=" + id + "]";
	}

	public SomeDataWithId() {
		super();
	}

	public SomeDataWithId(String something, int id) {
		super(something);
		this.id = id;
	}

}
