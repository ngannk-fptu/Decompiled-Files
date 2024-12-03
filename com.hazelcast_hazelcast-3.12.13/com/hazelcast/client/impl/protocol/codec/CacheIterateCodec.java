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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class CacheIterateCodec {
    public static final CacheMessageType REQUEST_TYPE = CacheMessageType.CACHE_ITERATE;
    public static final int RESPONSE_TYPE = 116;

    public static ClientMessage encodeRequest(String name, int partitionId, int tableIndex, int batch) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, partitionId, tableIndex, batch);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Cache.iterate");
        clientMessage.set(name);
        clientMessage.set(partitionId);
        clientMessage.set(tableIndex);
        clientMessage.set(batch);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
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

    public static ClientMessage encodeResponse(int tableIndex, Collection<Data> keys) {
        int requiredDataSize = ResponseParameters.calculateDataSize(tableIndex, keys);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(116);
        clientMessage.set(tableIndex);
        clientMessage.set(keys.size());
        for (Data keys_item : keys) {
            clientMessage.set(keys_item);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        int tableIndex = 0;
        parameters.tableIndex = tableIndex = clientMessage.getInt();
        ArrayList<Data> keys = null;
        int keys_size = clientMessage.getInt();
        keys = new ArrayList<Data>(keys_size);
        for (int keys_index = 0; keys_index < keys_size; ++keys_index) {
            Data keys_item = clientMessage.getData();
            keys.add(keys_item);
        }
        parameters.keys = keys;
        return parameters;
    }

    public static class ResponseParameters {
        public int tableIndex;
        public List<Data> keys;

        public static int calculateDataSize(int tableIndex, Collection<Data> keys) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            dataSize += 4;
            for (Data keys_item : keys) {
                dataSize += ParameterUtil.calculateDataSize(keys_item);
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

