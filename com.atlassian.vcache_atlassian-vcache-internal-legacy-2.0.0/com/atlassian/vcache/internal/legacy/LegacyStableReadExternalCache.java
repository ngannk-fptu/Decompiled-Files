/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.marshalling.api.MarshallingPair
 *  com.atlassian.vcache.ExternalCacheException
 *  com.atlassian.vcache.PutPolicy
 *  com.atlassian.vcache.internal.MetricLabel
 *  com.atlassian.vcache.internal.RequestContext
 *  com.atlassian.vcache.internal.core.ExternalCacheKeyGenerator
 *  com.atlassian.vcache.internal.core.cas.IdentifiedData
 *  com.atlassian.vcache.internal.core.cas.IdentifiedUtils
 *  com.atlassian.vcache.internal.core.metrics.CacheType
 *  com.atlassian.vcache.internal.core.metrics.MetricsRecorder
 *  com.atlassian.vcache.internal.core.service.AbstractExternalCacheRequestContext
 *  com.atlassian.vcache.internal.core.service.AbstractStableReadExternalCache
 *  com.atlassian.vcache.internal.core.service.UnversionedExternalCacheRequestContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.vcache.internal.legacy;

import com.atlassian.cache.Cache;
import com.atlassian.marshalling.api.MarshallingPair;
import com.atlassian.vcache.ExternalCacheException;
import com.atlassian.vcache.PutPolicy;
import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.core.ExternalCacheKeyGenerator;
import com.atlassian.vcache.internal.core.cas.IdentifiedData;
import com.atlassian.vcache.internal.core.cas.IdentifiedUtils;
import com.atlassian.vcache.internal.core.metrics.CacheType;
import com.atlassian.vcache.internal.core.metrics.MetricsRecorder;
import com.atlassian.vcache.internal.core.service.AbstractExternalCacheRequestContext;
import com.atlassian.vcache.internal.core.service.AbstractStableReadExternalCache;
import com.atlassian.vcache.internal.core.service.UnversionedExternalCacheRequestContext;
import com.atlassian.vcache.internal.legacy.LegacyServiceSettings;
import com.atlassian.vcache.internal.legacy.LegacyUtils;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LegacyStableReadExternalCache<V>
extends AbstractStableReadExternalCache<V> {
    private static final Logger log = LoggerFactory.getLogger(LegacyStableReadExternalCache.class);
    private final Cache<String, IdentifiedData> delegate;
    private final Supplier<RequestContext> contextSupplier;
    private final ExternalCacheKeyGenerator keyGenerator;
    private final Optional<MarshallingPair<V>> valueMarshalling;
    private final LegacyServiceSettings serviceSettings;

    LegacyStableReadExternalCache(Cache<String, IdentifiedData> delegate, Supplier<RequestContext> contextSupplier, ExternalCacheKeyGenerator keyGenerator, Optional<MarshallingPair<V>> valueMarshalling, LegacyServiceSettings serviceSettings, MetricsRecorder metricsRecorder) {
        super(delegate.getName(), metricsRecorder, serviceSettings.getLockTimeout(), (n, ex) -> {});
        this.delegate = Objects.requireNonNull(delegate);
        this.contextSupplier = Objects.requireNonNull(contextSupplier);
        this.keyGenerator = Objects.requireNonNull(keyGenerator);
        this.valueMarshalling = Objects.requireNonNull(valueMarshalling);
        this.serviceSettings = Objects.requireNonNull(serviceSettings);
    }

    public boolean internalPut(String internalKey, V value, PutPolicy policy) {
        String externalKey = this.ensureCacheContext().externalEntryKeyFor(internalKey);
        IdentifiedData identifiedData = IdentifiedUtils.marshall(value, this.valueMarshalling);
        return LegacyUtils.directPut(externalKey, identifiedData, policy, this.delegate, this.serviceSettings.isAvoidCasOps());
    }

    protected void internalRemove(Iterable<String> internalKeys) {
        AbstractExternalCacheRequestContext<V> cacheContext = this.ensureCacheContext();
        for (String key : internalKeys) {
            this.delegate.remove((Object)cacheContext.externalEntryKeyFor(key));
            cacheContext.recordValue(key, Optional.empty());
        }
    }

    protected void internalRemoveAll() {
        this.delegate.removeAll();
    }

    protected Logger getLogger() {
        return log;
    }

    protected AbstractExternalCacheRequestContext<V> ensureCacheContext() {
        RequestContext requestContext = this.contextSupplier.get();
        return (AbstractExternalCacheRequestContext)requestContext.computeIfAbsent((Object)this, () -> {
            log.trace("Cache {}: Setting up a new context", (Object)this.delegate.getName());
            return new UnversionedExternalCacheRequestContext(this.keyGenerator, this.delegate.getName(), () -> ((RequestContext)requestContext).partitionIdentifier(), this.lockTimeout);
        });
    }

    protected V handleCreation(String internalKey, V candidateValue) throws ExecutionException, InterruptedException {
        AbstractExternalCacheRequestContext<V> cacheContext = this.ensureCacheContext();
        IdentifiedData candidateIdentifiedData = IdentifiedUtils.marshall(candidateValue, this.valueMarshalling);
        String externalKey = cacheContext.externalEntryKeyFor(internalKey);
        this.metricsRecorder.record(this.name, CacheType.EXTERNAL, MetricLabel.NUMBER_OF_REMOTE_GET, 1L);
        if (this.serviceSettings.isAvoidCasOps()) {
            this.delegate.put((Object)externalKey, (Object)candidateIdentifiedData);
        } else {
            Optional otherAddedValue = IdentifiedUtils.unmarshall((IdentifiedData)((IdentifiedData)this.delegate.putIfAbsent((Object)externalKey, (Object)candidateIdentifiedData)), this.valueMarshalling);
            if (otherAddedValue.isPresent()) {
                this.getLogger().info("Cache {}, unable to add candidate for key {}, use what was added", (Object)this.name, (Object)internalKey);
                this.metricsRecorder.record(this.name, CacheType.EXTERNAL, MetricLabel.NUMBER_OF_REMOTE_GET, 1L);
                return (V)otherAddedValue.get();
            }
        }
        return candidateValue;
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

