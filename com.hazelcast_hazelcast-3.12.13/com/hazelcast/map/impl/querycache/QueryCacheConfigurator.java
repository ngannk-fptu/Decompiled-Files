/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache;

import com.hazelcast.config.QueryCacheConfig;

public interface QueryCacheConfigurator {
    public QueryCacheConfig getOrCreateConfiguration(String var1, String var2, String var3);

    public QueryCacheConfig getOrNull(String var1, String var2, String var3);

    public void removeConfiguration(String var1, String var2);
}

