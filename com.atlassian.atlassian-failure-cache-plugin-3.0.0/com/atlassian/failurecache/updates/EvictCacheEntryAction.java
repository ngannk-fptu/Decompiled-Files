/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.failurecache.updates;

import com.atlassian.failurecache.ExpiringValue;
import com.atlassian.failurecache.MutableCache;
import com.atlassian.failurecache.updates.MutateCacheAction;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvictCacheEntryAction<K, V>
implements MutateCacheAction<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(EvictCacheEntryAction.class);
    private final K key;
    private final ExpiringValue<V> currentValue;

    public EvictCacheEntryAction(K key, ExpiringValue<V> currentValue) {
        this.key = Preconditions.checkNotNull(key, (Object)"key");
        this.currentValue = (ExpiringValue)Preconditions.checkNotNull(currentValue, (Object)"currentValue");
    }

    @Override
    public void apply(MutableCache<K, V> cache) {
        if (!cache.remove(this.key, this.currentValue)) {
            logger.debug("Cache entry with key '{}' was modified while refreshing - not removing value.", this.key);
        }
    }
}

