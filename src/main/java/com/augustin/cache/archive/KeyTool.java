package com.augustin.cache.archive;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;

public class KeyTool {
	private static final Logger logger = Logger.getLogger(KeyTool.class.getName());
	private List<String> headersToIncludeInKey;
	
	public KeyTool() {
		
	}
	
	public KeyTool(List<String> headersToIncludeInKey) {
		this.headersToIncludeInKey = headersToIncludeInKey;
	}
	
	public String generate(ContainerRequestContext request) {
		return generate(request, this.headersToIncludeInKey);
	}
	
	public String generate(ContainerRequestContext request, List<String> headersToIncludeInKey) {
		String res = "";
	      for (final String h : headersToIncludeInKey) {
	        res = res + h + ":" + request.getHeaderString(h);
	      }
	      final String key = request.getMethod() + ":" + request.getUriInfo().getPath() + ":" + res;
	      logger.info("===Key produced : " + key);
	      return key;
	}
}
