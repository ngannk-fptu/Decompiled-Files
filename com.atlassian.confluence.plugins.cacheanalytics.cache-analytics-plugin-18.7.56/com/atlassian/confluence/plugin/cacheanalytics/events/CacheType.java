/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.ManagedCache
 */
package com.atlassian.confluence.plugin.cacheanalytics.events;

import com.atlassian.cache.ManagedCache;

public enum CacheType {
    LOCAL,
    REPLICATE_VIA_COPY,
    REPLICATE_VIA_INVALIDATION;


    public static CacheType forCache(ManagedCache cache) {
        if (cache.isLocal()) {
            return LOCAL;
        }
        if (cache.isReplicateViaCopy()) {
            return REPLICATE_VIA_COPY;
        }
        return REPLICATE_VIA_INVALIDATION;
    }
}

