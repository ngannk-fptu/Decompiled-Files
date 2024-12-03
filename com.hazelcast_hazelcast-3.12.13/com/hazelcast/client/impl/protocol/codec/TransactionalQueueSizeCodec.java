/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalQueueMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class TransactionalQueueSizeCodec {
    public static final TransactionalQueueMessageType REQUEST_TYPE = TransactionalQueueMessageType.TRANSACTIONALQUEUE_SIZE;
    public static final int RESPONSE_TYPE = 102;

    public static ClientMessage encodeRequest(String name, String txnId, long threadId) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, txnId, threadId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("TransactionalQueue.size");
        clientMessage.set(name);
        clientMessage.set(txnId);
        clientMessage.set(threadId);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        String txnId = null;
        parameters.txnId = txnId = clientMessage.getStringUtf8();
        long threadId = 0L;
        parameters.threadId = threadId = clientMessage.getLong();
        return parameters;
    }

    public static ClientMessage encodeResponse(int response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(102);
        clientMessage.set(response);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        int response = 0;
        parameters.response = response = clientMessage.getInt();
        return parameters;
    }

    public static class ResponseParameters {
        public int response;

        public static int calculateDataSize(int response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += 4;
        }
    }

    public static class RequestParameters {
        public static final TransactionalQueueMessageType TYPE = REQUEST_TYPE;
        public String name;
        public String txnId;
        public long threadId;

        public static int calculateDataSize(String name, String txnId, long threadId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(txnId);
            return dataSize += 8;
        }
    }
}

