package com.augustin.cache.feature;

import java.lang.reflect.Method;

import javax.ws.rs.GET;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.FeatureContext;

import com.augustin.cache.customcache.ExtendedCache;
import com.augustin.cache.logic.CacheRequestFilter;
import com.augustin.cache.logic.CacheResponseFilter;
import com.augustin.cache.logic.HttpResponse;
import com.augustin.cache.logic.ResponseWrapper;

/**
 * 
 * @author amedoatinsa
 *
 */
public class CacheDynamicFeature implements DynamicFeature {
	
	private ExtendedCache<ResponseWrapper<HttpResponse>> cache;
	private CacheResponseFilter cacheResponseFilter;
	
	/**
	 * Soft reference will be used to store values
	 */
	public CacheDynamicFeature() {
		this(-1);
	}
	
	/**
	 * Strong/soft reference will be used to store values
	 * @param cacheSize in Kilobytes.
	 * Negative value for soft reference and positive value for strong reference
	 */
	public CacheDynamicFeature(long cacheSize) {
		if(cacheSize > 0) cacheSize = cacheSize * 1024L;
		this.cache = new ExtendedCache<>(cacheSize);
		this.cacheResponseFilter = new CacheResponseFilter(this.cache);
	}
	
	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
		CacheControl cacheControl = getCacheControl(resourceInfo);
		
		if(cacheControl != null) {
			context.register(new CacheRequestFilter(cache, cacheControl));
			context.register(cacheResponseFilter);
		}
	}
	
	public CacheControl getCacheControl(ResourceInfo resourceInfo) {
		final Class<?> declaring = resourceInfo.getResourceClass();
        final Method method = resourceInfo.getResourceMethod();

        if (declaring == null || method == null) {
            return null;
        }
        if (!method.isAnnotationPresent(GET.class)) {
            return null;
        }

        Cache classCached = declaring.getAnnotation(Cache.class);
        Cache methodCached = method.getAnnotation(Cache.class);
        CacheControl cacheControl = null;
        if (methodCached != null) {
            cacheControl = initCacheControl(methodCached);
        } else if (classCached != null) {
            cacheControl = initCacheControl(classCached);
        }
        
        return cacheControl;
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
