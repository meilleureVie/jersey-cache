package com.augustin.cache.logic;

import javax.ws.rs.core.CacheControl;

public class CacheEntryContext {
	public static final String NAME = CacheEntryContext.class.getName();
	
	public TempResponseWrapper<HttpResponse> placeHolder;
	public String key;
	public CacheControl cacheControl;
	public boolean responseExists;
}