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
public final class CPAtomicRefCompareAndSetCodec {
    public static final CPAtomicRefMessageType REQUEST_TYPE = CPAtomicRefMessageType.CPATOMICREF_COMPAREANDSET;
    public static final int RESPONSE_TYPE = 101;

    public static ClientMessage encodeRequest(RaftGroupId groupId, String name, Data oldValue, Data newValue) {
        int requiredDataSize = RequestParameters.calculateDataSize(groupId, name, oldValue, newValue);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("CPAtomicRef.compareAndSet");
        RaftGroupIdCodec.encode(groupId, clientMessage);
        clientMessage.set(name);
        if (oldValue == null) {
            boolean oldValue_isNull = true;
            clientMessage.set(oldValue_isNull);
        } else {
            boolean oldValue_isNull = false;
            clientMessage.set(oldValue_isNull);
            clientMessage.set(oldValue);
        }
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
        if (clientMessage.isComplete()) {
            return parameters;
        }
        RaftGroupId groupId = null;
        parameters.groupId = groupId = RaftGroupIdCodec.decode(clientMessage);
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        Data oldValue = null;
        boolean oldValue_isNull = clientMessage.getBoolean();
        if (!oldValue_isNull) {
            parameters.oldValue = oldValue = clientMessage.getData();
        }
        Data newValue = null;
        boolean newValue_isNull = clientMessage.getBoolean();
        if (!newValue_isNull) {
            parameters.newValue = newValue = clientMessage.getData();
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
        public static final CPAtomicRefMessageType TYPE = REQUEST_TYPE;
        public RaftGroupId groupId;
        public String name;
        public Data oldValue;
        public Data newValue;

        public static int calculateDataSize(RaftGroupId groupId, String name, Data oldValue, Data newValue) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += RaftGroupIdCodec.calculateDataSize(groupId);
            dataSize += ParameterUtil.calculateDataSize(name);
            ++dataSize;
            if (oldValue != null) {
                dataSize += ParameterUtil.calculateDataSize(oldValue);
            }
            ++dataSize;
            if (newValue != null) {
                dataSize += ParameterUtil.calculateDataSize(newValue);
            }
            return dataSize;
        }
    }
}

