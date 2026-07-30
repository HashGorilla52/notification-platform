package com.notification.userservice.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * By default, add "/{id}" to endpoint path and set response method (default GET) and status (default 200).
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@RequestMapping
@ResponseStatus
public @interface ById {
    @AliasFor(annotation = RequestMapping.class, attribute = "method")
    RequestMethod method() default RequestMethod.GET;

    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String path() default "/{id}";

    @AliasFor(annotation = ResponseStatus.class, attribute = "value")
    HttpStatus status() default HttpStatus.OK;

}
