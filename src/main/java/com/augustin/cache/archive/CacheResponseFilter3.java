package com.augustin.cache.archive;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.augustin.cache.customcache.ExtendedCache;
import com.augustin.cache.logic.CacheEntryContext;
import com.augustin.cache.logic.HttpResponse;
import com.augustin.cache.logic.TempResponseWrapper;

public class CacheResponseFilter3 implements ContainerResponseFilter {
	
	private static Logger logger = LoggerFactory.getLogger(CacheResponseFilter3.class);
	
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		
		CacheEntryContext entryContext;
		entryContext = (CacheEntryContext) requestContext.getProperty(CacheEntryContext.NAME);
		if(entryContext == null || entryContext.responseExists) return;
		
		//String key = entryContext.key;
		//TempResponseWrapper<HttpResponse> entry = entryContext.value;
		KeyTool4 keyTool = null; //entryContext.keyTool;
		//ExtendedCache<TempResponseWrapper<HttpResponse>> cache = entryContext.cache;
		CacheControl cacheControl = entryContext.cacheControl;
		/*
		synchronized(entry) {
			if(isSuccessful(responseContext.getStatus())) {
				if(keyTool.verify(key, requestContext, responseContext)) {
					responseContext.getHeaders().putSingle(HttpHeaders.CACHE_CONTROL, cacheControl.toString());
					//entry.setData(HttpResponse.from(responseContext), cacheControl.getMaxAge() * 1000L);
					cache.put(key, entry, cacheControl.getMaxAge() * 1000L);
				}else {
					String expectedMediaType = keyTool.getSelectedMediaType(key);
					String msg = String.format(
							"content type expected : %s - content type produced : %s\n"
							+ "it means the provided response can not be serialized in content type expected\n"
							+ "so due to cache consistence, this response will not be cached",
							expectedMediaType, responseContext.getMediaType().toString());
					responseContext.setStatus(500);
					responseContext.setEntity(msg);
					///entry.setData(HttpResponse.from(responseContext), cacheControl.getMaxAge() * 1000L);
					cache.remove(key);
					logger.error(msg);
				}
			}else {
				//entry.setData(HttpResponse.from(responseContext), cacheControl.getMaxAge() * 1000L);
				cache.remove(key);
			}
			entry.setReady();
			notifyAll();
		}
		*/
	}

	public boolean isSuccessful(int status) {
		return (status >= 200 && status <= 299);
	}
}
