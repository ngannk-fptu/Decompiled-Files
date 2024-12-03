/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.dynamicconfig;

import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.internal.dynamicconfig.ClusterWideConfigurationService;
import com.hazelcast.internal.dynamicconfig.DynamicConfigListener;

public class EmptyDynamicConfigListener
implements DynamicConfigListener {
    @Override
    public void onConfigRegistered(MapConfig configObject) {
    }

    @Override
    public void onConfigRegistered(CacheSimpleConfig configObject) {
    }

    @Override
    public void onServiceInitialized(ClusterWideConfigurationService configurationService) {
    }
}

