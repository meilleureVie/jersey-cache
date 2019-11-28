package com.augustin.cache.archive;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyTool4 {
	private static final Logger logger = LoggerFactory.getLogger(KeyTool4.class);
	
	private static final String KEY_WITH_ACCEPTED_MEDIA_TYPES_PATTERN = "%s:%s:Accept:%s";
	private static final String KEY_WITHOUT_ACCEPTED_MEDIA_TYPES_PATTERN = "%s:%s";
	
	private List<MediaType> availableMediaTypes;
	
	public KeyTool4(List<MediaType> availableMediaTypes) {
		this.availableMediaTypes = availableMediaTypes;
	}
	
	public String getSelectedMediaType(String key) {
		String[] mediaTypes = key.split(":Accept:");
		if(mediaTypes.length == 2) return mediaTypes[1];
		return null;
	}
	
	public String generate(ContainerRequestContext request) {
		String key = generate(request.getMethod(), request.getUriInfo().getPath(), 
				request.getAcceptableMediaTypes());
		logger.info("===Key produced : {}" + key);
		return key;
	}
	
	public boolean verify(String key, ContainerRequestContext request, ContainerResponseContext response) {
		String newKey = generate(request.getMethod(), request.getUriInfo().getPath(), 
				Arrays.asList(response.getMediaType()));
		logger.info("===new Key verified : {}" + newKey);
		return key.equals(newKey);
	}
	
	private String generate(String httpMethod, String baseUrl, List<MediaType> acceptableMediaTypes) {
		if(availableMediaTypes == null || availableMediaTypes.isEmpty()) {
			return String.format(KEY_WITHOUT_ACCEPTED_MEDIA_TYPES_PATTERN, httpMethod, baseUrl);
		}
		
		if(acceptableMediaTypes == null || acceptableMediaTypes.isEmpty()) {
			return String.format(KEY_WITH_ACCEPTED_MEDIA_TYPES_PATTERN, httpMethod, baseUrl, 
					getMediaTypeWithoutEncoding(availableMediaTypes.get(0)));
		}
		
		for(MediaType mediaType : acceptableMediaTypes) {
			if(mediaType.isCompatible(MediaType.WILDCARD_TYPE)) {
				return String.format(KEY_WITH_ACCEPTED_MEDIA_TYPES_PATTERN, httpMethod, baseUrl, 
						getMediaTypeWithoutEncoding(availableMediaTypes.get(0)));
			}
			
			for(int i = 0; i < availableMediaTypes.size(); i++) {
				MediaType availableMediaType = availableMediaTypes.get(0);
				if(mediaType.isCompatible(availableMediaType)) {
					return String.format(KEY_WITH_ACCEPTED_MEDIA_TYPES_PATTERN, httpMethod, baseUrl, 
							getMediaTypeWithoutEncoding(mediaType));
				}
			}
		};
		
		return null;
	}
	
	public static String getMediaTypeWithoutEncoding(MediaType mediaType) {
		return (mediaType.getType() + "/" + mediaType.getSubtype()).trim().toLowerCase();
	}
}