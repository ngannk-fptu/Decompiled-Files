/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.config.QueueStoreConfig;
import com.hazelcast.core.QueueStore;
import com.hazelcast.core.QueueStoreFactory;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.Properties;

public class QueueStoreConfigHolder {
    private final String className;
    private final String factoryClassName;
    private final Data implementation;
    private final Data factoryImplementation;
    private final Properties properties;
    private final boolean enabled;

    public QueueStoreConfigHolder(String className, String factoryClassName, Data implementation, Data factoryImplementation, Properties properties, boolean enabled) {
        this.className = className;
        this.factoryClassName = factoryClassName;
        this.implementation = implementation;
        this.factoryImplementation = factoryImplementation;
        this.properties = properties;
        this.enabled = enabled;
    }

    public String getClassName() {
        return this.className;
    }

    public String getFactoryClassName() {
        return this.factoryClassName;
    }

    public Data getImplementation() {
        return this.implementation;
    }

    public Data getFactoryImplementation() {
        return this.factoryImplementation;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public QueueStoreConfig asQueueStoreConfig(SerializationService serializationService) {
        QueueStoreConfig config = new QueueStoreConfig();
        config.setClassName(this.className);
        config.setEnabled(this.enabled);
        config.setFactoryClassName(this.factoryClassName);
        config.setProperties(this.properties);
        QueueStore storeImplementation = (QueueStore)serializationService.toObject(this.implementation);
        QueueStoreFactory storeFactoryImplementation = (QueueStoreFactory)serializationService.toObject(this.factoryImplementation);
        config.setStoreImplementation(storeImplementation);
        config.setFactoryImplementation(storeFactoryImplementation);
        return config;
    }

    public static QueueStoreConfigHolder of(QueueStoreConfig queueStoreConfig, SerializationService serializationService) {
        if (queueStoreConfig == null) {
            return null;
        }
        if (queueStoreConfig.getClassName() == null && queueStoreConfig.getFactoryClassName() == null && queueStoreConfig.getStoreImplementation() == null && queueStoreConfig.getFactoryImplementation() == null && queueStoreConfig.isEnabled()) {
            throw new IllegalArgumentException("One of className, factoryClassName, storeImplementation, factoryImplementation has to be not null");
        }
        return new QueueStoreConfigHolder(queueStoreConfig.getClassName(), queueStoreConfig.getFactoryClassName(), (Data)serializationService.toData(queueStoreConfig.getStoreImplementation()), (Data)serializationService.toData(queueStoreConfig.getFactoryImplementation()), queueStoreConfig.getProperties(), queueStoreConfig.isEnabled());
    }
}

