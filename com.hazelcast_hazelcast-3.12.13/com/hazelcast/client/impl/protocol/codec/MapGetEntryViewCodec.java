/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.EntryViewCodec;
import com.hazelcast.client.impl.protocol.codec.MapMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.map.impl.SimpleEntryView;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class MapGetEntryViewCodec {
    public static final MapMessageType REQUEST_TYPE = MapMessageType.MAP_GETENTRYVIEW;
    public static final int RESPONSE_TYPE = 111;

    public static ClientMessage encodeRequest(String name, Data key, long threadId) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, key, threadId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Map.getEntryView");
        clientMessage.set(name);
        clientMessage.set(key);
        clientMessage.set(threadId);
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
        return parameters;
    }

    public static ClientMessage encodeResponse(SimpleEntryView<Data, Data> response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(111);
        if (response == null) {
            boolean response_isNull = true;
            clientMessage.set(response_isNull);
        } else {
            boolean response_isNull = false;
            clientMessage.set(response_isNull);
            EntryViewCodec.encode(response, clientMessage);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeResponse(SimpleEntryView<Data, Data> response, long maxIdle) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response, maxIdle);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(111);
        if (response == null) {
            boolean response_isNull = true;
            clientMessage.set(response_isNull);
        } else {
            boolean response_isNull = false;
            clientMessage.set(response_isNull);
            EntryViewCodec.encode(response, clientMessage);
        }
        clientMessage.set(maxIdle);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        SimpleEntryView<Data, Data> response = null;
        boolean response_isNull = clientMessage.getBoolean();
        if (!response_isNull) {
            response = EntryViewCodec.decode(clientMessage);
            parameters.response = response;
        }
        if (clientMessage.isComplete()) {
            return parameters;
        }
        long maxIdle = 0L;
        parameters.maxIdle = maxIdle = clientMessage.getLong();
        parameters.maxIdleExist = true;
        return parameters;
    }

    public static class ResponseParameters {
        public SimpleEntryView<Data, Data> response;
        public boolean maxIdleExist = false;
        public long maxIdle;

        public static int calculateDataSize(SimpleEntryView<Data, Data> response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            ++dataSize;
            if (response != null) {
                dataSize += EntryViewCodec.calculateDataSize(response);
            }
            return dataSize;
        }

        public static int calculateDataSize(SimpleEntryView<Data, Data> response, long maxIdle) {
            int dataSize = ClientMessage.HEADER_SIZE;
            ++dataSize;
            if (response != null) {
                dataSize += EntryViewCodec.calculateDataSize(response);
            }
            return dataSize += 8;
        }
    }

    public static class RequestParameters {
        public static final MapMessageType TYPE = REQUEST_TYPE;
        public String name;
        public Data key;
        public long threadId;

        public static int calculateDataSize(String name, Data key, long threadId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(key);
            return dataSize += 8;
        }
    }
}

