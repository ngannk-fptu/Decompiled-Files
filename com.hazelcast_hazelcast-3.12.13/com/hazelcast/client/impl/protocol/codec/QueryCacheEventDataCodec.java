/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.map.impl.querycache.event.DefaultQueryCacheEventData;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import com.hazelcast.nio.serialization.Data;

public final class QueryCacheEventDataCodec {
    private QueryCacheEventDataCodec() {
    }

    public static QueryCacheEventData decode(ClientMessage clientMessage) {
        boolean isNullValue;
        DefaultQueryCacheEventData queryCacheEventData = new DefaultQueryCacheEventData();
        queryCacheEventData.setSequence(clientMessage.getLong());
        boolean isNullKey = clientMessage.getBoolean();
        if (!isNullKey) {
            queryCacheEventData.setDataKey(clientMessage.getData());
        }
        if (!(isNullValue = clientMessage.getBoolean())) {
            queryCacheEventData.setDataNewValue(clientMessage.getData());
        }
        queryCacheEventData.setEventType(clientMessage.getInt());
        queryCacheEventData.setPartitionId(clientMessage.getInt());
        return queryCacheEventData;
    }

    public static void encode(QueryCacheEventData queryCacheEventData, ClientMessage clientMessage) {
        Data dataNewValue;
        clientMessage.set(queryCacheEventData.getSequence());
        Data dataKey = queryCacheEventData.getDataKey();
        boolean isNullKey = dataKey == null;
        clientMessage.set(isNullKey);
        if (!isNullKey) {
            clientMessage.set(dataKey);
        }
        boolean isNullValue = (dataNewValue = queryCacheEventData.getDataNewValue()) == null;
        clientMessage.set(isNullValue);
        if (!isNullValue) {
            clientMessage.set(dataNewValue);
        }
        clientMessage.set(queryCacheEventData.getEventType());
        clientMessage.set(queryCacheEventData.getPartitionId());
    }

    public static int calculateDataSize(QueryCacheEventData queryCacheEventData) {
        boolean isNullValue;
        boolean isNullKey;
        int dataSize = 8;
        ++dataSize;
        Data dataKey = queryCacheEventData.getDataKey();
        boolean bl = isNullKey = dataKey == null;
        if (!isNullKey) {
            dataSize += ParameterUtil.calculateDataSize(dataKey);
        }
        ++dataSize;
        Data dataNewValue = queryCacheEventData.getDataNewValue();
        boolean bl2 = isNullValue = dataNewValue == null;
        if (!isNullValue) {
            dataSize += ParameterUtil.calculateDataSize(dataNewValue);
        }
        dataSize += 4;
        return dataSize += 4;
    }
}

