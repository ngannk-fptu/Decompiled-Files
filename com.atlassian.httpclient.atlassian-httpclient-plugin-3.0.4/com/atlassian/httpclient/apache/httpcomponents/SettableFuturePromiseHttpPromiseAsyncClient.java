/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.executor.ThreadLocalContextManager
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  io.atlassian.util.concurrent.Promise
 *  io.atlassian.util.concurrent.Promises
 *  javax.annotation.Nonnull
 */
package com.atlassian.httpclient.apache.httpcomponents;

import com.atlassian.httpclient.apache.httpcomponents.PromiseHttpAsyncClient;
import com.atlassian.sal.api.executor.ThreadLocalContextManager;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import io.atlassian.util.concurrent.Promise;
import io.atlassian.util.concurrent.Promises;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nonnull;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.protocol.HttpContext;

final class SettableFuturePromiseHttpPromiseAsyncClient<C>
implements PromiseHttpAsyncClient {
    private final HttpAsyncClient client;
    private final ThreadLocalContextManager<C> threadLocalContextManager;
    private final Executor executor;

    SettableFuturePromiseHttpPromiseAsyncClient(HttpAsyncClient client, ThreadLocalContextManager<C> threadLocalContextManager, Executor executor) {
        this.client = (HttpAsyncClient)Preconditions.checkNotNull((Object)client);
        this.threadLocalContextManager = (ThreadLocalContextManager)Preconditions.checkNotNull(threadLocalContextManager);
        this.executor = new ThreadLocalDelegateExecutor<C>(threadLocalContextManager, executor);
    }

    @Override
    public Promise<HttpResponse> execute(HttpUriRequest request, HttpContext context) {
        final CompletableFuture future = new CompletableFuture();
        this.client.execute(request, context, (FutureCallback<HttpResponse>)new ThreadLocalContextAwareFutureCallback<C, HttpResponse>(this.threadLocalContextManager){

            @Override
            void doCompleted(HttpResponse httpResponse) {
                try {
                    SettableFuturePromiseHttpPromiseAsyncClient.this.executor.execute(() -> future.complete(httpResponse));
                }
                catch (RejectedExecutionException e) {
                    SettableFuturePromiseHttpPromiseAsyncClient.this.handleRejectedExecution(future);
                }
            }

            @Override
            void doFailed(Exception ex) {
                try {
                    SettableFuturePromiseHttpPromiseAsyncClient.this.executor.execute(() -> future.completeExceptionally(ex));
                }
                catch (RejectedExecutionException ignored) {
                    future.completeExceptionally(ex);
                }
            }

            @Override
            void doCancelled() {
                TimeoutException timeoutException = new TimeoutException();
                try {
                    SettableFuturePromiseHttpPromiseAsyncClient.this.executor.execute(() -> future.completeExceptionally(timeoutException));
                }
                catch (RejectedExecutionException e) {
                    SettableFuturePromiseHttpPromiseAsyncClient.this.handleRejectedExecution(future);
                }
            }
        });
        return Promises.forCompletionStage(future);
    }

    private void handleRejectedExecution(CompletableFuture<HttpResponse> future) {
        future.completeExceptionally(new RuntimeException("Callback thread pool overflow - Unable to complete request"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    static <C> void runInContext(ThreadLocalContextManager<C> threadLocalContextManager, C threadLocalContext, ClassLoader contextClassLoader, Runnable runnable) {
        Object oldThreadLocalContext = threadLocalContextManager.getThreadLocalContext();
        ClassLoader oldCcl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            threadLocalContextManager.setThreadLocalContext(threadLocalContext);
            runnable.run();
        }
        finally {
            threadLocalContextManager.setThreadLocalContext(oldThreadLocalContext);
            Thread.currentThread().setContextClassLoader(oldCcl);
        }
    }

    private static final class ThreadLocalDelegateRunnable<C>
    implements Runnable {
        private final C context;
        private final Runnable delegate;
        private final ClassLoader contextClassLoader;
        private final ThreadLocalContextManager<C> manager;

        ThreadLocalDelegateRunnable(ThreadLocalContextManager<C> manager, Runnable delegate) {
            this.delegate = delegate;
            this.manager = manager;
            this.context = manager.getThreadLocalContext();
            this.contextClassLoader = Thread.currentThread().getContextClassLoader();
        }

        @Override
        public void run() {
            SettableFuturePromiseHttpPromiseAsyncClient.runInContext(this.manager, this.context, this.contextClassLoader, this.delegate);
        }
    }

    private static final class ThreadLocalDelegateExecutor<C>
    implements Executor {
        private final Executor delegate;
        private final ThreadLocalContextManager<C> manager;

        ThreadLocalDelegateExecutor(ThreadLocalContextManager<C> manager, Executor delegate) {
            this.delegate = (Executor)Preconditions.checkNotNull((Object)delegate);
            this.manager = (ThreadLocalContextManager)Preconditions.checkNotNull(manager);
        }

        @Override
        public void execute(@Nonnull Runnable runnable) {
            this.delegate.execute(new ThreadLocalDelegateRunnable<C>(this.manager, runnable));
        }
    }

    private static abstract class ThreadLocalContextAwareFutureCallback<C, HttpResponse>
    implements FutureCallback<HttpResponse> {
        private final ThreadLocalContextManager<C> threadLocalContextManager;
        private final C threadLocalContext;
        private final ClassLoader contextClassLoader;

        private ThreadLocalContextAwareFutureCallback(ThreadLocalContextManager<C> threadLocalContextManager) {
            this.threadLocalContextManager = (ThreadLocalContextManager)Preconditions.checkNotNull(threadLocalContextManager);
            this.threadLocalContext = threadLocalContextManager.getThreadLocalContext();
            this.contextClassLoader = Thread.currentThread().getContextClassLoader();
        }

        abstract void doCompleted(HttpResponse var1);

        abstract void doFailed(Exception var1);

        abstract void doCancelled();

        @Override
        public final void completed(HttpResponse response) {
            SettableFuturePromiseHttpPromiseAsyncClient.runInContext(this.threadLocalContextManager, this.threadLocalContext, this.contextClassLoader, () -> this.doCompleted(response));
        }

        @Override
        public final void failed(Exception ex) {
            SettableFuturePromiseHttpPromiseAsyncClient.runInContext(this.threadLocalContextManager, this.threadLocalContext, this.contextClassLoader, () -> this.doFailed(ex));
        }

        @Override
        public final void cancelled() {
            SettableFuturePromiseHttpPromiseAsyncClient.runInContext(this.threadLocalContextManager, this.threadLocalContext, this.contextClassLoader, this::doCancelled);
        }
    }
}

