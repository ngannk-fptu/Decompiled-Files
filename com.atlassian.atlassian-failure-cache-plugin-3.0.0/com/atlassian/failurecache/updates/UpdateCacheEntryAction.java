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

public class UpdateCacheEntryAction<K, V>
implements MutateCacheAction<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(UpdateCacheEntryAction.class);
    private K key;
    private ExpiringValue<V> oldValue;
    private ExpiringValue<V> newValue;

    public UpdateCacheEntryAction(K key, ExpiringValue<V> oldValue, ExpiringValue<V> newValue) {
        this.key = Preconditions.checkNotNull(key, (Object)"key");
        this.oldValue = (ExpiringValue)Preconditions.checkNotNull(oldValue, (Object)"oldValue");
        this.newValue = (ExpiringValue)Preconditions.checkNotNull(newValue, (Object)"newValue");
    }

    @Override
    public void apply(MutableCache<K, V> cache) {
        if (!cache.replace(this.key, this.oldValue, this.newValue)) {
            logger.debug("Cache entry with key '{}' was modified while refreshing - not updating value.", this.key);
        }
    }
}

