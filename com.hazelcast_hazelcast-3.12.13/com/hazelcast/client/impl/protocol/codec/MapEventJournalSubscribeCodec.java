/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class MapEventJournalSubscribeCodec {
    public static final MapMessageType REQUEST_TYPE = MapMessageType.MAP_EVENTJOURNALSUBSCRIBE;
    public static final int RESPONSE_TYPE = 125;

    public static ClientMessage encodeRequest(String name) {
        int requiredDataSize = RequestParameters.calculateDataSize(name);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Map.eventJournalSubscribe");
        clientMessage.set(name);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        return parameters;
    }

    public static ClientMessage encodeResponse(long oldestSequence, long newestSequence) {
        int requiredDataSize = ResponseParameters.calculateDataSize(oldestSequence, newestSequence);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(125);
        clientMessage.set(oldestSequence);
        clientMessage.set(newestSequence);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        long oldestSequence = 0L;
        parameters.oldestSequence = oldestSequence = clientMessage.getLong();
        long newestSequence = 0L;
        parameters.newestSequence = newestSequence = clientMessage.getLong();
        return parameters;
    }

    public static class ResponseParameters {
        public long oldestSequence;
        public long newestSequence;

        public static int calculateDataSize(long oldestSequence, long newestSequence) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 8;
            return dataSize += 8;
        }
    }

    public static class RequestParameters {
        public static final MapMessageType TYPE = REQUEST_TYPE;
        public String name;

        public static int calculateDataSize(String name) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += ParameterUtil.calculateDataSize(name);
        }
    }
}

