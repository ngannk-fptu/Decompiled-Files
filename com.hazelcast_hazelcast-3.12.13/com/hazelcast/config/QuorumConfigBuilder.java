/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.QuorumConfig;

public abstract class QuorumConfigBuilder {
    protected int size;
    protected boolean enabled = true;

    public QuorumConfigBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public QuorumConfigBuilder withQuorumSize(int size) {
        this.size = size;
        return this;
    }

    public abstract QuorumConfig build();
}

