package com.augustin.cache.customcache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ExtendedCache<T> { 
	private static final String NAME = "EXTENDED_CACHE_FOR_JERSEY_HTTP_RESPONSE";
	
	private static final int STRONG_REFERENCE = 0;
	private static final int SOFT_REFERENCE = 1;
	private int refType;
	
	private CacheManager cacheManager;
	private Cache<String, CacheEntry> cache;
	
	/**
	 * Soft reference will be used
	 */
	public ExtendedCache() {
		this(-1);
	}
	
	/**
	 * 
	 * @param cacheSize in bytes.
	 * Negative value for soft reference and positive value for strong reference
	 */
	public ExtendedCache(long cacheSize) {
		if(cacheSize < 0) {
			refType = SOFT_REFERENCE;
			initCache(0);
		}else {
			refType = STRONG_REFERENCE;
			initCache(cacheSize);
		}
	}
	
	/**
	 * 
	 * @param cacheSize in bytes.
	 */
	private void initCache(long cacheSize) {
		cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
		cache = cacheManager.createCache(NAME,
				CacheConfigurationBuilder.newCacheConfigurationBuilder(
						String.class, 
						CacheEntry.class,
						ResourcePoolsBuilder.newResourcePoolsBuilder()
								.heap(refType == SOFT_REFERENCE ? Long.MAX_VALUE : cacheSize, MemoryUnit.B)
						)
				.withExpiry(new PerObjectExpiryPolicy()));
	}
	
	public T computeIfAbsent(String key, T data, long timeToLive) {
		// step 1 : we try to get cache entry value if it exists
		T value = get(key);
		if(value != null) return value;
		
		// step 2 : if cache entry does not exist, we try to create one and add it to cache concurrently
		CacheEntry<T> valueReference;
		if(refType == STRONG_REFERENCE) {
			valueReference = new StrongCacheEntry<T>(data, timeToLive);
		}
		else if(refType == SOFT_REFERENCE) {
			valueReference = new SoftCacheEntry<T>(data, timeToLive);
		}
		else {
			throw new RuntimeException("ExtendedCache : valueRef type must be 0(strong) or 1(soft) reference.");
		}
		CacheEntry<T> valueReference2 = cache.putIfAbsent(key, valueReference);
		if(valueReference2 == null) {
			valueReference2 = cache.get(key);
		}
		if(valueReference2 != null && valueReference2.get() != null) {
			return valueReference2.get();
		}
		
		//step 3 : if cache entry is recycled due to cache timeout or soft reference, we try to set it again with lock
		synchronized(cache) {
			T value3 = get(key);
			if(value3 != null) return value3;
			
			put(key, data, timeToLive);
			return data;
		}
	}
	
	public void put(String key, T data, long timeToLive) {
		CacheEntry<T> valueReference;
		if(refType == STRONG_REFERENCE) {
			valueReference = new StrongCacheEntry<T>(data, timeToLive);
		}
		else if(refType == SOFT_REFERENCE) {
			valueReference = new SoftCacheEntry<T>(data, timeToLive);
		}
		else {
			throw new RuntimeException("ExtendedCache : valueRef type must be 0(strong) or 1(soft) reference.");
		}
		cache.put(key, valueReference);
	}
	
	public T get(String key) {
		CacheEntry<T> data  = cache.get(key);
		if(data == null || data.get() == null) return null;
		return data.get();
	}
	
	public void remove(String key) {
		cache.remove(key);
	}
}
