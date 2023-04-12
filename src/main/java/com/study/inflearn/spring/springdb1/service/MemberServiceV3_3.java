package com.study.inflearn.spring.springdb1.service;

import java.sql.SQLException;

import org.springframework.transaction.annotation.Transactional;

import com.study.inflearn.spring.springdb1.domain.Member;
import com.study.inflearn.spring.springdb1.repository.MemberRepositoryV3;

import lombok.extern.slf4j.Slf4j;

/*
 * 트랜잭션 - @Transactional AOP 사용
 * 
 * 클래스에 @Transactional 붙은 경우 -> 외부에서 호출 가능한 public 메서드가 AOP 적용 대상이된다.
 */
@Slf4j
public class MemberServiceV3_3 {

	private final MemberRepositoryV3 repo;

	public MemberServiceV3_3(MemberRepositoryV3 repo) {
		this.repo = repo;
	}

	@Transactional
	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		bizLogic(fromId, toId, money);
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
