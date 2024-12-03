/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.task.AsyncTaskExecutor
 *  org.springframework.core.task.SimpleAsyncTaskExecutor
 *  org.springframework.core.task.SyncTaskExecutor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.context.request.async;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.async.AsyncWebRequest;
import org.springframework.web.context.request.async.CallableInterceptorChain;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultInterceptorChain;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.context.request.async.TimeoutDeferredResultProcessingInterceptor;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.context.request.async.WebAsyncUtils;

public final class WebAsyncManager {
    private static final Object RESULT_NONE = new Object();
    private static final AsyncTaskExecutor DEFAULT_TASK_EXECUTOR = new SimpleAsyncTaskExecutor(WebAsyncManager.class.getSimpleName());
    private static final Log logger = LogFactory.getLog(WebAsyncManager.class);
    private static final CallableProcessingInterceptor timeoutCallableInterceptor = new TimeoutCallableProcessingInterceptor();
    private static final DeferredResultProcessingInterceptor timeoutDeferredResultInterceptor = new TimeoutDeferredResultProcessingInterceptor();
    private static Boolean taskExecutorWarning = true;
    private AsyncWebRequest asyncWebRequest;
    private AsyncTaskExecutor taskExecutor = DEFAULT_TASK_EXECUTOR;
    private volatile Object concurrentResult = RESULT_NONE;
    private volatile Object[] concurrentResultContext;
    private volatile boolean errorHandlingInProgress;
    private final Map<Object, CallableProcessingInterceptor> callableInterceptors = new LinkedHashMap<Object, CallableProcessingInterceptor>();
    private final Map<Object, DeferredResultProcessingInterceptor> deferredResultInterceptors = new LinkedHashMap<Object, DeferredResultProcessingInterceptor>();

    WebAsyncManager() {
    }

    public void setAsyncWebRequest(AsyncWebRequest asyncWebRequest) {
        Assert.notNull((Object)asyncWebRequest, (String)"AsyncWebRequest must not be null");
        this.asyncWebRequest = asyncWebRequest;
        this.asyncWebRequest.addCompletionHandler(() -> asyncWebRequest.removeAttribute(WebAsyncUtils.WEB_ASYNC_MANAGER_ATTRIBUTE, 0));
    }

