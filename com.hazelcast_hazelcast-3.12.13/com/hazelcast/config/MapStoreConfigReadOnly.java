/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.MapStoreConfig;
import java.util.Properties;

public class MapStoreConfigReadOnly
extends MapStoreConfig {
    public MapStoreConfigReadOnly(MapStoreConfig config) {
        super(config);
    }

    @Override
    public MapStoreConfig setClassName(String className) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public MapStoreConfig setFactoryClassName(String factoryClassName) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public MapStoreConfig setWriteDelaySeconds(int writeDelaySeconds) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public MapStoreConfig setWriteBatchSize(int writeBatchSize) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public MapStoreConfig setEnabled(boolean enabled) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public MapStoreConfig setImplementation(Object implementation) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public MapStoreConfig setInitialLoadMode(MapStoreConfig.InitialLoadMode initialLoadMode) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public MapStoreConfig setFactoryImplementation(Object factoryImplementation) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public MapStoreConfig setProperty(String name, String value) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public MapStoreConfig setProperties(Properties properties) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public MapStoreConfig setWriteCoalescing(boolean writeCoalescing) {
        throw new UnsupportedOperationException("This config is read-only");
    }
}

