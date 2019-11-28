package com.augustin.cache.logic;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import com.augustin.cache.customcache.ExtendedCache;

public class CacheRequestFilter implements ContainerRequestFilter {
	
	private ExtendedCache<ResponseWrapper<HttpResponse>> cache;
	private CacheControl cacheControl;
	
	public CacheRequestFilter(ExtendedCache<ResponseWrapper<HttpResponse>> cache, 
			CacheControl cacheControl) {
		this.cache = cache;
		this.cacheControl = cacheControl;
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String key = KeyTool.generate(requestContext);
		System.out.println("==key : " + key);
		ResponseWrapper<HttpResponse> entry = cache.computeIfAbsent(
				key,
				new TempResponseWrapper<>(), 
				Long.MAX_VALUE);
		
		if(entry instanceof CompletedResponseWrapper) {
			sendCachedResponse(requestContext, (CompletedResponseWrapper<HttpResponse>)entry);
		}	
		
		if(entry instanceof TempResponseWrapper) {
			TempResponseWrapper<HttpResponse> placeHolder = (TempResponseWrapper<HttpResponse>)entry;
			
			CacheEntryContext entryContext = new CacheEntryContext();
			entryContext.placeHolder = placeHolder;
			entryContext.cacheControl = this.cacheControl;
			entryContext.key = key;
			requestContext.setProperty(CacheEntryContext.NAME, entryContext);
			
			synchronized(placeHolder) {
				while(!placeHolder.isReady()) {
					if(placeHolder.isNew()) {
						placeHolder.setPending();
						break;
					}
					else if(placeHolder.isPending()) {
						try {
							placeHolder.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					else {
						throw new RuntimeException("CacheRequestFilter : Entry state must be between "
								+ ": new - pending - ready.");
					}
				}
			}
			
			if(placeHolder.isReady()) {
				entryContext.responseExists = true;
				sendResponse(requestContext, placeHolder);
			}
		}
	}
	
	public void sendResponse(ContainerRequestContext requestContext, 
			TempResponseWrapper<HttpResponse> responseWrapper) {
		System.out.println("===abort and send temp response fired");
		requestContext.abortWith(Response.fromResponse(responseWrapper.get().asResponse())
				.cacheControl(cacheControl)
				.build());
	}
	
	public void sendCachedResponse(ContainerRequestContext requestContext, 
			CompletedResponseWrapper<HttpResponse> responseWrapper) {
		System.out.println("===abort and send cached response fired");
		CacheControl newCacheControl = CacheControl.valueOf(cacheControl.toString());
		if(!responseWrapper.isExpired()) {
			newCacheControl.setMaxAge(responseWrapper.getExpirationInSeconds());
		}
		
		requestContext.abortWith(Response.fromResponse(responseWrapper.get().asResponse())
				.cacheControl(newCacheControl)
				.build());
	}
}
