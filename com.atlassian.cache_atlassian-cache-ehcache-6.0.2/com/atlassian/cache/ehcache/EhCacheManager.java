/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CacheSettingsDefaultsProvider
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.cache.Supplier
 *  com.atlassian.cache.impl.AbstractCacheManager
 *  com.atlassian.cache.impl.ReferenceKey
 *  com.atlassian.cache.impl.StrongSupplier
 *  com.atlassian.cache.impl.WeakSupplier
 *  com.atlassian.cache.impl.jmx.MBeanRegistrar
 *  com.atlassian.cache.impl.metrics.CacheManagerMetricEmitter
 *  com.atlassian.cache.impl.metrics.InstrumentedCache
 *  com.atlassian.cache.impl.metrics.InstrumentedCachedReference
 *  io.atlassian.util.concurrent.ManagedLock
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.sf.ehcache.CacheManager
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.management.ManagementService
 */
package com.atlassian.cache.ehcache;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CacheSettingsDefaultsProvider;
import com.atlassian.cache.CachedReference;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.Supplier;
import com.atlassian.cache.ehcache.DelegatingCache;
import com.atlassian.cache.ehcache.DelegatingCachedReference;
import com.atlassian.cache.ehcache.EhCacheHelper;
import com.atlassian.cache.ehcache.LoadingCache;
import com.atlassian.cache.ehcache.SynchronizedLoadingCacheDecorator;
import com.atlassian.cache.ehcache.replication.EhCacheReplicatorConfigFactory;
import com.atlassian.cache.ehcache.replication.rmi.RMICacheReplicatorConfigFactory;
import com.atlassian.cache.ehcache.wrapper.NoopValueProcessor;
import com.atlassian.cache.ehcache.wrapper.ValueProcessor;
import com.atlassian.cache.ehcache.wrapper.ValueProcessorAtlassianCacheLoaderDecorator;
import com.atlassian.cache.impl.AbstractCacheManager;
import com.atlassian.cache.impl.ReferenceKey;
import com.atlassian.cache.impl.StrongSupplier;
import com.atlassian.cache.impl.WeakSupplier;
import com.atlassian.cache.impl.jmx.MBeanRegistrar;
import com.atlassian.cache.impl.metrics.CacheManagerMetricEmitter;
import com.atlassian.cache.impl.metrics.InstrumentedCache;
import com.atlassian.cache.impl.metrics.InstrumentedCachedReference;
import io.atlassian.util.concurrent.ManagedLock;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.management.MBeanServer;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.management.ManagementService;

