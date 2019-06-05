package com.example;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.util.JSON_Reader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		JSON_Reader.loadSchema();
		SpringApplication.run(DemoApplication.class, args);
		System.out.println("HELLO CSYE7255!!\n");
		String inputJson = JSON_Reader.readJSONFile();
		//System.out.println("input json: " + inputJson);

//		ObjectMapper objectMapper = new ObjectMapper();
//		JsonNode rootNode;
//		try {
//			rootNode = objectMapper.readTree(inputJson);
//			System.out.printf("root: %s type=%s%n", rootNode, rootNode.getNodeType());
//			JSON_Reader.addKeys(inputJson, rootNode, map, suffix);
//			//JSON_Reader.traverse(rootNode, 1);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
	}

}
