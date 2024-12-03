/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.context.request.async;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;

public class DeferredResult<T> {
    private static final Object RESULT_NONE = new Object();
    private static final Log logger = LogFactory.getLog(DeferredResult.class);
    @Nullable
    private final Long timeoutValue;
    private final Supplier<?> timeoutResult;
    private Runnable timeoutCallback;
    private Consumer<Throwable> errorCallback;
    private Runnable completionCallback;
    private DeferredResultHandler resultHandler;
    private volatile Object result = RESULT_NONE;
    private volatile boolean expired;

    public DeferredResult() {
        this(null, () -> RESULT_NONE);
    }

    public DeferredResult(Long timeoutValue) {
        this(timeoutValue, () -> RESULT_NONE);
    }

    public DeferredResult(@Nullable Long timeoutValue, Object timeoutResult) {
        this.timeoutValue = timeoutValue;
        this.timeoutResult = () -> timeoutResult;
    }

    public DeferredResult(@Nullable Long timeoutValue, Supplier<?> timeoutResult) {
        this.timeoutValue = timeoutValue;
        this.timeoutResult = timeoutResult;
    }

    public final boolean isSetOrExpired() {
        return this.result != RESULT_NONE || this.expired;
    }

    public boolean hasResult() {
        return this.result != RESULT_NONE;
    }

    @Nullable
    public Object getResult() {
        Object resultToCheck = this.result;
        return resultToCheck != RESULT_NONE ? resultToCheck : null;
    }

    @Nullable
    final Long getTimeoutValue() {
        return this.timeoutValue;
    }

    public void onTimeout(Runnable callback) {
        this.timeoutCallback = callback;
    }

    public void onError(Consumer<Throwable> callback) {
        this.errorCallback = callback;
    }

    public void onCompletion(Runnable callback) {
        this.completionCallback = callback;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void setResultHandler(DeferredResultHandler resultHandler) {
        Object resultToHandle;
        Assert.notNull((Object)resultHandler, "DeferredResultHandler is required");
        if (this.expired) {
            return;
        }
        DeferredResult deferredResult = this;
        synchronized (deferredResult) {
            if (this.expired) {
                return;
            }
            resultToHandle = this.result;
            if (resultToHandle == RESULT_NONE) {
                this.resultHandler = resultHandler;
                return;
            }
        }
        try {
            resultHandler.handleResult(resultToHandle);
        }
        catch (Throwable ex) {
            logger.debug((Object)"Failed to process async result", ex);
        }
    }

    public boolean setResult(T result) {
        return this.setResultInternal(result);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean setResultInternal(Object result) {
        DeferredResultHandler resultHandlerToUse;
        if (this.isSetOrExpired()) {
            return false;
        }
        DeferredResult deferredResult = this;
        synchronized (deferredResult) {
            if (this.isSetOrExpired()) {
                return false;
            }
            this.result = result;
            resultHandlerToUse = this.resultHandler;
            if (resultHandlerToUse == null) {
                return true;
            }
            this.resultHandler = null;
        }
        resultHandlerToUse.handleResult(result);
        return true;
    }

    public boolean setErrorResult(Object result) {
        return this.setResultInternal(result);
    }

    final DeferredResultProcessingInterceptor getInterceptor() {
        return new DeferredResultProcessingInterceptor(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public <S> boolean handleTimeout(NativeWebRequest request, DeferredResult<S> deferredResult) {
                boolean continueProcessing = true;
                try {
                    if (DeferredResult.this.timeoutCallback != null) {
                        DeferredResult.this.timeoutCallback.run();
                    }
                }
                finally {
                    Object value = DeferredResult.this.timeoutResult.get();
                    if (value != RESULT_NONE) {
                        continueProcessing = false;
                        try {
                            DeferredResult.this.setResultInternal(value);
                        }
                        catch (Throwable ex) {
                            logger.debug((Object)"Failed to handle timeout result", ex);
                        }
                    }
                }
                return continueProcessing;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public <S> boolean handleError(NativeWebRequest request, DeferredResult<S> deferredResult, Throwable t) {
                try {
                    if (DeferredResult.this.errorCallback != null) {
                        DeferredResult.this.errorCallback.accept(t);
                    }
                }
                finally {
                    try {
                        DeferredResult.this.setResultInternal(t);
                    }
                    catch (Throwable ex) {
                        logger.debug((Object)"Failed to handle error result", ex);
                    }
                }
                return false;
            }

            public <S> void afterCompletion(NativeWebRequest request, DeferredResult<S> deferredResult) {
                DeferredResult.this.expired = true;
                if (DeferredResult.this.completionCallback != null) {
                    DeferredResult.this.completionCallback.run();
                }
            }
        };
    }

    @FunctionalInterface
    public static interface DeferredResultHandler {
        public void handleResult(Object var1);
    }
}

