/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nonnull.ReturnValuesAreNonnullByDefault
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.cache.hazelcast;

import com.atlassian.annotations.nonnull.ReturnValuesAreNonnullByDefault;
import com.atlassian.cache.hazelcast.HazelcastNameFactory;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class LegacyPrefixedNameFactory
implements HazelcastNameFactory {
    static final String PREFIX = "atlassian-cache.";
    static final String PREFIX_CACHE_REFERENCE = "atlassian-cache.CacheReference.";
    static final String PREFIX_CACHE = "atlassian-cache.Cache.";
    static final String CACHE_VERSION_PREFIX = "_CACHE_VERSION.";

    @Override
    public String getCacheInvalidationTopicName(String cacheName) {
        return PREFIX_CACHE + cacheName;
    }

    @Override
    public String getCachedReferenceInvalidationTopicName(String cacheName) {
        return PREFIX_CACHE_REFERENCE + cacheName;
    }

    @Override
    public String getCacheIMapName(String cacheName) {
        return PREFIX_CACHE + cacheName;
    }

    @Override
    public String getCachedReferenceIMapName(String cacheName) {
        return PREFIX_CACHE_REFERENCE + cacheName;
    }

    @Override
    public String getCacheVersionCounterName(String cacheName) {
        return CACHE_VERSION_PREFIX + cacheName;
    }
}

