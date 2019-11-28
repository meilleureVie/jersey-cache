package com.augustin.cache.customcache;

public class StrongCacheEntry<T> implements CacheEntry<T> {
	private T data;
	private long timeToLive;
	
	public StrongCacheEntry(T data, long timeToLive) {
		this.data = data;
		this.timeToLive = timeToLive;
	}
	
	@Override
	public T get() {
		return data;
	}
	
	@Override
	public long getTimeToLive() {
		return timeToLive;
	}
	
}
