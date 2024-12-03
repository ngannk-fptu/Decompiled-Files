/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.cache.impl.CacheEventData;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheEventDataCodec;
import com.hazelcast.client.impl.protocol.codec.CacheMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.logging.Logger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class CacheAddEntryListenerCodec {
    public static final CacheMessageType REQUEST_TYPE = CacheMessageType.CACHE_ADDENTRYLISTENER;
    public static final int RESPONSE_TYPE = 104;

    public static ClientMessage encodeRequest(String name, boolean localOnly) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, localOnly);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Cache.addEntryListener");
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

    public static ClientMessage encodeCacheEvent(int type, Collection<CacheEventData> keys, int completionId) {
        int dataSize = ClientMessage.HEADER_SIZE;
        dataSize += 4;
        dataSize += 4;
        for (CacheEventData keys_item : keys) {
            dataSize += CacheEventDataCodec.calculateDataSize(keys_item);
        }
        ClientMessage clientMessage = ClientMessage.createForEncode(dataSize += 4);
        clientMessage.setMessageType(210);
        clientMessage.addFlag((short)1);
        clientMessage.set(type);
        clientMessage.set(keys.size());
        for (CacheEventData keys_item : keys) {
            CacheEventDataCodec.encode(keys_item, clientMessage);
        }
        clientMessage.set(completionId);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static abstract class AbstractEventHandler {
        public void handle(ClientMessage clientMessage) {
            int messageType = clientMessage.getMessageType();
            if (messageType == 210) {
                int type = 0;
                type = clientMessage.getInt();
                ArrayList<CacheEventData> keys = null;
                int keys_size = clientMessage.getInt();
                keys = new ArrayList<CacheEventData>(keys_size);
                for (int keys_index = 0; keys_index < keys_size; ++keys_index) {
                    CacheEventData keys_item = CacheEventDataCodec.decode(clientMessage);
                    keys.add(keys_item);
                }
                int completionId = 0;
                completionId = clientMessage.getInt();
                this.handleCacheEventV10(type, keys, completionId);
                return;
            }
            Logger.getLogger(super.getClass()).warning("Unknown message type received on event handler :" + messageType);
        }

        public abstract void handleCacheEventV10(int var1, Collection<CacheEventData> var2, int var3);
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

