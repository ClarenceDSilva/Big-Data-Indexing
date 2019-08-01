package com.example.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.RedisResult;
import com.example.service.KafkaService;
import com.example.service.RedisService;
import com.example.util.JSON_Reader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
public class RedisRestController {

	@Autowired
	@Qualifier("RedisTemplate")
	private RedisTemplate<String, Object> template;
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private KafkaService kafkaService;
	
	public static final String ID = "id_";
	
	/* Controller method for testing basic REDIS calls */
	
	@GetMapping("/singlekey/{key}")
	public RedisResult getSingleValue(@PathVariable("key") String key) {
		String value = (String) this.template.opsForValue().get(key);
		RedisResult result = new RedisResult(key, value);
		return result;
	}

	@RequestMapping(value = "/values", method = RequestMethod.GET)
	public List<RedisResult> getValues() {
		List<RedisResult> results = new ArrayList<>();
		Set<String> keys = this.template.keys("*");
		for (String key : keys) {
			results.add(new RedisResult(key, (String) this.template.opsForValue().get(key)));
		}
		return results;
	}

	@PostMapping("/singlekey/{key}/{value}")
	public RedisResult addValue(@PathVariable("key") String key, @PathVariable("value") String value) {
		RedisResult result = new RedisResult(key, value);
		this.template.opsForValue().set(key, value);
		return result;
	}
	
	/* Controller methods for incorporating JSON data into REDIS */
	
