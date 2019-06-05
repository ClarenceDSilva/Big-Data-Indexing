package com.example.service;

import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

public interface RedisService {
	
	public String getValue(final String key);
	public void traverseInput(JsonNode inputData);
	public void populateNestedData(JsonNode parent, Set<String> childIdSet);
	public boolean deleteValue(String key);
}
