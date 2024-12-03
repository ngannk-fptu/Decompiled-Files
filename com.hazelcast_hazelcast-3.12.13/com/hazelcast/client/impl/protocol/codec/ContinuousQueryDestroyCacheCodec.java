/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ContinuousQueryMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ContinuousQueryDestroyCacheCodec {
    public static final ContinuousQueryMessageType REQUEST_TYPE = ContinuousQueryMessageType.CONTINUOUSQUERY_DESTROYCACHE;
    public static final int RESPONSE_TYPE = 101;

    public static ClientMessage encodeRequest(String mapName, String cacheName) {
        int requiredDataSize = RequestParameters.calculateDataSize(mapName, cacheName);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("ContinuousQuery.destroyCache");
        clientMessage.set(mapName);
        clientMessage.set(cacheName);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String mapName = null;
        parameters.mapName = mapName = clientMessage.getStringUtf8();
        String cacheName = null;
        parameters.cacheName = cacheName = clientMessage.getStringUtf8();
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
        public static final ContinuousQueryMessageType TYPE = REQUEST_TYPE;
        public String mapName;
        public String cacheName;

        public static int calculateDataSize(String mapName, String cacheName) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(mapName);
            return dataSize += ParameterUtil.calculateDataSize(cacheName);
        }
    }
}

