/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.vcache.LocalCacheOperations;
import com.atlassian.vcache.VCache;
import java.util.Set;

@PublicApi
public interface JvmCache<K, V>
extends VCache,
LocalCacheOperations<K, V> {
    public Set<K> getKeys();
}

