/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.RingbufferMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class RingbufferAddAllCodec {
    public static final RingbufferMessageType REQUEST_TYPE = RingbufferMessageType.RINGBUFFER_ADDALL;
    public static final int RESPONSE_TYPE = 103;

    public static ClientMessage encodeRequest(String name, Collection<Data> valueList, int overflowPolicy) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, valueList, overflowPolicy);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Ringbuffer.addAll");
        clientMessage.set(name);
        clientMessage.set(valueList.size());
        for (Data valueList_item : valueList) {
            clientMessage.set(valueList_item);
        }
        clientMessage.set(overflowPolicy);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        ArrayList<Data> valueList = null;
        int valueList_size = clientMessage.getInt();
        valueList = new ArrayList<Data>(valueList_size);
        for (int valueList_index = 0; valueList_index < valueList_size; ++valueList_index) {
            Data valueList_item = clientMessage.getData();
            valueList.add(valueList_item);
        }
        parameters.valueList = valueList;
        int overflowPolicy = 0;
        parameters.overflowPolicy = overflowPolicy = clientMessage.getInt();
        return parameters;
    }

    public static ClientMessage encodeResponse(long response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(103);
        clientMessage.set(response);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        long response = 0L;
        parameters.response = response = clientMessage.getLong();
        return parameters;
    }

    public static class ResponseParameters {
        public long response;

        public static int calculateDataSize(long response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += 8;
        }
    }

    public static class RequestParameters {
        public static final RingbufferMessageType TYPE = REQUEST_TYPE;
        public String name;
        public List<Data> valueList;
        public int overflowPolicy;

        public static int calculateDataSize(String name, Collection<Data> valueList, int overflowPolicy) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            for (Data valueList_item : valueList) {
                dataSize += ParameterUtil.calculateDataSize(valueList_item);
            }
            return dataSize += 4;
        }
    }
}

