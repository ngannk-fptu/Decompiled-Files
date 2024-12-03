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
public final class CPAtomicRefApplyCodec {
    public static final CPAtomicRefMessageType REQUEST_TYPE = CPAtomicRefMessageType.CPATOMICREF_APPLY;
    public static final int RESPONSE_TYPE = 105;

    public static ClientMessage encodeRequest(RaftGroupId groupId, String name, Data function, int returnValueType, boolean alter) {
        int requiredDataSize = RequestParameters.calculateDataSize(groupId, name, function, returnValueType, alter);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("CPAtomicRef.apply");
        RaftGroupIdCodec.encode(groupId, clientMessage);
        clientMessage.set(name);
        clientMessage.set(function);
        clientMessage.set(returnValueType);
        clientMessage.set(alter);
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
        Data function = null;
        parameters.function = function = clientMessage.getData();
        int returnValueType = 0;
        parameters.returnValueType = returnValueType = clientMessage.getInt();
        boolean alter = false;
        parameters.alter = alter = clientMessage.getBoolean();
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
        public Data function;
        public int returnValueType;
        public boolean alter;

        public static int calculateDataSize(RaftGroupId groupId, String name, Data function, int returnValueType, boolean alter) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += RaftGroupIdCodec.calculateDataSize(groupId);
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(function);
            dataSize += 4;
            return ++dataSize;
        }
    }
}

