/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.context.request.async;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;

class CallableInterceptorChain {
    private static final Log logger = LogFactory.getLog(CallableInterceptorChain.class);
    private final List<CallableProcessingInterceptor> interceptors;
    private int preProcessIndex = -1;
    private volatile Future<?> taskFuture;

    public CallableInterceptorChain(List<CallableProcessingInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public void setTaskFuture(Future<?> taskFuture) {
        this.taskFuture = taskFuture;
    }

    public void applyBeforeConcurrentHandling(NativeWebRequest request, Callable<?> task) throws Exception {
        for (CallableProcessingInterceptor interceptor : this.interceptors) {
            interceptor.beforeConcurrentHandling(request, task);
        }
    }

    public void applyPreProcess(NativeWebRequest request, Callable<?> task) throws Exception {
        for (CallableProcessingInterceptor interceptor : this.interceptors) {
            interceptor.preProcess(request, task);
            ++this.preProcessIndex;
        }
    }

    public Object applyPostProcess(NativeWebRequest request, Callable<?> task, Object concurrentResult) {
        Object exceptionResult = null;
        for (int i = this.preProcessIndex; i >= 0; --i) {
            try {
                this.interceptors.get(i).postProcess(request, task, concurrentResult);
                continue;
            }
            catch (Throwable ex) {
                if (exceptionResult != null) {
                    if (!logger.isTraceEnabled()) continue;
                    logger.trace((Object)"Ignoring failure in postProcess method", ex);
                    continue;
                }
                exceptionResult = ex;
            }
        }
        return exceptionResult != null ? exceptionResult : concurrentResult;
    }

    public Object triggerAfterTimeout(NativeWebRequest request, Callable<?> task) {
        this.cancelTask();
        for (CallableProcessingInterceptor interceptor : this.interceptors) {
            try {
                Object result = interceptor.handleTimeout(request, task);
                if (result != CallableProcessingInterceptor.RESPONSE_HANDLED) {
                    if (result == CallableProcessingInterceptor.RESULT_NONE) continue;
                    return result;
                }
                break;
            }
            catch (Throwable ex) {
                return ex;
            }
        }
        return CallableProcessingInterceptor.RESULT_NONE;
    }

    private void cancelTask() {
        Future<?> future = this.taskFuture;
        if (future != null) {
            try {
                future.cancel(true);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    public Object triggerAfterError(NativeWebRequest request, Callable<?> task, Throwable throwable) {
        this.cancelTask();
        for (CallableProcessingInterceptor interceptor : this.interceptors) {
            try {
                Object result = interceptor.handleError(request, task, throwable);
                if (result != CallableProcessingInterceptor.RESPONSE_HANDLED) {
                    if (result == CallableProcessingInterceptor.RESULT_NONE) continue;
                    return result;
                }
                break;
            }
            catch (Throwable ex) {
                return ex;
            }
        }
        return CallableProcessingInterceptor.RESULT_NONE;
    }

    public void triggerAfterCompletion(NativeWebRequest request, Callable<?> task) {
        for (int i = this.interceptors.size() - 1; i >= 0; --i) {
            try {
                this.interceptors.get(i).afterCompletion(request, task);
                continue;
            }
            catch (Throwable ex) {
                if (!logger.isTraceEnabled()) continue;
                logger.trace((Object)"Ignoring failure in afterCompletion method", ex);
            }
        }
    }
}

