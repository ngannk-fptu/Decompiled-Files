/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.config.MapIndexConfig;

public final class MapIndexConfigCodec {
    private MapIndexConfigCodec() {
    }

    public static MapIndexConfig decode(ClientMessage clientMessage) {
        MapIndexConfig config = new MapIndexConfig();
        config.setAttribute(clientMessage.getStringUtf8());
        config.setOrdered(clientMessage.getBoolean());
        return config;
    }

    public static void encode(MapIndexConfig config, ClientMessage clientMessage) {
        clientMessage.set(config.getAttribute()).set(config.isOrdered());
    }

    public static int calculateDataSize(MapIndexConfig config) {
        int dataSize = 1;
        return dataSize += ParameterUtil.calculateDataSize(config.getAttribute());
    }
}

