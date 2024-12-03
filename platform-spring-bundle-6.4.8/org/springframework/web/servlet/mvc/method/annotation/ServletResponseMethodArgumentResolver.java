/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletResponse
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.servlet.ServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class ServletResponseMethodArgumentResolver
implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        return ServletResponse.class.isAssignableFrom(paramType) || OutputStream.class.isAssignableFrom(paramType) || Writer.class.isAssignableFrom(paramType);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Class<?> paramType;
        if (mavContainer != null) {
            mavContainer.setRequestHandled(true);
        }
        if (ServletResponse.class.isAssignableFrom(paramType = parameter.getParameterType())) {
            return this.resolveNativeResponse(webRequest, paramType);
        }
        return this.resolveArgument(paramType, this.resolveNativeResponse(webRequest, ServletResponse.class));
    }

    private <T> T resolveNativeResponse(NativeWebRequest webRequest, Class<T> requiredType) {
        T nativeResponse = webRequest.getNativeResponse(requiredType);
        if (nativeResponse == null) {
            throw new IllegalStateException("Current response is not of type [" + requiredType.getName() + "]: " + webRequest);
        }
        return nativeResponse;
    }

    private Object resolveArgument(Class<?> paramType, ServletResponse response) throws IOException {
        if (OutputStream.class.isAssignableFrom(paramType)) {
            return response.getOutputStream();
        }
        if (Writer.class.isAssignableFrom(paramType)) {
            return response.getWriter();
        }
        throw new UnsupportedOperationException("Unknown parameter type: " + paramType);
    }
}

