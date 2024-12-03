/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class CacheIterateEntriesCodec {
    public static final CacheMessageType REQUEST_TYPE = CacheMessageType.CACHE_ITERATEENTRIES;
    public static final int RESPONSE_TYPE = 118;

    public static ClientMessage encodeRequest(String name, int partitionId, int tableIndex, int batch) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, partitionId, tableIndex, batch);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Cache.iterateEntries");
        clientMessage.set(name);
        clientMessage.set(partitionId);
        clientMessage.set(tableIndex);
        clientMessage.set(batch);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        int partitionId = 0;
        parameters.partitionId = partitionId = clientMessage.getInt();
        int tableIndex = 0;
        parameters.tableIndex = tableIndex = clientMessage.getInt();
        int batch = 0;
        parameters.batch = batch = clientMessage.getInt();
        return parameters;
    }

    public static ClientMessage encodeResponse(int tableIndex, Collection<Map.Entry<Data, Data>> entries) {
        int requiredDataSize = ResponseParameters.calculateDataSize(tableIndex, entries);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(118);
        clientMessage.set(tableIndex);
        clientMessage.set(entries.size());
        for (Map.Entry<Data, Data> entries_item : entries) {
            Data entries_itemKey = entries_item.getKey();
            Data entries_itemVal = entries_item.getValue();
            clientMessage.set(entries_itemKey);
            clientMessage.set(entries_itemVal);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        int tableIndex = 0;
        parameters.tableIndex = tableIndex = clientMessage.getInt();
        ArrayList<Map.Entry<Data, Data>> entries = null;
        int entries_size = clientMessage.getInt();
        entries = new ArrayList<Map.Entry<Data, Data>>(entries_size);
        for (int entries_index = 0; entries_index < entries_size; ++entries_index) {
            Data entries_item_key = clientMessage.getData();
            Data entries_item_val = clientMessage.getData();
            AbstractMap.SimpleEntry<Data, Data> entries_item = new AbstractMap.SimpleEntry<Data, Data>(entries_item_key, entries_item_val);
            entries.add(entries_item);
        }
        parameters.entries = entries;
        return parameters;
    }

    public static class ResponseParameters {
        public int tableIndex;
        public List<Map.Entry<Data, Data>> entries;

        public static int calculateDataSize(int tableIndex, Collection<Map.Entry<Data, Data>> entries) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            dataSize += 4;
            for (Map.Entry<Data, Data> entries_item : entries) {
                Data entries_itemKey = entries_item.getKey();
                Data entries_itemVal = entries_item.getValue();
                dataSize += ParameterUtil.calculateDataSize(entries_itemKey);
                dataSize += ParameterUtil.calculateDataSize(entries_itemVal);
            }
            return dataSize;
        }
    }

    public static class RequestParameters {
        public static final CacheMessageType TYPE = REQUEST_TYPE;
        public String name;
        public int partitionId;
        public int tableIndex;
        public int batch;

        public static int calculateDataSize(String name, int partitionId, int tableIndex, int batch) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            dataSize += 4;
            return dataSize += 4;
        }
    }
}

