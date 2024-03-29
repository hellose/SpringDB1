package com.study.inflearn.spring.springdb1.repository;

import com.study.inflearn.spring.springdb1.domain.Member;

public interface MemberRepository {
	Member save(Member member);

	Member findById(String memberId);

	void update(String memberId, int money);

	void delete(String memberId);
}
