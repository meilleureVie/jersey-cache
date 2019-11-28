package com.augustin.cache.logic;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;

import com.augustin.cache.customcache.ExtendedCache;

public class CacheResponseFilter implements ContainerResponseFilter {
	
	private ExtendedCache<ResponseWrapper<HttpResponse>> cache;
	
	public CacheResponseFilter(ExtendedCache<ResponseWrapper<HttpResponse>> cache) {
		this.cache = cache;
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		
		CacheEntryContext entryContext = (CacheEntryContext) requestContext.getProperty(CacheEntryContext.NAME);
		if(entryContext == null || entryContext.responseExists) return;
		
		TempResponseWrapper<HttpResponse> placeHolder = entryContext.placeHolder;
		CacheControl cacheControl = entryContext.cacheControl;
		String entryKey = entryContext.key;
		
		synchronized(placeHolder) {
			if(isSuccessful(responseContext.getStatus())) {
				cache.put(entryKey, 
						new CompletedResponseWrapper<>(HttpResponse.from(responseContext), 
								cacheControl.getMaxAge() * 1000L), 
						cacheControl.getMaxAge() * 1000L);
				
				responseContext.getHeaders().putSingle(HttpHeaders.CACHE_CONTROL, 
						cacheControl.toString());	
			}else {
				cache.remove(entryKey);
			}
			System.out.println("===new response filter fired");
			placeHolder.setResponse(HttpResponse.from(responseContext));
			placeHolder.setReady();
			placeHolder.notifyAll();
		}
	}

	public boolean isSuccessful(int status) {
		return (status >= 200 && status <= 299);
	}
}
