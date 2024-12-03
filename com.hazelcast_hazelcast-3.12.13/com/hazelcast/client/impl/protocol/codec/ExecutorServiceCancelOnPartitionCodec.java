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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ExecutorServiceCancelOnPartitionCodec {
    public static final ExecutorServiceMessageType REQUEST_TYPE = ExecutorServiceMessageType.EXECUTORSERVICE_CANCELONPARTITION;
    public static final int RESPONSE_TYPE = 101;

    public static ClientMessage encodeRequest(String uuid, int partitionId, boolean interrupt) {
        int requiredDataSize = RequestParameters.calculateDataSize(uuid, partitionId, interrupt);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("ExecutorService.cancelOnPartition");
        clientMessage.set(uuid);
        clientMessage.set(partitionId);
        clientMessage.set(interrupt);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String uuid = null;
        parameters.uuid = uuid = clientMessage.getStringUtf8();
        int partitionId = 0;
        parameters.partitionId = partitionId = clientMessage.getInt();
        boolean interrupt = false;
        parameters.interrupt = interrupt = clientMessage.getBoolean();
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
        public static final ExecutorServiceMessageType TYPE = REQUEST_TYPE;
        public String uuid;
        public int partitionId;
        public boolean interrupt;

        public static int calculateDataSize(String uuid, int partitionId, boolean interrupt) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(uuid);
            dataSize += 4;
            return ++dataSize;
        }
    }
}

