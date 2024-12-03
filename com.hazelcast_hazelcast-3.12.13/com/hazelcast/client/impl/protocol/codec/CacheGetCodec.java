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
public final class CacheGetCodec {
    public static final CacheMessageType REQUEST_TYPE = CacheMessageType.CACHE_GET;
    public static final int RESPONSE_TYPE = 105;

    public static ClientMessage encodeRequest(String name, Data key, Data expiryPolicy) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, key, expiryPolicy);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Cache.get");
        clientMessage.set(name);
        clientMessage.set(key);
        if (expiryPolicy == null) {
            boolean expiryPolicy_isNull = true;
            clientMessage.set(expiryPolicy_isNull);
        } else {
            boolean expiryPolicy_isNull = false;
            clientMessage.set(expiryPolicy_isNull);
            clientMessage.set(expiryPolicy);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        Data key = null;
        parameters.key = key = clientMessage.getData();
        Data expiryPolicy = null;
        boolean expiryPolicy_isNull = clientMessage.getBoolean();
        if (!expiryPolicy_isNull) {
            parameters.expiryPolicy = expiryPolicy = clientMessage.getData();
        }
        return parameters;
    }

    public static ClientMessage encodeResponse(Data response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(105);
        if (response == null) {
            boolean response_isNull = true;
            clientMessage.set(response_isNull);
        } else {
            boolean response_isNull = false;
            clientMessage.set(response_isNull);
            clientMessage.set(response);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        Data response = null;
        boolean response_isNull = clientMessage.getBoolean();
        if (!response_isNull) {
            parameters.response = response = clientMessage.getData();
        }
        return parameters;
    }

    public static class ResponseParameters {
        public Data response;

        public static int calculateDataSize(Data response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            ++dataSize;
            if (response != null) {
                dataSize += ParameterUtil.calculateDataSize(response);
            }
            return dataSize;
        }
    }

    public static class RequestParameters {
        public static final CacheMessageType TYPE = REQUEST_TYPE;
        public String name;
        public Data key;
        public Data expiryPolicy;

        public static int calculateDataSize(String name, Data key, Data expiryPolicy) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(key);
            ++dataSize;
            if (expiryPolicy != null) {
                dataSize += ParameterUtil.calculateDataSize(expiryPolicy);
            }
            return dataSize;
        }
    }
}

