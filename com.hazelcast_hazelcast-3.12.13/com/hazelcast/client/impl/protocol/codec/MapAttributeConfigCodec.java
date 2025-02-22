/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.config.MapAttributeConfig;

public final class MapAttributeConfigCodec {
    private MapAttributeConfigCodec() {
    }

    public static MapAttributeConfig decode(ClientMessage clientMessage) {
        MapAttributeConfig config = new MapAttributeConfig();
        config.setName(clientMessage.getStringUtf8());
        config.setExtractor(clientMessage.getStringUtf8());
        return config;
    }

    public static void encode(MapAttributeConfig config, ClientMessage clientMessage) {
        clientMessage.set(config.getName()).set(config.getExtractor());
    }

    public static int calculateDataSize(MapAttributeConfig config) {
        int dataSize = ParameterUtil.calculateDataSize(config.getName());
        return dataSize += ParameterUtil.calculateDataSize(config.getExtractor());
    }
}

