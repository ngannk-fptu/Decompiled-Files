/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AliasedDiscoveryConfig;

public class GcpConfig
extends AliasedDiscoveryConfig<GcpConfig> {
    public GcpConfig() {
        super("gcp");
    }

    public GcpConfig(GcpConfig gcpConfig) {
        super(gcpConfig);
    }

    @Override
    public int getId() {
        return 58;
    }
}

