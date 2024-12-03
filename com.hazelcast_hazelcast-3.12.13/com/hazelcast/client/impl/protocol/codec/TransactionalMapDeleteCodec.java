/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class TransactionalMapDeleteCodec {
    public static final TransactionalMapMessageType REQUEST_TYPE = TransactionalMapMessageType.TRANSACTIONALMAP_DELETE;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String name, String txnId, long threadId, Data key) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, txnId, threadId, key);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("TransactionalMap.delete");
        clientMessage.set(name);
        clientMessage.set(txnId);
        clientMessage.set(threadId);
        clientMessage.set(key);
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
        Data key = null;
        parameters.key = key = clientMessage.getData();
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
        public static final TransactionalMapMessageType TYPE = REQUEST_TYPE;
        public String name;
        public String txnId;
        public long threadId;
        public Data key;

        public static int calculateDataSize(String name, String txnId, long threadId, Data key) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(txnId);
            dataSize += 8;
            return dataSize += ParameterUtil.calculateDataSize(key);
        }
    }
}

