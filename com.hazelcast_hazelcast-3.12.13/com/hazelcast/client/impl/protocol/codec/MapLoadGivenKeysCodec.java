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
public final class MapLoadGivenKeysCodec {
    public static final MapMessageType REQUEST_TYPE = MapMessageType.MAP_LOADGIVENKEYS;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String name, Collection<Data> keys, boolean replaceExistingValues) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, keys, replaceExistingValues);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Map.loadGivenKeys");
        clientMessage.set(name);
        clientMessage.set(keys.size());
        for (Data keys_item : keys) {
            clientMessage.set(keys_item);
        }
        clientMessage.set(replaceExistingValues);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        ArrayList<Data> keys = null;
        int keys_size = clientMessage.getInt();
        keys = new ArrayList<Data>(keys_size);
        for (int keys_index = 0; keys_index < keys_size; ++keys_index) {
            Data keys_item = clientMessage.getData();
            keys.add(keys_item);
        }
        parameters.keys = keys;
        boolean replaceExistingValues = false;
        parameters.replaceExistingValues = replaceExistingValues = clientMessage.getBoolean();
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
        public static final MapMessageType TYPE = REQUEST_TYPE;
        public String name;
        public List<Data> keys;
        public boolean replaceExistingValues;

        public static int calculateDataSize(String name, Collection<Data> keys, boolean replaceExistingValues) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            for (Data keys_item : keys) {
                dataSize += ParameterUtil.calculateDataSize(keys_item);
            }
            return ++dataSize;
        }
    }
}

