package com.notification.userservice.annotation;

import com.notification.userservice.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
public @interface CurrentUser {
}
