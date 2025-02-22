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
public final class CacheRemoveCodec {
    public static final CacheMessageType REQUEST_TYPE = CacheMessageType.CACHE_REMOVE;
    public static final int RESPONSE_TYPE = 101;

    public static ClientMessage encodeRequest(String name, Data key, Data currentValue, int completionId) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, key, currentValue, completionId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Cache.remove");
        clientMessage.set(name);
        clientMessage.set(key);
        if (currentValue == null) {
            boolean currentValue_isNull = true;
            clientMessage.set(currentValue_isNull);
        } else {
            boolean currentValue_isNull = false;
            clientMessage.set(currentValue_isNull);
            clientMessage.set(currentValue);
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
        Data currentValue = null;
        boolean currentValue_isNull = clientMessage.getBoolean();
        if (!currentValue_isNull) {
            parameters.currentValue = currentValue = clientMessage.getData();
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
        public Data currentValue;
        public int completionId;

        public static int calculateDataSize(String name, Data key, Data currentValue, int completionId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(key);
            ++dataSize;
            if (currentValue != null) {
                dataSize += ParameterUtil.calculateDataSize(currentValue);
            }
            return dataSize += 4;
        }
    }
}

