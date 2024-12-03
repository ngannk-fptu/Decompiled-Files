/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.event;

import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;

public interface CacheEntryEventFilter<K, V> {
    public boolean evaluate(CacheEntryEvent<? extends K, ? extends V> var1) throws CacheEntryListenerException;
}

