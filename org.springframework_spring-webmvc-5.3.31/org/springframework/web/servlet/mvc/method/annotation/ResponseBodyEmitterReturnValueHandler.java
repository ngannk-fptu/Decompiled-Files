/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ReactiveAdapterRegistry
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.task.TaskExecutor
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpOutputMessage
 *  org.springframework.http.MediaType
 *  org.springframework.http.ResponseEntity
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.converter.StringHttpMessageConverter
 *  org.springframework.http.server.DelegatingServerHttpResponse
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.http.server.ServletServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.accept.ContentNegotiationManager
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.context.request.WebRequest
 *  org.springframework.web.context.request.async.DeferredResult
 *  org.springframework.web.context.request.async.WebAsyncUtils
 *  org.springframework.web.filter.ShallowEtagHeaderFilter
 *  org.springframework.web.method.support.HandlerMethodReturnValueHandler
 *  org.springframework.web.method.support.ModelAndViewContainer
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.DelegatingServerHttpResponse;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.ReactiveTypeHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public class ResponseBodyEmitterReturnValueHandler
implements HandlerMethodReturnValueHandler {
    private final List<HttpMessageConverter<?>> sseMessageConverters;
    private final ReactiveTypeHandler reactiveHandler;

    public ResponseBodyEmitterReturnValueHandler(List<HttpMessageConverter<?>> messageConverters) {
        Assert.notEmpty(messageConverters, (String)"HttpMessageConverter List must not be empty");
        this.sseMessageConverters = ResponseBodyEmitterReturnValueHandler.initSseConverters(messageConverters);
        this.reactiveHandler = new ReactiveTypeHandler();
    }

    public ResponseBodyEmitterReturnValueHandler(List<HttpMessageConverter<?>> messageConverters, ReactiveAdapterRegistry registry, TaskExecutor executor, ContentNegotiationManager manager) {
        Assert.notEmpty(messageConverters, (String)"HttpMessageConverter List must not be empty");
        this.sseMessageConverters = ResponseBodyEmitterReturnValueHandler.initSseConverters(messageConverters);
        this.reactiveHandler = new ReactiveTypeHandler(registry, executor, manager);
    }

    private static List<HttpMessageConverter<?>> initSseConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (!converter.canWrite(String.class, MediaType.TEXT_PLAIN)) continue;
            return converters;
        }
        ArrayList result = new ArrayList(converters.size() + 1);
        result.add((HttpMessageConverter<?>)new StringHttpMessageConverter(StandardCharsets.UTF_8));
        result.addAll(converters);
        return result;
    }

    public boolean supportsReturnType(MethodParameter returnType) {
        Class bodyType = ResponseEntity.class.isAssignableFrom(returnType.getParameterType()) ? ResolvableType.forMethodParameter((MethodParameter)returnType).getGeneric(new int[0]).resolve() : returnType.getParameterType();
        return bodyType != null && (ResponseBodyEmitter.class.isAssignableFrom(bodyType) || this.reactiveHandler.isReactiveType(bodyType));
    }

    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        HttpMessageConvertingHandler handler;
        ResponseBodyEmitter emitter;
        ServletRequest request;
        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }
        HttpServletResponse response = (HttpServletResponse)webRequest.getNativeResponse(HttpServletResponse.class);
        Assert.state((response != null ? 1 : 0) != 0, (String)"No HttpServletResponse");
        Object outputMessage = new ServletServerHttpResponse(response);
        if (returnValue instanceof ResponseEntity) {
            ResponseEntity responseEntity = (ResponseEntity)returnValue;
            response.setStatus(responseEntity.getStatusCodeValue());
            outputMessage.getHeaders().putAll((Map)responseEntity.getHeaders());
            returnValue = responseEntity.getBody();
            returnType = returnType.nested();
            if (returnValue == null) {
                mavContainer.setRequestHandled(true);
                outputMessage.flush();
                return;
            }
        }
        Assert.state(((request = (ServletRequest)webRequest.getNativeRequest(ServletRequest.class)) != null ? 1 : 0) != 0, (String)"No ServletRequest");
        if (returnValue instanceof ResponseBodyEmitter) {
            emitter = (ResponseBodyEmitter)returnValue;
        } else {
            emitter = this.reactiveHandler.handleValue(returnValue, returnType, mavContainer, webRequest);
            if (emitter == null) {
                outputMessage.getHeaders().forEach((headerName, headerValues) -> {
                    for (String headerValue : headerValues) {
                        response.addHeader(headerName, headerValue);
                    }
                });
                return;
            }
        }
        emitter.extendResponse((ServerHttpResponse)outputMessage);
        ShallowEtagHeaderFilter.disableContentCaching((ServletRequest)request);
        outputMessage = new StreamingServletServerHttpResponse((ServerHttpResponse)outputMessage);
        try {
            DeferredResult deferredResult = new DeferredResult(emitter.getTimeout());
            WebAsyncUtils.getAsyncManager((WebRequest)webRequest).startDeferredResultProcessing(deferredResult, new Object[]{mavContainer});
            handler = new HttpMessageConvertingHandler((ServerHttpResponse)outputMessage, deferredResult);
        }
        catch (Throwable ex) {
            emitter.initializeWithError(ex);
            throw ex;
        }
        emitter.initialize(handler);
    }

    private static class StreamingServletServerHttpResponse
    extends DelegatingServerHttpResponse {
        private final HttpHeaders mutableHeaders = new HttpHeaders();

        public StreamingServletServerHttpResponse(ServerHttpResponse delegate) {
            super(delegate);
            this.mutableHeaders.putAll((Map)delegate.getHeaders());
        }

        public HttpHeaders getHeaders() {
            return this.mutableHeaders;
        }
    }

    private class HttpMessageConvertingHandler
    implements ResponseBodyEmitter.Handler {
        private final ServerHttpResponse outputMessage;
        private final DeferredResult<?> deferredResult;

        public HttpMessageConvertingHandler(ServerHttpResponse outputMessage, DeferredResult<?> deferredResult) {
            this.outputMessage = outputMessage;
            this.deferredResult = deferredResult;
        }

        @Override
        public void send(Object data, @Nullable MediaType mediaType) throws IOException {
            this.sendInternal(data, mediaType);
        }

        private <T> void sendInternal(T data, @Nullable MediaType mediaType) throws IOException {
            for (HttpMessageConverter converter : ResponseBodyEmitterReturnValueHandler.this.sseMessageConverters) {
                if (!converter.canWrite(data.getClass(), mediaType)) continue;
                converter.write(data, mediaType, (HttpOutputMessage)this.outputMessage);
                this.outputMessage.flush();
                return;
            }
            throw new IllegalArgumentException("No suitable converter for " + data.getClass());
        }

        @Override
        public void complete() {
            try {
                this.outputMessage.flush();
                this.deferredResult.setResult(null);
            }
            catch (IOException ex) {
                this.deferredResult.setErrorResult((Object)ex);
            }
        }

        @Override
        public void completeWithError(Throwable failure) {
            this.deferredResult.setErrorResult((Object)failure);
        }

        @Override
        public void onTimeout(Runnable callback) {
            this.deferredResult.onTimeout(callback);
        }

        @Override
        public void onError(Consumer<Throwable> callback) {
            this.deferredResult.onError(callback);
        }

        @Override
        public void onCompletion(Runnable callback) {
            this.deferredResult.onCompletion(callback);
        }
    }
}

