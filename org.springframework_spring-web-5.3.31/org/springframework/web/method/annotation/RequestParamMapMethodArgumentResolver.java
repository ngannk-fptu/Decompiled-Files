/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.Part
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ResolvableType
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.method.annotation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;

public class RequestParamMapMethodArgumentResolver
implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        RequestParam requestParam = (RequestParam)parameter.getParameterAnnotation(RequestParam.class);
        return requestParam != null && Map.class.isAssignableFrom(parameter.getParameterType()) && !StringUtils.hasText((String)requestParam.name());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        ResolvableType resolvableType = ResolvableType.forMethodParameter((MethodParameter)parameter);
        if (MultiValueMap.class.isAssignableFrom(parameter.getParameterType())) {
            Class valueType = resolvableType.as(MultiValueMap.class).getGeneric(new int[]{1}).resolve();
            if (valueType == MultipartFile.class) {
                MultipartRequest multipartRequest = MultipartResolutionDelegate.resolveMultipartRequest(webRequest);
                return multipartRequest != null ? multipartRequest.getMultiFileMap() : new LinkedMultiValueMap(0);
            }
            if (valueType == Part.class) {
                HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
                if (servletRequest != null && MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
                    Collection parts = servletRequest.getParts();
                    LinkedMultiValueMap result = new LinkedMultiValueMap(parts.size());
                    for (Part part : parts) {
                        result.add((Object)part.getName(), (Object)part);
                    }
                    return result;
                }
                return new LinkedMultiValueMap(0);
            }
            Map<String, String[]> parameterMap = webRequest.getParameterMap();
            LinkedMultiValueMap result = new LinkedMultiValueMap(parameterMap.size());
            parameterMap.forEach((arg_0, arg_1) -> RequestParamMapMethodArgumentResolver.lambda$resolveArgument$0((MultiValueMap)result, arg_0, arg_1));
            return result;
        }
        Class valueType = resolvableType.asMap().getGeneric(new int[]{1}).resolve();
        if (valueType == MultipartFile.class) {
            MultipartRequest multipartRequest = MultipartResolutionDelegate.resolveMultipartRequest(webRequest);
            return multipartRequest != null ? multipartRequest.getFileMap() : new LinkedHashMap(0);
        }
        if (valueType == Part.class) {
            HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
            if (servletRequest != null && MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
                Collection parts = servletRequest.getParts();
                LinkedHashMap result = CollectionUtils.newLinkedHashMap((int)parts.size());
                for (Part part : parts) {
                    if (result.containsKey(part.getName())) continue;
                    result.put(part.getName(), part);
                }
                return result;
            }
            return new LinkedHashMap(0);
        }
        Map<String, String[]> parameterMap = webRequest.getParameterMap();
        LinkedHashMap result = CollectionUtils.newLinkedHashMap((int)parameterMap.size());
        parameterMap.forEach((key, values) -> {
            if (((String[])values).length > 0) {
                result.put(key, values[0]);
            }
        });
        return result;
    }

    private static /* synthetic */ void lambda$resolveArgument$0(MultiValueMap result, String key, String[] values) {
        for (String value : values) {
            result.add((Object)key, (Object)value);
        }
    }
}

