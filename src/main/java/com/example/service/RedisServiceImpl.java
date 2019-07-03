package com.example.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dao.RedisDao;
import com.example.util.JSON_Reader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Service
public class RedisServiceImpl implements RedisService {

	public static final String ID = "id_";

	@Autowired
	RedisDao<String> redisDao;

	public String getValue(final String key) {
		return redisDao.getValue(key);
	}

	@Override
	public void traverseInput(JsonNode inputData) {
		inputData.fields().forEachRemaining(entry -> {
			// Check if the field is an array
			if (entry.getValue().isArray()) {
				ArrayList<JsonNode> innerValues = new ArrayList<JsonNode>();
				Iterator<JsonNode> iterator = entry.getValue().iterator();
				while (iterator.hasNext()) {
					JsonNode jn = (JsonNode) iterator.next();

					if (jn.isContainerNode())
						traverseInput(jn);

					innerValues.add(replace(jn));
					traverseInput(jn);
				}
				if (!innerValues.isEmpty()) {
					((ArrayNode) entry.getValue()).removeAll();
					innerValues.forEach(s -> {
						if (s != null)
							((ArrayNode) entry.getValue()).add(s);
					});
				}
			}
			// Check if the field is an object
			else if (entry.getValue().isContainerNode()) {
				//System.out.println(entry.getValue().findValue("objectId"));
				//traverseInput(entry.getValue());
				replaceWithId(entry);
			}
		});
	}

	private void replaceWithId(Map.Entry<String, JsonNode> entry) {
		JsonNode node = replace(entry.getValue());
		entry.setValue(node);
	}

	private JsonNode replace(JsonNode entry) {
		ObjectMapper mapper = new ObjectMapper();
		String value = entry.toString();
		String id = ID + entry.get("objectType").asText() + "_" + entry.get("objectId").asText();
		JsonNode node = mapper.valueToTree(id);
		redisDao.putValue(id, value);
		return node;
	}

	@Override
	public void populateNestedData(JsonNode parent, Set<String> childIdSet) {

        if (parent == null) 
        	return;
        
        while (parent.toString().contains(ID)) 
        {
        	parent.fields().forEachRemaining(s -> {
                if (s.getValue().isArray())
                {
                    ArrayList<JsonNode> innerValues = new ArrayList<>();
                    s.getValue().iterator().forEachRemaining(node -> {
                        if (node.asText().startsWith((ID)))
                            innerValues.add(node);
                        if (node.isContainerNode()) 
                        	populateNestedData(node, childIdSet);
                        
                        node.iterator().forEachRemaining(innerNode -> {
                            if (innerNode.isContainerNode())
                                populateNestedData(node, childIdSet);
                        });
                    });
                    
                    if (!innerValues.isEmpty()) 
                    {
                        ((ArrayNode) s.getValue()).removeAll();
                        innerValues.forEach(innerValue -> {
                            if (childIdSet != null) childIdSet.add(innerValue.asText());
                            String value = redisDao.getValue(innerValue.asText());
                            if (value != null)
                                ((ArrayNode) s.getValue()).add(JSON_Reader.nodeFromString(value));
                        });
                    }
                }
                
                String value = s.getValue().asText();

                if (value.startsWith(ID)) 
                {
                    if (childIdSet != null) 
                    	childIdSet.add(value);
                    
                    String val = redisDao.getValue(value);
                    val = val == null ? "" : val;
                    JsonNode node = JSON_Reader.nodeFromString(val);
                    s.setValue(node);
                }
            });
        }
    }
	
	@Override
	public void postValue(final String key, final String value) 
	{
		//logger.info("postValue ( key : " + key + " value : " + value +  " - Start");
		redisDao.putValue(key, value);
		//logger.info("postValue ( key : " + key + " value : " + value +  " - End");
	}
	
	@Override
	public String getHash(String internalID) {
		return redisDao.getHash(internalID);
	}

	@Override
	public boolean deleteValue(String key) {
		return redisDao.deleteValue(key);
	}
	
	//NEW
	@Override
	public long deleteAllValues() {
		return redisDao.deleteAll();
	}
	
	@Override
	public void updateJsonObject(JsonNode parent, Set<String> childIdSet) {
//		inputData.fields().forEachRemaining(entry -> {
//			// Check if the field is an array
//			if (entry.getValue().isArray()) {
//				ArrayList<JsonNode> innerValues = new ArrayList<JsonNode>();
//				Iterator<JsonNode> iterator = entry.getValue().iterator();
//				while (iterator.hasNext()) {
//					JsonNode jn = (JsonNode) iterator.next();
//
//					if (jn.isContainerNode())
//						traverseInput(jn);
//				
//					innerValues.add(replace(jn));
//					traverseInput(jn);
//				}
//				if (!innerValues.isEmpty()) {
//					((ArrayNode) entry.getValue()).removeAll();
//					innerValues.forEach(s -> {
//						if (s != null)
//							((ArrayNode) entry.getValue()).add(s);
//					});
//				}
//			}
//			// Check if the field is an object
//			else if (entry.getValue().isContainerNode()) {
//				System.out.println("TRUE");
//				//System.out.println(entry.getValue().findValue("objectId"));
//				//System.out.println(entry.getValue());
//				traverseInput(entry.getValue());
//				//System.out.println(entry.getValue().toString());
//				replaceWithId(entry);
//			}
//		});
		
		 if (parent == null) 
	        	return;
	        
	        while (parent.toString().contains(ID)) 
	        {
	        	parent.fields().forEachRemaining(s -> {
	                if (s.getValue().isArray())
	                {
	                    ArrayList<JsonNode> innerValues = new ArrayList<>();
	                    s.getValue().iterator().forEachRemaining(node -> {
	                        if (node.asText().contains("1234512xvc1314asdfs-503"))
	                            innerValues.add(node);
	                        if (node.isContainerNode()) 
	                        	populateNestedData(node, childIdSet);
	                        
	                        node.iterator().forEachRemaining(innerNode -> {
	                            if (innerNode.isContainerNode())
	                                populateNestedData(node, childIdSet);
	                        });
	                    });
	                    
	                    if (!innerValues.isEmpty()) 
	                    {
	                        ((ArrayNode) s.getValue()).removeAll();
	                        innerValues.forEach(innerValue -> {
	                            if (childIdSet != null) childIdSet.add(innerValue.asText());
	                            String value = redisDao.getValue(innerValue.asText());
	                            if (value != null)
	                                ((ArrayNode) s.getValue()).add(JSON_Reader.nodeFromString(value));
	                        });
	                    }
	                }
	                
	                String value = s.getValue().asText();

	                if (value.startsWith(ID)) 
	                {
	                    if (childIdSet != null) 
	                    	childIdSet.add(value);
	                    
	                    String val = redisDao.getValue(value);
	                    val = val == null ? "" : val;
	                    JsonNode node = JSON_Reader.nodeFromString(val);
	                    s.setValue(node);
	                }
	            });
	        }
		
	}
}
