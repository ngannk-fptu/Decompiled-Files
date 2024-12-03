/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.logging.Logger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class CacheAddPartitionLostListenerCodec {
    public static final CacheMessageType REQUEST_TYPE = CacheMessageType.CACHE_ADDPARTITIONLOSTLISTENER;
    public static final int RESPONSE_TYPE = 104;

    public static ClientMessage encodeRequest(String name, boolean localOnly) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, localOnly);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Cache.addPartitionLostListener");
        clientMessage.set(name);
        clientMessage.set(localOnly);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        boolean localOnly = false;
        parameters.localOnly = localOnly = clientMessage.getBoolean();
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

    public static ClientMessage encodeCachePartitionLostEvent(int partitionId, String uuid) {
        int dataSize = ClientMessage.HEADER_SIZE;
        dataSize += 4;
        ClientMessage clientMessage = ClientMessage.createForEncode(dataSize += ParameterUtil.calculateDataSize(uuid));
        clientMessage.setMessageType(214);
        clientMessage.addFlag((short)1);
        clientMessage.set(partitionId);
        clientMessage.set(uuid);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static abstract class AbstractEventHandler {
        public void handle(ClientMessage clientMessage) {
            int messageType = clientMessage.getMessageType();
            if (messageType == 214) {
                int partitionId = 0;
                partitionId = clientMessage.getInt();
                String uuid = null;
                uuid = clientMessage.getStringUtf8();
                this.handleCachePartitionLostEventV10(partitionId, uuid);
                return;
            }
            Logger.getLogger(super.getClass()).warning("Unknown message type received on event handler :" + messageType);
        }

        public abstract void handleCachePartitionLostEventV10(int var1, String var2);
    }

    public static class ResponseParameters {
        public String response;

        public static int calculateDataSize(String response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += ParameterUtil.calculateDataSize(response);
        }
    }

    public static class RequestParameters {
        public static final CacheMessageType TYPE = REQUEST_TYPE;
        public String name;
        public boolean localOnly;

        public static int calculateDataSize(String name, boolean localOnly) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            return ++dataSize;
        }
    }
}

