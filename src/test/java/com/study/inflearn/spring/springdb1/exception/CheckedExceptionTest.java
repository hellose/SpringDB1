package com.study.inflearn.spring.springdb1.exception;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckedExceptionTest {

	@Test
	void checked_catch() {
		Service service = new Service();
		service.callCatch();
	}

	@Test
	void checked_throw() {
		Service service = new Service();
		assertThatThrownBy(() -> service.callThrow()).isInstanceOf(MyCheckedException.class);
	}

	static class MyCheckedException extends Exception {

		public MyCheckedException(String message) {
			super(message);
		}
	}

	static class Service {
		Repository repository = new Repository();

		public void callCatch() {
			try {
				repository.call();
			} catch (MyCheckedException e) {
				// 예외 처리 로직
				log.info("예외 처리, message={}", e.getMessage(), e);
			}
		}

		public void callThrow() throws MyCheckedException {
			repository.call();
		}
	}

	static class Repository {
		public void call() throws MyCheckedException {
			throw new MyCheckedException("ex");
		}
	}

}