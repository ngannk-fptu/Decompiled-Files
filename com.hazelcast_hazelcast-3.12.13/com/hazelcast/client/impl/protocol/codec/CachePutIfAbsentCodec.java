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

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class CachePutIfAbsentCodec {
    public static final CacheMessageType REQUEST_TYPE = CacheMessageType.CACHE_PUTIFABSENT;
    public static final int RESPONSE_TYPE = 101;

    public static ClientMessage encodeRequest(String name, Data key, Data value, Data expiryPolicy, int completionId) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, key, value, expiryPolicy, completionId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Cache.putIfAbsent");
        clientMessage.set(name);
        clientMessage.set(key);
        clientMessage.set(value);
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
        Data key = null;
        parameters.key = key = clientMessage.getData();
        Data value = null;
        parameters.value = value = clientMessage.getData();
        Data expiryPolicy = null;
        boolean expiryPolicy_isNull = clientMessage.getBoolean();
        if (!expiryPolicy_isNull) {
            parameters.expiryPolicy = expiryPolicy = clientMessage.getData();
        }
        int completionId = 0;
        parameters.completionId = completionId = clientMessage.getInt();
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
        public Data key;
        public Data value;
        public Data expiryPolicy;
        public int completionId;

        public static int calculateDataSize(String name, Data key, Data value, Data expiryPolicy, int completionId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(key);
            dataSize += ParameterUtil.calculateDataSize(value);
            ++dataSize;
            if (expiryPolicy != null) {
                dataSize += ParameterUtil.calculateDataSize(expiryPolicy);
            }
            return dataSize += 4;
        }
    }
}

