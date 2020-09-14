package com.optum.ssn.utility;

import com.EncryptionUtil;
import com.optum.ssn.utility.dao.impl.SsnTempImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@SpringBootApplication
public class Application implements CommandLineRunner{

	@Bean
	public EncryptionUtil getEncryptionUtil(){
		return new EncryptionUtil();
	}
	@Autowired
	SsnTempImpl repository;
	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Successfully created table");
		repository.runSsnProcess();
		//repository.insertSsnTemp(new SsnTempTable(134,"23424234"));

	}

}
