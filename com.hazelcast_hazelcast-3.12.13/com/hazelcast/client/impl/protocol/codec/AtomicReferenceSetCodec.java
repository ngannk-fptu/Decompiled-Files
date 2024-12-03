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
public final class AtomicReferenceSetCodec {
    public static final AtomicReferenceMessageType REQUEST_TYPE = AtomicReferenceMessageType.ATOMICREFERENCE_SET;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String name, Data newValue) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, newValue);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("AtomicReference.set");
        clientMessage.set(name);
        if (newValue == null) {
            boolean newValue_isNull = true;
            clientMessage.set(newValue_isNull);
        } else {
            boolean newValue_isNull = false;
            clientMessage.set(newValue_isNull);
            clientMessage.set(newValue);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        Data newValue = null;
        boolean newValue_isNull = clientMessage.getBoolean();
        if (!newValue_isNull) {
            parameters.newValue = newValue = clientMessage.getData();
        }
        return parameters;
    }

    public static ClientMessage encodeResponse() {
        int requiredDataSize = ResponseParameters.calculateDataSize();
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(100);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        return parameters;
    }

    public static class ResponseParameters {
        public static int calculateDataSize() {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize;
        }
    }

    public static class RequestParameters {
        public static final AtomicReferenceMessageType TYPE = REQUEST_TYPE;
        public String name;
        public Data newValue;

        public static int calculateDataSize(String name, Data newValue) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            ++dataSize;
            if (newValue != null) {
                dataSize += ParameterUtil.calculateDataSize(newValue);
            }
            return dataSize;
        }
    }
}

