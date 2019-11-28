package com.augustin.cache.archive;

import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyTool3 {
	private static final Logger logger = LoggerFactory.getLogger(KeyTool3.class);
	
	private static final String KEY_WITH_ACCEPTED_MEDIA_TYPES_PATTERN = "%s:%s:Accept:%s";
	private static final String KEY_WITHOUT_ACCEPTED_MEDIA_TYPES_PATTERN = "%s:%s";

	
	public static String generate(ContainerRequestContext request, 
			List<MediaType> serverAcceptableMediaTypes) {
		String key = generateInternal(request, serverAcceptableMediaTypes);
		logger.info("===Key produced : {}" + key);
		return key;
	}
	
	private static String generateInternal(ContainerRequestContext request, 
			List<MediaType> serverAcceptableMediaTypes) {
		if(serverAcceptableMediaTypes == null || serverAcceptableMediaTypes.isEmpty()) {
			return String.format(KEY_WITHOUT_ACCEPTED_MEDIA_TYPES_PATTERN, 
					request.getMethod(), request.getUriInfo().getPath());
		}
		
		List<MediaType> clientAcceptableMediaTypes = request.getAcceptableMediaTypes();
		if(clientAcceptableMediaTypes == null || clientAcceptableMediaTypes.isEmpty()) {
			return String.format(KEY_WITH_ACCEPTED_MEDIA_TYPES_PATTERN, 
					request.getMethod(), request.getUriInfo().getPath(), 
					serverAcceptableMediaTypes.get(0).toString());
		}
		
		for(MediaType clientMediaType : clientAcceptableMediaTypes) {
			if(clientMediaType.isCompatible(MediaType.WILDCARD_TYPE)) {
				return String.format(KEY_WITH_ACCEPTED_MEDIA_TYPES_PATTERN, 
						request.getMethod(), request.getUriInfo().getPath(), 
						serverAcceptableMediaTypes.get(0).toString());
			}
		
			for(int i = 0; i < serverAcceptableMediaTypes.size(); i++) {
				MediaType serverMediaType = serverAcceptableMediaTypes.get(0);
				if(clientMediaType.isCompatible(serverMediaType)) {
					return String.format(KEY_WITH_ACCEPTED_MEDIA_TYPES_PATTERN, 
							request.getMethod(), request.getUriInfo().getPath(), 
							clientMediaType.toString());
				}
			}
		};
		
		return null;
	}
}