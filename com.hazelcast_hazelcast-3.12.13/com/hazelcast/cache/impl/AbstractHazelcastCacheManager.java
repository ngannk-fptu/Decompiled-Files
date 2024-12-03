/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.CacheException
 *  javax.cache.configuration.CacheEntryListenerConfiguration
 *  javax.cache.configuration.CompleteConfiguration
 *  javax.cache.configuration.Configuration
 *  javax.cache.spi.CachingProvider
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.CacheUtil;
import com.hazelcast.cache.HazelcastCacheManager;
import com.hazelcast.cache.ICache;
import com.hazelcast.cache.impl.ICacheInternal;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.core.LifecycleService;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.SetUtil;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.cache.CacheException;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;

public abstract class AbstractHazelcastCacheManager
implements HazelcastCacheManager {
    protected final ConcurrentMap<String, ICacheInternal<?, ?>> caches = new ConcurrentHashMap();
    protected final CachingProvider cachingProvider;
    protected final HazelcastInstance hazelcastInstance;
    protected final boolean isDefaultURI;
    protected final boolean isDefaultClassLoader;
    protected final URI uri;
    protected final Properties properties;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final AtomicBoolean isDestroyed = new AtomicBoolean(false);
    private final WeakReference<ClassLoader> classLoaderReference;
    private final String cacheNamePrefix;
    private final String lifecycleListenerRegistrationId;

    public AbstractHazelcastCacheManager(CachingProvider cachingProvider, HazelcastInstance hazelcastInstance, URI uri, ClassLoader classLoader, Properties properties) {
        Preconditions.checkNotNull(cachingProvider, "CachingProvider missing");
        this.cachingProvider = cachingProvider;
        Preconditions.checkNotNull(hazelcastInstance, "hazelcastInstance cannot be null");
        this.hazelcastInstance = hazelcastInstance;
        this.isDefaultURI = uri == null || cachingProvider.getDefaultURI().equals(uri);
        this.uri = this.isDefaultURI ? cachingProvider.getDefaultURI() : uri;
        this.isDefaultClassLoader = classLoader == null || cachingProvider.getDefaultClassLoader().equals(classLoader);
        ClassLoader localClassLoader = this.isDefaultClassLoader ? cachingProvider.getDefaultClassLoader() : classLoader;
        this.classLoaderReference = new WeakReference<ClassLoader>(localClassLoader);
        this.properties = properties == null ? new Properties() : new Properties(properties);
        this.cacheNamePrefix = this.getCacheNamePrefix();
        this.lifecycleListenerRegistrationId = this.registerLifecycleListener();
    }

    private <K, V, C extends Configuration<K, V>> ICacheInternal<K, V> createCacheInternal(String cacheName, C configuration) throws IllegalArgumentException {
        this.ensureOpen();
        Preconditions.checkNotNull(cacheName, "cacheName must not be null");
        Preconditions.checkNotNull(configuration, "configuration must not be null");
        CacheConfig<K, V> newCacheConfig = this.createCacheConfig(cacheName, configuration);
        this.validateCacheConfig(newCacheConfig);
        if (this.caches.containsKey(newCacheConfig.getNameWithPrefix())) {
            throw new CacheException("A cache named '" + cacheName + "' already exists");
        }
        this.createCacheConfig(cacheName, newCacheConfig);
        ICacheInternal<K, V> cacheProxy = this.createCacheProxy(newCacheConfig);
        this.addCacheConfigIfAbsent(newCacheConfig);
        ICacheInternal<K, V> existingCache = this.caches.putIfAbsent(newCacheConfig.getNameWithPrefix(), cacheProxy);
        if (existingCache == null) {
            this.registerListeners(newCacheConfig, cacheProxy);
            return cacheProxy;
        }
        CacheConfig config = (CacheConfig)existingCache.getConfiguration(CacheConfig.class);
        if (config.equals(newCacheConfig)) {
            return existingCache;
        }
        throw new CacheException("A cache named " + cacheName + " already exists");
    }

    @Override
    public HazelcastInstance getHazelcastInstance() {
        return this.hazelcastInstance;
    }

    public <K, V, C extends Configuration<K, V>> ICache<K, V> createCache(String cacheName, C configuration) throws IllegalArgumentException {
        return this.createCacheInternal(cacheName, configuration);
    }

    public CachingProvider getCachingProvider() {
        return this.cachingProvider;
    }

    public URI getURI() {
        return this.uri;
    }

    public ClassLoader getClassLoader() {
        return (ClassLoader)this.classLoaderReference.get();
    }

    public Properties getProperties() {
        return this.properties;
    }

    public <K, V> ICache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType) {
        this.ensureOpen();
        Preconditions.checkNotNull(keyType, "keyType can not be null");
        Preconditions.checkNotNull(valueType, "valueType can not be null");
        ICacheInternal<?, ?> cache = this.getCacheUnchecked(cacheName);
        if (cache != null) {
            Configuration configuration = cache.getConfiguration(CacheConfig.class);
            if (configuration.getKeyType() != null && configuration.getKeyType().equals(keyType)) {
                if (configuration.getValueType() != null && configuration.getValueType().equals(valueType)) {
                    return this.ensureOpenIfAvailable(cache);
                }
                throw new ClassCastException("Incompatible cache value types specified, expected " + configuration.getValueType() + " but " + valueType + " was specified");
            }
            throw new ClassCastException("Incompatible cache key types specified, expected " + configuration.getKeyType() + " but " + keyType + " was specified");
        }
        return null;
    }

    public <K, V> ICache<K, V> getOrCreateCache(String cacheName, CacheConfig<K, V> cacheConfig) {
        this.ensureOpen();
        String cacheNameWithPrefix = this.getCacheNameWithPrefix(cacheName);
        ICacheInternal<K, V> cache = (ICacheInternal<K, V>)this.caches.get(cacheNameWithPrefix);
        if (cache == null) {
            cache = this.createCacheInternal(cacheName, (Configuration)cacheConfig);
        }
        return this.ensureOpenIfAvailable(cache);
    }

    public <K, V> ICache<K, V> getCache(String cacheName) {
        this.ensureOpen();
        ICacheInternal<?, ?> cache = this.getCacheUnchecked(cacheName);
        if (cache != null) {
            return this.ensureOpenIfAvailable(cache);
        }
        return null;
    }

    private <K, V> ICacheInternal<K, V> ensureOpenIfAvailable(ICacheInternal<K, V> cache) {
        if (cache != null && cache.isClosed() && !cache.isDestroyed()) {
            cache.open();
        }
        return cache;
    }

    private <K, V> ICacheInternal<?, ?> getCacheUnchecked(String cacheName) {
        String cacheNameWithPrefix = this.getCacheNameWithPrefix(cacheName);
        ICacheInternal<K, V> cache = (ICacheInternal<K, V>)this.caches.get(cacheNameWithPrefix);
        if (cache == null) {
            CacheConfig<K, V> cacheConfig = this.findCacheConfig(cacheNameWithPrefix, cacheName);
            if (cacheConfig == null) {
                return null;
            }
            ICacheInternal<K, V> cacheProxy = this.createCacheProxy(cacheConfig);
            this.addCacheConfigIfAbsent(cacheConfig);
            cache = this.caches.putIfAbsent(cacheNameWithPrefix, cacheProxy);
            if (cache == null) {
                this.registerListeners(cacheConfig, cacheProxy);
                cache = cacheProxy;
            }
        }
        if (cache != null) {
            cache.setCacheManager(this);
        }
        return cache;
    }

    public Iterable<String> getCacheNames() {
        this.ensureOpen();
        Set<String> names = SetUtil.createLinkedHashSet(this.caches.size());
        for (Map.Entry entry : this.caches.entrySet()) {
            String nameWithPrefix = (String)entry.getKey();
            int index = nameWithPrefix.indexOf(this.cacheNamePrefix) + this.cacheNamePrefix.length();
            String name = nameWithPrefix.substring(index);
            names.add(name);
        }
        return Collections.unmodifiableCollection(names);
    }

    public void destroyCache(String cacheName) {
        this.removeCache(cacheName, true);
    }

    @Override
    public void removeCache(String cacheName, boolean destroy) {
        this.ensureOpen();
        Preconditions.checkNotNull(cacheName, "cacheName cannot be null");
        String cacheNameWithPrefix = this.getCacheNameWithPrefix(cacheName);
        ICacheInternal cache = (ICacheInternal)this.caches.remove(cacheNameWithPrefix);
        if (cache != null && destroy) {
            cache.destroy();
        }
        this.removeCacheConfigFromLocal(cacheNameWithPrefix);
    }

    protected void removeCacheConfigFromLocal(String cacheNameWithPrefix) {
    }

    private String registerLifecycleListener() {
        return this.hazelcastInstance.getLifecycleService().addLifecycleListener(new LifecycleListener(){

            @Override
            public void stateChanged(LifecycleEvent event) {
                if (event.getState() == LifecycleEvent.LifecycleState.SHUTTING_DOWN) {
                    AbstractHazelcastCacheManager.this.onShuttingDown();
                }
            }
        });
    }

    private void deregisterLifecycleListener() {
        LifecycleService lifecycleService = this.hazelcastInstance.getLifecycleService();
        try {
            lifecycleService.removeLifecycleListener(this.lifecycleListenerRegistrationId);
        }
        catch (HazelcastInstanceNotActiveException e) {
            EmptyStatement.ignore(e);
        }
    }

    public void close() {
        if (this.isDestroyed.get() || !this.isClosed.compareAndSet(false, true)) {
            return;
        }
        this.deregisterLifecycleListener();
        for (ICacheInternal cache : this.caches.values()) {
            cache.close();
        }
        this.postClose();
    }

    @Override
    public void destroy() {
        if (!this.isDestroyed.compareAndSet(false, true)) {
            return;
        }
        this.deregisterLifecycleListener();
        for (ICacheInternal cache : this.caches.values()) {
            cache.destroy();
        }
        this.caches.clear();
        this.isClosed.set(true);
        this.postDestroy();
    }

    protected void postDestroy() {
    }

    public boolean isClosed() {
        return this.isClosed.get() || !this.hazelcastInstance.getLifecycleService().isRunning();
    }

    protected void ensureOpen() {
        if (this.isClosed()) {
            throw new IllegalStateException("CacheManager " + this.cacheNamePrefix + " is already closed.");
        }
    }

    private String getCacheNamePrefix() {
        String cacheNamePrefix = CacheUtil.getPrefix(this.isDefaultURI ? null : this.uri, this.isDefaultClassLoader ? null : this.getClassLoader());
        if (cacheNamePrefix == null) {
            return "/hz/";
        }
        return "/hz/" + cacheNamePrefix;
    }

    @Override
    public String getCacheNameWithPrefix(String name) {
        return this.cacheNamePrefix + name;
    }

    protected <K, V, C extends Configuration<K, V>> CacheConfig<K, V> createCacheConfig(String cacheName, C configuration) {
        CacheConfig cacheConfig;
        if (configuration instanceof CompleteConfiguration) {
            cacheConfig = new CacheConfig((CompleteConfiguration)configuration);
        } else {
            cacheConfig = new CacheConfig();
            cacheConfig.setStoreByValue(configuration.isStoreByValue());
            Class keyType = configuration.getKeyType();
            Class valueType = configuration.getValueType();
            cacheConfig.setTypes(keyType, valueType);
        }
        cacheConfig.setName(cacheName);
        cacheConfig.setManagerPrefix(this.cacheNamePrefix);
        cacheConfig.setUriString(this.getURI().toString());
        return cacheConfig;
    }

    private <K, V> void registerListeners(CacheConfig<K, V> cacheConfig, ICache<K, V> source) {
        for (CacheEntryListenerConfiguration listenerConfig : cacheConfig.getCacheEntryListenerConfigurations()) {
            ((ICacheInternal)source).registerCacheEntryListener(listenerConfig, false);
        }
    }

    public String toString() {
        return "HazelcastCacheManager{hazelcastInstance=" + this.hazelcastInstance + ", cachingProvider=" + this.cachingProvider + '}';
    }

    protected abstract <K, V> void validateCacheConfig(CacheConfig<K, V> var1);

    protected abstract <K, V> void addCacheConfigIfAbsent(CacheConfig<K, V> var1);

    protected abstract <K, V> ICacheInternal<K, V> createCacheProxy(CacheConfig<K, V> var1);

    protected abstract <K, V> CacheConfig<K, V> findCacheConfig(String var1, String var2);

    protected abstract <K, V> void createCacheConfig(String var1, CacheConfig<K, V> var2);

    protected abstract <K, V> CacheConfig<K, V> getCacheConfig(String var1, String var2);

    protected abstract void postClose();

    protected abstract void onShuttingDown();
}

