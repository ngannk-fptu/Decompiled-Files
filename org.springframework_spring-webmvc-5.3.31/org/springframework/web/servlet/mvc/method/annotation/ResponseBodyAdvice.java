/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;

public interface ResponseBodyAdvice<T> {
    public boolean supports(MethodParameter var1, Class<? extends HttpMessageConverter<?>> var2);

    @Nullable
    public T beforeBodyWrite(@Nullable T var1, MethodParameter var2, MediaType var3, Class<? extends HttpMessageConverter<?>> var4, ServerHttpRequest var5, ServerHttpResponse var6);
}

