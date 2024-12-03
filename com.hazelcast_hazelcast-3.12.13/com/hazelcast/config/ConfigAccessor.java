/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AdvancedNetworkConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.spi.annotation.PrivateApi;

@PrivateApi
public final class ConfigAccessor {
    private ConfigAccessor() {
    }

    public static NetworkConfig getActiveMemberNetworkConfig(Config config) {
        if (config.getAdvancedNetworkConfig().isEnabled()) {
            return new AdvancedNetworkConfig.MemberNetworkingView(config.getAdvancedNetworkConfig());
        }
        return config.getNetworkConfig();
    }
}

