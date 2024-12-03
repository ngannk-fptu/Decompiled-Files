/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Either
 *  com.google.common.base.Function
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  io.atlassian.util.concurrent.Timeout
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.internal.completion;

import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.ActivityProviderCallable;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.atlassian.util.concurrent.Timeout;
import java.util.Objects;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Completer {
    private static final Logger logger = LoggerFactory.getLogger(Completer.class);
    private final Executor executor;
    private final ExecutorCompletionServiceFactory completionServiceFactory;

    public Completer(Executor executor, ExecutorCompletionServiceFactory completionServiceFactory) {
        this.executor = Objects.requireNonNull(executor, "executor");
        this.completionServiceFactory = Objects.requireNonNull(completionServiceFactory, "completionServiceFactory");
    }

    public <T> Iterable<Either<ActivityProvider.Error, T>> invokeAll(Iterable<? extends ActivityProviderCallable<Either<ActivityProvider.Error, T>>> callables, long time, TimeUnit unit) {
        return this.invokeAll(callables, Timeout.getNanosTimeout((long)time, (TimeUnit)unit));
    }

    public <T> Iterable<Either<ActivityProvider.Error, T>> invokeAll(Iterable<? extends ActivityProviderCallable<Either<ActivityProvider.Error, T>>> jobs) {
        return this.invokeAll(jobs, null);
    }

    private <T> Iterable<Either<ActivityProvider.Error, T>> invokeAll(Iterable<? extends ActivityProviderCallable<Either<ActivityProvider.Error, T>>> callables, @Nullable Timeout nanosTimeout) {
        ImmutableList lazyAsyncSuppliers = ImmutableList.copyOf((Iterable)Iterables.transform(callables, new CompletionFunction((CompletionService)this.completionServiceFactory.create().apply((Object)this.executor), nanosTimeout)));
        return Iterables.transform((Iterable)lazyAsyncSuppliers, Completer.fromSupplier());
    }

    static <T> Function<Supplier<? extends T>, T> fromSupplier() {
        return new Function<Supplier<? extends T>, T>(){

            public T apply(Supplier<? extends T> supplier) {
                return supplier.get();
            }
        };
    }

    private static class CompletionFunction<T>
    implements Function<ActivityProviderCallable<Either<ActivityProvider.Error, T>>, Supplier<Either<ActivityProvider.Error, T>>> {
        private final CompletionService<Either<ActivityProvider.Error, T>> completionService;
        private Timeout nanosTimeout;

        CompletionFunction(CompletionService<Either<ActivityProvider.Error, T>> completionService, @Nullable Timeout nanosTimeout) {
            this.completionService = completionService;
            this.nanosTimeout = nanosTimeout;
        }

        public Supplier<Either<ActivityProvider.Error, T>> apply(final ActivityProviderCallable<Either<ActivityProvider.Error, T>> task) {
            final Future<Either<ActivityProvider.Error, T>> future = this.completionService.submit(task);
            return Suppliers.memoize((Supplier)new Supplier<Either<ActivityProvider.Error, T>>(){

                public Either<ActivityProvider.Error, T> get() {
                    try {
                        if (nanosTimeout == null) {
                            return (Either)future.get();
                        }
                        return (Either)future.get(nanosTimeout.getTime(), nanosTimeout.getUnit());
                    }
                    catch (InterruptedException e) {
                        logger.debug("Handling a non-timeout exception", (Throwable)e);
                        return Either.left((Object)ActivityProvider.Error.timeout(task.getActivityProvider()));
                    }
                    catch (ExecutionException e) {
                        logger.debug("Handling a non-timeout exception", (Throwable)e);
                        return Either.left((Object)ActivityProvider.Error.other(task.getActivityProvider()));
                    }
                    catch (TimeoutException e) {
                        logger.debug("Handling a timeout", (Object)e.getMessage());
                        future.cancel(true);
                        return Either.left((Object)ActivityProvider.Error.timeout(task.getActivityProvider()));
                    }
                }
            });
        }
    }

    public static interface ExecutorCompletionServiceFactory {
        public <T> Function<Executor, CompletionService<Either<ActivityProvider.Error, T>>> create();
    }
}

