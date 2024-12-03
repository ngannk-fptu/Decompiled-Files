/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ConditionMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ConditionAwaitCodec {
    public static final ConditionMessageType REQUEST_TYPE = ConditionMessageType.CONDITION_AWAIT;
    public static final int RESPONSE_TYPE = 101;

    public static ClientMessage encodeRequest(String name, long threadId, long timeout, String lockName) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, threadId, timeout, lockName);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Condition.await");
        clientMessage.set(name);
        clientMessage.set(threadId);
        clientMessage.set(timeout);
        clientMessage.set(lockName);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeRequest(String name, long threadId, long timeout, String lockName, long referenceId) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, threadId, timeout, lockName, referenceId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Condition.await");
        clientMessage.set(name);
        clientMessage.set(threadId);
        clientMessage.set(timeout);
        clientMessage.set(lockName);
        clientMessage.set(referenceId);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        long threadId = 0L;
        parameters.threadId = threadId = clientMessage.getLong();
        long timeout = 0L;
        parameters.timeout = timeout = clientMessage.getLong();
        String lockName = null;
        parameters.lockName = lockName = clientMessage.getStringUtf8();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        long referenceId = 0L;
        parameters.referenceId = referenceId = clientMessage.getLong();
        parameters.referenceIdExist = true;
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
        public static final ConditionMessageType TYPE = REQUEST_TYPE;
        public String name;
        public long threadId;
        public long timeout;
        public String lockName;
        public boolean referenceIdExist = false;
        public long referenceId;

        public static int calculateDataSize(String name, long threadId, long timeout, String lockName) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 8;
            dataSize += 8;
            return dataSize += ParameterUtil.calculateDataSize(lockName);
        }

        public static int calculateDataSize(String name, long threadId, long timeout, String lockName, long referenceId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 8;
            dataSize += 8;
            dataSize += ParameterUtil.calculateDataSize(lockName);
            return dataSize += 8;
        }
    }
}

