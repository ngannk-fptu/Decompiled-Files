/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

class RequestResponseBodyAdviceChain
implements RequestBodyAdvice,
ResponseBodyAdvice<Object> {
    private final List<Object> requestBodyAdvice = new ArrayList<Object>(4);
    private final List<Object> responseBodyAdvice = new ArrayList<Object>(4);

    public RequestResponseBodyAdviceChain(@Nullable List<Object> requestResponseBodyAdvice) {
        this.requestBodyAdvice.addAll(RequestResponseBodyAdviceChain.getAdviceByType(requestResponseBodyAdvice, RequestBodyAdvice.class));
        this.responseBodyAdvice.addAll(RequestResponseBodyAdviceChain.getAdviceByType(requestResponseBodyAdvice, ResponseBodyAdvice.class));
    }

    static <T> List<T> getAdviceByType(@Nullable List<Object> requestResponseBodyAdvice, Class<T> adviceType) {
        if (requestResponseBodyAdvice != null) {
            ArrayList<Object> result = new ArrayList<Object>();
            for (Object advice : requestResponseBodyAdvice) {
                Class<?> beanType = advice instanceof ControllerAdviceBean ? ((ControllerAdviceBean)advice).getBeanType() : advice.getClass();
                if (beanType == null || !adviceType.isAssignableFrom(beanType)) continue;
                result.add(advice);
            }
            return result;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean supports(MethodParameter param, Type type, Class<? extends HttpMessageConverter<?>> converterType) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage request, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        for (RequestBodyAdvice advice : this.getMatchingAdvice(parameter, RequestBodyAdvice.class)) {
            if (!advice.supports(parameter, targetType, converterType)) continue;
            request = advice.beforeBodyRead(request, parameter, targetType, converterType);
        }
        return request;
    }

    @Override
    public Object afterBodyRead(Object body2, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        for (RequestBodyAdvice advice : this.getMatchingAdvice(parameter, RequestBodyAdvice.class)) {
            if (!advice.supports(parameter, targetType, converterType)) continue;
            body2 = advice.afterBodyRead(body2, inputMessage, parameter, targetType, converterType);
        }
        return body2;
    }

    @Override
    @Nullable
    public Object beforeBodyWrite(@Nullable Object body2, MethodParameter returnType, MediaType contentType, Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest request, ServerHttpResponse response) {
        return this.processBody(body2, returnType, contentType, converterType, request, response);
    }

    @Override
    @Nullable
    public Object handleEmptyBody(@Nullable Object body2, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        for (RequestBodyAdvice advice : this.getMatchingAdvice(parameter, RequestBodyAdvice.class)) {
            if (!advice.supports(parameter, targetType, converterType)) continue;
            body2 = advice.handleEmptyBody(body2, inputMessage, parameter, targetType, converterType);
        }
        return body2;
    }

    @Nullable
    private <T> Object processBody(@Nullable Object body2, MethodParameter returnType, MediaType contentType, Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest request, ServerHttpResponse response) {
        for (ResponseBodyAdvice advice : this.getMatchingAdvice(returnType, ResponseBodyAdvice.class)) {
            if (!advice.supports(returnType, converterType)) continue;
            body2 = advice.beforeBodyWrite(body2, returnType, contentType, converterType, request, response);
        }
        return body2;
    }

    private <A> List<A> getMatchingAdvice(MethodParameter parameter, Class<? extends A> adviceType) {
        List<Object> availableAdvice = this.getAdvice(adviceType);
        if (CollectionUtils.isEmpty(availableAdvice)) {
            return Collections.emptyList();
        }
        ArrayList<Object> result = new ArrayList<Object>(availableAdvice.size());
        for (Object advice : availableAdvice) {
            if (advice instanceof ControllerAdviceBean) {
                ControllerAdviceBean adviceBean = (ControllerAdviceBean)advice;
                if (!adviceBean.isApplicableToBeanType(parameter.getContainingClass())) continue;
                advice = adviceBean.resolveBean();
            }
            if (!adviceType.isAssignableFrom(advice.getClass())) continue;
            result.add(advice);
        }
        return result;
    }

    private List<Object> getAdvice(Class<?> adviceType) {
        if (RequestBodyAdvice.class == adviceType) {
            return this.requestBodyAdvice;
        }
        if (ResponseBodyAdvice.class == adviceType) {
            return this.responseBodyAdvice;
        }
        throw new IllegalArgumentException("Unexpected adviceType: " + adviceType);
    }
}

