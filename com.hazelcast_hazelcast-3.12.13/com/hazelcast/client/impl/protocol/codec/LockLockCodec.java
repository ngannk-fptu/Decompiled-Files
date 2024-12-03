/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.LockMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class LockLockCodec {
    public static final LockMessageType REQUEST_TYPE = LockMessageType.LOCK_LOCK;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String name, long leaseTime, long threadId) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, leaseTime, threadId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(true);
        clientMessage.setOperationName("Lock.lock");
        clientMessage.set(name);
        clientMessage.set(leaseTime);
        clientMessage.set(threadId);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeRequest(String name, long leaseTime, long threadId, long referenceId) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, leaseTime, threadId, referenceId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(true);
        clientMessage.setOperationName("Lock.lock");
        clientMessage.set(name);
        clientMessage.set(leaseTime);
        clientMessage.set(threadId);
        clientMessage.set(referenceId);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        long leaseTime = 0L;
        parameters.leaseTime = leaseTime = clientMessage.getLong();
        long threadId = 0L;
        parameters.threadId = threadId = clientMessage.getLong();
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
        public static final LockMessageType TYPE = REQUEST_TYPE;
        public String name;
        public long leaseTime;
        public long threadId;
        public boolean referenceIdExist = false;
        public long referenceId;

        public static int calculateDataSize(String name, long leaseTime, long threadId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 8;
            return dataSize += 8;
        }

        public static int calculateDataSize(String name, long leaseTime, long threadId, long referenceId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 8;
            dataSize += 8;
            return dataSize += 8;
        }
    }
}

