/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cache.ConfluenceCache
 *  com.google.common.annotations.VisibleForTesting
 *  net.sf.ehcache.config.CacheConfiguration
 */
package com.atlassian.confluence.cache.ehcache;

import com.atlassian.confluence.cache.ConfluenceCache;
import com.google.common.annotations.VisibleForTesting;
import net.sf.ehcache.config.CacheConfiguration;

interface ConfluenceEhCache<K, V>
extends ConfluenceCache<K, V> {
    public boolean updateMaxEntriesLocalHeap(long var1);

    @VisibleForTesting
    public CacheConfiguration getEhCacheConfiguration();
}

