package com.insistingon.binlog.binlogportalspringbootstartertest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class BinlogportalSpringBootStarterTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(BinlogportalSpringBootStarterTestApplication.class, args);
		log.info("service start...");
	}

}
