/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  io.atlassian.util.concurrent.AsyncCompleter
 *  io.atlassian.util.concurrent.AsyncCompleter$Builder
 *  io.atlassian.util.concurrent.LazyReference$InitializationException
 *  io.atlassian.util.concurrent.ThreadFactories
 *  org.joda.time.Duration
 */
package com.atlassian.upm.pac;

import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import io.atlassian.util.concurrent.AsyncCompleter;
import io.atlassian.util.concurrent.LazyReference;
import io.atlassian.util.concurrent.ThreadFactories;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.joda.time.Duration;

class Execution {
    private final ExecutorService executorService;
    private final AsyncCompleter completer;

    Execution(ThreadLocalDelegateExecutorFactory factory) {
        this.executorService = factory.createExecutorService(Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)"UpmPacClient")));
        this.completer = new AsyncCompleter.Builder((Executor)this.executorService).limitParallelExecutionTo(8);
    }

    <T> Collection<T> invokeAll(Collection<? extends Callable<T>> jobs, Duration timeout) {
        Iterable lazy = this.completer.invokeAll(jobs, timeout.getMillis(), TimeUnit.MILLISECONDS);
        try {
            return Collections.unmodifiableList(StreamSupport.stream(lazy.spliterator(), false).collect(Collectors.toList()));
        }
        catch (LazyReference.InitializationException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException)e.getCause();
            }
            if (e.getCause() instanceof Error) {
                throw (Error)e.getCause();
            }
            throw new UndeclaredThrowableException(e.getCause(), "Invoking async jobs failed with an unexpected checked exception");
        }
    }

    void close() {
        this.executorService.shutdownNow();
    }
}

