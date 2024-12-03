/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.springframework.core.MethodParameter
 *  org.springframework.util.StringUtils
 *  org.springframework.web.bind.support.WebDataBinderFactory
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.support.ModelAndViewContainer
 */
package org.springframework.data.web;

import java.util.Arrays;
import javax.annotation.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolverSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class SortHandlerMethodArgumentResolver
extends SortHandlerMethodArgumentResolverSupport
implements SortArgumentResolver {
    public boolean supportsParameter(MethodParameter parameter) {
        return Sort.class.equals((Object)parameter.getParameterType());
    }

    @Override
    public Sort resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        String[] directionParameter = webRequest.getParameterValues(this.getSortParameter(parameter));
        if (directionParameter == null) {
            return this.getDefaultFromAnnotationOrFallback(parameter);
        }
        if (directionParameter.length == 1 && !StringUtils.hasText((String)directionParameter[0])) {
            return this.getDefaultFromAnnotationOrFallback(parameter);
        }
        return this.parseParameterIntoSort(Arrays.asList(directionParameter), this.getPropertyDelimiter());
    }
}

