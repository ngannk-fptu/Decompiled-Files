/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.method.annotation;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class RequestParamMapMethodArgumentResolver
implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
        return requestParam != null && Map.class.isAssignableFrom(parameter.getParameterType()) && !StringUtils.hasText(requestParam.name());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Map<String, String[]> parameterMap = webRequest.getParameterMap();
        if (MultiValueMap.class.isAssignableFrom(parameter.getParameterType())) {
            LinkedMultiValueMap result = new LinkedMultiValueMap(parameterMap.size());
            parameterMap.forEach((key, values) -> {
                for (String value : values) {
                    result.add(key, value);
                }
            });
            return result;
        }
        LinkedHashMap result = new LinkedHashMap(parameterMap.size());
        parameterMap.forEach((key, values) -> {
            if (((String[])values).length > 0) {
                result.put(key, values[0]);
            }
        });
        return result;
    }
}

