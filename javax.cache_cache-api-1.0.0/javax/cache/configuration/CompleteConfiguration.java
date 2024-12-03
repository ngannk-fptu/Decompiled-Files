/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.configuration;

import java.io.Serializable;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.Factory;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;

public interface CompleteConfiguration<K, V>
extends Configuration<K, V>,
Serializable {
    public boolean isReadThrough();

    public boolean isWriteThrough();

    public boolean isStatisticsEnabled();

    public boolean isManagementEnabled();

    public Iterable<CacheEntryListenerConfiguration<K, V>> getCacheEntryListenerConfigurations();

    public Factory<CacheLoader<K, V>> getCacheLoaderFactory();

    public Factory<CacheWriter<? super K, ? super V>> getCacheWriterFactory();

    public Factory<ExpiryPolicy> getExpiryPolicyFactory();
}

