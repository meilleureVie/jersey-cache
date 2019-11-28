package com.augustin.cache.logic;

import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Map.Entry;

public class HttpResponse {

	private final int statusCode;
	private MultivaluedMap<String, String> headers;
	private final int length;
	private final Object entity;

	public HttpResponse(final int statusCode, final MultivaluedMap<String, String> headers, final int length,
			final Object entity) {
		this.statusCode = statusCode;
		this.headers = headers;
		this.length = length;
		this.entity = entity;
		
		this.headers = new MultivaluedHashMap<>(headers);
		this.headers.remove(HttpHeaders.CONTENT_TYPE);
	}
	
	public Response asResponse() {
		final Response.ResponseBuilder responseBuilder = Response.status(getStatusCode()).entity(getEntity());
		for (Entry<String, List<String>> e : getHeaders().entrySet()) {
			for (String v : e.getValue()) {
				responseBuilder.header(e.getKey(), v);
			}
		}
		return responseBuilder.build();
	}
	
	public static HttpResponse from(final ContainerResponseContext responseContext) {
		return new HttpResponse(
				responseContext.getStatus(), 
				responseContext.getStringHeaders(),
				responseContext.getLength(), 
				responseContext.getEntity()
				);
	}

	public int getStatusCode() {
		return statusCode;
	}

	public MultivaluedMap<String, String> getHeaders() {
		return headers;
	}

	public int getLength() {
		return length;
	}

	public Object getEntity() {
		return entity;
	}
}