@ParametersAreNonnullByDefault
public class EhCacheManager
extends AbstractCacheManager
implements MBeanRegistrar {
    private final CacheManager delegate;
    @Nullable
    private final EhCacheReplicatorConfigFactory replicatorConfigFactory;
    private final ValueProcessor valueProcessor;
    private boolean statisticsEnabled = true;

    @Deprecated
    public EhCacheManager() {
        this(CacheManager.create(), null);
    }

    @Deprecated
    public EhCacheManager(CacheManager delegate, @Nullable CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider) {
        this(delegate, new RMICacheReplicatorConfigFactory(), cacheSettingsDefaultsProvider, null);
    }

    public EhCacheManager(CacheManager delegate, @Nullable EhCacheReplicatorConfigFactory replicatorConfigFactory, @Nullable CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider) {
        this(delegate, replicatorConfigFactory, cacheSettingsDefaultsProvider, null);
    }

    public EhCacheManager(CacheManager delegate, @Nullable EhCacheReplicatorConfigFactory replicatorConfigFactory, @Nullable CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider, @Nullable ValueProcessor valueProcessor) {
        this(delegate, replicatorConfigFactory, cacheSettingsDefaultsProvider, valueProcessor, new CacheManagerMetricEmitter());
    }

    @VisibleForTesting
    EhCacheManager(CacheManager delegate, @Nullable EhCacheReplicatorConfigFactory replicatorConfigFactory, @Nullable CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider, @Nullable ValueProcessor valueProcessor, @Nonnull CacheManagerMetricEmitter metricEmitter) {
        super(cacheSettingsDefaultsProvider, metricEmitter);
        this.delegate = delegate;
        this.replicatorConfigFactory = replicatorConfigFactory;
        this.valueProcessor = valueProcessor != null ? valueProcessor : new NoopValueProcessor();
    }

    CacheManager getEh() {
        return this.delegate;
    }

    public boolean isStatisticsEnabled() {
        return this.statisticsEnabled;
    }

    public void setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
    }

    @Nonnull
    public <V> CachedReference<V> getCachedReference(@Nonnull String name, @Nonnull Supplier<V> supplier, @Nonnull CacheSettings settings) {
        CacheSettings overridenSettings = settings.override(new CacheSettingsBuilder().flushable().maxEntries(1).build());
        return (CachedReference)((ManagedLock)this.cacheCreationLocks.apply(name)).withLock(() -> {
            Ehcache spCache = this.getLoadingCache(name, overridenSettings, new ValueProcessorAtlassianCacheLoaderDecorator(new SupplierAdapter(supplier), this.valueProcessor));
            InstrumentedCachedReference cache = InstrumentedCachedReference.wrap(DelegatingCachedReference.create(spCache, overridenSettings, this.valueProcessor));
            this.caches.put(name, new WeakSupplier((Object)cache));
            return cache;
        });
    }

    protected ManagedCache createSimpleCache(@Nonnull String name, @Nonnull CacheSettings settings) {
        ManagedCache cache;
        java.util.function.Supplier cacheSupplier = (java.util.function.Supplier)this.caches.get(name);
        if (cacheSupplier != null && (cache = (ManagedCache)cacheSupplier.get()) != null) {
            return cache;
        }
        return this.createManagedCacheInternal(name, settings);
    }

    private ManagedCache createManagedCacheInternal(@Nonnull String name, @Nonnull CacheSettings settings) {
        return (ManagedCache)((ManagedLock)this.cacheCreationLocks.apply(name)).withLock(() -> {
            ManagedCache result;
            ManagedCache managedCache = result = this.caches.get(name) == null ? null : (ManagedCache)((java.util.function.Supplier)this.caches.get(name)).get();
            if (result == null) {
                Ehcache simpleCache = this.createCache(name, settings, false);
                InstrumentedCache cache = InstrumentedCache.wrap(DelegatingCache.create(simpleCache, settings, this.valueProcessor));
                this.caches.put(name, new StrongSupplier((Object)cache));
                return cache;
            }
            return result;
        });
    }

    protected <K, V> ManagedCache createComputingCache(@Nonnull String name, @Nonnull CacheSettings settings, CacheLoader<K, V> loader) {
        return (ManagedCache)((ManagedLock)this.cacheCreationLocks.apply(name)).withLock(() -> {
            Ehcache spCache = this.getLoadingCache(name, settings, new ValueProcessorAtlassianCacheLoaderDecorator(loader, this.valueProcessor));
            InstrumentedCache cache = InstrumentedCache.wrap(DelegatingCache.create(spCache, settings, this.valueProcessor));
            this.caches.put(name, new WeakSupplier((Object)cache));
            return cache;
        });
    }

    private <K, V> Ehcache getLoadingCache(@Nonnull String name, @Nonnull CacheSettings settings, CacheLoader<K, V> loader) {
        SynchronizedLoadingCacheDecorator decorator;
        Ehcache ehcache = this.getCleanCache(name, settings);
        if (ehcache instanceof SynchronizedLoadingCacheDecorator) {
            decorator = (SynchronizedLoadingCacheDecorator)ehcache;
        } else {
            decorator = new SynchronizedLoadingCacheDecorator(ehcache);
            this.delegate.replaceCacheWithDecoratedCache(ehcache, (Ehcache)decorator);
        }
        return new LoadingCache<K, V>(decorator, loader);
    }

    private Ehcache getCleanCache(String name, CacheSettings settings) {
        Ehcache ehCache = this.delegate.getEhcache(name);
        if (ehCache != null) {
            ehCache.removeAll(true);
        } else {
            ehCache = this.createCache(name, settings, true);
        }
        return ehCache;
    }

    private Ehcache createCache(String name, CacheSettings settings, boolean selfLoading) {
        return new EhCacheHelper(this.replicatorConfigFactory).getEhcache(name, this.delegate, settings, selfLoading, this.statisticsEnabled);
    }

    public void shutdown() {
        this.delegate.shutdown();
    }

    public void registerMBeans(@Nullable MBeanServer mBeanServer) {
        if (mBeanServer != null) {
            ManagementService.registerMBeans((CacheManager)this.delegate, (MBeanServer)mBeanServer, (boolean)true, (boolean)true, (boolean)true, (boolean)true);
        }
    }

    public void unregisterMBeans(@Nullable MBeanServer mBeanServer) {
        if (mBeanServer != null) {
            ManagementService managementService = new ManagementService(this.delegate, mBeanServer, true, true, true, true);
            managementService.dispose();
        }
    }

    static class SupplierAdapter<V>
    implements CacheLoader<ReferenceKey, V> {
        private final Supplier<V> supplier;

        SupplierAdapter(Supplier<V> supplier) {
            this.supplier = supplier;
        }

        @Nonnull
        public V load(@Nonnull ReferenceKey key) {
            return (V)this.supplier.get();
        }
    }
}

