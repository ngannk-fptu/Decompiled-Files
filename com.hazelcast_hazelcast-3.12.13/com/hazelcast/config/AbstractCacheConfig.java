/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.configuration.CacheEntryListenerConfiguration
 *  javax.cache.configuration.CompleteConfiguration
 *  javax.cache.configuration.Factory
 *  javax.cache.expiry.EternalExpiryPolicy
 *  javax.cache.expiry.ExpiryPolicy
 *  javax.cache.integration.CacheLoader
 *  javax.cache.integration.CacheWriter
 */
package com.hazelcast.config;

import com.hazelcast.cache.impl.DeferredValue;
import com.hazelcast.config.CacheConfiguration;
import com.hazelcast.config.HotRestartConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.util.Preconditions;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;

@BinaryInterface
public abstract class AbstractCacheConfig<K, V>
implements CacheConfiguration<K, V>,
DataSerializable {
    private static final String DEFAULT_KEY_VALUE_TYPE = "java.lang.Object";
    protected DeferredValue<Factory<CacheLoader<K, V>>> cacheLoaderFactory;
    protected DeferredValue<Factory<CacheWriter<? super K, ? super V>>> cacheWriterFactory;
    protected DeferredValue<Factory<ExpiryPolicy>> expiryPolicyFactory;
    protected boolean isReadThrough;
    protected boolean isWriteThrough;
    protected boolean isStatisticsEnabled;
    protected boolean isStoreByValue;
    protected boolean isManagementEnabled;
    protected HotRestartConfig hotRestartConfig = new HotRestartConfig();
    protected transient ClassLoader classLoader;
    protected transient InternalSerializationService serializationService;
    protected Set<DeferredValue<CacheEntryListenerConfiguration<K, V>>> listenerConfigurations;
    private Class<K> keyType;
    private String keyClassName = "java.lang.Object";
    private Class<V> valueType;
    private String valueClassName = "java.lang.Object";

    public AbstractCacheConfig() {
        this.listenerConfigurations = this.createConcurrentSet();
        this.cacheLoaderFactory = DeferredValue.withNullValue();
        this.cacheWriterFactory = DeferredValue.withNullValue();
        this.expiryPolicyFactory = DeferredValue.withValue(EternalExpiryPolicy.factoryOf());
        this.isReadThrough = false;
        this.isWriteThrough = false;
        this.isStatisticsEnabled = false;
        this.isStoreByValue = true;
        this.isManagementEnabled = false;
    }

    public AbstractCacheConfig(CompleteConfiguration<K, V> configuration) {
        this.setKeyType(configuration.getKeyType());
        this.setValueType(configuration.getValueType());
        this.listenerConfigurations = this.createConcurrentSet();
        for (CacheEntryListenerConfiguration listenerConf : configuration.getCacheEntryListenerConfigurations()) {
            this.listenerConfigurations.add(DeferredValue.withValue(listenerConf));
        }
        this.cacheLoaderFactory = DeferredValue.withValue(configuration.getCacheLoaderFactory());
        this.cacheWriterFactory = DeferredValue.withValue(configuration.getCacheWriterFactory());
        Factory factory = configuration.getExpiryPolicyFactory();
        factory = factory == null ? EternalExpiryPolicy.factoryOf() : factory;
        this.expiryPolicyFactory = DeferredValue.withValue(factory);
        this.isReadThrough = configuration.isReadThrough();
        this.isWriteThrough = configuration.isWriteThrough();
        this.isStatisticsEnabled = configuration.isStatisticsEnabled();
        this.isStoreByValue = configuration.isStoreByValue();
        this.isManagementEnabled = configuration.isManagementEnabled();
    }

    @Override
    public CacheConfiguration<K, V> addCacheEntryListenerConfiguration(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        Preconditions.checkNotNull(cacheEntryListenerConfiguration, "CacheEntryListenerConfiguration can't be null");
        if (!this.getListenerConfigurations().add(cacheEntryListenerConfiguration)) {
            throw new IllegalArgumentException("A CacheEntryListenerConfiguration can be registered only once");
        }
        return this;
    }

    @Override
    public CacheConfiguration<K, V> removeCacheEntryListenerConfiguration(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        Preconditions.checkNotNull(cacheEntryListenerConfiguration, "CacheEntryListenerConfiguration can't be null");
        DeferredValue<CacheEntryListenerConfiguration<K, V>> lazyConfig = DeferredValue.withValue(cacheEntryListenerConfiguration);
        this.listenerConfigurations.remove(lazyConfig);
        return this;
    }

    public Iterable<CacheEntryListenerConfiguration<K, V>> getCacheEntryListenerConfigurations() {
        return this.getListenerConfigurations();
    }

    public boolean isReadThrough() {
        return this.isReadThrough;
    }

    @Override
    public CacheConfiguration<K, V> setReadThrough(boolean isReadThrough) {
        this.isReadThrough = isReadThrough;
        return this;
    }

    public boolean isWriteThrough() {
        return this.isWriteThrough;
    }

    @Override
    public CacheConfiguration<K, V> setWriteThrough(boolean isWriteThrough) {
        this.isWriteThrough = isWriteThrough;
        return this;
    }

    public boolean isStatisticsEnabled() {
        return this.isStatisticsEnabled;
    }

    @Override
    public CacheConfiguration<K, V> setStatisticsEnabled(boolean enabled) {
        this.isStatisticsEnabled = enabled;
        return this;
    }

    public boolean isManagementEnabled() {
        return this.isManagementEnabled;
    }

    @Override
    public CacheConfiguration<K, V> setManagementEnabled(boolean enabled) {
        this.isManagementEnabled = enabled;
        return this;
    }

    public HotRestartConfig getHotRestartConfig() {
        return this.hotRestartConfig;
    }

    public CacheConfiguration<K, V> setHotRestartConfig(HotRestartConfig hotRestartConfig) {
        this.hotRestartConfig = hotRestartConfig;
        return this;
    }

    public Factory<CacheLoader<K, V>> getCacheLoaderFactory() {
        return this.cacheLoaderFactory.get(this.serializationService);
    }

    @Override
    public CacheConfiguration<K, V> setCacheLoaderFactory(Factory<? extends CacheLoader<K, V>> cacheLoaderFactory) {
        this.cacheLoaderFactory = DeferredValue.withValue(cacheLoaderFactory);
        return this;
    }

    @Override
    public CacheConfiguration<K, V> setExpiryPolicyFactory(Factory<? extends ExpiryPolicy> expiryPolicyFactory) {
        this.expiryPolicyFactory = DeferredValue.withValue(expiryPolicyFactory);
        return this;
    }

    @Override
    public CacheConfiguration<K, V> setCacheWriterFactory(Factory<? extends CacheWriter<? super K, ? super V>> cacheWriterFactory) {
        this.cacheWriterFactory = DeferredValue.withValue(cacheWriterFactory);
        return this;
    }

    public Factory<CacheWriter<? super K, ? super V>> getCacheWriterFactory() {
        return this.cacheWriterFactory.get(this.serializationService);
    }

    public Factory<ExpiryPolicy> getExpiryPolicyFactory() {
        return this.expiryPolicyFactory.get(this.serializationService);
    }

    public Class<K> getKeyType() {
        return this.keyType != null ? this.keyType : this.resolveKeyType();
    }

    public String getKeyClassName() {
        return this.keyClassName;
    }

    public CacheConfiguration<K, V> setKeyClassName(String keyClassName) {
        this.keyClassName = keyClassName;
        return this;
    }

    public Class<V> getValueType() {
        return this.valueType != null ? this.valueType : this.resolveValueType();
    }

    public String getValueClassName() {
        return this.valueClassName;
    }

    public CacheConfiguration<K, V> setValueClassName(String valueClassName) {
        this.valueClassName = valueClassName;
        return this;
    }

    private Class<K> resolveKeyType() {
        this.keyType = this.resolve(this.keyClassName);
        return this.keyType;
    }

    private Class<V> resolveValueType() {
        this.valueType = this.resolve(this.valueClassName);
        return this.valueType;
    }

    private Class resolve(String className) {
        Class type = null;
        if (className != null) {
            try {
                type = ClassLoaderUtil.loadClass(this.classLoader, className);
            }
            catch (ClassNotFoundException e) {
                throw new HazelcastException("Could not resolve type " + className, e);
            }
        }
        if (type == null) {
            type = Object.class;
        }
        return type;
    }

    @Override
    public CacheConfiguration<K, V> setTypes(Class<K> keyType, Class<V> valueType) {
        if (keyType == null || valueType == null) {
            throw new NullPointerException("keyType and/or valueType can't be null");
        }
        this.setKeyType(keyType);
        this.setValueType(valueType);
        return this;
    }

    public boolean isStoreByValue() {
        return this.isStoreByValue;
    }

    @Override
    public CacheConfiguration<K, V> setStoreByValue(boolean storeByValue) {
        this.isStoreByValue = storeByValue;
        return this;
    }

    protected Set<DeferredValue<CacheEntryListenerConfiguration<K, V>>> createConcurrentSet() {
        return Collections.newSetFromMap(new ConcurrentHashMap());
    }

    public CacheConfiguration<K, V> setKeyType(Class<K> keyType) {
        this.keyType = keyType;
        if (keyType != null) {
            this.keyClassName = keyType.getName();
        }
        return this;
    }

    public CacheConfiguration<K, V> setValueType(Class<V> valueType) {
        this.valueType = valueType;
        if (valueType != null) {
            this.valueClassName = valueType.getName();
        }
        return this;
    }

    public CacheConfiguration<K, V> setListenerConfigurations() {
        this.listenerConfigurations = this.createConcurrentSet();
        return this;
    }

    protected CacheConfiguration<K, V> setListenerConfigurations(Set<CacheEntryListenerConfiguration<K, V>> listeners) {
        this.listenerConfigurations = DeferredValue.concurrentSetOfValues(listeners);
        return this;
    }

    public Set<CacheEntryListenerConfiguration<K, V>> getListenerConfigurations() {
        return DeferredValue.asPassThroughSet(this.listenerConfigurations, this.serializationService);
    }

    protected boolean hasListenerConfiguration() {
        return this.listenerConfigurations != null && !this.listenerConfigurations.isEmpty();
    }

    public int hashCode() {
        int result = this.cacheLoaderFactory != null ? this.cacheLoaderFactory.hashCode() : 0;
        result = 31 * result + this.listenerConfigurations.hashCode();
        result = 31 * result + this.keyType.hashCode();
        result = 31 * result + this.valueType.hashCode();
        result = 31 * result + (this.cacheWriterFactory != null ? this.cacheWriterFactory.hashCode() : 0);
        result = 31 * result + (this.expiryPolicyFactory != null ? this.expiryPolicyFactory.hashCode() : 0);
        result = 31 * result + (this.isReadThrough ? 1 : 0);
        result = 31 * result + (this.isWriteThrough ? 1 : 0);
        result = 31 * result + (this.isStatisticsEnabled ? 1 : 0);
        result = 31 * result + (this.isStoreByValue ? 1 : 0);
        result = 31 * result + (this.isManagementEnabled ? 1 : 0);
        return result;
    }

    public boolean equals(Object o) {
        Set<CacheEntryListenerConfiguration<K, V>> thatListenerConfigs;
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof AbstractCacheConfig)) {
            return false;
        }
        AbstractCacheConfig that = (AbstractCacheConfig)o;
        if (this.isManagementEnabled != that.isManagementEnabled) {
            return false;
        }
        if (this.isReadThrough != that.isReadThrough) {
            return false;
        }
        if (this.isStatisticsEnabled != that.isStatisticsEnabled) {
            return false;
        }
        if (this.isStoreByValue != that.isStoreByValue) {
            return false;
        }
        if (this.isWriteThrough != that.isWriteThrough) {
            return false;
        }
        Factory<CacheLoader<K, V>> thisCacheLoaderFactory = this.getCacheLoaderFactory();
        Factory<CacheLoader<K, V>> thatCacheLoaderFactory = that.getCacheLoaderFactory();
        if (thisCacheLoaderFactory != null ? !thisCacheLoaderFactory.equals(thatCacheLoaderFactory) : thatCacheLoaderFactory != null) {
            return false;
        }
        Factory<CacheWriter<K, V>> thisCacheWriterFactory = this.getCacheWriterFactory();
        Factory<CacheWriter<K, V>> thatCacheWriterFactory = that.getCacheWriterFactory();
        if (thisCacheWriterFactory != null ? !thisCacheWriterFactory.equals(thatCacheWriterFactory) : thatCacheWriterFactory != null) {
            return false;
        }
        Factory<ExpiryPolicy> thisExpiryPolicyFactory = this.getExpiryPolicyFactory();
        Factory<ExpiryPolicy> thatExpiryPolicyFactory = that.getExpiryPolicyFactory();
        if (thisExpiryPolicyFactory != null ? !thisExpiryPolicyFactory.equals(thatExpiryPolicyFactory) : thatExpiryPolicyFactory != null) {
            return false;
        }
        Set<CacheEntryListenerConfiguration<K, V>> thisListenerConfigs = this.getListenerConfigurations();
        if (!thisListenerConfigs.equals(thatListenerConfigs = that.getListenerConfigurations())) {
            return false;
        }
        return this.keyValueTypesEqual(that);
    }

    protected void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    protected boolean keyValueTypesEqual(AbstractCacheConfig that) {
        if (!this.getKeyType().equals(that.getKeyType())) {
            return false;
        }
        return this.getValueType().equals(that.getValueType());
    }
}

