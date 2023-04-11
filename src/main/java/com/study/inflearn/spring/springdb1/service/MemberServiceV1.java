package com.study.inflearn.spring.springdb1.service;

import java.sql.SQLException;

import com.study.inflearn.spring.springdb1.domain.Member;
import com.study.inflearn.spring.springdb1.repository.MemberRepositoryV1;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberServiceV1 {

	private final MemberRepositoryV1 repo;

	// 드랜잭션은 비즈니스 로직이 있는 서비스 계층에서 시작해야 한다. 비즈니스 로직이 잘못되면 해당 비즈니스 로직으로 인해 문제가 되는 부분을
	// 함께 롤백해야하기 때문이다.
	// -> accountTransfer 메서드 시작시 start, 메서드 종료시 commit or rollback 수행
	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
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
