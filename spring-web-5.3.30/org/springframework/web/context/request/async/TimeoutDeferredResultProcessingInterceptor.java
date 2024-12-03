/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.context.request.async;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;

public class TimeoutDeferredResultProcessingInterceptor
implements DeferredResultProcessingInterceptor {
    @Override
    public <T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> result) throws Exception {
        result.setErrorResult(new AsyncRequestTimeoutException());
        return false;
    }
}

