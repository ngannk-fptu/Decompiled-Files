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
public final class CacheCreateConfigCodec {
    public static final CacheMessageType REQUEST_TYPE = CacheMessageType.CACHE_CREATECONFIG;
    public static final int RESPONSE_TYPE = 105;

    public static ClientMessage encodeRequest(Data cacheConfig, boolean createAlsoOnOthers) {
        int requiredDataSize = RequestParameters.calculateDataSize(cacheConfig, createAlsoOnOthers);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Cache.createConfig");
        clientMessage.set(cacheConfig);
        clientMessage.set(createAlsoOnOthers);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        Data cacheConfig = null;
        parameters.cacheConfig = cacheConfig = clientMessage.getData();
        boolean createAlsoOnOthers = false;
        parameters.createAlsoOnOthers = createAlsoOnOthers = clientMessage.getBoolean();
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
        public Data cacheConfig;
        public boolean createAlsoOnOthers;

        public static int calculateDataSize(Data cacheConfig, boolean createAlsoOnOthers) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(cacheConfig);
            return ++dataSize;
        }
    }
}

