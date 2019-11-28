package com.augustin.cache.logic;

import java.net.URI;

import javax.ws.rs.container.ContainerRequestContext;

public class KeyTool {
	
	public static String generate(ContainerRequestContext request) {
		URI uri = request.getUriInfo().getRequestUri();
		String path = uri.getPath();
		return  uri.getQuery() != null ? path += "?" + uri.getQuery() : path;
	}
}
