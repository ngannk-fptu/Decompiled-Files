/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter
 *  org.springframework.http.converter.json.MappingJacksonValue
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

public abstract class AbstractMappingJacksonResponseBodyAdvice
implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return AbstractJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    @Nullable
    public final Object beforeBodyWrite(@Nullable Object body2, MethodParameter returnType, MediaType contentType, Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body2 == null) {
            return null;
        }
        MappingJacksonValue container = this.getOrCreateContainer(body2);
        this.beforeBodyWriteInternal(container, contentType, returnType, request, response);
        return container;
    }

    protected MappingJacksonValue getOrCreateContainer(Object body2) {
        return body2 instanceof MappingJacksonValue ? (MappingJacksonValue)body2 : new MappingJacksonValue(body2);
    }

    protected abstract void beforeBodyWriteInternal(MappingJacksonValue var1, MediaType var2, MethodParameter var3, ServerHttpRequest var4, ServerHttpResponse var5);
}

