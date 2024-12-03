/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.MapIndexConfig;

public class MapIndexConfigReadOnly
extends MapIndexConfig {
    public MapIndexConfigReadOnly(MapIndexConfig config) {
        super(config);
    }

    @Override
    public MapIndexConfig setAttribute(String attribute) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public MapIndexConfig setOrdered(boolean ordered) {
        throw new UnsupportedOperationException("This config is read-only");
    }
}

