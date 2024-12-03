/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.nio.serialization.BinaryInterface;

@BinaryInterface
public class MaxSizeConfigReadOnly
extends MaxSizeConfig {
    public MaxSizeConfigReadOnly(MaxSizeConfig config) {
        super(config);
    }

    @Override
    public MaxSizeConfig setSize(int size) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public MaxSizeConfig setMaxSizePolicy(MaxSizeConfig.MaxSizePolicy maxSizePolicy) {
        throw new UnsupportedOperationException("This config is read-only");
    }
}

