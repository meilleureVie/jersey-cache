package com.augustin.cache.customcache;

public interface CacheEntry<T> {
	
	T get();
	long getTimeToLive();
}
