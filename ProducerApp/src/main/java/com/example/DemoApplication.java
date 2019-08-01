package com.example;

import javax.servlet.Filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import com.example.util.JSON_Reader;

@SpringBootApplication
public class DemoApplication {
	
	@Bean
	public Filter etagFilter() {
		return new ShallowEtagHeaderFilter();
	}
	
	public static void main(String[] args) {
		JSON_Reader.loadSchema();
		SpringApplication.run(DemoApplication.class, args);
		System.out.println("HELLO CSYE7255!!\n");
	}

}