    public void setTaskExecutor(AsyncTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public boolean isConcurrentHandlingStarted() {
        return this.asyncWebRequest != null && this.asyncWebRequest.isAsyncStarted();
    }

    public boolean hasConcurrentResult() {
        return this.concurrentResult != RESULT_NONE;
    }

    public Object getConcurrentResult() {
        return this.concurrentResult;
    }

    public Object[] getConcurrentResultContext() {
        return this.concurrentResultContext;
    }

    @Nullable
    public CallableProcessingInterceptor getCallableInterceptor(Object key) {
        return this.callableInterceptors.get(key);
    }

    @Nullable
    public DeferredResultProcessingInterceptor getDeferredResultInterceptor(Object key) {
        return this.deferredResultInterceptors.get(key);
    }

    public void registerCallableInterceptor(Object key, CallableProcessingInterceptor interceptor) {
        Assert.notNull((Object)key, (String)"Key is required");
        Assert.notNull((Object)interceptor, (String)"CallableProcessingInterceptor is required");
        this.callableInterceptors.put(key, interceptor);
    }

    public void registerCallableInterceptors(CallableProcessingInterceptor ... interceptors) {
        Assert.notNull((Object)interceptors, (String)"A CallableProcessingInterceptor is required");
        for (CallableProcessingInterceptor interceptor : interceptors) {
            String key = interceptor.getClass().getName() + ":" + interceptor.hashCode();
            this.callableInterceptors.put(key, interceptor);
        }
    }

    public void registerDeferredResultInterceptor(Object key, DeferredResultProcessingInterceptor interceptor) {
        Assert.notNull((Object)key, (String)"Key is required");
        Assert.notNull((Object)interceptor, (String)"DeferredResultProcessingInterceptor is required");
        this.deferredResultInterceptors.put(key, interceptor);
    }

    public void registerDeferredResultInterceptors(DeferredResultProcessingInterceptor ... interceptors) {
        Assert.notNull((Object)interceptors, (String)"A DeferredResultProcessingInterceptor is required");
        for (DeferredResultProcessingInterceptor interceptor : interceptors) {
            String key = interceptor.getClass().getName() + ":" + interceptor.hashCode();
            this.deferredResultInterceptors.put(key, interceptor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearConcurrentResult() {
        WebAsyncManager webAsyncManager = this;
        synchronized (webAsyncManager) {
            this.concurrentResult = RESULT_NONE;
            this.concurrentResultContext = null;
        }
    }

    public void startCallableProcessing(Callable<?> callable, Object ... processingContext) throws Exception {
        Assert.notNull(callable, (String)"Callable must not be null");
        this.startCallableProcessing(new WebAsyncTask(callable), processingContext);
    }

    public void startCallableProcessing(WebAsyncTask<?> webAsyncTask, Object ... processingContext) throws Exception {
        AsyncTaskExecutor executor;
        Assert.notNull(webAsyncTask, (String)"WebAsyncTask must not be null");
        Assert.state((this.asyncWebRequest != null ? 1 : 0) != 0, (String)"AsyncWebRequest must not be null");
        Long timeout = webAsyncTask.getTimeout();
        if (timeout != null) {
            this.asyncWebRequest.setTimeout(timeout);
        }
        if ((executor = webAsyncTask.getExecutor()) != null) {
            this.taskExecutor = executor;
        } else {
            this.logExecutorWarning();
        }
        ArrayList<CallableProcessingInterceptor> interceptors = new ArrayList<CallableProcessingInterceptor>();
        interceptors.add(webAsyncTask.getInterceptor());
        interceptors.addAll(this.callableInterceptors.values());
        interceptors.add(timeoutCallableInterceptor);
        Callable<?> callable = webAsyncTask.getCallable();
        CallableInterceptorChain interceptorChain = new CallableInterceptorChain(interceptors);
        this.asyncWebRequest.addTimeoutHandler(() -> {
            Object result;
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Async request timeout for " + this.formatRequestUri()));
            }
            if ((result = interceptorChain.triggerAfterTimeout(this.asyncWebRequest, callable)) != CallableProcessingInterceptor.RESULT_NONE) {
                this.setConcurrentResultAndDispatch(result);
            }
        });
        this.asyncWebRequest.addErrorHandler(ex -> {
            if (!this.errorHandlingInProgress) {
                Object result;
                if (logger.isDebugEnabled()) {
                    logger.debug((Object)("Async request error for " + this.formatRequestUri() + ": " + ex));
                }
                result = (result = interceptorChain.triggerAfterError(this.asyncWebRequest, callable, (Throwable)ex)) != CallableProcessingInterceptor.RESULT_NONE ? result : ex;
                this.setConcurrentResultAndDispatch(result);
            }
        });
        this.asyncWebRequest.addCompletionHandler(() -> interceptorChain.triggerAfterCompletion(this.asyncWebRequest, callable));
        interceptorChain.applyBeforeConcurrentHandling(this.asyncWebRequest, callable);
        this.startAsyncProcessing(processingContext);
        try {
            Future future = this.taskExecutor.submit(() -> {
                Object result = null;
                try {
                    interceptorChain.applyPreProcess(this.asyncWebRequest, callable);
                    result = callable.call();
                }
                catch (Throwable ex) {
                    result = ex;
                }
                finally {
                    result = interceptorChain.applyPostProcess(this.asyncWebRequest, callable, result);
                }
                this.setConcurrentResultAndDispatch(result);
            });
            interceptorChain.setTaskFuture(future);
        }
        catch (RejectedExecutionException ex2) {
            Object result = interceptorChain.applyPostProcess(this.asyncWebRequest, callable, ex2);
            this.setConcurrentResultAndDispatch(result);
            throw ex2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void logExecutorWarning() {
        if (taskExecutorWarning.booleanValue() && logger.isWarnEnabled()) {
            AsyncTaskExecutor asyncTaskExecutor = DEFAULT_TASK_EXECUTOR;
            synchronized (asyncTaskExecutor) {
                AsyncTaskExecutor executor = this.taskExecutor;
                if (taskExecutorWarning.booleanValue() && (executor instanceof SimpleAsyncTaskExecutor || executor instanceof SyncTaskExecutor)) {
                    String executorTypeName = executor.getClass().getSimpleName();
                    logger.warn((Object)("\n!!!\nAn Executor is required to handle java.util.concurrent.Callable return values.\nPlease, configure a TaskExecutor in the MVC config under \"async support\".\nThe " + executorTypeName + " currently in use is not suitable under load.\n-------------------------------\nRequest URI: '" + this.formatRequestUri() + "'\n!!!"));
                    taskExecutorWarning = false;
                }
            }
        }
    }

    private String formatRequestUri() {
        HttpServletRequest request = this.asyncWebRequest.getNativeRequest(HttpServletRequest.class);
        return request != null ? request.getRequestURI() : "servlet container";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setConcurrentResultAndDispatch(Object result) {
        WebAsyncManager webAsyncManager = this;
        synchronized (webAsyncManager) {
            if (this.concurrentResult != RESULT_NONE) {
                return;
            }
            this.concurrentResult = result;
            this.errorHandlingInProgress = result instanceof Throwable;
        }
        if (this.asyncWebRequest.isAsyncComplete()) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Async result set but request already complete: " + this.formatRequestUri()));
            }
            return;
        }
        if (logger.isDebugEnabled()) {
            boolean isError = result instanceof Throwable;
            logger.debug((Object)("Async " + (isError ? "error" : "result set") + ", dispatch to " + this.formatRequestUri()));
        }
        this.asyncWebRequest.dispatch();
    }

    public void startDeferredResultProcessing(DeferredResult<?> deferredResult, Object ... processingContext) throws Exception {
        Assert.notNull(deferredResult, (String)"DeferredResult must not be null");
        Assert.state((this.asyncWebRequest != null ? 1 : 0) != 0, (String)"AsyncWebRequest must not be null");
        Long timeout = deferredResult.getTimeoutValue();
        if (timeout != null) {
            this.asyncWebRequest.setTimeout(timeout);
        }
        ArrayList<DeferredResultProcessingInterceptor> interceptors = new ArrayList<DeferredResultProcessingInterceptor>();
        interceptors.add(deferredResult.getInterceptor());
        interceptors.addAll(this.deferredResultInterceptors.values());
        interceptors.add(timeoutDeferredResultInterceptor);
        DeferredResultInterceptorChain interceptorChain = new DeferredResultInterceptorChain(interceptors);
        this.asyncWebRequest.addTimeoutHandler(() -> {
            try {
                interceptorChain.triggerAfterTimeout(this.asyncWebRequest, deferredResult);
            }
            catch (Throwable ex) {
                this.setConcurrentResultAndDispatch(ex);
            }
        });
        this.asyncWebRequest.addErrorHandler(ex -> {
            if (!this.errorHandlingInProgress) {
                try {
                    if (!interceptorChain.triggerAfterError(this.asyncWebRequest, deferredResult, (Throwable)ex)) {
                        return;
                    }
                    deferredResult.setErrorResult(ex);
                }
                catch (Throwable interceptorEx) {
                    this.setConcurrentResultAndDispatch(interceptorEx);
                }
            }
        });
        this.asyncWebRequest.addCompletionHandler(() -> interceptorChain.triggerAfterCompletion(this.asyncWebRequest, deferredResult));
        interceptorChain.applyBeforeConcurrentHandling(this.asyncWebRequest, deferredResult);
        this.startAsyncProcessing(processingContext);
        try {
            interceptorChain.applyPreProcess(this.asyncWebRequest, deferredResult);
            deferredResult.setResultHandler(result -> {
                result = interceptorChain.applyPostProcess(this.asyncWebRequest, deferredResult, result);
                this.setConcurrentResultAndDispatch(result);
            });
        }
        catch (Throwable ex2) {
            this.setConcurrentResultAndDispatch(ex2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void startAsyncProcessing(Object[] processingContext) {
        WebAsyncManager webAsyncManager = this;
        synchronized (webAsyncManager) {
            this.concurrentResult = RESULT_NONE;
            this.concurrentResultContext = processingContext;
            this.errorHandlingInProgress = false;
        }
        this.asyncWebRequest.startAsync();
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"Started async request");
        }
    }
}

