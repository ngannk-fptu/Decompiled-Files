/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class DynamicConfigAddEventJournalConfigCodec {
    public static final DynamicConfigMessageType REQUEST_TYPE = DynamicConfigMessageType.DYNAMICCONFIG_ADDEVENTJOURNALCONFIG;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String mapName, String cacheName, boolean enabled, int capacity, int timeToLiveSeconds) {
        int requiredDataSize = RequestParameters.calculateDataSize(mapName, cacheName, enabled, capacity, timeToLiveSeconds);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.addEventJournalConfig");
        if (mapName == null) {
            boolean mapName_isNull = true;
            clientMessage.set(mapName_isNull);
        } else {
            boolean mapName_isNull = false;
            clientMessage.set(mapName_isNull);
            clientMessage.set(mapName);
        }
        if (cacheName == null) {
            boolean cacheName_isNull = true;
            clientMessage.set(cacheName_isNull);
        } else {
            boolean cacheName_isNull = false;
            clientMessage.set(cacheName_isNull);
            clientMessage.set(cacheName);
        }
        clientMessage.set(enabled);
        clientMessage.set(capacity);
        clientMessage.set(timeToLiveSeconds);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        String mapName = null;
        boolean mapName_isNull = clientMessage.getBoolean();
        if (!mapName_isNull) {
            parameters.mapName = mapName = clientMessage.getStringUtf8();
        }
        String cacheName = null;
        boolean cacheName_isNull = clientMessage.getBoolean();
        if (!cacheName_isNull) {
            parameters.cacheName = cacheName = clientMessage.getStringUtf8();
        }
        boolean enabled = false;
        parameters.enabled = enabled = clientMessage.getBoolean();
        int capacity = 0;
        parameters.capacity = capacity = clientMessage.getInt();
        int timeToLiveSeconds = 0;
        parameters.timeToLiveSeconds = timeToLiveSeconds = clientMessage.getInt();
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
        public static final DynamicConfigMessageType TYPE = REQUEST_TYPE;
        public String mapName;
        public String cacheName;
        public boolean enabled;
        public int capacity;
        public int timeToLiveSeconds;

        public static int calculateDataSize(String mapName, String cacheName, boolean enabled, int capacity, int timeToLiveSeconds) {
            int dataSize = ClientMessage.HEADER_SIZE;
            ++dataSize;
            if (mapName != null) {
                dataSize += ParameterUtil.calculateDataSize(mapName);
            }
            ++dataSize;
            if (cacheName != null) {
                dataSize += ParameterUtil.calculateDataSize(cacheName);
            }
            ++dataSize;
            dataSize += 4;
            return dataSize += 4;
        }
    }
}

