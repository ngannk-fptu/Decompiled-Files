/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.Properties;

public class MapStoreConfigHolder {
    private boolean enabled;
    private boolean writeCoalescing;
    private String className;
    private String factoryClassName;
    private int writeDelaySeconds;
    private int writeBatchSize;
    private Data implementation;
    private Data factoryImplementation;
    private Properties properties;
    private String initialLoadMode;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isWriteCoalescing() {
        return this.writeCoalescing;
    }

    public void setWriteCoalescing(boolean writeCoalescing) {
        this.writeCoalescing = writeCoalescing;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFactoryClassName() {
        return this.factoryClassName;
    }

    public void setFactoryClassName(String factoryClassName) {
        this.factoryClassName = factoryClassName;
    }

    public int getWriteDelaySeconds() {
        return this.writeDelaySeconds;
    }

    public void setWriteDelaySeconds(int writeDelaySeconds) {
        this.writeDelaySeconds = writeDelaySeconds;
    }

    public int getWriteBatchSize() {
        return this.writeBatchSize;
    }

    public void setWriteBatchSize(int writeBatchSize) {
        this.writeBatchSize = writeBatchSize;
    }

    public Data getImplementation() {
        return this.implementation;
    }

    public void setImplementation(Data implementation) {
        this.implementation = implementation;
    }

    public Data getFactoryImplementation() {
        return this.factoryImplementation;
    }

    public void setFactoryImplementation(Data factoryImplementation) {
        this.factoryImplementation = factoryImplementation;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getInitialLoadMode() {
        return this.initialLoadMode;
    }

    public void setInitialLoadMode(String initialLoadMode) {
        this.initialLoadMode = initialLoadMode;
    }

    public MapStoreConfig asMapStoreConfig(SerializationService serializationService) {
        MapStoreConfig config = new MapStoreConfig();
        config.setClassName(this.className);
        config.setEnabled(this.enabled);
        config.setFactoryClassName(this.factoryClassName);
        config.setInitialLoadMode(MapStoreConfig.InitialLoadMode.valueOf(this.initialLoadMode));
        config.setProperties(this.properties);
        config.setWriteBatchSize(this.writeBatchSize);
        config.setWriteCoalescing(this.writeCoalescing);
        config.setWriteDelaySeconds(this.writeDelaySeconds);
        config.setImplementation(serializationService.toObject(this.implementation));
        config.setFactoryImplementation(serializationService.toObject(this.factoryImplementation));
        return config;
    }

    public static MapStoreConfigHolder of(MapStoreConfig config, SerializationService serializationService) {
        if (config == null) {
            return null;
        }
        MapStoreConfigHolder holder = new MapStoreConfigHolder();
        holder.setClassName(config.getClassName());
        holder.setEnabled(config.isEnabled());
        holder.setFactoryClassName(config.getFactoryClassName());
        holder.setFactoryImplementation((Data)serializationService.toData(config.getFactoryImplementation()));
        holder.setImplementation((Data)serializationService.toData(config.getImplementation()));
        holder.setInitialLoadMode(config.getInitialLoadMode().name());
        holder.setProperties(config.getProperties());
        holder.setWriteBatchSize(config.getWriteBatchSize());
        holder.setWriteCoalescing(config.isWriteCoalescing());
        holder.setWriteDelaySeconds(config.getWriteDelaySeconds());
        return holder;
    }
}

