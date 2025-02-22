/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ReplicatedMapEntrySetCodec {
    public static final ReplicatedMapMessageType REQUEST_TYPE = ReplicatedMapMessageType.REPLICATEDMAP_ENTRYSET;
    public static final int RESPONSE_TYPE = 117;

    public static ClientMessage encodeRequest(String name) {
        int requiredDataSize = RequestParameters.calculateDataSize(name);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("ReplicatedMap.entrySet");
        clientMessage.set(name);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        return parameters;
    }

    public static ClientMessage encodeResponse(Collection<Map.Entry<Data, Data>> response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(117);
        clientMessage.set(response.size());
        for (Map.Entry<Data, Data> response_item : response) {
            Data response_itemKey = response_item.getKey();
            Data response_itemVal = response_item.getValue();
            clientMessage.set(response_itemKey);
            clientMessage.set(response_itemVal);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        ArrayList<Map.Entry<Data, Data>> response = null;
        int response_size = clientMessage.getInt();
        response = new ArrayList<Map.Entry<Data, Data>>(response_size);
        for (int response_index = 0; response_index < response_size; ++response_index) {
            Data response_item_key = clientMessage.getData();
            Data response_item_val = clientMessage.getData();
            AbstractMap.SimpleEntry<Data, Data> response_item = new AbstractMap.SimpleEntry<Data, Data>(response_item_key, response_item_val);
            response.add(response_item);
        }
        parameters.response = response;
        return parameters;
    }

    public static class ResponseParameters {
        public List<Map.Entry<Data, Data>> response;

        public static int calculateDataSize(Collection<Map.Entry<Data, Data>> response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            for (Map.Entry<Data, Data> response_item : response) {
                Data response_itemKey = response_item.getKey();
                Data response_itemVal = response_item.getValue();
                dataSize += ParameterUtil.calculateDataSize(response_itemKey);
                dataSize += ParameterUtil.calculateDataSize(response_itemVal);
            }
            return dataSize;
        }
    }

    public static class RequestParameters {
        public static final ReplicatedMapMessageType TYPE = REQUEST_TYPE;
        public String name;

        public static int calculateDataSize(String name) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += ParameterUtil.calculateDataSize(name);
        }
    }
}

