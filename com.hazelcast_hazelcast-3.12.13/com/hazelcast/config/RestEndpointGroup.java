/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

public enum RestEndpointGroup {
    CLUSTER_READ(true),
    CLUSTER_WRITE(false),
    HEALTH_CHECK(true),
    HOT_RESTART(false),
    WAN(false),
    DATA(false);

    private final boolean enabledByDefault;

    private RestEndpointGroup(boolean enabledByDefault) {
        this.enabledByDefault = enabledByDefault;
    }

    public boolean isEnabledByDefault() {
        return this.enabledByDefault;
    }
}

