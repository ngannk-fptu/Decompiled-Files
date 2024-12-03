/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class MultiMapTryLockCodec {
    public static final MultiMapMessageType REQUEST_TYPE = MultiMapMessageType.MULTIMAP_TRYLOCK;
    public static final int RESPONSE_TYPE = 101;

    public static ClientMessage encodeRequest(String name, Data key, long threadId, long lease, long timeout) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, key, threadId, lease, timeout);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(true);
        clientMessage.setOperationName("MultiMap.tryLock");
        clientMessage.set(name);
        clientMessage.set(key);
        clientMessage.set(threadId);
        clientMessage.set(lease);
        clientMessage.set(timeout);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeRequest(String name, Data key, long threadId, long lease, long timeout, long referenceId) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, key, threadId, lease, timeout, referenceId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(true);
        clientMessage.setOperationName("MultiMap.tryLock");
        clientMessage.set(name);
        clientMessage.set(key);
        clientMessage.set(threadId);
        clientMessage.set(lease);
        clientMessage.set(timeout);
        clientMessage.set(referenceId);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        Data key = null;
        parameters.key = key = clientMessage.getData();
        long threadId = 0L;
        parameters.threadId = threadId = clientMessage.getLong();
        long lease = 0L;
        parameters.lease = lease = clientMessage.getLong();
        long timeout = 0L;
        parameters.timeout = timeout = clientMessage.getLong();
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
        public static final MultiMapMessageType TYPE = REQUEST_TYPE;
        public String name;
        public Data key;
        public long threadId;
        public long lease;
        public long timeout;
        public boolean referenceIdExist = false;
        public long referenceId;

        public static int calculateDataSize(String name, Data key, long threadId, long lease, long timeout) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(key);
            dataSize += 8;
            dataSize += 8;
            return dataSize += 8;
        }

        public static int calculateDataSize(String name, Data key, long threadId, long lease, long timeout, long referenceId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(key);
            dataSize += 8;
            dataSize += 8;
            dataSize += 8;
            return dataSize += 8;
        }
    }
}

