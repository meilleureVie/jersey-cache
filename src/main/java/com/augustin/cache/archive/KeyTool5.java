package com.augustin.cache.archive;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyTool5 {
	private static final Logger logger = LoggerFactory.getLogger(KeyTool5.class);
	
	/**
	 * 
	 * @param request req
	 * @param selectedMediaType se
	 * @return ex : _temp:GET:/aphedd-tontine/api/test/uri-info:Accept:application/json
	 */
	public static String generateTempKey(ContainerRequestContext request, MediaType selectedMediaType) {
		String key = generateKey(request, selectedMediaType);
		return "_temp:" + key;
	}
	
	/**
	 * 
	 * @param request req
	 * @param selectedMediaType mediaType
	 * @return string
	 * ex1 : GET:/aphedd-tontine/api/test/uri-info?query1=ok&amp;query2=no&amp;query3=yesno:Accept:application/json
	 * ex2 : GET:/aphedd-tontine/api/test/uri-info:Accept:application/json
	 */
	//
	public static String generateKey(ContainerRequestContext request, MediaType selectedMediaType) {
		String method = request.getMethod();
		URI uri = request.getUriInfo().getRequestUri();
		String path = uri.getPath();
		path = uri.getQuery() != null ? path += "?" + uri.getQuery() : path;
		
		String pattern = "%s:%s:Accept:%s";
		return String.format(pattern, method, path, selectedMediaType);
	}
	
	public static List<MediaType> getFilteredMediaTypes(List<MediaType> requestMediaTypes, 
			List<MediaType> resourceMediaTypes) {
		
		// in case request acceptable media types is absent
		// use wildcard
		if(requestMediaTypes == null || requestMediaTypes.isEmpty()) {
			logger.warn("why request media type is absent ?");
			return Arrays.asList(MediaType.WILDCARD_TYPE);
		}
		
		// in case resource method does not have any produces annotation
		// use request acceptable media types
		if(resourceMediaTypes == null || resourceMediaTypes.isEmpty()) {
			logger.warn("why resource method does not produces any media type ?");
			return requestMediaTypes;
		}
		
		// filter resourceMediaTypes by requestMediaTypes
		// filtered media types may be empty
		List<MediaType> filteredMediaTypes = new ArrayList<>();
		for(MediaType requestMediaType : requestMediaTypes) {
			for(MediaType resourceMediaType : resourceMediaTypes) {
				if(requestMediaType.isCompatible(resourceMediaType))
					filteredMediaTypes.add(requestMediaType);
			}
		}
		return filteredMediaTypes;
	}
	
	public static String getMediaTypeWithoutEncoding(MediaType mediaType) {
		return (mediaType.getType() + "/" + mediaType.getSubtype()).trim().toLowerCase();
	}
}