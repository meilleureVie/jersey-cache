package com.augustin.cache.archive;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;

public class KeyTool2 {
	private static final Logger logger = Logger.getLogger(KeyTool2.class.getName());

	public static String generate(ContainerRequestContext request) {
		String accept;
		
		List<MediaType> acceptableMediaTypes = request.getAcceptableMediaTypes();
        if (acceptableMediaTypes != null && !acceptableMediaTypes.isEmpty()) {
        	accept = acceptableMediaTypes.get(0).toString();
        } else {
        	accept = MediaType.WILDCARD;
        }
		
		final String key = request.getMethod() + ":" + request.getUriInfo().getPath() + ":Accept:" + accept;
		logger.info("===Key produced : " + key);
		return key;
	}
}