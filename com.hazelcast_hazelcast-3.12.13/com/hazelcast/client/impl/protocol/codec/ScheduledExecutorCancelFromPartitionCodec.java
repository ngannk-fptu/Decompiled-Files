/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ScheduledExecutorCancelFromPartitionCodec {
    public static final ScheduledExecutorMessageType REQUEST_TYPE = ScheduledExecutorMessageType.SCHEDULEDEXECUTOR_CANCELFROMPARTITION;
    public static final int RESPONSE_TYPE = 101;

    public static ClientMessage encodeRequest(String schedulerName, String taskName, boolean mayInterruptIfRunning) {
        int requiredDataSize = RequestParameters.calculateDataSize(schedulerName, taskName, mayInterruptIfRunning);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("ScheduledExecutor.cancelFromPartition");
        clientMessage.set(schedulerName);
        clientMessage.set(taskName);
        clientMessage.set(mayInterruptIfRunning);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        String schedulerName = null;
        parameters.schedulerName = schedulerName = clientMessage.getStringUtf8();
        String taskName = null;
        parameters.taskName = taskName = clientMessage.getStringUtf8();
        boolean mayInterruptIfRunning = false;
        parameters.mayInterruptIfRunning = mayInterruptIfRunning = clientMessage.getBoolean();
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
        public static final ScheduledExecutorMessageType TYPE = REQUEST_TYPE;
        public String schedulerName;
        public String taskName;
        public boolean mayInterruptIfRunning;

        public static int calculateDataSize(String schedulerName, String taskName, boolean mayInterruptIfRunning) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(schedulerName);
            dataSize += ParameterUtil.calculateDataSize(taskName);
            return ++dataSize;
        }
    }
}

