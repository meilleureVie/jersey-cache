package com.augustin.cache.logic;

/**
 * a wrapper for our http response to synchronize its concurrent access
 * @author amedoatinsa
 *
 * @param <T> referent
 */
public class TempResponseWrapper<T> implements ResponseWrapper<T> {
	
	private static final int STATE_NEW = 0;
	private static final int STATE_PENDING = 1; 
	private static final int STATE_READY = 2;
	
	private T response;
	private int state;
	
	public TempResponseWrapper() {
		
	}
	
	@Override
	public T get() {
		return this.response;
	}
	
	public void setResponse(T response) {
		this.response = response;
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
	
	public void setPending() {
		this.state = STATE_PENDING;
	}
	
	public void setReady() {
		this.state = STATE_READY;
	}
}