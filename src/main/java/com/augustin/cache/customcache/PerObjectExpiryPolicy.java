package com.augustin.cache.customcache;

import java.time.Duration;
import java.util.function.Supplier;

import org.ehcache.expiry.ExpiryPolicy;

public class PerObjectExpiryPolicy implements ExpiryPolicy<Object, Object>  {
	
	@Override
	public Duration getExpiryForCreation(Object key, Object value) {
		return getDuration(value);
	}
	
	@Override
	public Duration getExpiryForAccess(Object key, Supplier<? extends Object> value) {
		return null;
	}

	@Override
	public Duration getExpiryForUpdate(Object key, Supplier<? extends Object> oldValue, Object newValue) {
		return getDuration(newValue);
	}
	
	@SuppressWarnings("rawtypes")
	public Duration getDuration(Object value) {
		if(value instanceof CacheEntry) {
			long timeToLive = ((CacheEntry)value).getTimeToLive();
			return Duration.ofMillis(timeToLive);
		}
		throw new RuntimeException("PerObjectExpiryPolicy must cache only CacheEntry object.");
	}
}
