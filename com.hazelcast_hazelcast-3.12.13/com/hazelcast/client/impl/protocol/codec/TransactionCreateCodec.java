/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class TransactionCreateCodec {
    public static final TransactionMessageType REQUEST_TYPE = TransactionMessageType.TRANSACTION_CREATE;
    public static final int RESPONSE_TYPE = 104;

    public static ClientMessage encodeRequest(long timeout, int durability, int transactionType, long threadId) {
        int requiredDataSize = RequestParameters.calculateDataSize(timeout, durability, transactionType, threadId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(true);
        clientMessage.setOperationName("Transaction.create");
        clientMessage.set(timeout);
        clientMessage.set(durability);
        clientMessage.set(transactionType);
        clientMessage.set(threadId);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        long timeout = 0L;
        parameters.timeout = timeout = clientMessage.getLong();
        int durability = 0;
        parameters.durability = durability = clientMessage.getInt();
        int transactionType = 0;
        parameters.transactionType = transactionType = clientMessage.getInt();
        long threadId = 0L;
        parameters.threadId = threadId = clientMessage.getLong();
        return parameters;
    }

    public static ClientMessage encodeResponse(String response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(104);
        clientMessage.set(response);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        String response = null;
        parameters.response = response = clientMessage.getStringUtf8();
        return parameters;
    }

    public static class ResponseParameters {
        public String response;

        public static int calculateDataSize(String response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += ParameterUtil.calculateDataSize(response);
        }
    }

    public static class RequestParameters {
        public static final TransactionMessageType TYPE = REQUEST_TYPE;
        public long timeout;
        public int durability;
        public int transactionType;
        public long threadId;

        public static int calculateDataSize(long timeout, int durability, int transactionType, long threadId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 8;
            dataSize += 4;
            dataSize += 4;
            return dataSize += 8;
        }
    }
}

