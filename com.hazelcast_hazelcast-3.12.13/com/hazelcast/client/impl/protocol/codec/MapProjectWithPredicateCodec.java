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
public final class MapProjectWithPredicateCodec {
    public static final MapMessageType REQUEST_TYPE = MapMessageType.MAP_PROJECTWITHPREDICATE;
    public static final int RESPONSE_TYPE = 119;

    public static ClientMessage encodeRequest(String name, Data projection, Data predicate) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, projection, predicate);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Map.projectWithPredicate");
        clientMessage.set(name);
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
        Data projection = null;
        parameters.projection = projection = clientMessage.getData();
        Data predicate = null;
        parameters.predicate = predicate = clientMessage.getData();
        return parameters;
    }

    public static ClientMessage encodeResponse(Collection<Data> response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(119);
        clientMessage.set(response.size());
        for (Data response_item : response) {
            boolean response_item_isNull;
            if (response_item == null) {
                response_item_isNull = true;
                clientMessage.set(response_item_isNull);
                continue;
            }
            response_item_isNull = false;
            clientMessage.set(response_item_isNull);
            clientMessage.set(response_item);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        ArrayList<Data> response = null;
        int response_size = clientMessage.getInt();
        response = new ArrayList<Data>(response_size);
        for (int response_index = 0; response_index < response_size; ++response_index) {
            Data response_item = null;
            boolean response_item_isNull = clientMessage.getBoolean();
            if (!response_item_isNull) {
                response_item = clientMessage.getData();
            }
            response.add(response_item);
        }
        parameters.response = response;
        return parameters;
    }

    public static class ResponseParameters {
        public List<Data> response;

        public static int calculateDataSize(Collection<Data> response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            for (Data response_item : response) {
                ++dataSize;
                if (response_item == null) continue;
                dataSize += ParameterUtil.calculateDataSize(response_item);
            }
            return dataSize;
        }
    }

    public static class RequestParameters {
        public static final MapMessageType TYPE = REQUEST_TYPE;
        public String name;
        public Data projection;
        public Data predicate;

        public static int calculateDataSize(String name, Data projection, Data predicate) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(projection);
            return dataSize += ParameterUtil.calculateDataSize(predicate);
        }
    }
}

