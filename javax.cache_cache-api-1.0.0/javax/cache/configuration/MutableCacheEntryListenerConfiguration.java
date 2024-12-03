/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.configuration;

import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryListener;

public class MutableCacheEntryListenerConfiguration<K, V>
implements CacheEntryListenerConfiguration<K, V> {
    public static final long serialVersionUID = 201306200822L;
    private Factory<CacheEntryListener<? super K, ? super V>> listenerFactory;
    private Factory<CacheEntryEventFilter<? super K, ? super V>> filterFactory;
    private boolean isOldValueRequired;
    private boolean isSynchronous;

    public MutableCacheEntryListenerConfiguration(CacheEntryListenerConfiguration<K, V> configuration) {
        this.listenerFactory = configuration.getCacheEntryListenerFactory();
        this.filterFactory = configuration.getCacheEntryEventFilterFactory();
        this.isOldValueRequired = configuration.isOldValueRequired();
        this.isSynchronous = configuration.isSynchronous();
    }

    public MutableCacheEntryListenerConfiguration(Factory<? extends CacheEntryListener<? super K, ? super V>> listenerFactory, Factory<? extends CacheEntryEventFilter<? super K, ? super V>> filterFactory, boolean isOldValueRequired, boolean isSynchronous) {
        this.listenerFactory = listenerFactory;
        this.filterFactory = filterFactory;
        this.isOldValueRequired = isOldValueRequired;
        this.isSynchronous = isSynchronous;
    }

    @Override
    public Factory<CacheEntryListener<? super K, ? super V>> getCacheEntryListenerFactory() {
        return this.listenerFactory;
    }

    public MutableCacheEntryListenerConfiguration<K, V> setCacheEntryListenerFactory(Factory<? extends CacheEntryListener<? super K, ? super V>> listenerFactory) {
        this.listenerFactory = listenerFactory;
        return this;
    }

    @Override
    public Factory<CacheEntryEventFilter<? super K, ? super V>> getCacheEntryEventFilterFactory() {
        return this.filterFactory;
    }

    public MutableCacheEntryListenerConfiguration<K, V> setCacheEntryEventFilterFactory(Factory<? extends CacheEntryEventFilter<? super K, ? super V>> filterFactory) {
        this.filterFactory = filterFactory;
        return this;
    }

    @Override
    public boolean isOldValueRequired() {
        return this.isOldValueRequired;
    }

    public MutableCacheEntryListenerConfiguration<K, V> setOldValueRequired(boolean isOldValueRequired) {
        this.isOldValueRequired = isOldValueRequired;
        return this;
    }

    @Override
    public boolean isSynchronous() {
        return this.isSynchronous;
    }

    public MutableCacheEntryListenerConfiguration<K, V> setSynchronous(boolean isSynchronous) {
        this.isSynchronous = isSynchronous;
        return this;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.filterFactory == null ? 0 : this.filterFactory.hashCode());
        result = 31 * result + (this.isOldValueRequired ? 1231 : 1237);
        result = 31 * result + (this.isSynchronous ? 1231 : 1237);
        result = 31 * result + (this.listenerFactory == null ? 0 : this.listenerFactory.hashCode());
        return result;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof MutableCacheEntryListenerConfiguration)) {
            return false;
        }
        MutableCacheEntryListenerConfiguration other = (MutableCacheEntryListenerConfiguration)object;
        if (this.filterFactory == null ? other.filterFactory != null : !this.filterFactory.equals(other.filterFactory)) {
            return false;
        }
        if (this.isOldValueRequired != other.isOldValueRequired) {
            return false;
        }
        if (this.isSynchronous != other.isSynchronous) {
            return false;
        }
        return !(this.listenerFactory == null ? other.listenerFactory != null : !this.listenerFactory.equals(other.listenerFactory));
    }
}

