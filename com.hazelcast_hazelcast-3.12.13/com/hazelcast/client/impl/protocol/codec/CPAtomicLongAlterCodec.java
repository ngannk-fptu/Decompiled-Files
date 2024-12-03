/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPAtomicLongMessageType;
import com.hazelcast.client.impl.protocol.codec.RaftGroupIdCodec;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class CPAtomicLongAlterCodec {
    public static final CPAtomicLongMessageType REQUEST_TYPE = CPAtomicLongMessageType.CPATOMICLONG_ALTER;
    public static final int RESPONSE_TYPE = 103;

    public static ClientMessage encodeRequest(RaftGroupId groupId, String name, Data function, int returnValueType) {
        int requiredDataSize = RequestParameters.calculateDataSize(groupId, name, function, returnValueType);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("CPAtomicLong.alter");
        RaftGroupIdCodec.encode(groupId, clientMessage);
        clientMessage.set(name);
        clientMessage.set(function);
        clientMessage.set(returnValueType);
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
        public static final CPAtomicLongMessageType TYPE = REQUEST_TYPE;
        public RaftGroupId groupId;
        public String name;
        public Data function;
        public int returnValueType;

        public static int calculateDataSize(RaftGroupId groupId, String name, Data function, int returnValueType) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += RaftGroupIdCodec.calculateDataSize(groupId);
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(function);
            return dataSize += 4;
        }
    }
}

