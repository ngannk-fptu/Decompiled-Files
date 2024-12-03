/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.failurecache.failures.ExponentialBackOffFailureCache$Builder
 *  com.atlassian.failurecache.failures.FailureCache
 *  com.atlassian.failurecache.util.date.Clock
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.streams.api.common.Either
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.streams.internal;

import com.atlassian.failurecache.failures.ExponentialBackOffFailureCache;
import com.atlassian.failurecache.failures.FailureCache;
import com.atlassian.failurecache.util.date.Clock;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.internal.ActivityProvider;
import com.atlassian.streams.internal.ActivityProviderCallable;
import com.atlassian.streams.internal.Sys;
import com.atlassian.streams.internal.completion.Execution;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public final class StreamsCompletionService
implements InitializingBean,
DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(StreamsCompletionService.class);
    private static final boolean IGNORE_FAILURE_CACHE = Sys.inDevMode() && Boolean.getBoolean("com.atlassian.streams.aggregator.ignore.failure.cache");
    private final ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory;
    private final PluginEventManager pluginEventManager;
    private final ResettableLazyReference<Execution> async = new ResettableLazyReference<Execution>(){

        protected Execution create() throws Exception {
            return new Execution(StreamsCompletionService.this.threadLocalDelegateExecutorFactory, StreamsCompletionService.this.failureCache);
        }
    };
    private final FailureCache<ActivityProvider> failureCache;

    StreamsCompletionService(ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory, PluginEventManager pluginEventManager, Clock clock) {
        this.threadLocalDelegateExecutorFactory = (ThreadLocalDelegateExecutorFactory)Preconditions.checkNotNull((Object)threadLocalDelegateExecutorFactory, (Object)"threadLocalDelegateExecutorFactory");
        this.pluginEventManager = (PluginEventManager)Preconditions.checkNotNull((Object)pluginEventManager, (Object)"pluginEventManager");
        this.failureCache = new ExponentialBackOffFailureCache.Builder().maxExpiry(30L, TimeUnit.MINUTES).clock(clock).build();
    }

    public Predicate<ActivityProvider> reachable() {
        return activityProvider -> IGNORE_FAILURE_CACHE || !this.failureCache.isFailing(activityProvider);
    }

    public <T> Iterable<Either<ActivityProvider.Error, T>> execute(Iterable<? extends ActivityProviderCallable<Either<ActivityProvider.Error, T>>> callables) {
        if (Iterables.isEmpty(callables)) {
            return Collections.emptySet();
        }
        Iterable results = ((Execution)this.async.get()).invokeAll(callables);
        this.registerFailures(results);
        return results;
    }

    public <T> Iterable<Either<ActivityProvider.Error, T>> execute(Iterable<? extends ActivityProviderCallable<Either<ActivityProvider.Error, T>>> callables, long time, TimeUnit unit) {
        if (Iterables.isEmpty(callables)) {
            return Collections.emptySet();
        }
        Iterable results = ((Execution)this.async.get()).invokeAll(callables, time, unit);
        this.registerFailures(results);
        return results;
    }

    private <T> void registerFailures(Iterable<Either<ActivityProvider.Error, T>> results) {
        for (ActivityProvider.Error error : Either.getLefts(results)) {
            if (!error.getActivityProvider().isDefined() || StreamsCompletionService.isUserSpecificError(error) || error.getType() == ActivityProvider.Error.Type.THROTTLED) continue;
            logger.warn("Registering failure for stream provider {} due to error {}", (Object)((ActivityProvider)error.getActivityProvider().get()).getName(), (Object)error);
            this.failureCache.registerFailure(error.getActivityProvider().get());
        }
    }

    private static boolean isUserSpecificError(ActivityProvider.Error errors) {
        ActivityProvider.Error.Type type = errors.getType();
        return type == ActivityProvider.Error.Type.CREDENTIALS_REQUIRED || type == ActivityProvider.Error.Type.UNAUTHORIZED;
    }

    private void resetCompletionService() {
        if (this.async.isInitialized()) {
            ((Execution)this.async.get()).close();
        }
        this.async.reset();
    }

    public synchronized void afterPropertiesSet() {
        this.resetCompletionService();
        this.pluginEventManager.register((Object)this);
    }

    public synchronized void destroy() {
        this.resetCompletionService();
        this.pluginEventManager.unregister((Object)this);
    }

    @PluginEventListener
    public void onShutdown(PluginFrameworkShutdownEvent event) {
        this.resetCompletionService();
    }
}

