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
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ScheduledExecutorSubmitToAddressCodec {
    public static final ScheduledExecutorMessageType REQUEST_TYPE = ScheduledExecutorMessageType.SCHEDULEDEXECUTOR_SUBMITTOADDRESS;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String schedulerName, Address address, byte type, String taskName, Data task, long initialDelayInMillis, long periodInMillis) {
        int requiredDataSize = RequestParameters.calculateDataSize(schedulerName, address, type, taskName, task, initialDelayInMillis, periodInMillis);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("ScheduledExecutor.submitToAddress");
        clientMessage.set(schedulerName);
        AddressCodec.encode(address, clientMessage);
        clientMessage.set(type);
        clientMessage.set(taskName);
        clientMessage.set(task);
        clientMessage.set(initialDelayInMillis);
        clientMessage.set(periodInMillis);
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
        Address address = null;
        parameters.address = address = AddressCodec.decode(clientMessage);
        byte type = 0;
        parameters.type = type = clientMessage.getByte();
        String taskName = null;
        parameters.taskName = taskName = clientMessage.getStringUtf8();
        Data task = null;
        parameters.task = task = clientMessage.getData();
        long initialDelayInMillis = 0L;
        parameters.initialDelayInMillis = initialDelayInMillis = clientMessage.getLong();
        long periodInMillis = 0L;
        parameters.periodInMillis = periodInMillis = clientMessage.getLong();
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
        public Address address;
        public byte type;
        public String taskName;
        public Data task;
        public long initialDelayInMillis;
        public long periodInMillis;

        public static int calculateDataSize(String schedulerName, Address address, byte type, String taskName, Data task, long initialDelayInMillis, long periodInMillis) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(schedulerName);
            dataSize += AddressCodec.calculateDataSize(address);
            ++dataSize;
            dataSize += ParameterUtil.calculateDataSize(taskName);
            dataSize += ParameterUtil.calculateDataSize(task);
            dataSize += 8;
            return dataSize += 8;
        }
    }
}

