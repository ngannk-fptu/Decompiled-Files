/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.config.CacheSimpleEntryListenerConfig;

public final class CacheSimpleEntryListenerConfigCodec {
    private static final int ENCODED_BOOLEANS = 4;

    private CacheSimpleEntryListenerConfigCodec() {
    }

    public static CacheSimpleEntryListenerConfig decode(ClientMessage clientMessage) {
        boolean entryEventFilterFactory_isNull;
        CacheSimpleEntryListenerConfig config = new CacheSimpleEntryListenerConfig();
        config.setSynchronous(clientMessage.getBoolean());
        config.setOldValueRequired(clientMessage.getBoolean());
        boolean entryListenerFactory_isNull = clientMessage.getBoolean();
        if (!entryListenerFactory_isNull) {
            config.setCacheEntryListenerFactory(clientMessage.getStringUtf8());
        }
        if (!(entryEventFilterFactory_isNull = clientMessage.getBoolean())) {
            config.setCacheEntryEventFilterFactory(clientMessage.getStringUtf8());
        }
        return config;
    }

    public static void encode(CacheSimpleEntryListenerConfig config, ClientMessage clientMessage) {
        clientMessage.set(config.isSynchronous()).set(config.isOldValueRequired());
        boolean entryListenerFactory_isNull = config.getCacheEntryListenerFactory() == null;
        clientMessage.set(entryListenerFactory_isNull);
        if (!entryListenerFactory_isNull) {
            clientMessage.set(config.getCacheEntryListenerFactory());
        }
        boolean entryEventFilterFactory_isNull = config.getCacheEntryEventFilterFactory() == null;
        clientMessage.set(entryEventFilterFactory_isNull);
        if (!entryEventFilterFactory_isNull) {
            clientMessage.set(config.getCacheEntryEventFilterFactory());
        }
    }

    public static int calculateDataSize(CacheSimpleEntryListenerConfig config) {
        int dataSize = 4;
        if (config.getCacheEntryListenerFactory() != null) {
            dataSize += ParameterUtil.calculateDataSize(config.getCacheEntryListenerFactory());
        }
        if (config.getCacheEntryEventFilterFactory() != null) {
            dataSize += ParameterUtil.calculateDataSize(config.getCacheEntryEventFilterFactory());
        }
        return dataSize;
    }
}

