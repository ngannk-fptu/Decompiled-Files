/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.cache.CacheEventType;
import com.hazelcast.cache.impl.CacheEventData;
import com.hazelcast.cache.impl.CacheEventDataImpl;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;

public final class CacheEventDataCodec {
    private CacheEventDataCodec() {
    }

    public static CacheEventData decode(ClientMessage clientMessage) {
        int typeId = clientMessage.getInt();
        String name = clientMessage.getStringUtf8();
        boolean key_isNull = clientMessage.getBoolean();
        Data key = null;
        if (!key_isNull) {
            key = clientMessage.getData();
        }
        boolean value_isNull = clientMessage.getBoolean();
        Data value = null;
        if (!value_isNull) {
            value = clientMessage.getData();
        }
        boolean dataOldValue_isNull = clientMessage.getBoolean();
        Data oldValue = null;
        if (!dataOldValue_isNull) {
            oldValue = clientMessage.getData();
        }
        boolean isOldValueAvailable = clientMessage.getBoolean();
        return new CacheEventDataImpl(name, CacheEventType.getByType(typeId), key, value, oldValue, isOldValueAvailable);
    }

    public static void encode(CacheEventData cacheEventData, ClientMessage clientMessage) {
        Data dataOldValue;
        Data dataValue;
        clientMessage.set(cacheEventData.getCacheEventType().getType());
        clientMessage.set(cacheEventData.getName());
        Data dataKey = cacheEventData.getDataKey();
        boolean dataKey_isNull = dataKey == null;
        clientMessage.set(dataKey_isNull);
        if (!dataKey_isNull) {
            clientMessage.set(dataKey);
        }
        boolean dataValue_isNull = (dataValue = cacheEventData.getDataValue()) == null;
        clientMessage.set(dataValue_isNull);
        if (!dataValue_isNull) {
            clientMessage.set(dataValue);
        }
        boolean dataOldValue_isNull = (dataOldValue = cacheEventData.getDataOldValue()) == null;
        clientMessage.set(dataOldValue_isNull);
        if (!dataOldValue_isNull) {
            clientMessage.set(dataOldValue);
        }
        clientMessage.set(cacheEventData.isOldValueAvailable());
    }

    public static int calculateDataSize(CacheEventData cacheEventData) {
        int dataSize = 4;
        dataSize += ParameterUtil.calculateDataSize(cacheEventData.getName());
        Data dataKey = cacheEventData.getDataKey();
        dataSize = dataKey == null ? ++dataSize : (dataSize += ParameterUtil.calculateDataSize(dataKey));
        Data dataValue = cacheEventData.getDataValue();
        dataSize = dataValue == null ? ++dataSize : (dataSize += ParameterUtil.calculateDataSize(dataValue));
        Data dataOldValue = cacheEventData.getDataOldValue();
        dataSize = dataOldValue == null ? ++dataSize : (dataSize += ParameterUtil.calculateDataSize(dataOldValue));
        return ++dataSize;
    }
}

