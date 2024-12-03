/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class QueueContainsAllCodec {
    public static final QueueMessageType REQUEST_TYPE = QueueMessageType.QUEUE_CONTAINSALL;
    public static final int RESPONSE_TYPE = 101;

    public static ClientMessage encodeRequest(String name, Collection<Data> dataList) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, dataList);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Queue.containsAll");
        clientMessage.set(name);
        clientMessage.set(dataList.size());
        for (Data dataList_item : dataList) {
            clientMessage.set(dataList_item);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        ArrayList<Data> dataList = null;
        int dataList_size = clientMessage.getInt();
        dataList = new ArrayList<Data>(dataList_size);
        for (int dataList_index = 0; dataList_index < dataList_size; ++dataList_index) {
            Data dataList_item = clientMessage.getData();
            dataList.add(dataList_item);
        }
        parameters.dataList = dataList;
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
        public static final QueueMessageType TYPE = REQUEST_TYPE;
        public String name;
        public List<Data> dataList;

        public static int calculateDataSize(String name, Collection<Data> dataList) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            for (Data dataList_item : dataList) {
                dataSize += ParameterUtil.calculateDataSize(dataList_item);
            }
            return dataSize;
        }
    }
}

