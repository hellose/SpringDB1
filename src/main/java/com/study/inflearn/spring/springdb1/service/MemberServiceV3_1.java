package com.study.inflearn.spring.springdb1.service;

import java.sql.SQLException;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.study.inflearn.spring.springdb1.domain.Member;
import com.study.inflearn.spring.springdb1.repository.MemberRepositoryV3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * 트랜잭션 - 트랜잭션 매니저 사용
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

	private final PlatformTransactionManager transactionManager;
	private final MemberRepositoryV3 repo;

	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		// 트랜잭션 시작
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			// 비즈니스 로직 수행
			bizLogic(fromId, toId, money);
			// 성공시 커밋
			transactionManager.commit(status);
		} catch (Exception e) {
			// 실패시 롤백
			transactionManager.rollback(status);
			throw new IllegalStateException(e);
		}
		// transactionManager.commit, rollback시 자동 release
		// -> finally block 필요 없음

	}

	// 트랜잭션 관리 로직과 비지니스 로직(계좌이체 기능) 분리
	private void bizLogic(String fromId, String toId, int money) throws SQLException {
		Member fromMember = repo.findById(fromId);
		Member toMember = repo.findById(toId);

		repo.update(fromId, fromMember.getMoney() - money);

		validation(toMember);

		repo.update(toId, toMember.getMoney() + money);
	}

	private void validation(Member toMember) {
		// ex에게 보내는 경우 예외 발생시킴
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("이체중 예외 발생");
		}
	}

}
