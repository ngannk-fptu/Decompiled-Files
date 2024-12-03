/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.event;

import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryListenerException;

public interface CacheEntryRemovedListener<K, V>
extends CacheEntryListener<K, V> {
    public void onRemoved(Iterable<CacheEntryEvent<? extends K, ? extends V>> var1) throws CacheEntryListenerException;
}

