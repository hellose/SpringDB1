package com.study.inflearn.spring.springdb1.service;

import static com.study.inflearn.spring.springdb1.connection.ConnectionConst.PASSWORD;
import static com.study.inflearn.spring.springdb1.connection.ConnectionConst.URL;
import static com.study.inflearn.spring.springdb1.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import com.study.inflearn.spring.springdb1.domain.Member;
import com.study.inflearn.spring.springdb1.repository.MemberRepositoryV3;

/*
 * 트랜잭션 - 트랜잭션 매니저
 */
public class MemberServiceV3_1Test {

	public static final String MEMBER_A = "memberA";
	public static final String MEMBER_B = "memberB";
	public static final String MEMBER_EX = "ex";

	private MemberRepositoryV3 repository;
	private MemberServiceV3_1 service;

	@BeforeEach
	void before() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
		repository = new MemberRepositoryV3(dataSource);

		// JDBC용 트랜잭션 매니저 DataSourceTransactionManager 주임
		// 데이터소스를 통해 커넥션을 생성하므로 생성자 인자 필요
		PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);

		service = new MemberServiceV3_1(transactionManager, repository);
	}

	@AfterEach
	void after() throws SQLException {
		repository.delete(MEMBER_A);
		repository.delete(MEMBER_B);
		repository.delete(MEMBER_EX);
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
	@DisplayName("이체중 예외 발생")
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
}
