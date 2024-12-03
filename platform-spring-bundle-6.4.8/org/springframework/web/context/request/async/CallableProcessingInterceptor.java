/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.context.request.async;

import java.util.concurrent.Callable;
import org.springframework.web.context.request.NativeWebRequest;

public interface CallableProcessingInterceptor {
    public static final Object RESULT_NONE = new Object();
    public static final Object RESPONSE_HANDLED = new Object();

    default public <T> void beforeConcurrentHandling(NativeWebRequest request, Callable<T> task) throws Exception {
    }

    default public <T> void preProcess(NativeWebRequest request, Callable<T> task) throws Exception {
    }

    default public <T> void postProcess(NativeWebRequest request, Callable<T> task, Object concurrentResult) throws Exception {
    }

    default public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
        return RESULT_NONE;
    }

    default public <T> Object handleError(NativeWebRequest request, Callable<T> task, Throwable t) throws Exception {
        return RESULT_NONE;
    }

    default public <T> void afterCompletion(NativeWebRequest request, Callable<T> task) throws Exception {
    }
}

