package com.study.inflearn.spring.springdb1.service;

import org.springframework.transaction.annotation.Transactional;

import com.study.inflearn.spring.springdb1.domain.Member;
import com.study.inflearn.spring.springdb1.repository.MemberRepository;

import lombok.extern.slf4j.Slf4j;

/*
 * 예외 누수 문제 해결
 * SQLException 제거
 * MemberRepository 인터페이스에 의존하도록 변경됨
 */
@Slf4j
public class MemberServiceV4 {

	private final MemberRepository repo;

	public MemberServiceV4(MemberRepository repo) {
		this.repo = repo;
	}

	@Transactional
	public void accountTransfer(String fromId, String toId, int money) {
		bizLogic(fromId, toId, money);
	}

	private void bizLogic(String fromId, String toId, int money) {
		Member fromMember = repo.findById(fromId);
		Member toMember = repo.findById(toId);

		repo.update(fromId, fromMember.getMoney() - money);

		validation(toMember);

		repo.update(toId, toMember.getMoney() + money);
	}

	private void validation(Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("이체중 예외 발생");
		}
	}

}
