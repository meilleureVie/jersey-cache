package com.augustin.cache.customcache;

import java.lang.ref.SoftReference;

public class SoftCacheEntry<T> extends SoftReference<T> implements CacheEntry<T> {
	private long timeToLive;
	
	public SoftCacheEntry(T referent, long timeToLive) {
		super(referent);
		this.timeToLive = timeToLive;
	}
	
	@Override
	public long getTimeToLive() {
		return timeToLive;
	}
}