/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalListMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class TransactionalListRemoveCodec {
    public static final TransactionalListMessageType REQUEST_TYPE = TransactionalListMessageType.TRANSACTIONALLIST_REMOVE;
    public static final int RESPONSE_TYPE = 101;

    public static ClientMessage encodeRequest(String name, String txnId, long threadId, Data item) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, txnId, threadId, item);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("TransactionalList.remove");
        clientMessage.set(name);
        clientMessage.set(txnId);
        clientMessage.set(threadId);
        clientMessage.set(item);
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
        Data item = null;
        parameters.item = item = clientMessage.getData();
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
        public static final TransactionalListMessageType TYPE = REQUEST_TYPE;
        public String name;
        public String txnId;
        public long threadId;
        public Data item;

        public static int calculateDataSize(String name, String txnId, long threadId, Data item) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(txnId);
            dataSize += 8;
            return dataSize += ParameterUtil.calculateDataSize(item);
        }
    }
}

