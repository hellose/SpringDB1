package com.study.inflearn.spring.springdb1.service;

import static com.study.inflearn.spring.springdb1.connection.ConnectionConst.PASSWORD;
import static com.study.inflearn.spring.springdb1.connection.ConnectionConst.URL;
import static com.study.inflearn.spring.springdb1.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import com.study.inflearn.spring.springdb1.domain.Member;
import com.study.inflearn.spring.springdb1.repository.MemberRepositoryV3;

import lombok.extern.slf4j.Slf4j;

/*
 * 트랜잭션 - @Transaction 테스트
 */

//스프링 AOP를 적용하려면 스프링 컨테이너 필요 -> 어노테이션 사용 -> 스프링 부트를 통해 스프링 컨테이너 생성
@SpringBootTest
@Slf4j
public class MemberServiceV3_3Test {

	public static final String MEMBER_A = "memberA";
	public static final String MEMBER_B = "memberB";
	public static final String MEMBER_EX = "ex";

	@Autowired
	private MemberRepositoryV3 repository;
	@Autowired
	private MemberServiceV3_3 service;

	// 스프링 부트가 자동으로 만들어주는 빈들에 추가로 필요한 스프링 빈 등록해줌
	@TestConfiguration
	static class TestConfig {

		// 트랜잭션 매니저와, Repository에서 사용
		@Bean
		DataSource dataSource() {
			return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
		}

		// 스프링 트랜잭션 AOP는 스프링 빈에 등록된 트랜잭션 매니저를 찾아서 사용하기 때문에 빈으로 등록해주어야함
		@Bean
		PlatformTransactionManager transactionManager() {
			return new DataSourceTransactionManager(dataSource());
		}

		@Bean
		MemberRepositoryV3 memberRepositoryV3() {
			return new MemberRepositoryV3(dataSource());
		}

		@Bean
		MemberServiceV3_3 memberServiceV3_3() {
			return new MemberServiceV3_3(memberRepositoryV3());
		}
	}

	@AfterEach
	void after() throws SQLException {
//		repository.delete(MEMBER_A);
//		repository.delete(MEMBER_B);
//		repository.delete(MEMBER_EX);
	}

	@Test
	@DisplayName("정상 이체")
	void accountTransfer() throws SQLException {
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
	void accountTransferEx() throws SQLException {
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

	@Test
	void AopCheck() {
		log.info("memberService class={}", service.getClass());
		log.info("memberRepository class={}", repository.getClass());
		Assertions.assertThat(AopUtils.isAopProxy(service)).isTrue();
		Assertions.assertThat(AopUtils.isAopProxy(repository)).isFalse();
	}
}
