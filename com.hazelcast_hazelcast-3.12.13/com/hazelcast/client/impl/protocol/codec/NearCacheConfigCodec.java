/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.EvictionConfigCodec;
import com.hazelcast.client.impl.protocol.codec.NearCachePreloaderConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.EvictionConfigHolder;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.NearCacheConfigHolder;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.config.NearCachePreloaderConfig;

public final class NearCacheConfigCodec {
    private static final int ENCODED_BOOLEANS = 4;
    private static final int ENCODED_INTS = 2;

    private NearCacheConfigCodec() {
    }

    public static NearCacheConfigHolder decode(ClientMessage clientMessage) {
        String name = clientMessage.getStringUtf8();
        String inMemoryFormat = clientMessage.getStringUtf8();
        boolean serializeKeys = clientMessage.getBoolean();
        boolean invalidateOnChange = clientMessage.getBoolean();
        int timeToLiveSeconds = clientMessage.getInt();
        int maxIdleSeconds = clientMessage.getInt();
        boolean cacheLocalEntries = clientMessage.getBoolean();
        String localUpdatePolicy = clientMessage.getStringUtf8();
        EvictionConfigHolder evictionConfigHolder = EvictionConfigCodec.decode(clientMessage);
        boolean isNullPreloaderConfig = clientMessage.getBoolean();
        NearCachePreloaderConfig preloaderConfigHolder = null;
        if (!isNullPreloaderConfig) {
            preloaderConfigHolder = NearCachePreloaderConfigCodec.decode(clientMessage);
        }
        return new NearCacheConfigHolder(name, inMemoryFormat, serializeKeys, invalidateOnChange, timeToLiveSeconds, maxIdleSeconds, evictionConfigHolder, cacheLocalEntries, localUpdatePolicy, preloaderConfigHolder);
    }

    public static void encode(NearCacheConfigHolder config, ClientMessage clientMessage) {
        clientMessage.set(config.getName()).set(config.getInMemoryFormat()).set(config.isSerializeKeys()).set(config.isInvalidateOnChange()).set(config.getTimeToLiveSeconds()).set(config.getMaxIdleSeconds()).set(config.isCacheLocalEntries()).set(config.getLocalUpdatePolicy());
        EvictionConfigCodec.encode(config.getEvictionConfigHolder(), clientMessage);
        boolean isNullPreloaderConfig = config.getPreloaderConfig() == null;
        clientMessage.set(isNullPreloaderConfig);
        if (!isNullPreloaderConfig) {
            NearCachePreloaderConfigCodec.encode(config.getPreloaderConfig(), clientMessage);
        }
    }

    public static int calculateDataSize(NearCacheConfigHolder config) {
        int dataSize = 12;
        dataSize += ParameterUtil.calculateDataSize(config.getName());
        dataSize += ParameterUtil.calculateDataSize(config.getInMemoryFormat());
        dataSize += ParameterUtil.calculateDataSize(config.getLocalUpdatePolicy());
        dataSize += EvictionConfigCodec.calculateDataSize(config.getEvictionConfigHolder());
        if (config.getPreloaderConfig() != null) {
            dataSize += NearCachePreloaderConfigCodec.calculateDataSize(config.getPreloaderConfig());
        }
        return dataSize;
    }
}

