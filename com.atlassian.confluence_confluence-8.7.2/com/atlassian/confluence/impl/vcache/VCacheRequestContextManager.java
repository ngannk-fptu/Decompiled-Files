/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.internal.NameValidator
 *  com.atlassian.vcache.internal.RequestContext
 *  com.atlassian.vcache.internal.core.DefaultRequestContext
 *  com.google.common.annotations.VisibleForTesting
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.vcache;

import com.atlassian.confluence.impl.vcache.NoOpRequestContext;
import com.atlassian.confluence.vcache.VCacheRequestContextOperations;
import com.atlassian.vcache.internal.NameValidator;
import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.core.DefaultRequestContext;
import com.google.common.annotations.VisibleForTesting;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VCacheRequestContextManager
implements VCacheRequestContextOperations {
    private static final Logger log = LoggerFactory.getLogger(VCacheRequestContextManager.class);
    private final ThreadLocal<RequestContext> threadRequestContexts = new ThreadLocal();
    private final Supplier<String> defaultPartitionIdentifier;
    private final Object cleanupCallbackKey = new Object();

    public VCacheRequestContextManager(String defaultPartitionIdentifier) {
        NameValidator.requireValidPartitionIdentifier((String)defaultPartitionIdentifier);
        this.defaultPartitionIdentifier = () -> defaultPartitionIdentifier;
    }

    @NonNull RequestContext getCurrentRequestContext() {
        RequestContext current = this.threadRequestContexts.get();
        if (current == null) {
            log.trace("VCache request context requested for uninitialized thread; returning fallback context; Transactional cache access will not work on this thread.");
            return new NoOpRequestContext(this.defaultPartitionIdentifier);
        }
        return current;
    }

    @Override
    public <T, X extends Throwable> T doInRequestContext(VCacheRequestContextOperations.Action<T, X> action) throws X {
        return this.doInRequestContextInternal(action);
    }

    @Override
    @Deprecated
    public <T, X extends Throwable> T doInRequestContext(String partitionIdentifer, VCacheRequestContextOperations.Action<T, X> action) throws X {
        return this.doInRequestContextInternal(action);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <T, X extends Throwable> T doInRequestContextInternal(VCacheRequestContextOperations.Action<T, X> action) throws X {
        Runnable cleanup = this.initRequestContext();
        try {
            T t = action.perform();
            return t;
        }
        finally {
            cleanup.run();
        }
    }

    @VisibleForTesting
    public @NonNull Runnable initRequestContext() {
        boolean originator;
        RequestContext existingContext = this.threadRequestContexts.get();
        boolean bl = originator = existingContext == null;
        if (originator) {
            log.trace("Setting new thread-local request context");
            this.threadRequestContexts.set((RequestContext)new DefaultRequestContext(this.defaultPartitionIdentifier));
            return () -> {
                this.invokeCleanupCallback(this.threadRequestContexts.get());
                this.threadRequestContexts.remove();
                log.trace("Removed thread-local request context");
            };
        }
        log.trace("Thread-local request context already present, skipping new context creation");
        return () -> log.trace("Skipped removal of thread-local request context");
    }

    RequestContext getCurrentRequestContext(Consumer<RequestContext> cleanupCallback) {
        RequestContext requestContext = this.getCurrentRequestContext();
        requestContext.computeIfAbsent(this.cleanupCallbackKey, () -> cleanupCallback);
        return requestContext;
    }

    private void invokeCleanupCallback(RequestContext requestContext) {
        requestContext.get(this.cleanupCallbackKey).ifPresent(cleanupCallback -> cleanupCallback.accept(requestContext));
    }
}

