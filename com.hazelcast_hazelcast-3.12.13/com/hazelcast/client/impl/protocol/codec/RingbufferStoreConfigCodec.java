/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.PropertiesCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.RingbufferStoreConfigHolder;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import java.util.Properties;

public final class RingbufferStoreConfigCodec {
    private static final byte CONFIG_TYPE_CLASS_NAME = 0;
    private static final byte CONFIG_TYPE_FACTORY_CLASS_NAME = 1;
    private static final byte CONFIG_TYPE_STORE_IMPLEMENTATION = 2;
    private static final byte CONFIG_TYPE_FACTORY_IMPLEMENTATION = 3;

    private RingbufferStoreConfigCodec() {
    }

    public static RingbufferStoreConfigHolder decode(ClientMessage clientMessage) {
        byte storeConfigType = clientMessage.getByte();
        String className = null;
        String factoryClassName = null;
        Data implementation = null;
        Data factoryImplementation = null;
        switch (storeConfigType) {
            case 0: {
                className = clientMessage.getStringUtf8();
                break;
            }
            case 1: {
                factoryClassName = clientMessage.getStringUtf8();
                break;
            }
            case 2: {
                implementation = clientMessage.getData();
                break;
            }
            case 3: {
                factoryImplementation = clientMessage.getData();
                break;
            }
            default: {
                throw new HazelcastSerializationException(String.format("Cannot decode ringbuffer store type %d", storeConfigType));
            }
        }
        boolean isNullProperties = clientMessage.getBoolean();
        Properties properties = null;
        if (!isNullProperties) {
            properties = PropertiesCodec.decode(clientMessage);
        }
        boolean enabled = clientMessage.getBoolean();
        return new RingbufferStoreConfigHolder(className, factoryClassName, implementation, factoryImplementation, properties, enabled);
    }

    public static void encode(RingbufferStoreConfigHolder storeConfig, ClientMessage clientMessage) {
        if (storeConfig.getImplementation() != null) {
            clientMessage.set((byte)2).set(storeConfig.getImplementation());
        } else if (storeConfig.getClassName() != null) {
            clientMessage.set((byte)0).set(storeConfig.getClassName());
        } else if (storeConfig.getFactoryImplementation() != null) {
            clientMessage.set((byte)3).set(storeConfig.getFactoryImplementation());
        } else {
            clientMessage.set((byte)1).set(storeConfig.getFactoryClassName());
        }
        boolean isNullProperties = storeConfig.getProperties() == null;
        clientMessage.set(isNullProperties);
        if (!isNullProperties) {
            PropertiesCodec.encode(storeConfig.getProperties(), clientMessage);
        }
        clientMessage.set(storeConfig.isEnabled());
    }

    public static int calculateDataSize(RingbufferStoreConfigHolder storeConfig) {
        int dataSize = 3;
        dataSize = storeConfig.getImplementation() != null ? (dataSize += ParameterUtil.calculateDataSize(storeConfig.getImplementation())) : (storeConfig.getClassName() != null ? (dataSize += ParameterUtil.calculateDataSize(storeConfig.getClassName())) : (storeConfig.getFactoryImplementation() != null ? (dataSize += ParameterUtil.calculateDataSize(storeConfig.getFactoryImplementation())) : (dataSize += ParameterUtil.calculateDataSize(storeConfig.getFactoryClassName()))));
        return dataSize += PropertiesCodec.calculateDataSize(storeConfig.getProperties());
    }
}

