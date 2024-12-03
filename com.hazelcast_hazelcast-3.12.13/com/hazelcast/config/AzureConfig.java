/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AliasedDiscoveryConfig;

public class AzureConfig
extends AliasedDiscoveryConfig<AzureConfig> {
    public AzureConfig() {
        super("azure");
    }

    public AzureConfig(AzureConfig azureConfig) {
        super(azureConfig);
    }

    @Override
    public int getId() {
        return 59;
    }
}

