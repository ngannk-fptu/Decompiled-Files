/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.cache.Cache
 */
package com.atlassian.confluence.plugin.cache;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.cache.Cache;

@ExperimentalApi
public interface PluginCacheResolver {
    public <K, V> Cache<K, V> getCache(String var1);
}

