/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.PropertiesCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.MapStoreConfigHolder;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;

public final class MapStoreConfigCodec {
    private static final int ENCODED_BOOLEANS = 7;
    private static final int ENCODED_INTS = 2;

    private MapStoreConfigCodec() {
    }

    public static MapStoreConfigHolder decode(ClientMessage clientMessage) {
        boolean isNullFactoryImplementation;
        boolean isNullImplementation;
        boolean isNullFactoryClassName;
        boolean isNullClassName;
        MapStoreConfigHolder config = new MapStoreConfigHolder();
        config.setEnabled(clientMessage.getBoolean());
        config.setWriteCoalescing(clientMessage.getBoolean());
        config.setWriteDelaySeconds(clientMessage.getInt());
        config.setWriteBatchSize(clientMessage.getInt());
        config.setInitialLoadMode(clientMessage.getStringUtf8());
        boolean isNullProperties = clientMessage.getBoolean();
        if (!isNullProperties) {
            config.setProperties(PropertiesCodec.decode(clientMessage));
        }
        if (!(isNullClassName = clientMessage.getBoolean())) {
            config.setClassName(clientMessage.getStringUtf8());
        }
        if (!(isNullFactoryClassName = clientMessage.getBoolean())) {
            config.setFactoryClassName(clientMessage.getStringUtf8());
        }
        if (!(isNullImplementation = clientMessage.getBoolean())) {
            config.setImplementation(clientMessage.getData());
        }
        if (!(isNullFactoryImplementation = clientMessage.getBoolean())) {
            config.setFactoryImplementation(clientMessage.getData());
        }
        return config;
    }

    public static void encode(MapStoreConfigHolder config, ClientMessage clientMessage) {
        clientMessage.set(config.isEnabled()).set(config.isWriteCoalescing()).set(config.getWriteDelaySeconds()).set(config.getWriteBatchSize()).set(config.getInitialLoadMode());
        boolean isNullProperties = config.getProperties() == null;
        clientMessage.set(isNullProperties);
        if (!isNullProperties) {
            PropertiesCodec.encode(config.getProperties(), clientMessage);
        }
        boolean isNullClassName = config.getClassName() == null;
        clientMessage.set(isNullClassName);
        if (!isNullClassName) {
            clientMessage.set(config.getClassName());
        }
        boolean isNullFactoryClassName = config.getFactoryClassName() == null;
        clientMessage.set(isNullFactoryClassName);
        if (!isNullFactoryClassName) {
            clientMessage.set(config.getFactoryClassName());
        }
        boolean isNullImplementation = config.getImplementation() == null;
        clientMessage.set(isNullImplementation);
        if (!isNullImplementation) {
            clientMessage.set(config.getImplementation());
        }
        boolean isNullFactoryImplementation = config.getFactoryImplementation() == null;
        clientMessage.set(isNullFactoryImplementation);
        if (!isNullFactoryImplementation) {
            clientMessage.set(config.getFactoryImplementation());
        }
    }

    public static int calculateDataSize(MapStoreConfigHolder config) {
        int dataSize = 15;
        if (config.getProperties() != null) {
            dataSize += PropertiesCodec.calculateDataSize(config.getProperties());
        }
        dataSize += ParameterUtil.calculateDataSize(config.getInitialLoadMode());
        if (config.getClassName() != null) {
            dataSize += ParameterUtil.calculateDataSize(config.getClassName());
        }
        if (config.getFactoryClassName() != null) {
            dataSize += ParameterUtil.calculateDataSize(config.getFactoryClassName());
        }
        if (config.getImplementation() != null) {
            dataSize += ParameterUtil.calculateDataSize(config.getImplementation());
        }
        if (config.getFactoryImplementation() != null) {
            dataSize += ParameterUtil.calculateDataSize(config.getFactoryImplementation());
        }
        return dataSize;
    }
}

