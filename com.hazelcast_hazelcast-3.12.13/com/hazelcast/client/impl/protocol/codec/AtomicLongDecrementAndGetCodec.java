/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicLongMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class AtomicLongDecrementAndGetCodec {
    public static final AtomicLongMessageType REQUEST_TYPE = AtomicLongMessageType.ATOMICLONG_DECREMENTANDGET;
    public static final int RESPONSE_TYPE = 103;

    public static ClientMessage encodeRequest(String name) {
        int requiredDataSize = RequestParameters.calculateDataSize(name);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("AtomicLong.decrementAndGet");
        clientMessage.set(name);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        return parameters;
    }

    public static ClientMessage encodeResponse(long response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(103);
        clientMessage.set(response);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        long response = 0L;
        parameters.response = response = clientMessage.getLong();
        return parameters;
    }

    public static class ResponseParameters {
        public long response;

        public static int calculateDataSize(long response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += 8;
        }
    }

    public static class RequestParameters {
        public static final AtomicLongMessageType TYPE = REQUEST_TYPE;
        public String name;

        public static int calculateDataSize(String name) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += ParameterUtil.calculateDataSize(name);
        }
    }
}

