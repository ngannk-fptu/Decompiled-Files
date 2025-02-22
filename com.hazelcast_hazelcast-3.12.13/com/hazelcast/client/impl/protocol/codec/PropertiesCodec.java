/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import java.util.Properties;
import java.util.Set;

public final class PropertiesCodec {
    private PropertiesCodec() {
    }

    public static Properties decode(ClientMessage clientMessage) {
        int entriesCount = clientMessage.getInt();
        Properties properties = new Properties();
        for (int i = 0; i < entriesCount; ++i) {
            properties.setProperty(clientMessage.getStringUtf8(), clientMessage.getStringUtf8());
        }
        return properties;
    }

    public static void encode(Properties properties, ClientMessage clientMessage) {
        Set<String> propertyKeys = properties.stringPropertyNames();
        clientMessage.set(propertyKeys.size());
        for (String key : propertyKeys) {
            clientMessage.set(key).set(properties.getProperty(key));
        }
    }

    public static int calculateDataSize(Properties properties) {
        int dataSize = 4;
        Set<String> propertyKeys = properties.stringPropertyNames();
        for (String key : propertyKeys) {
            dataSize += ParameterUtil.calculateDataSize(key);
            dataSize += ParameterUtil.calculateDataSize(properties.getProperty(key));
        }
        return dataSize;
    }
}

