package com.notification.userservice.annotation.resolver;

import com.notification.userservice.annotation.CurrentUser;
import com.notification.userservice.entity.User;
import com.notification.userservice.repository.auth.UserRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Resolves method parameters annotated with {@link CurrentUser}.
 * <p>
 * Supports two parameter types:
 * <ul>
 *   <li>{@link User} — returns the authenticated user's entity from the security context</li>
 *   <li>{@link String} — returns the authenticated user's email</li>
 * </ul>
 *
 * @see CurrentUser
 * @see HandlerMethodArgumentResolver
 */
@Component
@AllArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && (parameter.getParameterType().equals(User.class) || parameter.getParameterType().equals(String.class));
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter,
                                            @Nullable ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest,
                                            @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (parameter.getParameterType().equals(String.class)) {
            return auth.getName();
        }
        return (User) auth.getPrincipal();
    }
}
