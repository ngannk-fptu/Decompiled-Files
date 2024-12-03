/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPAtomicRefMessageType;
import com.hazelcast.client.impl.protocol.codec.RaftGroupIdCodec;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class CPAtomicRefSetCodec {
    public static final CPAtomicRefMessageType REQUEST_TYPE = CPAtomicRefMessageType.CPATOMICREF_SET;
    public static final int RESPONSE_TYPE = 105;

    public static ClientMessage encodeRequest(RaftGroupId groupId, String name, Data newValue, boolean returnOldValue) {
        int requiredDataSize = RequestParameters.calculateDataSize(groupId, name, newValue, returnOldValue);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("CPAtomicRef.set");
        RaftGroupIdCodec.encode(groupId, clientMessage);
        clientMessage.set(name);
        if (newValue == null) {
            boolean newValue_isNull = true;
            clientMessage.set(newValue_isNull);
        } else {
            boolean newValue_isNull = false;
            clientMessage.set(newValue_isNull);
            clientMessage.set(newValue);
        }
        clientMessage.set(returnOldValue);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        RaftGroupId groupId = null;
        parameters.groupId = groupId = RaftGroupIdCodec.decode(clientMessage);
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        Data newValue = null;
        boolean newValue_isNull = clientMessage.getBoolean();
        if (!newValue_isNull) {
            parameters.newValue = newValue = clientMessage.getData();
        }
        boolean returnOldValue = false;
        parameters.returnOldValue = returnOldValue = clientMessage.getBoolean();
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
        public static final CPAtomicRefMessageType TYPE = REQUEST_TYPE;
        public RaftGroupId groupId;
        public String name;
        public Data newValue;
        public boolean returnOldValue;

        public static int calculateDataSize(RaftGroupId groupId, String name, Data newValue, boolean returnOldValue) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += RaftGroupIdCodec.calculateDataSize(groupId);
            dataSize += ParameterUtil.calculateDataSize(name);
            ++dataSize;
            if (newValue != null) {
                dataSize += ParameterUtil.calculateDataSize(newValue);
            }
            return ++dataSize;
        }
    }
}

