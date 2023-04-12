package com.study.inflearn.spring.springdb1.service;

import java.sql.SQLException;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.study.inflearn.spring.springdb1.domain.Member;
import com.study.inflearn.spring.springdb1.repository.MemberRepositoryV3;

import lombok.extern.slf4j.Slf4j;

/*
 * 트랜잭션 - 트랜잭션 템플릿 사용
 */
@Slf4j
public class MemberServiceV3_2 {

	private final TransactionTemplate txTemplate;
	private final MemberRepositoryV3 repo;

	public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 repo) {
		// TransactionTemplate이 TransactionManager 대신 수행
		this.txTemplate = new TransactionTemplate(transactionManager);
		this.repo = repo;
	}

	public void accountTransfer(String fromId, String toId, int money) throws SQLException {

		// executeWithoutResult 메서드
		// 성공 -> commit진행
		// Runtime Exception 발생 -> rollback진행
		// Checked Exception 발생한 경우 default -> commit진행(뒷부분에서 다시 설명)

		// but.
		// commit, rollback코드를 줄였지만 여전히 트랜잭션 처리 기술 로직이 포함되어있음
		// -> 순수한 비즈니스 로직만 남기는 목표를 당성하지 못함
		txTemplate.executeWithoutResult(status -> {
			try {
				bizLogic(fromId, toId, money);
			} catch (SQLException e) {
				// checked 예외를 runtime 예외로 바꿔서 다시 던져줘야함
				throw new IllegalStateException(e);
			}
		});
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
