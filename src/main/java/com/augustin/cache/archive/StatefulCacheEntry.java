package com.augustin.cache.archive;

/**
 * a wrapper for our http response to synchronize its concurrent access
 * @author amedoatinsa
 *
 * @param <T> referent
 */
public class StatefulCacheEntry<T> {
	
	private static final int STATE_NEW = 0;
	private static final int STATE_PENDING = 1; 
	private static final int STATE_READY = 2;
	
	private int state;
	private T data;
	
	private long timeToLive;
	private long lastModified;
	
	public StatefulCacheEntry() {
		
	}
		
	public T getData() {
		return this.data;
	}
	
	public StatefulCacheEntry<T> setData(final T data) {
		this.data = data;
		return this;
	}
	
	public StatefulCacheEntry<T> setData(final T data, long timeToLive) {
		setData(data);
		setTimeToLive(timeToLive);
		return this;
	}
	
	public StatefulCacheEntry<T> setPending() {
		state = STATE_PENDING;
		return this;
	}

	public StatefulCacheEntry<T> setReady() {
		state = STATE_READY;
		return this;
	}
		
	public boolean isNew() {
		return state == STATE_NEW;
	}

	public boolean isPending() {
		return state == STATE_PENDING;
	}
	
	public boolean isReady() {
		return state == STATE_READY;
	}
	
	public long getTimeToLive() {
		return this.timeToLive;
	}
	
	public void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
		this.lastModified = System.currentTimeMillis();
	}
	
	public long getLastModified() {
		return this.lastModified;
	}
	
	public int getExpirationInSeconds() {
        int expirationInSeconds = (int)((lastModified + timeToLive - System.currentTimeMillis()) / 1000L);
        return expirationInSeconds;
    }
	
	public boolean isExpired() {
		int expiration = getExpirationInSeconds();
		if(expiration <= 0) return true;
		return false;
	}
	
	public void unblock() {
		synchronized (this) {
			notifyAll();
		}
	}

}
