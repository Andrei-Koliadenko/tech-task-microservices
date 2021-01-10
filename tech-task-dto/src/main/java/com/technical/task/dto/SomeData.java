package com.technical.task.dto;

public class SomeData {
	String something;

	public String getSomething() {
		return something;
	}

	public void setSomething(String something) {
		this.something = something;
	}

	@Override
	public String toString() {
		return "SomeData [something=" + something + "]";
	}

	public SomeData() {

	}

	public SomeData(String something) {
		super();
		this.something = something;
	}

}
