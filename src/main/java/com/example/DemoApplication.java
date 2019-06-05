package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.util.JSON_Reader;

@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		JSON_Reader.loadSchema();
		SpringApplication.run(DemoApplication.class, args);
		System.out.println("HELLO CSYE7255!!\n");
	}

}
