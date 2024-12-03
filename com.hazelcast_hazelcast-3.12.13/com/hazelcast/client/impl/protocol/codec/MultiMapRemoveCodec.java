/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class MultiMapRemoveCodec {
    public static final MultiMapMessageType REQUEST_TYPE = MultiMapMessageType.MULTIMAP_REMOVE;
    public static final int RESPONSE_TYPE = 106;

    public static ClientMessage encodeRequest(String name, Data key, long threadId) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, key, threadId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("MultiMap.remove");
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

    public static ClientMessage encodeResponse(Collection<Data> response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(106);
        clientMessage.set(response.size());
        for (Data response_item : response) {
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
            Data response_item = clientMessage.getData();
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
                dataSize += ParameterUtil.calculateDataSize(response_item);
            }
            return dataSize;
        }
    }

    public static class RequestParameters {
        public static final MultiMapMessageType TYPE = REQUEST_TYPE;
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

