package com.augustin.cache.archive;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.augustin.cache.customcache.ExtendedCache;
import com.augustin.cache.logic.CacheEntryContext;
import com.augustin.cache.logic.HttpResponse;
import com.augustin.cache.logic.TempResponseWrapper;

public class CacheRequestFilter2 implements ContainerRequestFilter {
	
	private ExtendedCache<TempResponseWrapper<HttpResponse>> cache;
	private CacheControl cacheControl;
	private KeyTool4 keyTool;
	
	public CacheRequestFilter2(
			ExtendedCache<TempResponseWrapper<HttpResponse>> cache, 
			CacheControl cacheControl,
			List<MediaType> acceptedMediaTypes) {
		this.cache = cache;
		this.cacheControl = cacheControl;
		this.keyTool = new KeyTool4(acceptedMediaTypes);
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String key = keyTool.generate(requestContext);
		if(key == null || key.isEmpty()) return;
		
		TempResponseWrapper<HttpResponse> entry;
		entry = cache.computeIfAbsent(key, new TempResponseWrapper<>(), Long.MAX_VALUE);
		
		CacheEntryContext entryContext = new CacheEntryContext();
		//entryContext.key = key;
		//entryContext.value = entry;
		//entryContext.keyTool;// = keyTool;
		//entryContext.cache = cache;
		requestContext.setProperty(CacheEntryContext.NAME, entryContext);
		
		synchronized (entry) {
			while(!entry.isReady()) {
				if(entry.isNew()) {
					entry.setPending();
					entryContext.cacheControl = cacheControl;
				}
				else if(entry.isPending()) {
					try {
						entry.wait();
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
		
		entryContext.responseExists = true;
		onReady(requestContext, entry);
	}
	
	public void onReady(ContainerRequestContext requestContext, TempResponseWrapper<HttpResponse> entry) {
		
		CacheControl newCacheControl = new CacheControl();
		//if(!entry.isExpired()) newCacheControl.setMaxAge(entry.getExpirationInSeconds()); 
		/*
		requestContext.abortWith(Response.fromResponse(entry.getData().asResponse())
				.cacheControl(newCacheControl)
				.build());*/
	}

}
