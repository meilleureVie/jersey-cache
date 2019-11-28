Jersey http memory cache
========================

Caching is not support in jersey native.
So i develop a feature which can help to do that.

mecanism
--------
 * This feature will cache http response in memory ehcache for a given time.
 * Also, the header 'Cache-Control' will be added to each response to send to client.
 * We can only cache resource called by GET method; other http methods will be ignored
 * Soft or strong references are used to store data
 * Our cache is also sensible to Header 'Accept'; it can use the same cached response to produces formatted response in content type specified in header 'Accept'
 * Our cache is also sensible to query parameters; same base url with different query parameters means two different requests

How to use
----------

In main application: Register cache

```java
@ApplicationPath("/")
public class Application extends ResourceConfig {

    public Application() {
		...
	
		// Register cache feature as soft reference
		// in this case soft references as used
    	register(CacheDynamicFeature.class);
		
		//---or---
		// here strong reference are used and cache size is 10kb
		register(new CacheDynamicFeature(10));
		
		...
	}
}
```

In resource : Instruct caching filter to cache operations

we can do this to cache a resource method ouput

```java
	@GET
  	@Produces(MediaType.APPLICATION_JSON)
  	@Path("hello")
  	@Cache(maxAge = 60 * 5) // caching for 5 minutes
  	public Object getCached() {
    	return dao.get();
  	}
```

we can also do this to cache all resources method output in a resource class

```java
@Path("cache")
@Cache(maxAge = 60) // caching for 1 minute
public class CacheResource {

	@GET
  	@Produces(MediaType.APPLICATION_JSON)
  	@Path("hello")
  	public Object getCached() {
    	return dao.get();
  	}
}
```

dependencies
------------

```xml
<dependency>
	<groupId>com.github.meilleureVie</groupId>
	<artifactId>jersey-cache</artifactId>
	<version>1.0</version>
</dependency>

<dependency>
	<groupId>org.ehcache</groupId>
	<artifactId>ehcache</artifactId>
	<version>3.8.0</version>
</dependency>
```