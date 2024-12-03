/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.marshalling.api.MarshallingPair
 *  com.atlassian.vcache.DirectExternalCache
 *  com.atlassian.vcache.ExternalCacheSettings
 *  com.atlassian.vcache.JvmCache
 *  com.atlassian.vcache.JvmCacheSettings
 *  com.atlassian.vcache.StableReadExternalCache
 *  com.atlassian.vcache.TransactionalExternalCache
 *  com.atlassian.vcache.internal.BegunTransactionalActivityHandler
 *  com.atlassian.vcache.internal.RequestContext
 *  com.atlassian.vcache.internal.VCacheCreationHandler
 *  com.atlassian.vcache.internal.VCacheSettingsDefaultsProvider
 *  com.atlassian.vcache.internal.core.ExternalCacheKeyGenerator
 *  com.atlassian.vcache.internal.core.Sha1ExternalCacheKeyGenerator
 *  com.atlassian.vcache.internal.core.cas.IdentifiedData
 *  com.atlassian.vcache.internal.core.metrics.MetricsCollector
 *  com.atlassian.vcache.internal.core.metrics.MetricsRecorder
 *  com.atlassian.vcache.internal.core.service.AbstractVCacheService
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.vcache.internal.legacy;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.marshalling.api.MarshallingPair;
import com.atlassian.vcache.DirectExternalCache;
import com.atlassian.vcache.ExternalCacheSettings;
import com.atlassian.vcache.JvmCache;
import com.atlassian.vcache.JvmCacheSettings;
import com.atlassian.vcache.StableReadExternalCache;
import com.atlassian.vcache.TransactionalExternalCache;
import com.atlassian.vcache.internal.BegunTransactionalActivityHandler;
import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.VCacheCreationHandler;
import com.atlassian.vcache.internal.VCacheSettingsDefaultsProvider;
import com.atlassian.vcache.internal.core.ExternalCacheKeyGenerator;
import com.atlassian.vcache.internal.core.Sha1ExternalCacheKeyGenerator;
import com.atlassian.vcache.internal.core.cas.IdentifiedData;
import com.atlassian.vcache.internal.core.metrics.MetricsCollector;
import com.atlassian.vcache.internal.core.metrics.MetricsRecorder;
import com.atlassian.vcache.internal.core.service.AbstractVCacheService;
import com.atlassian.vcache.internal.legacy.LegacyDirectExternalCache;
import com.atlassian.vcache.internal.legacy.LegacyJvmCache;
import com.atlassian.vcache.internal.legacy.LegacyServiceSettings;
import com.atlassian.vcache.internal.legacy.LegacyStableReadExternalCache;
import com.atlassian.vcache.internal.legacy.LegacyTransactionalExternalCache;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LegacyVCacheService
extends AbstractVCacheService {
    private static final Logger log = LoggerFactory.getLogger(LegacyVCacheService.class);
    private final Supplier<CacheFactory> cacheFactorySupplier;
    private final LegacyServiceSettings serviceSettings;

    public LegacyVCacheService(String productIdentifier, Supplier<RequestContext> threadLocalContextSupplier, Supplier<RequestContext> workContextContextSupplier, VCacheSettingsDefaultsProvider defaultsProvider, VCacheCreationHandler creationHandler, MetricsCollector metricsCollector, Supplier<CacheFactory> cacheFactorySupplier, LegacyServiceSettings serviceSettings, BegunTransactionalActivityHandler begunTransactionalActivityHandler) {
        super(threadLocalContextSupplier, workContextContextSupplier, defaultsProvider, creationHandler, metricsCollector, (ExternalCacheKeyGenerator)new Sha1ExternalCacheKeyGenerator(productIdentifier), begunTransactionalActivityHandler, serviceSettings.getLockTimeout());
        this.cacheFactorySupplier = Objects.requireNonNull(cacheFactorySupplier);
        this.serviceSettings = Objects.requireNonNull(serviceSettings);
    }

    public LegacyVCacheService(Supplier<RequestContext> threadLocalContextSupplier, Supplier<RequestContext> workContextContextSupplier, VCacheSettingsDefaultsProvider defaultsProvider, VCacheCreationHandler creationHandler, MetricsCollector metricsCollector, ExternalCacheKeyGenerator externalCacheKeyGenerator, Supplier<CacheFactory> cacheFactorySupplier, LegacyServiceSettings serviceSettings, BegunTransactionalActivityHandler begunTransactionalActivityHandler) {
        super(threadLocalContextSupplier, workContextContextSupplier, defaultsProvider, creationHandler, metricsCollector, externalCacheKeyGenerator, begunTransactionalActivityHandler, serviceSettings.getLockTimeout());
        this.cacheFactorySupplier = Objects.requireNonNull(cacheFactorySupplier);
        this.serviceSettings = Objects.requireNonNull(serviceSettings);
    }

    protected Logger log() {
        return log;
    }

    protected <K, V> JvmCache<K, V> createJvmCache(String name, JvmCacheSettings settings) {
        CacheSettings legacySettings = new CacheSettingsBuilder().local().maxEntries(((Integer)settings.getMaxEntries().get()).intValue()).expireAfterWrite(((Duration)settings.getDefaultTtl().get()).toNanos(), TimeUnit.NANOSECONDS).build();
        return new LegacyJvmCache(this.cacheFactorySupplier.get().getCache(name, null, legacySettings), this.serviceSettings.getLockTimeout());
    }

    protected <V> TransactionalExternalCache<V> createTransactionalExternalCache(String name, ExternalCacheSettings settings, MarshallingPair<V> valueMarshalling, boolean valueSerializable) {
        CacheSettings legacySettings = this.buildLegacySettings(settings);
        return new LegacyTransactionalExternalCache<V>((Cache<String, IdentifiedData>)this.cacheFactorySupplier.get().getCache(name, null, legacySettings), this.threadLocalContextSupplier, this.externalCacheKeyGenerator, this.serviceSettings.isSerializationHack() && valueSerializable ? Optional.empty() : Optional.of(valueMarshalling), this.transactionControlManager, this.serviceSettings, (MetricsRecorder)this.metricsCollector);
    }

    protected <V> StableReadExternalCache<V> createStableReadExternalCache(String name, ExternalCacheSettings settings, MarshallingPair<V> valueMarshalling, boolean valueSerializable) {
        CacheSettings legacySettings = this.buildLegacySettings(settings);
        return new LegacyStableReadExternalCache<V>((Cache<String, IdentifiedData>)this.cacheFactorySupplier.get().getCache(name, null, legacySettings), this.workContextContextSupplier, this.externalCacheKeyGenerator, this.serviceSettings.isSerializationHack() && valueSerializable ? Optional.empty() : Optional.of(valueMarshalling), this.serviceSettings, (MetricsRecorder)this.metricsCollector);
    }

    protected <V> DirectExternalCache<V> createDirectExternalCache(String name, ExternalCacheSettings settings, MarshallingPair<V> valueMarshalling, boolean valueSerializable) {
        CacheSettings legacySettings = this.buildLegacySettings(settings);
        return new LegacyDirectExternalCache<V>((Cache<String, IdentifiedData>)this.cacheFactorySupplier.get().getCache(name, null, legacySettings), this.workContextContextSupplier, this.externalCacheKeyGenerator, this.serviceSettings.isSerializationHack() && valueSerializable ? Optional.empty() : Optional.of(valueMarshalling), this.serviceSettings);
    }

    private CacheSettings buildLegacySettings(ExternalCacheSettings settings) {
        return new CacheSettingsBuilder().remote().maxEntries(((Integer)settings.getEntryCountHint().get()).intValue()).expireAfterWrite(((Duration)settings.getDefaultTtl().get()).toNanos(), TimeUnit.NANOSECONDS).build();
    }
}

