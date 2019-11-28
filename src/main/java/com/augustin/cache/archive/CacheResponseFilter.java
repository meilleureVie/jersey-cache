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

public class CacheResponseFilter implements ContainerResponseFilter {
	
	private static Logger logger = LoggerFactory.getLogger(CacheResponseFilter.class);
	
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		
		CacheEntryContext entryContext;
		entryContext = (CacheEntryContext) requestContext.getProperty(CacheEntryContext.NAME);
		if(entryContext == null || entryContext.responseExists) return;
		
		//String key = entryContext.key;
		//TempResponseWrapper<HttpResponse> entry = entryContext.value;
		KeyTool4 keyTool = null;// = entryContext.keyTool;
		//ExtendedCache<TempResponseWrapper<HttpResponse>> cache = entryContext.cache;
		CacheControl cacheControl = entryContext.cacheControl;
		/*
		synchronized(entry) {
			if(responseContext.getStatus() == 200) {
				if(keyTool.verify(key, requestContext, responseContext)) {
					responseContext.getHeaders().putSingle(HttpHeaders.CACHE_CONTROL, cacheControl.toString());
					//entry.setData(HttpResponse.from(responseContext), cacheControl.getMaxAge() * 1000L);
					cache.put(key, entry, cacheControl.getMaxAge() * 1000L);
				}else {
					cache.remove(key);
					logger.warn("key before does not match key after. "
							+ "Maybe one of your body writer can not serialize this response in first content type selected.");
				}
			}else {
				cache.remove(key);
			}
			
			if(entry.isPending()) {
				entry.setReady();
				entry.notifyAll();
			}
			else {
				throw new RuntimeException("CacheResponseFilter : only pending state can come here.");
			}
		}
		*/
	}

}
