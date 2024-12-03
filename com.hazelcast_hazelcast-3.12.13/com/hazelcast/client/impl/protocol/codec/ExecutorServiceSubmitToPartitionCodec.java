/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ExecutorServiceMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ExecutorServiceSubmitToPartitionCodec {
    public static final ExecutorServiceMessageType REQUEST_TYPE = ExecutorServiceMessageType.EXECUTORSERVICE_SUBMITTOPARTITION;
    public static final int RESPONSE_TYPE = 105;

    public static ClientMessage encodeRequest(String name, String uuid, Data callable, int partitionId) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, uuid, callable, partitionId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("ExecutorService.submitToPartition");
        clientMessage.set(name);
        clientMessage.set(uuid);
        clientMessage.set(callable);
        clientMessage.set(partitionId);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        String uuid = null;
        parameters.uuid = uuid = clientMessage.getStringUtf8();
        Data callable = null;
        parameters.callable = callable = clientMessage.getData();
        int partitionId = 0;
        parameters.partitionId = partitionId = clientMessage.getInt();
        return parameters;
    }

    public static ClientMessage encodeResponse(Data response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(105);
        if (response == null) {
            boolean response_isNull = true;
            clientMessage.set(response_isNull);
        } else {
            boolean response_isNull = false;
            clientMessage.set(response_isNull);
            clientMessage.set(response);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        Data response = null;
        boolean response_isNull = clientMessage.getBoolean();
        if (!response_isNull) {
            parameters.response = response = clientMessage.getData();
        }
        return parameters;
    }

    public static class ResponseParameters {
        public Data response;

        public static int calculateDataSize(Data response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            ++dataSize;
            if (response != null) {
                dataSize += ParameterUtil.calculateDataSize(response);
            }
            return dataSize;
        }
    }

    public static class RequestParameters {
        public static final ExecutorServiceMessageType TYPE = REQUEST_TYPE;
        public String name;
        public String uuid;
        public Data callable;
        public int partitionId;

        public static int calculateDataSize(String name, String uuid, Data callable, int partitionId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(uuid);
            dataSize += ParameterUtil.calculateDataSize(callable);
            return dataSize += 4;
        }
    }
}

