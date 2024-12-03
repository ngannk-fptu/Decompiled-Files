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
public final class MultiMapForceUnlockCodec {
    public static final MultiMapMessageType REQUEST_TYPE = MultiMapMessageType.MULTIMAP_FORCEUNLOCK;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String name, Data key) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, key);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("MultiMap.forceUnlock");
        clientMessage.set(name);
        clientMessage.set(key);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeRequest(String name, Data key, long referenceId) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, key, referenceId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("MultiMap.forceUnlock");
        clientMessage.set(name);
        clientMessage.set(key);
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
        public static final MultiMapMessageType TYPE = REQUEST_TYPE;
        public String name;
        public Data key;
        public boolean referenceIdExist = false;
        public long referenceId;

        public static int calculateDataSize(String name, Data key) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            return dataSize += ParameterUtil.calculateDataSize(key);
        }

        public static int calculateDataSize(String name, Data key, long referenceId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(key);
            return dataSize += 8;
        }
    }
}

