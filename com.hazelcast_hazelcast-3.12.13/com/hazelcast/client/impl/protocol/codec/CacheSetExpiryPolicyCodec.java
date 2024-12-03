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
public final class CacheSetExpiryPolicyCodec {
    public static final CacheMessageType REQUEST_TYPE = CacheMessageType.CACHE_SETEXPIRYPOLICY;
    public static final int RESPONSE_TYPE = 101;

    public static ClientMessage encodeRequest(String name, Collection<Data> keys, Data expiryPolicy) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, keys, expiryPolicy);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Cache.setExpiryPolicy");
        clientMessage.set(name);
        clientMessage.set(keys.size());
        for (Data keys_item : keys) {
            clientMessage.set(keys_item);
        }
        clientMessage.set(expiryPolicy);
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
        ArrayList<Data> keys = null;
        int keys_size = clientMessage.getInt();
        keys = new ArrayList<Data>(keys_size);
        for (int keys_index = 0; keys_index < keys_size; ++keys_index) {
            Data keys_item = clientMessage.getData();
            keys.add(keys_item);
        }
        parameters.keys = keys;
        Data expiryPolicy = null;
        parameters.expiryPolicy = expiryPolicy = clientMessage.getData();
        return parameters;
    }

    public static ClientMessage encodeResponse(boolean response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(101);
        clientMessage.set(response);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        boolean response = false;
        parameters.response = response = clientMessage.getBoolean();
        return parameters;
    }

    public static class ResponseParameters {
        public boolean response;

        public static int calculateDataSize(boolean response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return ++dataSize;
        }
    }

    public static class RequestParameters {
        public static final CacheMessageType TYPE = REQUEST_TYPE;
        public String name;
        public List<Data> keys;
        public Data expiryPolicy;

        public static int calculateDataSize(String name, Collection<Data> keys, Data expiryPolicy) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            for (Data keys_item : keys) {
                dataSize += ParameterUtil.calculateDataSize(keys_item);
            }
            return dataSize += ParameterUtil.calculateDataSize(expiryPolicy);
        }
    }
}

