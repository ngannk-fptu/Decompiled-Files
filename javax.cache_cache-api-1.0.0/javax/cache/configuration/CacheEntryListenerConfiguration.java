/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.configuration;

import java.io.Serializable;
import javax.cache.configuration.Factory;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryListener;

public interface CacheEntryListenerConfiguration<K, V>
extends Serializable {
    public Factory<CacheEntryListener<? super K, ? super V>> getCacheEntryListenerFactory();

    public boolean isOldValueRequired();

    public Factory<CacheEntryEventFilter<? super K, ? super V>> getCacheEntryEventFilterFactory();

    public boolean isSynchronous();
}

