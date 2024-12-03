/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.context.request.async;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;

class DeferredResultInterceptorChain {
    private static final Log logger = LogFactory.getLog(DeferredResultInterceptorChain.class);
    private final List<DeferredResultProcessingInterceptor> interceptors;
    private int preProcessingIndex = -1;

    public DeferredResultInterceptorChain(List<DeferredResultProcessingInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public void applyBeforeConcurrentHandling(NativeWebRequest request, DeferredResult<?> deferredResult) throws Exception {
        for (DeferredResultProcessingInterceptor interceptor : this.interceptors) {
            interceptor.beforeConcurrentHandling(request, deferredResult);
        }
    }

    public void applyPreProcess(NativeWebRequest request, DeferredResult<?> deferredResult) throws Exception {
        for (DeferredResultProcessingInterceptor interceptor : this.interceptors) {
            interceptor.preProcess(request, deferredResult);
            ++this.preProcessingIndex;
        }
    }

    public Object applyPostProcess(NativeWebRequest request, DeferredResult<?> deferredResult, Object concurrentResult) {
        try {
            for (int i2 = this.preProcessingIndex; i2 >= 0; --i2) {
                this.interceptors.get(i2).postProcess(request, deferredResult, concurrentResult);
            }
        }
        catch (Throwable ex) {
            return ex;
        }
        return concurrentResult;
    }

    public void triggerAfterTimeout(NativeWebRequest request, DeferredResult<?> deferredResult) throws Exception {
        for (DeferredResultProcessingInterceptor interceptor : this.interceptors) {
            if (deferredResult.isSetOrExpired()) {
                return;
            }
            if (interceptor.handleTimeout(request, deferredResult)) continue;
            break;
        }
    }

    public boolean triggerAfterError(NativeWebRequest request, DeferredResult<?> deferredResult, Throwable ex) throws Exception {
        for (DeferredResultProcessingInterceptor interceptor : this.interceptors) {
            if (deferredResult.isSetOrExpired()) {
                return false;
            }
            if (interceptor.handleError(request, deferredResult, ex)) continue;
            return false;
        }
        return true;
    }

    public void triggerAfterCompletion(NativeWebRequest request, DeferredResult<?> deferredResult) {
        for (int i2 = this.preProcessingIndex; i2 >= 0; --i2) {
            try {
                this.interceptors.get(i2).afterCompletion(request, deferredResult);
                continue;
            }
            catch (Throwable ex) {
                logger.trace((Object)"Ignoring failure in afterCompletion method", ex);
            }
        }
    }
}

