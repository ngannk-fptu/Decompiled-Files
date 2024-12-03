/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.configuration;

import java.util.HashSet;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;

public class MutableConfiguration<K, V>
implements CompleteConfiguration<K, V> {
    public static final long serialVersionUID = 201306200821L;
    protected Class<K> keyType;
    protected Class<V> valueType;
    protected HashSet<CacheEntryListenerConfiguration<K, V>> listenerConfigurations;
    protected Factory<CacheLoader<K, V>> cacheLoaderFactory;
    protected Factory<CacheWriter<? super K, ? super V>> cacheWriterFactory;
    protected Factory<ExpiryPolicy> expiryPolicyFactory;
    protected boolean isReadThrough;
    protected boolean isWriteThrough;
    protected boolean isStatisticsEnabled;
    protected boolean isStoreByValue;
    protected boolean isManagementEnabled;

    public MutableConfiguration() {
        this.keyType = Object.class;
        this.valueType = Object.class;
        this.listenerConfigurations = new HashSet();
        this.cacheLoaderFactory = null;
        this.cacheWriterFactory = null;
        this.expiryPolicyFactory = EternalExpiryPolicy.factoryOf();
        this.isReadThrough = false;
        this.isWriteThrough = false;
        this.isStatisticsEnabled = false;
        this.isStoreByValue = true;
        this.isManagementEnabled = false;
    }

    public MutableConfiguration(CompleteConfiguration<K, V> configuration) {
        this.keyType = configuration.getKeyType();
        this.valueType = configuration.getValueType();
        this.listenerConfigurations = new HashSet();
        for (CacheEntryListenerConfiguration<K, V> definition : configuration.getCacheEntryListenerConfigurations()) {
            this.addCacheEntryListenerConfiguration(definition);
        }
        this.cacheLoaderFactory = configuration.getCacheLoaderFactory();
        this.cacheWriterFactory = configuration.getCacheWriterFactory();
        this.expiryPolicyFactory = configuration.getExpiryPolicyFactory() == null ? EternalExpiryPolicy.factoryOf() : configuration.getExpiryPolicyFactory();
        this.isReadThrough = configuration.isReadThrough();
        this.isWriteThrough = configuration.isWriteThrough();
        this.isStatisticsEnabled = configuration.isStatisticsEnabled();
        this.isStoreByValue = configuration.isStoreByValue();
        this.isManagementEnabled = configuration.isManagementEnabled();
    }

    @Override
    public Class<K> getKeyType() {
        return this.keyType;
    }

    @Override
    public Class<V> getValueType() {
        return this.valueType;
    }

    public MutableConfiguration<K, V> setTypes(Class<K> keyType, Class<V> valueType) {
        if (keyType == null || valueType == null) {
            throw new NullPointerException("keyType and/or valueType can't be null");
        }
        this.keyType = keyType;
        this.valueType = valueType;
        return this;
    }

    @Override
    public Iterable<CacheEntryListenerConfiguration<K, V>> getCacheEntryListenerConfigurations() {
        return this.listenerConfigurations;
    }

    public MutableConfiguration<K, V> addCacheEntryListenerConfiguration(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        if (cacheEntryListenerConfiguration == null) {
            throw new NullPointerException("CacheEntryListenerConfiguration can't be null");
        }
        boolean alreadyExists = false;
        for (CacheEntryListenerConfiguration<K, V> c : this.listenerConfigurations) {
            if (!c.equals(cacheEntryListenerConfiguration)) continue;
            alreadyExists = true;
        }
        if (alreadyExists) {
            throw new IllegalArgumentException("A CacheEntryListenerConfiguration can be registered only once");
        }
        this.listenerConfigurations.add(cacheEntryListenerConfiguration);
        return this;
    }

    public MutableConfiguration<K, V> removeCacheEntryListenerConfiguration(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        if (cacheEntryListenerConfiguration == null) {
            throw new NullPointerException("CacheEntryListenerConfiguration can't be null");
        }
        this.listenerConfigurations.remove(cacheEntryListenerConfiguration);
        return this;
    }

    @Override
    public Factory<CacheLoader<K, V>> getCacheLoaderFactory() {
        return this.cacheLoaderFactory;
    }

    public MutableConfiguration<K, V> setCacheLoaderFactory(Factory<? extends CacheLoader<K, V>> factory) {
        this.cacheLoaderFactory = factory;
        return this;
    }

    @Override
    public Factory<CacheWriter<? super K, ? super V>> getCacheWriterFactory() {
        return this.cacheWriterFactory;
    }

    public MutableConfiguration<K, V> setCacheWriterFactory(Factory<? extends CacheWriter<? super K, ? super V>> factory) {
        this.cacheWriterFactory = factory;
        return this;
    }

    @Override
    public Factory<ExpiryPolicy> getExpiryPolicyFactory() {
        return this.expiryPolicyFactory;
    }

    public MutableConfiguration<K, V> setExpiryPolicyFactory(Factory<? extends ExpiryPolicy> factory) {
        this.expiryPolicyFactory = factory == null ? EternalExpiryPolicy.factoryOf() : factory;
        return this;
    }

    @Override
    public boolean isReadThrough() {
        return this.isReadThrough;
    }

    public MutableConfiguration<K, V> setReadThrough(boolean isReadThrough) {
        this.isReadThrough = isReadThrough;
        return this;
    }

    @Override
    public boolean isWriteThrough() {
        return this.isWriteThrough;
    }

    public MutableConfiguration<K, V> setWriteThrough(boolean isWriteThrough) {
        this.isWriteThrough = isWriteThrough;
        return this;
    }

    @Override
    public boolean isStoreByValue() {
        return this.isStoreByValue;
    }

    public MutableConfiguration<K, V> setStoreByValue(boolean isStoreByValue) {
        this.isStoreByValue = isStoreByValue;
        return this;
    }

    @Override
    public boolean isStatisticsEnabled() {
        return this.isStatisticsEnabled;
    }

    public MutableConfiguration<K, V> setStatisticsEnabled(boolean enabled) {
        this.isStatisticsEnabled = enabled;
        return this;
    }

    @Override
    public boolean isManagementEnabled() {
        return this.isManagementEnabled;
    }

    public MutableConfiguration<K, V> setManagementEnabled(boolean enabled) {
        this.isManagementEnabled = enabled;
        return this;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.keyType.hashCode();
        result = 31 * result + this.valueType.hashCode();
        result = 31 * result + (this.listenerConfigurations == null ? 0 : this.listenerConfigurations.hashCode());
        result = 31 * result + (this.cacheLoaderFactory == null ? 0 : this.cacheLoaderFactory.hashCode());
        result = 31 * result + (this.cacheWriterFactory == null ? 0 : this.cacheWriterFactory.hashCode());
        result = 31 * result + (this.expiryPolicyFactory == null ? 0 : this.expiryPolicyFactory.hashCode());
        result = 31 * result + (this.isReadThrough ? 1231 : 1237);
        result = 31 * result + (this.isStatisticsEnabled ? 1231 : 1237);
        result = 31 * result + (this.isStoreByValue ? 1231 : 1237);
        result = 31 * result + (this.isWriteThrough ? 1231 : 1237);
        return result;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof MutableConfiguration)) {
            return false;
        }
        MutableConfiguration other = (MutableConfiguration)object;
        if (!this.keyType.equals(other.keyType)) {
            return false;
        }
        if (!this.valueType.equals(other.valueType)) {
            return false;
        }
        if (!this.listenerConfigurations.equals(other.listenerConfigurations)) {
            return false;
        }
        if (this.cacheLoaderFactory == null ? other.cacheLoaderFactory != null : !this.cacheLoaderFactory.equals(other.cacheLoaderFactory)) {
            return false;
        }
        if (this.cacheWriterFactory == null ? other.cacheWriterFactory != null : !this.cacheWriterFactory.equals(other.cacheWriterFactory)) {
            return false;
        }
        if (this.expiryPolicyFactory == null ? other.expiryPolicyFactory != null : !this.expiryPolicyFactory.equals(other.expiryPolicyFactory)) {
            return false;
        }
        if (this.isReadThrough != other.isReadThrough) {
            return false;
        }
        if (this.isStatisticsEnabled != other.isStatisticsEnabled) {
            return false;
        }
        if (this.isStoreByValue != other.isStoreByValue) {
            return false;
        }
        return this.isWriteThrough == other.isWriteThrough;
    }
}

