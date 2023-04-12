package com.study.inflearn.spring.springdb1.repository.ex.basic;

public class MyDbException extends RuntimeException {

	public MyDbException() {
	}

	public MyDbException(String message) {
		super(message);
	}

	public MyDbException(Throwable cause) {
		super(cause);
	}

	public MyDbException(String message, Throwable cause) {
		super(message, cause);
	}

}
