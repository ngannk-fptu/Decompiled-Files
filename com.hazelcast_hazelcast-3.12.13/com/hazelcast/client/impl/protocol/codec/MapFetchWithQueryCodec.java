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
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class MapFetchWithQueryCodec {
    public static final MapMessageType REQUEST_TYPE = MapMessageType.MAP_FETCHWITHQUERY;
    public static final int RESPONSE_TYPE = 124;

    public static ClientMessage encodeRequest(String name, int tableIndex, int batch, Data projection, Data predicate) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, tableIndex, batch, projection, predicate);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Map.fetchWithQuery");
        clientMessage.set(name);
        clientMessage.set(tableIndex);
        clientMessage.set(batch);
        clientMessage.set(projection);
        clientMessage.set(predicate);
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
        int tableIndex = 0;
        parameters.tableIndex = tableIndex = clientMessage.getInt();
        int batch = 0;
        parameters.batch = batch = clientMessage.getInt();
        Data projection = null;
        parameters.projection = projection = clientMessage.getData();
        Data predicate = null;
        parameters.predicate = predicate = clientMessage.getData();
        return parameters;
    }

    public static ClientMessage encodeResponse(Collection<Data> results, int nextTableIndexToReadFrom) {
        int requiredDataSize = ResponseParameters.calculateDataSize(results, nextTableIndexToReadFrom);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(124);
        clientMessage.set(results.size());
        for (Data results_item : results) {
            boolean results_item_isNull;
            if (results_item == null) {
                results_item_isNull = true;
                clientMessage.set(results_item_isNull);
                continue;
            }
            results_item_isNull = false;
            clientMessage.set(results_item_isNull);
            clientMessage.set(results_item);
        }
        clientMessage.set(nextTableIndexToReadFrom);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        ArrayList<Data> results = null;
        int results_size = clientMessage.getInt();
        results = new ArrayList<Data>(results_size);
        for (int results_index = 0; results_index < results_size; ++results_index) {
            Data results_item = null;
            boolean results_item_isNull = clientMessage.getBoolean();
            if (!results_item_isNull) {
                results_item = clientMessage.getData();
            }
            results.add(results_item);
        }
        parameters.results = results;
        int nextTableIndexToReadFrom = 0;
        parameters.nextTableIndexToReadFrom = nextTableIndexToReadFrom = clientMessage.getInt();
        return parameters;
    }

    public static class ResponseParameters {
        public List<Data> results;
        public int nextTableIndexToReadFrom;

        public static int calculateDataSize(Collection<Data> results, int nextTableIndexToReadFrom) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            for (Data results_item : results) {
                ++dataSize;
                if (results_item == null) continue;
                dataSize += ParameterUtil.calculateDataSize(results_item);
            }
            return dataSize += 4;
        }
    }

    public static class RequestParameters {
        public static final MapMessageType TYPE = REQUEST_TYPE;
        public String name;
        public int tableIndex;
        public int batch;
        public Data projection;
        public Data predicate;

        public static int calculateDataSize(String name, int tableIndex, int batch, Data projection, Data predicate) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            dataSize += 4;
            dataSize += ParameterUtil.calculateDataSize(projection);
            return dataSize += ParameterUtil.calculateDataSize(predicate);
        }
    }
}

