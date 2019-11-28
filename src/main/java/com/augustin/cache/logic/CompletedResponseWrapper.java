package com.augustin.cache.logic;

/**
 * 
 * @author amedoatinsa
 *
 * @param <T> referent
 */
public class CompletedResponseWrapper<T> implements ResponseWrapper<T> {
	
	private T response;
	private long timeToLive;
	private long lastModified;
	
	/**
	 * 
	 * @param response referent
	 * @param timeToLiveInMillis in millisec
	 */
	public CompletedResponseWrapper(T response, long timeToLiveInMillis) {
		this.response = response;
		this.timeToLive = timeToLiveInMillis;
		this.lastModified = System.currentTimeMillis();
	}
	
	@Override
	public T get() {
		return this.response;
	}
	
	public boolean isExpired() {
		int expiration = getExpirationInSeconds();
		if(expiration <= 0) return true;
		return false;
	}
	
	public int getExpirationInSeconds() {
        int expirationInSeconds = (int)((lastModified + timeToLive - System.currentTimeMillis()) / 1000L);
        return expirationInSeconds;
    }
}
