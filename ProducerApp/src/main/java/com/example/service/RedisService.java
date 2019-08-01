package com.example.service;

import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

public interface RedisService {
	
	public String getValue(final String key);
	public void traverseInput(JsonNode inputData);
	public void populateNestedData(JsonNode parent, Set<String> childIdSet);
	public boolean deleteValue(String key);
	public void updateJsonObject(JsonNode parent, Set<String> childIdSet);
	public long deleteAllValues();
	public void postValue(final String key, final String value);
	public String getHash(String internalID);
	
}
