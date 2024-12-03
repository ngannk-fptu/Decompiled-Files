/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AddressCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.Address;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ScheduledExecutorGetStatsFromAddressCodec {
    public static final ScheduledExecutorMessageType REQUEST_TYPE = ScheduledExecutorMessageType.SCHEDULEDEXECUTOR_GETSTATSFROMADDRESS;
    public static final int RESPONSE_TYPE = 120;

    public static ClientMessage encodeRequest(String schedulerName, String taskName, Address address) {
        int requiredDataSize = RequestParameters.calculateDataSize(schedulerName, taskName, address);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("ScheduledExecutor.getStatsFromAddress");
        clientMessage.set(schedulerName);
        clientMessage.set(taskName);
        AddressCodec.encode(address, clientMessage);
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
        Address address = null;
        parameters.address = address = AddressCodec.decode(clientMessage);
        return parameters;
    }

    public static ClientMessage encodeResponse(long lastIdleTimeNanos, long totalIdleTimeNanos, long totalRuns, long totalRunTimeNanos) {
        int requiredDataSize = ResponseParameters.calculateDataSize(lastIdleTimeNanos, totalIdleTimeNanos, totalRuns, totalRunTimeNanos);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(120);
        clientMessage.set(lastIdleTimeNanos);
        clientMessage.set(totalIdleTimeNanos);
        clientMessage.set(totalRuns);
        clientMessage.set(totalRunTimeNanos);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeResponse(long lastIdleTimeNanos, long totalIdleTimeNanos, long totalRuns, long totalRunTimeNanos, long lastRunDurationNanos) {
        int requiredDataSize = ResponseParameters.calculateDataSize(lastIdleTimeNanos, totalIdleTimeNanos, totalRuns, totalRunTimeNanos, lastRunDurationNanos);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(120);
        clientMessage.set(lastIdleTimeNanos);
        clientMessage.set(totalIdleTimeNanos);
        clientMessage.set(totalRuns);
        clientMessage.set(totalRunTimeNanos);
        clientMessage.set(lastRunDurationNanos);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        long lastIdleTimeNanos = 0L;
        parameters.lastIdleTimeNanos = lastIdleTimeNanos = clientMessage.getLong();
        long totalIdleTimeNanos = 0L;
        parameters.totalIdleTimeNanos = totalIdleTimeNanos = clientMessage.getLong();
        long totalRuns = 0L;
        parameters.totalRuns = totalRuns = clientMessage.getLong();
        long totalRunTimeNanos = 0L;
        parameters.totalRunTimeNanos = totalRunTimeNanos = clientMessage.getLong();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        long lastRunDurationNanos = 0L;
        parameters.lastRunDurationNanos = lastRunDurationNanos = clientMessage.getLong();
        parameters.lastRunDurationNanosExist = true;
        return parameters;
    }

    public static class ResponseParameters {
        public long lastIdleTimeNanos;
        public long totalIdleTimeNanos;
        public long totalRuns;
        public long totalRunTimeNanos;
        public boolean lastRunDurationNanosExist = false;
        public long lastRunDurationNanos;

        public static int calculateDataSize(long lastIdleTimeNanos, long totalIdleTimeNanos, long totalRuns, long totalRunTimeNanos) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 8;
            dataSize += 8;
            dataSize += 8;
            return dataSize += 8;
        }

        public static int calculateDataSize(long lastIdleTimeNanos, long totalIdleTimeNanos, long totalRuns, long totalRunTimeNanos, long lastRunDurationNanos) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 8;
            dataSize += 8;
            dataSize += 8;
            dataSize += 8;
            return dataSize += 8;
        }
    }

    public static class RequestParameters {
        public static final ScheduledExecutorMessageType TYPE = REQUEST_TYPE;
        public String schedulerName;
        public String taskName;
        public Address address;

        public static int calculateDataSize(String schedulerName, String taskName, Address address) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(schedulerName);
            dataSize += ParameterUtil.calculateDataSize(taskName);
            return dataSize += AddressCodec.calculateDataSize(address);
        }
    }
}

