/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.marshalling.api.MarshallingException
 *  com.atlassian.vcache.ExternalCache
 *  com.atlassian.vcache.ExternalCacheException
 *  com.atlassian.vcache.ExternalCacheException$Reason
 *  com.atlassian.vcache.internal.ExternalCacheExceptionListener
 *  com.atlassian.vcache.internal.NameValidator
 *  org.slf4j.Logger
 */
package com.atlassian.vcache.internal.core.service;

import com.atlassian.marshalling.api.MarshallingException;
import com.atlassian.vcache.ExternalCache;
import com.atlassian.vcache.ExternalCacheException;
import com.atlassian.vcache.internal.ExternalCacheExceptionListener;
import com.atlassian.vcache.internal.NameValidator;
import com.atlassian.vcache.internal.core.VCacheCoreUtils;
import com.atlassian.vcache.internal.core.service.AbstractExternalCacheRequestContext;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import org.slf4j.Logger;

public abstract class AbstractExternalCache<V>
implements ExternalCache<V> {
    protected final String name;
    protected final Duration lockTimeout;
    private ExternalCacheExceptionListener externalCacheExceptionListener;

    protected AbstractExternalCache(String name, Duration lockTimeout, ExternalCacheExceptionListener externalCacheExceptionListener) {
        this.name = NameValidator.requireValidCacheName((String)name);
        this.lockTimeout = Objects.requireNonNull(lockTimeout);
        this.externalCacheExceptionListener = Objects.requireNonNull(externalCacheExceptionListener);
    }

    protected abstract AbstractExternalCacheRequestContext<V> ensureCacheContext();

    protected abstract Logger getLogger();

    protected abstract ExternalCacheException mapException(Exception var1);

    public final String getName() {
        return this.name;
    }

    protected <T> CompletionStage<T> perform(Callable<T> txn) {
        return this.perform(txn, i -> {});
    }

    protected <T> CompletionStage<T> perform(Callable<T> txn, Consumer<T> successHandler) {
        ExternalCacheException exception;
        try {
            T outcome = txn.call();
            successHandler.accept(outcome);
            return VCacheCoreUtils.successful(outcome);
        }
        catch (MarshallingException ex) {
            exception = new ExternalCacheException(ExternalCacheException.Reason.MARSHALLER_FAILURE, (Throwable)ex);
        }
        catch (InterruptedException | ExecutionException ex) {
            exception = new ExternalCacheException(ExternalCacheException.Reason.UNCLASSIFIED_FAILURE, (Throwable)ex);
        }
        catch (ExternalCacheException ece) {
            exception = ece;
        }
        catch (Exception ex) {
            exception = this.mapException(ex);
        }
        this.externalCacheExceptionListener.onThrow(this.getName(), exception);
        return VCacheCoreUtils.failed(new CompletableFuture(), exception);
    }
}

