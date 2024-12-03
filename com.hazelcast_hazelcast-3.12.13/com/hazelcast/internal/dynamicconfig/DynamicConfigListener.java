/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.dynamicconfig;

import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.internal.dynamicconfig.ClusterWideConfigurationService;

public interface DynamicConfigListener {
    public void onServiceInitialized(ClusterWideConfigurationService var1);

    public void onConfigRegistered(MapConfig var1);

    public void onConfigRegistered(CacheSimpleConfig var1);
}

