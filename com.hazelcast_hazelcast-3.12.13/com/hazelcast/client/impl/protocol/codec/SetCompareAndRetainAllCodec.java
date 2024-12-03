/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SetMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class SetCompareAndRetainAllCodec {
    public static final SetMessageType REQUEST_TYPE = SetMessageType.SET_COMPAREANDRETAINALL;
    public static final int RESPONSE_TYPE = 101;

    public static ClientMessage encodeRequest(String name, Collection<Data> values) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, values);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Set.compareAndRetainAll");
        clientMessage.set(name);
        clientMessage.set(values.size());
        for (Data values_item : values) {
            clientMessage.set(values_item);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        ArrayList<Data> values = null;
        int values_size = clientMessage.getInt();
        values = new ArrayList<Data>(values_size);
        for (int values_index = 0; values_index < values_size; ++values_index) {
            Data values_item = clientMessage.getData();
            values.add(values_item);
        }
        parameters.values = values;
        return parameters;
    }

    public static ClientMessage encodeResponse(boolean response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(101);
        clientMessage.set(response);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        boolean response = false;
        parameters.response = response = clientMessage.getBoolean();
        return parameters;
    }

    public static class ResponseParameters {
        public boolean response;

        public static int calculateDataSize(boolean response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return ++dataSize;
        }
    }

    public static class RequestParameters {
        public static final SetMessageType TYPE = REQUEST_TYPE;
        public String name;
        public List<Data> values;

        public static int calculateDataSize(String name, Collection<Data> values) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            for (Data values_item : values) {
                dataSize += ParameterUtil.calculateDataSize(values_item);
            }
            return dataSize;
        }
    }
}

