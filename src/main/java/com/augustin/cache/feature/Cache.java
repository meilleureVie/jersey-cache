package com.augustin.cache.feature;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {
	
	/**
	 * time to live (TTL) in second
	 * @return time to live in second
	 */
	int maxAge() default -1;
	boolean mustRevalidate() default false;
}