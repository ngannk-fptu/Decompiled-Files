/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.QueueStoreConfig;
import com.hazelcast.core.QueueStore;
import com.hazelcast.core.QueueStoreFactory;
import java.util.Properties;

public class QueueStoreConfigReadOnly
extends QueueStoreConfig {
    public QueueStoreConfigReadOnly(QueueStoreConfig config) {
        super(config);
    }

    @Override
    public QueueStoreConfig setStoreImplementation(QueueStore storeImplementation) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public QueueStoreConfig setEnabled(boolean enabled) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public QueueStoreConfig setClassName(String className) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public QueueStoreConfig setProperties(Properties properties) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public QueueStoreConfig setProperty(String name, String value) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public QueueStoreConfig setFactoryClassName(String factoryClassName) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public QueueStoreConfig setFactoryImplementation(QueueStoreFactory factoryImplementation) {
        throw new UnsupportedOperationException("This config is read-only");
    }
}

