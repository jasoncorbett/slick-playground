package com.slickqa.slickqaweb.routes;


import io.vertx.core.http.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A method that is a http request handler with a particular path.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Handler {
    HttpMethod method() default HttpMethod.GET;
    String path() default "";
    boolean usesBody() default false;
}
