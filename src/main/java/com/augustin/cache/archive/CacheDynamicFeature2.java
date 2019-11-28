package com.augustin.cache.archive;

import java.lang.reflect.Method;

import javax.ws.rs.GET;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.FeatureContext;

import com.augustin.cache.customcache.ExtendedCache;
import com.augustin.cache.feature.Cache;
import com.augustin.cache.logic.HttpResponse;
import com.augustin.cache.logic.TempResponseWrapper;

public class CacheDynamicFeature2 implements DynamicFeature {
	
	private ExtendedCache<TempResponseWrapper<HttpResponse>> cache;
	
	/**
	 * Soft reference will be used to store values
	 */
	public CacheDynamicFeature2() {
		cache = new ExtendedCache<>();
	}
	
	/**
	 * Strong reference will be used to store values
	 * @param cacheSize in Kilobytes
	 */
	public CacheDynamicFeature2(long cacheSize) {
		cacheSize = cacheSize * 1024L;
		cache = new ExtendedCache<>(cacheSize);
	}
	
	
	
	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
		final Class<?> declaring = resourceInfo.getResourceClass();
        final Method method = resourceInfo.getResourceMethod();

        if (declaring == null || method == null) {
            return;
        }
        if (!method.isAnnotationPresent(GET.class)) {
            return;
        }

        Cache classCached = declaring.getAnnotation(Cache.class);
        Cache methodCached = method.getAnnotation(Cache.class);
        CacheControl cacheControl = null;
        if (methodCached != null) {
            cacheControl = initCacheControl(methodCached);
        } else if (classCached != null) {
            cacheControl = initCacheControl(classCached);
        }
        
        if (cacheControl != null) {
        	//context.register(new CacheRequestFilter(cache, cacheControl));
        }
	}
	
	protected CacheControl initCacheControl(Cache methodCached) {
        CacheControl cacheControl = new CacheControl();
        if (methodCached.maxAge() > -1) {
            cacheControl.setMaxAge(methodCached.maxAge());
        }
        cacheControl.setMustRevalidate((methodCached.mustRevalidate()));
        return cacheControl;
    }
	
	
}
