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
public final class CachePutAllCodec {
    public static final CacheMessageType REQUEST_TYPE = CacheMessageType.CACHE_PUTALL;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String name, Collection<Map.Entry<Data, Data>> entries, Data expiryPolicy, int completionId) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, entries, expiryPolicy, completionId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Cache.putAll");
        clientMessage.set(name);
        clientMessage.set(entries.size());
        for (Map.Entry<Data, Data> entries_item : entries) {
            Data entries_itemKey = entries_item.getKey();
            Data entries_itemVal = entries_item.getValue();
            clientMessage.set(entries_itemKey);
            clientMessage.set(entries_itemVal);
        }
        if (expiryPolicy == null) {
            boolean expiryPolicy_isNull = true;
            clientMessage.set(expiryPolicy_isNull);
        } else {
            boolean expiryPolicy_isNull = false;
            clientMessage.set(expiryPolicy_isNull);
            clientMessage.set(expiryPolicy);
        }
        clientMessage.set(completionId);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
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
        Data expiryPolicy = null;
        boolean expiryPolicy_isNull = clientMessage.getBoolean();
        if (!expiryPolicy_isNull) {
            parameters.expiryPolicy = expiryPolicy = clientMessage.getData();
        }
        int completionId = 0;
        parameters.completionId = completionId = clientMessage.getInt();
        return parameters;
    }

    public static ClientMessage encodeResponse() {
        int requiredDataSize = ResponseParameters.calculateDataSize();
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(100);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        return parameters;
    }

    public static class ResponseParameters {
        public static int calculateDataSize() {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize;
        }
    }

    public static class RequestParameters {
        public static final CacheMessageType TYPE = REQUEST_TYPE;
        public String name;
        public List<Map.Entry<Data, Data>> entries;
        public Data expiryPolicy;
        public int completionId;

        public static int calculateDataSize(String name, Collection<Map.Entry<Data, Data>> entries, Data expiryPolicy, int completionId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            for (Map.Entry<Data, Data> entries_item : entries) {
                Data entries_itemKey = entries_item.getKey();
                Data entries_itemVal = entries_item.getValue();
                dataSize += ParameterUtil.calculateDataSize(entries_itemKey);
                dataSize += ParameterUtil.calculateDataSize(entries_itemVal);
            }
            ++dataSize;
            if (expiryPolicy != null) {
                dataSize += ParameterUtil.calculateDataSize(expiryPolicy);
            }
            return dataSize += 4;
        }
    }
}

