/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.config.NearCachePreloaderConfig;

public final class NearCachePreloaderConfigCodec {
    private NearCachePreloaderConfigCodec() {
    }

    public static NearCachePreloaderConfig decode(ClientMessage clientMessage) {
        NearCachePreloaderConfig config = new NearCachePreloaderConfig();
        config.setEnabled(clientMessage.getBoolean());
        config.setDirectory(clientMessage.getStringUtf8());
        config.setStoreInitialDelaySeconds(clientMessage.getInt());
        config.setStoreIntervalSeconds(clientMessage.getInt());
        return config;
    }

    public static void encode(NearCachePreloaderConfig config, ClientMessage clientMessage) {
        clientMessage.set(config.isEnabled()).set(config.getDirectory()).set(config.getStoreInitialDelaySeconds()).set(config.getStoreIntervalSeconds());
    }

    public static int calculateDataSize(NearCachePreloaderConfig config) {
        int dataSize = 9;
        return dataSize += ParameterUtil.calculateDataSize(config.getDirectory());
    }
}

