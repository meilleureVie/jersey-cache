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
import com.augustin.cache.logic.ResponseWrapper;
import com.augustin.cache.logic.TempResponseWrapper;

public class CacheRequestFilter3 implements ContainerRequestFilter {
	
	private ExtendedCache<ResponseWrapper<HttpResponse>> cache;
	private CacheControl cacheControl;
	private List<MediaType> resourceMediaTypes;
	
	public CacheRequestFilter3(
			ExtendedCache<ResponseWrapper<HttpResponse>> cache, 
			CacheControl cacheControl,
			List<MediaType> resourceMediaTypes) {
		this.cache = cache;
		this.cacheControl = cacheControl;
		this.resourceMediaTypes = resourceMediaTypes;
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		List<MediaType> filteredMediaTypes = KeyTool5.getFilteredMediaTypes(
				requestContext.getAcceptableMediaTypes(), resourceMediaTypes);
		// unsupported media type will be thrown later
		// so no need to continue
		if(filteredMediaTypes.isEmpty()) return;
		
		// try to get a cached response for any matched acceptable media type
		for(MediaType mediaType : filteredMediaTypes) {
			String key = KeyTool5.generateKey(requestContext, mediaType);
			TempResponseWrapper<HttpResponse> entry = null; //cache.get(key);
			if(entry != null) {
				sendCachedResponse(requestContext, entry);
				return;
			}
		}
		
		// if any cached response is not found
		// use synchronized temp key to build response
		String tempKey = KeyTool5.generateTempKey(requestContext, filteredMediaTypes.get(0));
		
		TempResponseWrapper<HttpResponse> entry = null;
		//entry = cache.computeIfAbsent(tempKey, new TempResponseWrapper<>(), Long.MAX_VALUE);
		
		CacheEntryContext entryContext = new CacheEntryContext();
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
		sendCachedResponse(requestContext, entry);
	}
	
	public void sendCachedResponse(ContainerRequestContext requestContext, 
			TempResponseWrapper<HttpResponse> entry) {
		
		CacheControl newCacheControl = new CacheControl();
		//if(!entry.isExpired()) newCacheControl.setMaxAge(entry.getExpirationInSeconds()); 
		/*
		requestContext.abortWith(Response.fromResponse(entry.getData().asResponse())
				.cacheControl(newCacheControl)
				.build());*/
	}

}
