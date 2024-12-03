/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsDefaultsProvider
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.cache.Supplier
 *  com.atlassian.cache.ehcache.EhCacheManager
 *  com.atlassian.confluence.cache.ConfluenceManagedCache
 *  com.atlassian.confluence.impl.cache.whitelist.CacheOperationsWhitelistService
 *  com.atlassian.confluence.util.profiling.ConfluenceMonitoring
 *  com.atlassian.util.profiling.Metrics
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  javax.annotation.PreDestroy
 *  net.sf.ehcache.CacheManager
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.config.CacheConfiguration
 *  net.sf.ehcache.config.Configuration
 *  net.sf.ehcache.config.SizeOfPolicyConfiguration
 *  net.sf.ehcache.config.SizeOfPolicyConfiguration$MaxDepthExceededBehavior
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cache.ehcache;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsDefaultsProvider;
import com.atlassian.cache.CachedReference;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.Supplier;
import com.atlassian.confluence.cache.ConfluenceManagedCache;
import com.atlassian.confluence.cache.ehcache.ConfluenceEhCache;
import com.atlassian.confluence.cache.ehcache.DefaultConfluenceEhCache;
import com.atlassian.confluence.impl.cache.whitelist.CacheOperationsWhitelistService;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoring;
import com.atlassian.util.profiling.Metrics;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PreDestroy;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EhCacheManager
implements CacheManager {
    private static final Logger startupLog = LoggerFactory.getLogger((String)"com.atlassian.confluence.lifecycle");
    private final Set<String> cachedReferenceNames = Collections.synchronizedSet(new HashSet());
    private final CacheManager atlassianCacheDelegate;
    private final net.sf.ehcache.CacheManager ehCacheDelegateManager;

    public EhCacheManager(CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider) {
        startupLog.info("Loading EhCache cache manager");
        this.ehCacheDelegateManager = EhCacheManager.loadAndConfigureDelegate(cacheSettingsDefaultsProvider);
        this.atlassianCacheDelegate = new com.atlassian.cache.ehcache.EhCacheManager(this.ehCacheDelegateManager, null, cacheSettingsDefaultsProvider);
    }

    @Deprecated(forRemoval=true)
    public EhCacheManager(ConfluenceMonitoring confluenceMonitoring, CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider, CacheOperationsWhitelistService cacheOperationsWhitelistService) {
        startupLog.info("Loading EhCache cache manager");
        this.ehCacheDelegateManager = EhCacheManager.loadAndConfigureDelegate(cacheSettingsDefaultsProvider);
        this.atlassianCacheDelegate = new com.atlassian.cache.ehcache.EhCacheManager(this.ehCacheDelegateManager, null, cacheSettingsDefaultsProvider);
    }

    private static net.sf.ehcache.CacheManager loadAndConfigureDelegate(CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider) {
        return net.sf.ehcache.CacheManager.create((Configuration)EhCacheManager.getDefaultCacheConfig(cacheSettingsDefaultsProvider));
    }

    static Configuration getDefaultCacheConfig(CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider) {
        CacheSettings defaultCacheSettings = cacheSettingsDefaultsProvider.getDefaults("default");
        return new Configuration().defaultCache(new CacheConfiguration("defaultCache", defaultCacheSettings.getMaxEntries().intValue()).timeToIdleSeconds(defaultCacheSettings.getExpireAfterAccess().longValue()).timeToLiveSeconds(defaultCacheSettings.getExpireAfterWrite().longValue()).maxEntriesLocalHeap(defaultCacheSettings.getMaxEntries().intValue()).eternal(false).sizeOfPolicy(new SizeOfPolicyConfiguration().maxDepth(32).maxDepthExceededBehavior(SizeOfPolicyConfiguration.MaxDepthExceededBehavior.ABORT)));
    }

    @PreDestroy
    public void shutdownCacheManager() {
        startupLog.info("Shutting down EhCache cache manager");
        this.ehCacheDelegateManager.shutdown();
    }

    public void flushCaches() {
        String[] cacheNames = this.ehCacheDelegateManager.getCacheNames();
        this.emitFlushCachesMetric();
        ImmutableSet allNonFlushableCacheNames = ImmutableSet.builder().addAll(Iterables.transform((Iterable)Iterables.filter((Iterable)this.atlassianCacheDelegate.getManagedCaches(), (Predicate)Predicates.not(this.flushable())), this.cacheName())).build();
        for (String cacheName : cacheNames) {
            if (allNonFlushableCacheNames.contains(cacheName)) continue;
            this.ehCacheDelegateManager.getEhcache(cacheName).removeAll();
        }
    }

    private void emitFlushCachesMetric() {
        String CACHE_FLUSH_METRIC_NAME = "cacheManager.flushAll";
        String CLASS_NAME_KEY = "className";
        Metrics.metric((String)"cacheManager.flushAll").withInvokerPluginKey().tag("className", this.getClass().getName()).withAnalytics().incrementCounter(Long.valueOf(1L));
    }

    private Predicate<ManagedCache> flushable() {
        return ManagedCache::isFlushable;
    }

    private Function<ManagedCache, String> cacheName() {
        return ManagedCache::getName;
    }

    net.sf.ehcache.CacheManager getDelegateEhCacheManager() {
        return this.ehCacheDelegateManager;
    }

    @Deprecated
    public @NonNull Collection<Cache<?, ?>> getCaches() {
        ArrayList ret = new ArrayList();
        for (String cacheName : this.ehCacheDelegateManager.getCacheNames()) {
            if (this.cachedReferenceNames.contains(cacheName)) continue;
            ret.add((Cache<?, ?>)this.getCache(cacheName));
        }
        return ret;
    }

    private <K, V> ConfluenceEhCache<K, V> wrapCache(Cache<K, V> cache) {
        Ehcache ehCache = this.ehCacheDelegateManager.getEhcache(cache.getName());
        CacheConfiguration ehCacheConfig = ehCache.getCacheConfiguration();
        return new DefaultConfluenceEhCache<K, V>(cache, ehCacheConfig);
    }

    public <K, V> @NonNull ConfluenceEhCache<K, V> getCache(@NonNull String name) {
        return this.wrapCache(this.atlassianCacheDelegate.getCache(name));
    }

    @Deprecated
    public <K, V> @NonNull Cache<K, V> getCache(@NonNull String name, @NonNull Class<K> keyType, @NonNull Class<V> valueType) {
        return this.wrapCache(this.atlassianCacheDelegate.getCache(name, keyType, valueType));
    }

    public @NonNull Collection<ManagedCache> getManagedCaches() {
        Collection managedCaches = Collections2.filter((Collection)this.atlassianCacheDelegate.getManagedCaches(), EhCacheManager.notACachedReference());
        return Collections2.transform((Collection)managedCaches, cache -> cache == null ? null : new ConfluenceManagedCache(cache, cache.isFlushable()));
    }

    private static Predicate<ManagedCache> notACachedReference() {
        return input -> !(input instanceof CachedReference);
    }

    public ManagedCache getManagedCache(@NonNull String name) {
        ManagedCache mc = this.atlassianCacheDelegate.getManagedCache(name);
        return null == mc ? null : new ConfluenceManagedCache(mc, mc.isFlushable());
    }

    public void shutdown() {
        this.atlassianCacheDelegate.shutdown();
    }

    public <V> @NonNull CachedReference<V> getCachedReference(@NonNull String name, @NonNull Supplier<V> supplier) {
        CachedReference result = this.atlassianCacheDelegate.getCachedReference(name, supplier);
        this.cachedReferenceNames.add(name);
        return result;
    }

    public <V> @NonNull CachedReference<V> getCachedReference(@NonNull String name, @NonNull Supplier<V> supplier, @NonNull CacheSettings settings) {
        CachedReference result = this.atlassianCacheDelegate.getCachedReference(name, supplier, settings);
        this.cachedReferenceNames.add(name);
        return result;
    }

    public <V> @NonNull CachedReference<V> getCachedReference(@NonNull Class<?> owningClass, @NonNull String fieldName, @NonNull Supplier<V> supplier) {
        String fullName = this.toName(owningClass, fieldName);
        CachedReference result = this.atlassianCacheDelegate.getCachedReference(fullName, supplier);
        this.cachedReferenceNames.add(fullName);
        return result;
    }

    public <V> @NonNull CachedReference<V> getCachedReference(@NonNull Class<?> owningClass, @NonNull String fieldName, @NonNull Supplier<V> supplier, @NonNull CacheSettings settings) {
        String fullName = this.toName(owningClass, fieldName);
        CachedReference result = this.atlassianCacheDelegate.getCachedReference(fullName, supplier, settings);
        this.cachedReferenceNames.add(fullName);
        return result;
    }

    public <K, V> @NonNull Cache<K, V> getCache(@NonNull Class<?> owningClass, @NonNull String name) {
        return this.wrapCache(this.atlassianCacheDelegate.getCache(owningClass, name));
    }

    public <K, V> @NonNull Cache<K, V> getCache(@NonNull String name, CacheLoader<K, V> loader) {
        return this.wrapCache(this.atlassianCacheDelegate.getCache(name, loader));
    }

    public <K, V> @NonNull ConfluenceEhCache<K, V> getCache(@NonNull String name, CacheLoader<K, V> loader, @NonNull CacheSettings settings) {
        return this.wrapCache(this.atlassianCacheDelegate.getCache(name, loader, settings));
    }

    public net.sf.ehcache.CacheManager getEhCacheManager() {
        return this.ehCacheDelegateManager;
    }

    private String toName(Class<?> owningClass, String fieldName) {
        return owningClass.getName() + "." + (String)Preconditions.checkNotNull((Object)fieldName);
    }
}

