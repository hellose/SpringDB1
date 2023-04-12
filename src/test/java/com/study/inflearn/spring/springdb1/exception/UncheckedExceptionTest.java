package com.study.inflearn.spring.springdb1.exception;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

//메서드 throws 언체크드예외 -> 개발자가 이런 예외가 발생하다는 점을 IDE를 통해 좀 더 편리하게 인지할 수 있다.
@Slf4j
public class UncheckedExceptionTest {
	@Test
	void unchecked_catch() {
		Service service = new Service();
		service.callCatch();
	}

	@Test
	void unchecked_throw() {
		Service service = new Service();
		assertThatThrownBy(() -> service.callThrow()).isInstanceOf(MyUncheckedException.class);
	}

	static class MyUncheckedException extends RuntimeException {
		public MyUncheckedException(String message) {
			super(message);
		}
	}

	static class Service {
		Repository repository = new Repository();

		public void callCatch() {
			try {
				repository.call();
			} catch (MyUncheckedException e) {
				// 예외 처리 로직
				log.info("예외 처리, message={}", e.getMessage(), e);
			}
		}

		public void callThrow() {
			repository.call();
		}
	}

	static class Repository {
		public void call() {
			throw new MyUncheckedException("ex");
		}
	}
}