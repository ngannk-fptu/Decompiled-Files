/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheEntryListener
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.cache.CachedReferenceListener
 *  com.atlassian.cache.Supplier
 *  com.atlassian.util.concurrent.LazyReference
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationAdapter
 */
package com.atlassian.confluence.cache;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheEntryListener;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CachedReference;
import com.atlassian.cache.CachedReferenceListener;
import com.atlassian.cache.Supplier;
import com.atlassian.confluence.cache.CacheMonitoringUtils;
import com.atlassian.confluence.cache.Deferred;
import com.atlassian.confluence.cache.DeferredCachedReference;
import com.atlassian.confluence.cache.DeferredOperationsCache;
import com.atlassian.confluence.concurrent.LockFactory;
import com.atlassian.confluence.concurrent.ResettableThreadLocal;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCache;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCacheFactory;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoring;
import com.atlassian.confluence.util.profiling.Split;
import com.atlassian.util.concurrent.LazyReference;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

public class TransactionalCacheFactory
implements CacheFactory,
TransactionAwareCacheFactory {
    private static final Logger log = LoggerFactory.getLogger(TransactionalCacheFactory.class);
    private static final boolean STACK_TRACE_ON_NO_TX = Boolean.getBoolean("TransactionalCacheFactory.stackTraceOnNoTx");
    private static final boolean DEV_MODE = ConfluenceSystemProperties.isDevMode();
    private final ResettableThreadLocal<DelegateCacheSynchronization> synchronizationThreadLocal = new ResettableThreadLocal<DelegateCacheSynchronization>(){

        @Override
        protected DelegateCacheSynchronization initialValue() {
            return new DelegateCacheSynchronization();
        }
    };
    private final CacheFactory cacheFactory;
    private final SynchronizationManager synchronizationManager;
    private final ConfluenceMonitoring confluenceMonitoring;

    public TransactionalCacheFactory(CacheFactory cacheFactory, SynchronizationManager synchronizationManager, ConfluenceMonitoring confluenceMonitoring) {
        this.cacheFactory = Objects.requireNonNull(cacheFactory);
        this.synchronizationManager = Objects.requireNonNull(synchronizationManager);
        this.confluenceMonitoring = Objects.requireNonNull(confluenceMonitoring);
    }

    @Deprecated(since="8.0", forRemoval=true)
    public TransactionalCacheFactory(CacheFactory cacheFactory, LockFactory lockFactory, SynchronizationManager synchronizationManager, ConfluenceMonitoring confluenceMonitoring) {
        this.cacheFactory = (CacheFactory)Preconditions.checkNotNull((Object)cacheFactory);
        this.synchronizationManager = (SynchronizationManager)Preconditions.checkNotNull((Object)synchronizationManager);
        this.confluenceMonitoring = (ConfluenceMonitoring)Preconditions.checkNotNull((Object)confluenceMonitoring);
    }

    @Deprecated
    public <K, V> @NonNull Cache<K, V> getCache(@NonNull String cacheName, CacheLoader<K, V> loader, @NonNull CacheSettings required) {
        return new TransactionalCache<K, V>(cacheName, loader, required);
    }

    private void bindCacheSynchronizationIfNecessary() {
        if (!this.getTransactionCacheMap().isEmpty() || !this.getTransactionalCachedReferenceMap().isEmpty()) {
            return;
        }
        log.trace("Registering transactional synchronization for thread: {}", (Object)Thread.currentThread().getName());
        this.synchronizationManager.registerSynchronization((TransactionSynchronization)this.synchronizationThreadLocal.get());
    }

    private <K, V> Cache<K, V> getTransactionalCache(String cacheName, CacheLoader<K, V> loader, CacheSettings required) {
        DeferredOperationsCache<K, V> transactionalCache;
        if (this.getTransactionCacheMap().get(cacheName) != null) {
            log.trace("Pre-bound transactional cache found for cache name: {}", (Object)cacheName);
            transactionalCache = this.getTransactionCacheMap().get(cacheName);
        } else {
            log.trace("Binding new transactional cache to thread local: {}", (Object)cacheName);
            Cache delegate = this.cacheFactory.getCache(cacheName, null, required);
            transactionalCache = DeferredOperationsCache.create(delegate, loader);
            this.bindCacheSynchronizationIfNecessary();
            this.getTransactionCacheMap().put(cacheName, transactionalCache);
        }
        return transactionalCache;
    }

    private Map<String, DeferredOperationsCache> getTransactionCacheMap() {
        DelegateCacheSynchronization cacheSynchronization = (DelegateCacheSynchronization)((Object)this.synchronizationThreadLocal.get());
        return cacheSynchronization.getCacheMap();
    }

    private Map<String, DeferredCachedReference> getTransactionalCachedReferenceMap() {
        DelegateCacheSynchronization cacheSynchronization = (DelegateCacheSynchronization)((Object)this.synchronizationThreadLocal.get());
        return cacheSynchronization.getReferenceMap();
    }

    @VisibleForTesting
    public void clearCurrentThreadTransactionalCaches() {
        this.unbindCaches();
    }

    private void unbindCaches() {
        log.trace("Unbinding transactional caches for thread: {}", (Object)Thread.currentThread().getName());
        this.synchronizationThreadLocal.reset();
    }

    Iterable<Deferred> forceUnbindCaches() {
        Iterable<Deferred> deferreds = this.getDeferreds();
        this.unbindCaches();
        return deferreds;
    }

    Iterable<Deferred> getDeferreds() {
        return Iterables.concat(this.getTransactionCacheMap().values(), this.getTransactionalCachedReferenceMap().values());
    }

    @Override
    public <K, V> TransactionAwareCache<K, V> getTxCache(String cacheName, CacheLoader<K, V> loader) {
        return TransactionAwareCache.from(this.getCache(cacheName, loader));
    }

    @Override
    public <K, V> TransactionAwareCache<K, V> getTxCache(String cacheName) {
        return TransactionAwareCache.from(this.getCache(cacheName));
    }

    private static void logNonTxUsageWarning(String cacheName) {
        if (DEV_MODE) {
            log.warn("Update operation performed on transactional cache [{}] outside of a transaction. All updates to this cache should be performed from a thread with a valid transaction context.", (Object)cacheName);
            if (log.isDebugEnabled()) {
                log.debug("Cache usage call stack", (Throwable)new RuntimeException());
            } else if (STACK_TRACE_ON_NO_TX) {
                log.warn("Update operation performed on transactional cache [{}] outside of a transaction.", (Object)cacheName, (Object)new RuntimeException());
            }
        }
    }

    @Deprecated
    public <K, V> @NonNull Cache<K, V> getCache(@NonNull String name, @NonNull Class<K> keyType, @NonNull Class<V> valueType) {
        return this.getCache(name);
    }

    @Deprecated
    public <K, V> @NonNull Cache<K, V> getCache(@NonNull Class<?> owningClass, @NonNull String name) {
        return this.getCache(owningClass.getName() + "." + name);
    }

    @Deprecated
    public <K, V> @NonNull Cache<K, V> getCache(@NonNull String name, CacheLoader<K, V> loader) {
        return this.getCache(name, loader, new CacheSettingsBuilder().build());
    }

    @Deprecated
    public <K, V> @NonNull Cache<K, V> getCache(@NonNull String name) {
        return this.getCache(name, null);
    }

    public <V> @NonNull CachedReference<V> getCachedReference(@NonNull String name, @NonNull Supplier<V> supplier, @NonNull CacheSettings required) {
        return new TransactionAwareCachedReference<V>(name, supplier, required);
    }

    private <V> CachedReference<V> getTransactionalCachedReference(String name, Supplier<V> supplier, CachedReference<V> backingCachedReference) {
        DeferredCachedReference transactionalCache;
        if (this.getTransactionalCachedReferenceMap().get(name) != null) {
            log.trace("Pre-bound transactional cached reference found for cache name: {}", (Object)name);
            transactionalCache = this.getTransactionalCachedReferenceMap().get(name);
        } else {
            log.trace("Binding new transactional cached reference to thread local: {}", (Object)name);
            transactionalCache = DeferredCachedReference.create(name, supplier, backingCachedReference);
            this.bindCacheSynchronizationIfNecessary();
            this.getTransactionalCachedReferenceMap().put(name, transactionalCache);
        }
        return transactionalCache;
    }

    public <V> @NonNull CachedReference<V> getCachedReference(@NonNull String name, @NonNull Supplier<V> supplier) {
        return this.getCachedReference(name, supplier, new CacheSettingsBuilder().build());
    }

    public <V> @NonNull CachedReference<V> getCachedReference(@NonNull Class<?> owningClass, @NonNull String name, @NonNull Supplier<V> supplier) {
        return this.getCachedReference(owningClass, name, supplier, new CacheSettingsBuilder().build());
    }

    public <V> @NonNull CachedReference<V> getCachedReference(@NonNull Class<?> owningClass, @NonNull String name, @NonNull Supplier<V> supplier, @NonNull CacheSettings required) {
        return this.getCachedReference(owningClass.getName() + "." + name, supplier, required);
    }

    private class TransactionAwareCachedReference<V>
    implements CachedReference<V> {
        private final String cacheName;
        private final Supplier<V> supplier;
        private final CachedReference<V> backingCachedReference;

        TransactionAwareCachedReference(String cacheName, Supplier<V> supplier, CacheSettings settings) {
            this.cacheName = cacheName;
            this.supplier = supplier;
            this.backingCachedReference = TransactionalCacheFactory.this.cacheFactory.getCachedReference(cacheName, supplier, settings);
        }

        private CachedReference<V> getDelegate(boolean warnIfNotInTransaction) {
            if (TransactionalCacheFactory.this.synchronizationManager.isTransactionActive()) {
                return TransactionalCacheFactory.this.getTransactionalCachedReference(this.cacheName, this.supplier, this.backingCachedReference);
            }
            if (warnIfNotInTransaction) {
                TransactionalCacheFactory.logNonTxUsageWarning(this.cacheName);
            }
            return this.backingCachedReference;
        }

        public @NonNull V get() {
            return (V)this.getDelegate(true).get();
        }

        public void reset() {
            this.getDelegate(true).reset();
        }

        public boolean isPresent() {
            return this.getDelegate(false).isPresent();
        }

        @Nonnull
        public Optional<V> getIfPresent() {
            return this.getDelegate(false).getIfPresent();
        }

        public void addListener(@NonNull CachedReferenceListener<V> vCachedReferenceListener, boolean b) {
            this.getDelegate(false).addListener(vCachedReferenceListener, b);
        }

        public void removeListener(@NonNull CachedReferenceListener<V> vCachedReferenceListener) {
            this.getDelegate(false).removeListener(vCachedReferenceListener);
        }
    }

    private class TransactionalCache<K, V>
    implements Cache<K, V> {
        private final String cacheName;
        private final CacheLoader<K, V> loader;
        private final CacheSettings required;
        private final LazyReference<Cache<K, V>> nonTransactionalDelegateRef;

        TransactionalCache(final String cacheName, final CacheLoader<K, V> loader, final CacheSettings required) {
            this.cacheName = cacheName;
            this.loader = loader;
            this.required = required;
            this.nonTransactionalDelegateRef = new LazyReference<Cache<K, V>>(){

                protected Cache<K, V> create() throws Exception {
                    return TransactionalCacheFactory.this.cacheFactory.getCache(cacheName, loader, required);
                }
            };
        }

        private Cache<K, V> getDelegate(boolean warnIfNotInTransaction) {
            if (TransactionalCacheFactory.this.synchronizationManager.isTransactionActive()) {
                return TransactionalCacheFactory.this.getTransactionalCache(this.cacheName, this.loader, this.required);
            }
            if (warnIfNotInTransaction) {
                TransactionalCacheFactory.logNonTxUsageWarning(this.cacheName);
            }
            return (Cache)this.nonTransactionalDelegateRef.get();
        }

        public @NonNull String getName() {
            return this.cacheName;
        }

        public boolean containsKey(@NonNull K key) {
            return this.getDelegate(false).containsKey(key);
        }

        public @NonNull Collection<K> getKeys() {
            return this.getDelegate(false).getKeys();
        }

        public V get(@NonNull K key) {
            return (V)this.getDelegate(false).get(key);
        }

        public @NonNull V get(@NonNull K k, @NonNull Supplier<? extends V> supplier) {
            return (V)this.getDelegate(false).get(k, supplier);
        }

        public void put(@NonNull K key, @NonNull V value) {
            this.getDelegate(true).put(key, value);
        }

        public void remove(@NonNull K key) {
            this.getDelegate(true).remove(key);
        }

        public void removeAll() {
            this.getDelegate(true).removeAll();
        }

        public V putIfAbsent(@NonNull K key, @NonNull V value) {
            return (V)this.getDelegate(true).putIfAbsent(key, value);
        }

        public boolean replace(@NonNull K key, @NonNull V oldValue, @NonNull V newValue) {
            return this.getDelegate(true).replace(key, oldValue, newValue);
        }

        public void addListener(@NonNull CacheEntryListener<K, V> kvCacheEntryListener, boolean b) {
            this.getDelegate(false).addListener(kvCacheEntryListener, b);
        }

        public void removeListener(@NonNull CacheEntryListener<K, V> kvCacheEntryListener) {
            this.getDelegate(false).removeListener(kvCacheEntryListener);
        }

        public boolean remove(@NonNull K key, @NonNull V value) {
            return this.getDelegate(true).remove(key, value);
        }
    }

    private class DelegateCacheSynchronization
    extends TransactionSynchronizationAdapter {
        private Map<String, DeferredOperationsCache> cacheMap = new HashMap<String, DeferredOperationsCache>();
        private Map<String, DeferredCachedReference> referenceMap = new HashMap<String, DeferredCachedReference>();

        private DelegateCacheSynchronization() {
        }

        public void suspend() {
            TransactionalCacheFactory.this.synchronizationThreadLocal.reset();
        }

        public void resume() {
            TransactionalCacheFactory.this.synchronizationThreadLocal.set(this);
        }

        public int getOrder() {
            return 1;
        }

        public void afterCompletion(int status) {
            try {
                log.trace("Performing after transaction completion tasks");
                if (status != 0) {
                    log.trace("Transaction was not committed; cache changes not performed");
                    return;
                }
                this.synchronizeCaches();
            }
            finally {
                TransactionalCacheFactory.this.unbindCaches();
            }
        }

        private void synchronizeCaches() {
            try (Split ignored = this.startTimer();){
                for (Deferred cache : TransactionalCacheFactory.this.getDeferreds()) {
                    if (log.isTraceEnabled()) {
                        log.trace("Synchronizing transactional {}: {}", (Object)cache.getType(), (Object)cache.getName());
                    }
                    try {
                        this.synchronizeCache(cache);
                    }
                    catch (Exception | LinkageError e) {
                        this.handleCacheSynchronizationFailure(cache, e);
                    }
                }
            }
        }

        private Split startTimer() {
            return CacheMonitoringUtils.startSplit(TransactionalCacheFactory.this.confluenceMonitoring, "SynchronizeCaches");
        }

        private void handleCacheSynchronizationFailure(Deferred cache, Throwable e) {
            log.error("Could not synchronise transactional " + cache.getType() + " [" + cache.getName() + "]. Attempting flush instead.", e);
            cache.clear();
        }

        private void synchronizeCache(Deferred cache) {
            if (cache.hasDeferredOperations()) {
                cache.sync();
            }
        }

        public Map<String, DeferredOperationsCache> getCacheMap() {
            return this.cacheMap;
        }

        public Map<String, DeferredCachedReference> getReferenceMap() {
            return this.referenceMap;
        }
    }
}

