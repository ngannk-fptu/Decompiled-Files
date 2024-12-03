/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AliasedDiscoveryConfig;

public class EurekaConfig
extends AliasedDiscoveryConfig<EurekaConfig> {
    public EurekaConfig() {
        super("eureka");
    }

    public EurekaConfig(EurekaConfig eurekaConfig) {
        super(eurekaConfig);
    }

    @Override
    public int getId() {
        return 57;
    }
}

