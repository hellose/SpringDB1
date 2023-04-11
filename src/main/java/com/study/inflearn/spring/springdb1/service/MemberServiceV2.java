package com.study.inflearn.spring.springdb1.service;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.study.inflearn.spring.springdb1.domain.Member;
import com.study.inflearn.spring.springdb1.repository.MemberRepositoryV2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * 트랜잭션 - Connection 파라메터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

	// 서비스 계층의 시작시 Connection이 필요하기 때문에 필요
	private final DataSource dataSource;
	private final MemberRepositoryV2 repo;

	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		Connection con = dataSource.getConnection();
		try {
			con.setAutoCommit(false);

			// 비즈니스 로직 수행
			bizLogic(con, fromId, toId, money);

			// 성공시 커밋
			con.commit();
		} catch (Exception e) {
			// 실패시 롤백
			con.rollback();
			throw new IllegalStateException(e);
		} finally {
			releaseConnection(con);
		}

	}

	// 트랜잭션 관리 로직과 비지니스 로직(계좌이체 기능) 분리
	private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
		Member fromMember = repo.findById(con, fromId);
		Member toMember = repo.findById(con, toId);

		repo.update(con, fromId, fromMember.getMoney() - money);

		validation(toMember);

		repo.update(con, toId, toMember.getMoney() + money);
	}

	private void releaseConnection(Connection con) {
		if (con != null) {
			try {
				// 커넥션 풀을 사용하는 DataSource 고려
				con.setAutoCommit(true);

				con.close();
			} catch (Exception e) {
				log.info("error", e);
			}
		}
	}

	private void validation(Member toMember) {
		// ex에게 보내는 경우 예외 발생시킴
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("이체중 예외 발생");
		}
	}

}
