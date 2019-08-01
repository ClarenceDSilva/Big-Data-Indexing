package com.example.dao;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Jedis;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class RedisDaoImpl<T> implements RedisDao<T> {

	private RedisTemplate<String, T> redisTemplate;
	private HashOperations<String, Object, T> hashOperation;
	//private ListOperations<String,T> listOperation;
	private ValueOperations<String, T> valueOperations;

	@Autowired
	RedisDaoImpl(RedisTemplate<String, T> redisTemplate) {
		this.redisTemplate = redisTemplate;
		this.hashOperation = redisTemplate.opsForHash();
		//this.listOperation = redisTemplate.opsForList();
		this.valueOperations = redisTemplate.opsForValue();
	}

	@SuppressWarnings("unchecked")
	public void putMap(String redisKey, Object key, Object data) {
		hashOperation.put(redisKey, key, (T) data);
	}

	public T getMapAsSingleEntry(String redisKey, Object key) {
		return hashOperation.get(redisKey, key);
	}

	public Map<Object, T> getMapAsAll(String redisKey) {
		return hashOperation.entries(redisKey);
	}

	@SuppressWarnings("unchecked")
	public void putValue(String key, Object value) {
		valueOperations.set(key, (T) value);
	}

	@SuppressWarnings("unchecked")
	public void putValueWithExpireTime(String key, Object value, long timeout, TimeUnit unit) {
		valueOperations.set(key, (T) value, timeout, unit);
	}
	@Override
	public T getValue(String key) {
		return valueOperations.get(key);
	}

	public void setExpire(String key, long timeout, TimeUnit unit) {
		redisTemplate.expire(key, timeout, unit);
	}

	@Override
	public boolean deleteValue(String key) {
		return redisTemplate.delete(key);
	}
	
	@Override
	public long deleteAll() {
		//redisTemplate.opsForList().rightPushIfPresent(key, value);
		String keys = "*";
		Collection<String> allKeys = new HashSet<String>();
		allKeys = redisTemplate.keys(keys);
		return redisTemplate.delete(allKeys);
	}
	
	public T getHash(String input) {
		return valueOperations.get(input + "_hash");
	}
	
	public int add(int a) {
		return 1;
	}

}