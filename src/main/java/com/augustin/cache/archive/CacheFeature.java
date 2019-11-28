package com.augustin.cache.archive;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.augustin.cache.feature.CacheDynamicFeature;

public class CacheFeature implements Feature {
	
	private CacheDynamicFeature cacheDynamicFeature;
	
	/**
	 * Soft reference will be used to store values
	 */
	public CacheFeature() {
		cacheDynamicFeature = new CacheDynamicFeature();
	}
	
	/**
	 * Strong reference will be used to store values
	 * @param cacheSize in Kilobytes
	 */
	public CacheFeature(long cacheSize) {
		cacheDynamicFeature = new CacheDynamicFeature(cacheSize);
	}
	
	@Override
	public boolean configure(FeatureContext context) {
		context.register(cacheDynamicFeature);
		//context.register(CacheResponseFilter.class);
		return true;
	}
}
