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

@PublicApi
public interface RequestCache<K, V>
extends VCache,
LocalCacheOperations<K, V> {
}

