/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.web.bind.annotation.PathVariable
 *  org.springframework.web.bind.support.WebDataBinderFactory
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.support.HandlerMethodArgumentResolver
 *  org.springframework.web.method.support.ModelAndViewContainer
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

public class PathVariableMapMethodArgumentResolver
implements HandlerMethodArgumentResolver {
    public boolean supportsParameter(MethodParameter parameter) {
        PathVariable ann = (PathVariable)parameter.getParameterAnnotation(PathVariable.class);
        return ann != null && Map.class.isAssignableFrom(parameter.getParameterType()) && !StringUtils.hasText((String)ann.value());
    }

    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Map uriTemplateVars = (Map)webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, 0);
        if (!CollectionUtils.isEmpty((Map)uriTemplateVars)) {
            return new LinkedHashMap(uriTemplateVars);
        }
        return Collections.emptyMap();
    }
}

