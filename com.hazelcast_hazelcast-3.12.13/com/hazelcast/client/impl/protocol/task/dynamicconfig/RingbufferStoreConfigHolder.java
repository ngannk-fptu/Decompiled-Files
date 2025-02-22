/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.config.RingbufferStoreConfig;
import com.hazelcast.core.RingbufferStore;
import com.hazelcast.core.RingbufferStoreFactory;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.Properties;

public class RingbufferStoreConfigHolder {
    private final String className;
    private final String factoryClassName;
    private final Data implementation;
    private final Data factoryImplementation;
    private final Properties properties;
    private final boolean enabled;

    public RingbufferStoreConfigHolder(String className, String factoryClassName, Data implementation, Data factoryImplementation, Properties properties, boolean enabled) {
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

    public RingbufferStoreConfig asRingbufferStoreConfig(SerializationService serializationService) {
        RingbufferStoreConfig config = new RingbufferStoreConfig();
        config.setClassName(this.className);
        config.setEnabled(this.enabled);
        config.setFactoryClassName(this.factoryClassName);
        config.setProperties(this.properties);
        RingbufferStore storeImplementation = (RingbufferStore)serializationService.toObject(this.implementation);
        RingbufferStoreFactory storeFactoryImplementation = (RingbufferStoreFactory)serializationService.toObject(this.factoryImplementation);
        config.setStoreImplementation(storeImplementation);
        config.setFactoryImplementation(storeFactoryImplementation);
        return config;
    }

    public static RingbufferStoreConfigHolder of(RingbufferStoreConfig ringbufferStoreConfig, SerializationService serializationService) {
        if (ringbufferStoreConfig.getClassName() == null && ringbufferStoreConfig.getFactoryClassName() == null && ringbufferStoreConfig.getStoreImplementation() == null && ringbufferStoreConfig.getFactoryImplementation() == null && ringbufferStoreConfig.isEnabled()) {
            throw new IllegalArgumentException("One of className, factoryClassName, storeImplementation, factoryImplementation has to be not null");
        }
        return new RingbufferStoreConfigHolder(ringbufferStoreConfig.getClassName(), ringbufferStoreConfig.getFactoryClassName(), (Data)serializationService.toData(ringbufferStoreConfig.getStoreImplementation()), (Data)serializationService.toData(ringbufferStoreConfig.getFactoryImplementation()), ringbufferStoreConfig.getProperties(), ringbufferStoreConfig.isEnabled());
    }
}

