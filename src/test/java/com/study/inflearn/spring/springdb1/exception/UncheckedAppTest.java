package com.study.inflearn.spring.springdb1.exception;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

//예외를 처리하지 못하고 다시 던질때에는 반드시 기존 예외를 포함해야한다.
@Slf4j
public class UncheckedAppTest {
	@Test
	void unchecked() {
		Controller controller = new Controller();
		assertThatThrownBy(() -> controller.request()).isInstanceOf(Exception.class);
	}

	@Test
	void printEx() {
		Controller controller = new Controller();
		try {
			controller.request();
		} catch (Exception e) {
			// e.printStackTrace();
			log.info("ex", e);
		}
	}

	static class Controller {
		Service service = new Service();

		public void request() {
			service.logic();
		}
	}

	static class Service {
		Repository repository = new Repository();
		NetworkClient networkClient = new NetworkClient();

		public void logic() {
			repository.call();
			networkClient.call();
		}
	}

	static class NetworkClient {
		public void call() {
			throw new RuntimeConnectException("연결 실패");
		}
	}

	static class Repository {

		public void call() {
			try {
				runSQL();
			} catch (SQLException e) {
				// 예외 미포함(실무에서 흔히하는 실수)
				// -> Caused By가 출력되지 않아 어떠한 예외가 발생했는지 확인할 수 없다.
//				throw new RuntimeSQLException();

				// 예외 포함
				throw new RuntimeSQLException(e);
			}
		}

		private void runSQL() throws SQLException {
			throw new SQLException("ex");
		}
	}

	static class RuntimeConnectException extends RuntimeException {
		public RuntimeConnectException(String message) {
			super(message);
		}
	}

	static class RuntimeSQLException extends RuntimeException {

		public RuntimeSQLException() {
		}

		public RuntimeSQLException(Throwable cause) {
			super(cause);
		}
	}
}