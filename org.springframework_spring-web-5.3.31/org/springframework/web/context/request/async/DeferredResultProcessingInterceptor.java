/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.context.request.async;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;

public interface DeferredResultProcessingInterceptor {
    default public <T> void beforeConcurrentHandling(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
    }

    default public <T> void preProcess(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
    }

    default public <T> void postProcess(NativeWebRequest request, DeferredResult<T> deferredResult, Object concurrentResult) throws Exception {
    }

    default public <T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        return true;
    }

    default public <T> boolean handleError(NativeWebRequest request, DeferredResult<T> deferredResult, Throwable t) throws Exception {
        return true;
    }

    default public <T> void afterCompletion(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
    }
}

