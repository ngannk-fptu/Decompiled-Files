/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.web.method.annotation;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class RequestHeaderMapMethodArgumentResolver
implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestHeader.class) && Map.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Class paramType = parameter.getParameterType();
        if (MultiValueMap.class.isAssignableFrom(paramType)) {
            HttpHeaders result = HttpHeaders.class.isAssignableFrom(paramType) ? new HttpHeaders() : new LinkedMultiValueMap();
            Iterator<String> iterator = webRequest.getHeaderNames();
            while (iterator.hasNext()) {
                String headerName = iterator.next();
                String[] headerValues = webRequest.getHeaderValues(headerName);
                if (headerValues == null) continue;
                for (String headerValue : headerValues) {
                    result.add(headerName, headerValue);
                }
            }
            return result;
        }
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        Iterator<String> iterator = webRequest.getHeaderNames();
        while (iterator.hasNext()) {
            String headerName = iterator.next();
            String headerValue = webRequest.getHeader(headerName);
            if (headerValue == null) continue;
            result.put(headerName, headerValue);
        }
        return result;
    }
}

