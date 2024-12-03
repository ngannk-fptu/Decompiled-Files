/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class AtomicReferenceCompareAndSetCodec {
    public static final AtomicReferenceMessageType REQUEST_TYPE = AtomicReferenceMessageType.ATOMICREFERENCE_COMPAREANDSET;
    public static final int RESPONSE_TYPE = 101;

    public static ClientMessage encodeRequest(String name, Data expected, Data updated) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, expected, updated);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("AtomicReference.compareAndSet");
        clientMessage.set(name);
        if (expected == null) {
            boolean expected_isNull = true;
            clientMessage.set(expected_isNull);
        } else {
            boolean expected_isNull = false;
            clientMessage.set(expected_isNull);
            clientMessage.set(expected);
        }
        if (updated == null) {
            boolean updated_isNull = true;
            clientMessage.set(updated_isNull);
        } else {
            boolean updated_isNull = false;
            clientMessage.set(updated_isNull);
            clientMessage.set(updated);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        Data expected = null;
        boolean expected_isNull = clientMessage.getBoolean();
        if (!expected_isNull) {
            parameters.expected = expected = clientMessage.getData();
        }
        Data updated = null;
        boolean updated_isNull = clientMessage.getBoolean();
        if (!updated_isNull) {
            parameters.updated = updated = clientMessage.getData();
        }
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
        public static final AtomicReferenceMessageType TYPE = REQUEST_TYPE;
        public String name;
        public Data expected;
        public Data updated;

        public static int calculateDataSize(String name, Data expected, Data updated) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            ++dataSize;
            if (expected != null) {
                dataSize += ParameterUtil.calculateDataSize(expected);
            }
            ++dataSize;
            if (updated != null) {
                dataSize += ParameterUtil.calculateDataSize(updated);
            }
            return dataSize;
        }
    }
}

