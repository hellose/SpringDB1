package com.study.inflearn.spring.springdb1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.study.inflearn.spring.springdb1.domain.Member;
import com.study.inflearn.spring.springdb1.repository.MemberRepository;
import com.study.inflearn.spring.springdb1.repository.MemberRepositoryV4_1;

import lombok.extern.slf4j.Slf4j;

/*
 * 예외 누수 문제 해결
 * SQLException 제거
 * MemberRepository 인터페이스에 의존하도록 변경됨
 */

@SpringBootTest
@Slf4j
public class MemberServiceV4Test {

	public static final String MEMBER_A = "memberA";
	public static final String MEMBER_B = "memberB";
	public static final String MEMBER_EX = "ex";

	@Autowired
	private MemberRepository repository;
	@Autowired
	private MemberServiceV4 service;

	@TestConfiguration
	static class TestConfig {

		private final DataSource dataSource;

		// 생성자에서 자동으로 dataSource 주입해줌
		public TestConfig(DataSource dataSource) {
			this.dataSource = dataSource;
		}

		@Bean
		PlatformTransactionManager transactionManager() {
			return new DataSourceTransactionManager(dataSource);
		}

		@Bean
		MemberRepositoryV4_1 memberRepository() {
			return new MemberRepositoryV4_1(dataSource);
		}

		@Bean
		MemberServiceV4 memberServiceV4() {
			return new MemberServiceV4(memberRepository());
		}
	}

	@AfterEach
	void after() {
		repository.delete(MEMBER_A);
		repository.delete(MEMBER_B);
		repository.delete(MEMBER_EX);
	}

	@Test
	@DisplayName("정상 이체")
	void accountTransfer() {
		// given
		Member memberA = new Member(MEMBER_A, 10000);
		repository.save(memberA);
		Member memberB = new Member(MEMBER_B, 10000);
		repository.save(memberB);

		// when
		service.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

		// that
		Member findMemberA = repository.findById(memberA.getMemberId());
		Member findMemberB = repository.findById(memberB.getMemberId());

		assertThat(findMemberA.getMoney()).isEqualTo(8000);
		assertThat(findMemberB.getMoney()).isEqualTo(12000);
	}

	@Test
	@DisplayName("이체중 예외 발생하여 정상 롤백")
	void accountTransferEx() {
		// given
		Member memberA = new Member(MEMBER_A, 10000);
		repository.save(memberA);
		Member memberEx = new Member(MEMBER_EX, 10000);
		repository.save(memberEx);

		// when
		assertThatThrownBy(() -> service.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
				.isInstanceOf(IllegalStateException.class);

		// then
		Member findMemberA = repository.findById(memberA.getMemberId());
		Member findMemberB = repository.findById(memberEx.getMemberId());

		assertThat(findMemberA.getMoney()).isEqualTo(10000);
		assertThat(findMemberB.getMoney()).isEqualTo(10000);
	}

}
