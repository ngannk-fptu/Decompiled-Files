/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ContinuousQueryMessageType;
import com.hazelcast.client.impl.protocol.codec.QueryCacheEventDataCodec;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ContinuousQueryAddListenerCodec {
    public static final ContinuousQueryMessageType REQUEST_TYPE = ContinuousQueryMessageType.CONTINUOUSQUERY_ADDLISTENER;
    public static final int RESPONSE_TYPE = 104;

    public static ClientMessage encodeRequest(String listenerName, boolean localOnly) {
        int requiredDataSize = RequestParameters.calculateDataSize(listenerName, localOnly);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("ContinuousQuery.addListener");
        clientMessage.set(listenerName);
        clientMessage.set(localOnly);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String listenerName = null;
        parameters.listenerName = listenerName = clientMessage.getStringUtf8();
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

    public static ClientMessage encodeQueryCacheSingleEvent(QueryCacheEventData data) {
        int dataSize = ClientMessage.HEADER_SIZE;
        ClientMessage clientMessage = ClientMessage.createForEncode(dataSize += QueryCacheEventDataCodec.calculateDataSize(data));
        clientMessage.setMessageType(212);
        clientMessage.addFlag((short)1);
        QueryCacheEventDataCodec.encode(data, clientMessage);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeQueryCacheBatchEvent(Collection<QueryCacheEventData> events, String source, int partitionId) {
        int dataSize = ClientMessage.HEADER_SIZE;
        dataSize += 4;
        for (QueryCacheEventData events_item : events) {
            dataSize += QueryCacheEventDataCodec.calculateDataSize(events_item);
        }
        dataSize += ParameterUtil.calculateDataSize(source);
        ClientMessage clientMessage = ClientMessage.createForEncode(dataSize += 4);
        clientMessage.setMessageType(213);
        clientMessage.addFlag((short)1);
        clientMessage.set(events.size());
        for (QueryCacheEventData events_item : events) {
            QueryCacheEventDataCodec.encode(events_item, clientMessage);
        }
        clientMessage.set(source);
        clientMessage.set(partitionId);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static abstract class AbstractEventHandler {
        public void handle(ClientMessage clientMessage) {
            int messageType = clientMessage.getMessageType();
            if (messageType == 212) {
                QueryCacheEventData data = null;
                data = QueryCacheEventDataCodec.decode(clientMessage);
                this.handleQueryCacheSingleEventV10(data);
                return;
            }
            if (messageType == 213) {
                ArrayList<QueryCacheEventData> events = null;
                int events_size = clientMessage.getInt();
                events = new ArrayList<QueryCacheEventData>(events_size);
                for (int events_index = 0; events_index < events_size; ++events_index) {
                    QueryCacheEventData events_item = QueryCacheEventDataCodec.decode(clientMessage);
                    events.add(events_item);
                }
                String source = null;
                source = clientMessage.getStringUtf8();
                int partitionId = 0;
                partitionId = clientMessage.getInt();
                this.handleQueryCacheBatchEventV10(events, source, partitionId);
                return;
            }
            Logger.getLogger(super.getClass()).warning("Unknown message type received on event handler :" + messageType);
        }

        public abstract void handleQueryCacheSingleEventV10(QueryCacheEventData var1);

        public abstract void handleQueryCacheBatchEventV10(Collection<QueryCacheEventData> var1, String var2, int var3);
    }

    public static class ResponseParameters {
        public String response;

        public static int calculateDataSize(String response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += ParameterUtil.calculateDataSize(response);
        }
    }

    public static class RequestParameters {
        public static final ContinuousQueryMessageType TYPE = REQUEST_TYPE;
        public String listenerName;
        public boolean localOnly;

        public static int calculateDataSize(String listenerName, boolean localOnly) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(listenerName);
            return ++dataSize;
        }
    }
}

