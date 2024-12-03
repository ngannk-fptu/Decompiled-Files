/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SemaphoreMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class SemaphoreTryAcquireCodec {
    public static final SemaphoreMessageType REQUEST_TYPE = SemaphoreMessageType.SEMAPHORE_TRYACQUIRE;
    public static final int RESPONSE_TYPE = 101;

    public static ClientMessage encodeRequest(String name, int permits, long timeout) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, permits, timeout);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(true);
        clientMessage.setOperationName("Semaphore.tryAcquire");
        clientMessage.set(name);
        clientMessage.set(permits);
        clientMessage.set(timeout);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        int permits = 0;
        parameters.permits = permits = clientMessage.getInt();
        long timeout = 0L;
        parameters.timeout = timeout = clientMessage.getLong();
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
        public static final SemaphoreMessageType TYPE = REQUEST_TYPE;
        public String name;
        public int permits;
        public long timeout;

        public static int calculateDataSize(String name, int permits, long timeout) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            return dataSize += 8;
        }
    }
}