	@RequestMapping(value = "/value/{object}/{key}", method = RequestMethod.GET)
    public ResponseEntity<String> getValue(@PathVariable String object, @PathVariable String key) {

		String internalID = ID + object + "_" + key;
        String value = redisService.getValue(internalID);
        
        if (value == null) {
            return new ResponseEntity<String>("{\"message\": \"No Data Found\" }", HttpStatus.NOT_FOUND);
        }
        
        try
        {
        	JsonNode node = JSON_Reader.nodeFromString(value);
    		redisService.populateNestedData(node, null);
    		value = node.toString();
    		return ResponseEntity.ok().body(value);
        }
        catch(Exception e)
        {
        	System.out.println(e.getMessage());
        }
        
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

	@RequestMapping(value = "/value/{object}", method = RequestMethod.POST)
    public ResponseEntity<String> postValue(@PathVariable String object, HttpEntity<String> input) {

		String planId = "";
		JsonNode rootNode = JSON_Reader.validateAgainstSchema(input.getBody());
		if(null != rootNode)
		{
			String objectId = rootNode.get("objectId").textValue();
	        planId = ID + rootNode.get("objectType").textValue() + "_" + objectId;
			
			if (redisService.getValue(planId) != null) {
	            return ResponseEntity.status(HttpStatus.CONFLICT).body(" {\"message\": \"resource already exisits with Id: " + planId + "\" }");
	        }
			
			redisService.traverseInput(rootNode);
			this.template.opsForValue().set(planId, rootNode.toString());
			
			//Enqueue the data in kafka
			kafkaService.publish(input.getBody(), "index");
		}else {
			return ResponseEntity.ok().body(" {\"message\": \"Error validating the input data\" }");
		}
		
        return ResponseEntity.ok().body(" {\"message\": \"Created data with key: " + planId + "\" }");
    }
	
	@RequestMapping(value = "/value/{object}/{key}", method = RequestMethod.PATCH)
    public ResponseEntity<String> patchValue(@PathVariable String object, @PathVariable String key, HttpEntity<String> input,
    												@RequestHeader(value = "If-None-Match", defaultValue = "-1x") String reqEtag) {

		String internalID = ID + object + "_" + key;
        String value = redisService.getValue(internalID);
        
        if (value == null) {
            return new ResponseEntity<String>("{\"message\": \"No Data Found\" }", HttpStatus.NOT_FOUND);
        }
        
        String etag = redisService.getHash(internalID);
        if (reqEtag.equalsIgnoreCase(etag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        
        try
        {
        	//Get the old node from redis using the object Id 
        	JsonNode oldNode = JSON_Reader.nodeFromString(value);
        	redisService.populateNestedData(oldNode, null);
    		value = oldNode.toString();
    		
    		//Construct the new node from the input body
    		String inputData = input.getBody();
    		JsonNode newNode = JSON_Reader.nodeFromString(inputData);
    		
    		ArrayNode planServicesNew = (ArrayNode) newNode.get("linkedPlanServices");
            Set<JsonNode> planServicesSet = new HashSet<>();
            Set<String> objectIds = new HashSet<String>();

            planServicesNew.addAll((ArrayNode) oldNode.get("linkedPlanServices"));

            for(JsonNode node : planServicesNew)
            {
            	Iterator<Entry<String, JsonNode>> sitr = node.fields();
            	while(sitr.hasNext())
            	{
            		Entry<String, JsonNode> val = sitr.next();
            		if(val.getKey().equals("objectId"))
            		{
            			if(!objectIds.contains(val.getValue().toString())) 
            			{
            				planServicesSet.add(node);
            				objectIds.add(val.getValue().toString());
            			}
            		}
            	}
            }
            
            planServicesNew.removeAll();
            
            if (!planServicesSet.isEmpty())
                planServicesSet.forEach(s -> { planServicesNew.add(s); });
            
	            redisService.traverseInput(newNode);
	            redisService.postValue(internalID, newNode.toString());
            
	            //Enqueue the data in kafka
	            kafkaService.publish(input.getBody(), "index");
            
        }
        catch(Exception e)
        {
        	return new ResponseEntity<>(" {\"message\": \"Invalid Data\" }", HttpStatus.BAD_REQUEST);
        }
		
		return ResponseEntity.ok().body(" {\"message\": \"Updated data with key: " + internalID + "\" }");
    }
	
	@RequestMapping(value = "/{object}/{objectId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteObject(@PathVariable("object") String object, @PathVariable("objectId") String objectId) {

        String internalID = ID + object + "_" + objectId;
        String masterObject = redisService.getValue(internalID);
        Set<String> childIdSet = new HashSet<String>();
        childIdSet.add(internalID);
        redisService.populateNestedData(JSON_Reader.nodeFromString(masterObject), childIdSet);
        boolean deleteSuccess = false;
        
        for(String id : childIdSet)
        {
        	deleteSuccess = redisService.deleteValue(id);
        }
        //Enqueue the data in kafka
      	kafkaService.publish(objectId, "delete");
        
        if(deleteSuccess)
        	return new ResponseEntity<>(" {\"message\": \"Deleted\" }", HttpStatus.OK);
        
        return new ResponseEntity<>(" {\"message\": \"Nothing to delete\" }", HttpStatus.NOT_FOUND);
    }
	
	//PUT
	@RequestMapping(value = "/value/{object}/{key}", method = RequestMethod.PUT)
    public ResponseEntity<String> putValue(@PathVariable String object, HttpEntity<String> input, @PathVariable String key) {
		String internalID = ID + object + "_" + key;
        String masterObject = redisService.getValue(internalID);
        
        if (masterObject == null) {
            return new ResponseEntity<String>("{\"message\": \"No Data Found\" }", HttpStatus.NOT_FOUND);
        }
		
        Set<String> childIdSet = new HashSet<String>();
        childIdSet.add(internalID);
        redisService.populateNestedData(JSON_Reader.nodeFromString(masterObject), childIdSet);
        boolean deleteSuccess = false;
        
        for(String id : childIdSet)
        {
        	deleteSuccess = redisService.deleteValue(id);
        }
        
        if(deleteSuccess)
        {
        	String planId = "";
    		JsonNode rootNode = JSON_Reader.validateAgainstSchema(input.getBody());
    		if(null != rootNode)
    		{
    			String objectId = rootNode.get("objectId").textValue();
    	        planId = ID + rootNode.get("objectType").textValue() + "_" + objectId;
    			
    			if (redisService.getValue(planId) != null) {
    	            return ResponseEntity.status(HttpStatus.CONFLICT).body(" {\"message\": \"A resource already exisits with the id: " + planId + "\" }");
    	        }
    			
    			redisService.traverseInput(rootNode);
    			redisService.postValue(planId, rootNode.toString());
    			//Enqueue the data in kafka
	            kafkaService.publish(input.getBody(), "index");
    		}
    		else
    		{
    			return ResponseEntity.ok().body(" {\"message\": \"Error validating the input data\" }");
    		}
    		
            return ResponseEntity.ok().body(" {\"message\": \"Updated data with key: " + planId + "\" }");
        }
        
        return ResponseEntity.ok().body(" {\"message\": \"Error updating the object }");
    }
}
