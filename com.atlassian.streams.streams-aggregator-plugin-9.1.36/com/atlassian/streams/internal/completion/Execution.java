/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.failurecache.failures.FailureCache
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.spi.CancellableTask
 *  com.atlassian.streams.spi.CancellableTask$Result
 *  com.google.common.base.Function
 *  io.atlassian.util.concurrent.ThreadFactories
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.internal.completion;

import com.atlassian.failurecache.failures.FailureCache;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.ActivityProviderCallable;
import com.atlassian.streams.internal.completion.Completer;
import com.atlassian.streams.spi.CancellableTask;
import com.google.common.base.Function;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Execution {
    private static final int MAX_POOL_SIZE = Integer.getInteger("streams.completion.service.pool.max", 32);
    private final ExecutorService executorService;
    private final Completer completer;
    private final FailureCache failureCache;
    private static final Logger logger = LoggerFactory.getLogger(Execution.class);

    public Execution(ThreadLocalDelegateExecutorFactory factory, FailureCache failureCache) {
        this.failureCache = Objects.requireNonNull(failureCache, "failureCache can't be null");
        this.executorService = factory.createExecutorService(this.newLimitedCachedThreadPool(ThreadFactories.namedThreadFactory((String)"StreamsCompletionService:"), MAX_POOL_SIZE));
        this.completer = new Completer(this.executorService, Execution.cancellingCompletionServiceFactory(failureCache));
    }

    private ExecutorService newLimitedCachedThreadPool(ThreadFactory threadFactory, int limit) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(limit, limit, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }

    public <T> Iterable<Either<ActivityProvider.Error, T>> invokeAll(Iterable<? extends ActivityProviderCallable<Either<ActivityProvider.Error, T>>> jobs) {
        return this.completer.invokeAll(jobs);
    }

    public <T> Iterable<Either<ActivityProvider.Error, T>> invokeAll(Iterable<? extends ActivityProviderCallable<Either<ActivityProvider.Error, T>>> jobs, long time, TimeUnit unit) {
        return this.completer.invokeAll(jobs, time, unit);
    }

    public void close() {
        this.executorService.shutdownNow();
    }

    private static Completer.ExecutorCompletionServiceFactory cancellingCompletionServiceFactory(FailureCache failureCache) {
        return new CancellingCompletionServiceFactory(failureCache);
    }

    private static final class CancellingCompletionService<T>
    implements CompletionService<T> {
        private final CompletionService<T> delegate;
        private final FailureCache failureCache;
        private final ConcurrentHashMap<Future<T>, Future<T>> originalFutureToWrappedFuture;

        CancellingCompletionService(Executor executor, FailureCache failureCache) {
            this.delegate = new ExecutorCompletionService<T>(executor);
            this.failureCache = failureCache;
            this.originalFutureToWrappedFuture = new ConcurrentHashMap();
        }

        @Override
        public Future<T> poll() {
            return this.wrappedFuture(this.delegate.poll());
        }

        @Override
        public Future<T> poll(long timeout, TimeUnit unit) throws InterruptedException {
            return this.wrappedFuture(this.delegate.poll(timeout, unit));
        }

        @Override
        public Future<T> submit(final Callable<T> task) {
            logger.debug("Submitting task stream provider {}", (Object)((ActivityProviderCallable)task).getActivityProvider().getName());
            final Future<T> f = this.delegate.submit(task);
            Future wrappedFuture = new Future<T>(){
                volatile boolean cancelled = false;

                @Override
                public boolean cancel(boolean mayInterrupt) {
                    if (task instanceof CancellableTask) {
                        CancellableTask.Result r = ((CancellableTask)task).cancel();
                        switch (r) {
                            case CANCELLED: {
                                this.cancelled = true;
                                break;
                            }
                            case CANNOT_CANCEL: {
                                this.cancelled = false;
                                break;
                            }
                            case INTERRUPT: {
                                this.cancelled = f.cancel(mayInterrupt);
                                break;
                            }
                            default: {
                                throw new IllegalStateException("Unknown result type '" + r + "' returned from CancellableTask.cancel");
                            }
                        }
                    } else {
                        this.cancelled = f.cancel(mayInterrupt);
                    }
                    logger.warn("Registering failure for stream provider {} due to cancellation (timeout)", (Object)((ActivityProviderCallable)task).getActivityProvider().getName());
                    failureCache.registerFailure((Object)((ActivityProviderCallable)task).getActivityProvider());
                    originalFutureToWrappedFuture.remove(f);
                    return this.cancelled;
                }

                @Override
                public T get() throws InterruptedException, ExecutionException {
                    logger.debug("Attempting get from stream provider {}", (Object)((ActivityProviderCallable)task).getActivityProvider().getName());
                    Object value = f.get();
                    failureCache.registerSuccess((Object)((ActivityProviderCallable)task).getActivityProvider());
                    return value;
                }

                @Override
                public T get(long t, TimeUnit u) throws InterruptedException, ExecutionException, TimeoutException {
                    logger.debug("Attempting get from stream provider {} with timeout {} {}", new Object[]{((ActivityProviderCallable)task).getActivityProvider().getName(), String.valueOf(t), u.toString()});
                    Object value = f.get(t, u);
                    failureCache.registerSuccess((Object)((ActivityProviderCallable)task).getActivityProvider());
                    return value;
                }

                @Override
                public boolean isCancelled() {
                    return this.cancelled;
                }

                @Override
                public boolean isDone() {
                    return this.cancelled || f.isDone();
                }
            };
            this.originalFutureToWrappedFuture.put(f, wrappedFuture);
            return wrappedFuture;
        }

        @Override
        public Future<T> submit(Runnable task, T result) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public Future<T> take() throws InterruptedException {
            return this.wrappedFuture(this.delegate.take());
        }

        private Future<T> wrappedFuture(Future<T> future) {
            if (future == null) {
                return future;
            }
            return this.originalFutureToWrappedFuture.remove(future);
        }
    }

    private static class CancellingCompletionServiceFactory
    implements Completer.ExecutorCompletionServiceFactory {
        private final FailureCache failureCache;

        private CancellingCompletionServiceFactory(FailureCache failureCache) {
            this.failureCache = failureCache;
        }

        @Override
        public <T> Function<Executor, CompletionService<Either<ActivityProvider.Error, T>>> create() {
            return new Function<Executor, CompletionService<Either<ActivityProvider.Error, T>>>(){

                public CompletionService<Either<ActivityProvider.Error, T>> apply(Executor e) {
                    return new CancellingCompletionService(e, failureCache);
                }
            };
        }
    }
}

