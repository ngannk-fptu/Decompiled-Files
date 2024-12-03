/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheException
 *  com.atlassian.marshalling.api.MarshallingPair
 *  com.atlassian.vcache.ExternalCacheException
 *  com.atlassian.vcache.internal.RequestContext
 *  com.atlassian.vcache.internal.core.ExternalCacheKeyGenerator
 *  com.atlassian.vcache.internal.core.TransactionControl
 *  com.atlassian.vcache.internal.core.TransactionControlManager
 *  com.atlassian.vcache.internal.core.cas.IdentifiedData
 *  com.atlassian.vcache.internal.core.cas.IdentifiedUtils
 *  com.atlassian.vcache.internal.core.metrics.MetricsRecorder
 *  com.atlassian.vcache.internal.core.service.AbstractExternalCacheRequestContext
 *  com.atlassian.vcache.internal.core.service.AbstractExternalCacheRequestContext$DeferredOperation
 *  com.atlassian.vcache.internal.core.service.AbstractTransactionalExternalCache
 *  com.atlassian.vcache.internal.core.service.UnversionedExternalCacheRequestContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.vcache.internal.legacy;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheException;
import com.atlassian.marshalling.api.MarshallingPair;
import com.atlassian.vcache.ExternalCacheException;
import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.core.ExternalCacheKeyGenerator;
import com.atlassian.vcache.internal.core.TransactionControl;
import com.atlassian.vcache.internal.core.TransactionControlManager;
import com.atlassian.vcache.internal.core.cas.IdentifiedData;
import com.atlassian.vcache.internal.core.cas.IdentifiedUtils;
import com.atlassian.vcache.internal.core.metrics.MetricsRecorder;
import com.atlassian.vcache.internal.core.service.AbstractExternalCacheRequestContext;
import com.atlassian.vcache.internal.core.service.AbstractTransactionalExternalCache;
import com.atlassian.vcache.internal.core.service.UnversionedExternalCacheRequestContext;
import com.atlassian.vcache.internal.legacy.LegacyServiceSettings;
import com.atlassian.vcache.internal.legacy.LegacyUtils;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LegacyTransactionalExternalCache<V>
extends AbstractTransactionalExternalCache<V> {
    private static final Logger log = LoggerFactory.getLogger(LegacyTransactionalExternalCache.class);
    private final Cache<String, IdentifiedData> delegate;
    private final ExternalCacheKeyGenerator keyGenerator;
    private final Optional<MarshallingPair<V>> valueMarshalling;
    private final TransactionControlManager transactionControlManager;
    private final LegacyServiceSettings serviceSettings;

    LegacyTransactionalExternalCache(Cache<String, IdentifiedData> delegate, Supplier<RequestContext> contextSupplier, ExternalCacheKeyGenerator keyGenerator, Optional<MarshallingPair<V>> valueMarshalling, TransactionControlManager transactionControlManager, LegacyServiceSettings serviceSettings, MetricsRecorder metricsRecorder) {
        super(delegate.getName(), contextSupplier, metricsRecorder, serviceSettings.getLockTimeout(), (n, e) -> {});
        this.delegate = Objects.requireNonNull(delegate);
        this.keyGenerator = Objects.requireNonNull(keyGenerator);
        this.valueMarshalling = Objects.requireNonNull(valueMarshalling);
        this.transactionControlManager = Objects.requireNonNull(transactionControlManager);
        this.serviceSettings = Objects.requireNonNull(serviceSettings);
    }

    public void transactionSync() {
        log.trace("Cache {}: synchronising operations", (Object)this.name);
        AbstractExternalCacheRequestContext<V> cacheContext = this.ensureCacheContext();
        if (cacheContext.hasRemoveAll()) {
            this.delegate.removeAll();
        }
        this.performKeyedOperations(cacheContext);
        cacheContext.forgetAll();
    }

    private void performKeyedOperations(AbstractExternalCacheRequestContext<V> cacheContext) {
        try {
            for (Map.Entry entry : cacheContext.getKeyedOperations()) {
                String externalKey = cacheContext.externalEntryKeyFor((String)entry.getKey());
                if (((AbstractExternalCacheRequestContext.DeferredOperation)entry.getValue()).isRemove()) {
                    log.trace("Cache {}: performing remove on entry {}", (Object)this.name, entry.getKey());
                    this.delegate.remove((Object)externalKey);
                    continue;
                }
                log.trace("Cache {}: performing {} on entry {}", new Object[]{this.name, ((AbstractExternalCacheRequestContext.DeferredOperation)entry.getValue()).getPolicy(), entry.getKey()});
                IdentifiedData identifiedData = IdentifiedUtils.marshall((Object)((AbstractExternalCacheRequestContext.DeferredOperation)entry.getValue()).getValue(), this.valueMarshalling);
                boolean putOutcome = LegacyUtils.directPut(externalKey, identifiedData, ((AbstractExternalCacheRequestContext.DeferredOperation)entry.getValue()).getPolicy(), this.delegate, this.serviceSettings.isAvoidCasOps());
                if (putOutcome) continue;
                log.debug("Cache {}: Unable to perform put() operation {} on entry {}, clearing cache", new Object[]{this.name, ((AbstractExternalCacheRequestContext.DeferredOperation)entry.getValue()).getPolicy(), entry.getKey()});
                this.delegate.removeAll();
                break;
            }
        }
        catch (CacheException | ExternalCacheException bugger) {
            log.error("Cache {}: an operation failed in transaction sync, so clearing the cache", (Object)this.name, (Object)bugger);
            this.delegate.removeAll();
        }
    }

    protected AbstractExternalCacheRequestContext<V> ensureCacheContext() {
        RequestContext requestContext = (RequestContext)this.contextSupplier.get();
        this.transactionControlManager.registerTransactionalExternalCache(requestContext, this.name, (TransactionControl)this);
        return (AbstractExternalCacheRequestContext)requestContext.computeIfAbsent((Object)this, () -> {
            log.trace("Cache {}: Setting up a new context", (Object)this.name);
            return new UnversionedExternalCacheRequestContext(this.keyGenerator, this.delegate.getName(), () -> ((RequestContext)requestContext).partitionIdentifier(), this.serviceSettings.getLockTimeout());
        });
    }

    protected Logger getLogger() {
        return log;
    }

    protected final ExternalCacheException mapException(Exception ex) {
        return LegacyUtils.mapException(ex);
    }

    protected final Optional<V> directGet(String externalKey) {
        return IdentifiedUtils.unmarshall((IdentifiedData)((IdentifiedData)this.delegate.get((Object)externalKey)), this.valueMarshalling);
    }

    protected final Map<String, Optional<V>> directGetBulk(Set<String> externalKeys) {
        return LegacyUtils.directGetBulk(externalKeys, this.delegate, this.valueMarshalling);
    }
}

