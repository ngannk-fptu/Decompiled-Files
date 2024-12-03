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
public final class AtomicLongGetAndSetCodec {
    public static final AtomicLongMessageType REQUEST_TYPE = AtomicLongMessageType.ATOMICLONG_GETANDSET;
    public static final int RESPONSE_TYPE = 103;

    public static ClientMessage encodeRequest(String name, long newValue) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, newValue);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("AtomicLong.getAndSet");
        clientMessage.set(name);
        clientMessage.set(newValue);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        long newValue = 0L;
        parameters.newValue = newValue = clientMessage.getLong();
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
        public long newValue;

        public static int calculateDataSize(String name, long newValue) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            return dataSize += 8;
        }
    }
}

