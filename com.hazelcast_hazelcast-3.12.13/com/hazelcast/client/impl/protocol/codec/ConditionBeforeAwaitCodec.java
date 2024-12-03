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
public final class ConditionBeforeAwaitCodec {
    public static final ConditionMessageType REQUEST_TYPE = ConditionMessageType.CONDITION_BEFOREAWAIT;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String name, long threadId, String lockName) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, threadId, lockName);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Condition.beforeAwait");
        clientMessage.set(name);
        clientMessage.set(threadId);
        clientMessage.set(lockName);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeRequest(String name, long threadId, String lockName, long referenceId) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, threadId, lockName, referenceId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Condition.beforeAwait");
        clientMessage.set(name);
        clientMessage.set(threadId);
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
        public static final ConditionMessageType TYPE = REQUEST_TYPE;
        public String name;
        public long threadId;
        public String lockName;
        public boolean referenceIdExist = false;
        public long referenceId;

        public static int calculateDataSize(String name, long threadId, String lockName) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 8;
            return dataSize += ParameterUtil.calculateDataSize(lockName);
        }

        public static int calculateDataSize(String name, long threadId, String lockName, long referenceId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 8;
            dataSize += ParameterUtil.calculateDataSize(lockName);
            return dataSize += 8;
        }
    }
}

