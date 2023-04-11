package com.study.inflearn.spring.springdb1.connection;


import static com.study.inflearn.spring.springdb1.connection.ConnectionConst.PASSWORD;
import static com.study.inflearn.spring.springdb1.connection.ConnectionConst.URL;
import static com.study.inflearn.spring.springdb1.connection.ConnectionConst.USERNAME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectionTest {

	@Test
	void driverManager() throws SQLException {
		Connection con1 
			= DriverManager.getConnection(URL, USERNAME, PASSWORD);
		Connection con2
			= DriverManager.getConnection(URL, USERNAME, PASSWORD);
		
		log.info("connection={}, class={}", con1, con1.getClass());
		log.info("connection={}, class={}", con2, con2.getClass());
	}
	
	@Test
	void dataSourceDriverManager() throws SQLException {
		//DriverManagerDataSource - 항상 새로운 커넥션을 획득
		DriverManagerDataSource dataSource
			= new DriverManagerDataSource(URL, USERNAME, PASSWORD);
		useDataSource(dataSource);
	}
	
	private void useDataSource(DataSource dataSource) throws SQLException {
		//DriverManagerDataSource에 셋팅이되어있어서 getConnection 메서드 호출시 인자가 필요없는 간편함 존재
		//반면에 DriverManager를 사용하는 경우 getConnection마다 인자를 넘겨야한다.
		Connection con1 = dataSource.getConnection();
		Connection con2 = dataSource.getConnection();
		
//		Connection con3 = dataSource.getConnection();
//		Connection con4 = dataSource.getConnection();
//		Connection con5 = dataSource.getConnection();
//		Connection con6 = dataSource.getConnection();
//		Connection con7 = dataSource.getConnection();
//		Connection con8 = dataSource.getConnection();
//		Connection con9 = dataSource.getConnection();
//		Connection con10 = dataSource.getConnection();
//		Connection con11 = dataSource.getConnection();
		
	}
	
	@Test
	void dataSourceConnectionPool() throws SQLException, InterruptedException {
		//커넥션 풀링
		
		//spring에서 jdbc 사용시 자동으로 추가되는 디펜던시
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(URL);
		dataSource.setUsername(USERNAME);
		dataSource.setPassword(PASSWORD);
		dataSource.setMaximumPoolSize(10);
		dataSource.setPoolName("My H2 Connection Pool");
		
//		dataSource.setConnectionTimeout(10000);
		
		//컨넥션 풀 생성시점 -> 최초 dataSource.getConnection()호출시
		useDataSource(dataSource);
		
		Thread.sleep(3000);
	}
}
