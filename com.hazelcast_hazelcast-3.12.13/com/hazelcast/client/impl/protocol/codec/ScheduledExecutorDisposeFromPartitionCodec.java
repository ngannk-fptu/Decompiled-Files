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
public final class ScheduledExecutorDisposeFromPartitionCodec {
    public static final ScheduledExecutorMessageType REQUEST_TYPE = ScheduledExecutorMessageType.SCHEDULEDEXECUTOR_DISPOSEFROMPARTITION;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String schedulerName, String taskName) {
        int requiredDataSize = RequestParameters.calculateDataSize(schedulerName, taskName);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("ScheduledExecutor.disposeFromPartition");
        clientMessage.set(schedulerName);
        clientMessage.set(taskName);
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
        public static final ScheduledExecutorMessageType TYPE = REQUEST_TYPE;
        public String schedulerName;
        public String taskName;

        public static int calculateDataSize(String schedulerName, String taskName) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(schedulerName);
            return dataSize += ParameterUtil.calculateDataSize(taskName);
        }
    }
}

