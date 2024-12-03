/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.MapAttributeConfig;

public class MapAttributeConfigReadOnly
extends MapAttributeConfig {
    public MapAttributeConfigReadOnly(MapAttributeConfig config) {
        super(config);
    }

    @Override
    public MapAttributeConfig setName(String attribute) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public MapAttributeConfig setExtractor(String type) {
        throw new UnsupportedOperationException("This config is read-only");
    }
}

