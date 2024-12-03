/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigMessageType;
import com.hazelcast.client.impl.protocol.codec.RingbufferStoreConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.RingbufferStoreConfigHolder;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class DynamicConfigAddRingbufferConfigCodec {
    public static final DynamicConfigMessageType REQUEST_TYPE = DynamicConfigMessageType.DYNAMICCONFIG_ADDRINGBUFFERCONFIG;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String name, int capacity, int backupCount, int asyncBackupCount, int timeToLiveSeconds, String inMemoryFormat, RingbufferStoreConfigHolder ringbufferStoreConfig) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, capacity, backupCount, asyncBackupCount, timeToLiveSeconds, inMemoryFormat, ringbufferStoreConfig);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.addRingbufferConfig");
        clientMessage.set(name);
        clientMessage.set(capacity);
        clientMessage.set(backupCount);
        clientMessage.set(asyncBackupCount);
        clientMessage.set(timeToLiveSeconds);
        clientMessage.set(inMemoryFormat);
        if (ringbufferStoreConfig == null) {
            boolean ringbufferStoreConfig_isNull = true;
            clientMessage.set(ringbufferStoreConfig_isNull);
        } else {
            boolean ringbufferStoreConfig_isNull = false;
            clientMessage.set(ringbufferStoreConfig_isNull);
            RingbufferStoreConfigCodec.encode(ringbufferStoreConfig, clientMessage);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeRequest(String name, int capacity, int backupCount, int asyncBackupCount, int timeToLiveSeconds, String inMemoryFormat, RingbufferStoreConfigHolder ringbufferStoreConfig, String quorumName, String mergePolicy, int mergeBatchSize) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, capacity, backupCount, asyncBackupCount, timeToLiveSeconds, inMemoryFormat, ringbufferStoreConfig, quorumName, mergePolicy, mergeBatchSize);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.addRingbufferConfig");
        clientMessage.set(name);
        clientMessage.set(capacity);
        clientMessage.set(backupCount);
        clientMessage.set(asyncBackupCount);
        clientMessage.set(timeToLiveSeconds);
        clientMessage.set(inMemoryFormat);
        if (ringbufferStoreConfig == null) {
            boolean ringbufferStoreConfig_isNull = true;
            clientMessage.set(ringbufferStoreConfig_isNull);
        } else {
            boolean ringbufferStoreConfig_isNull = false;
            clientMessage.set(ringbufferStoreConfig_isNull);
            RingbufferStoreConfigCodec.encode(ringbufferStoreConfig, clientMessage);
        }
        if (quorumName == null) {
            boolean quorumName_isNull = true;
            clientMessage.set(quorumName_isNull);
        } else {
            boolean quorumName_isNull = false;
            clientMessage.set(quorumName_isNull);
            clientMessage.set(quorumName);
        }
        clientMessage.set(mergePolicy);
        clientMessage.set(mergeBatchSize);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        int capacity = 0;
        parameters.capacity = capacity = clientMessage.getInt();
        int backupCount = 0;
        parameters.backupCount = backupCount = clientMessage.getInt();
        int asyncBackupCount = 0;
        parameters.asyncBackupCount = asyncBackupCount = clientMessage.getInt();
        int timeToLiveSeconds = 0;
        parameters.timeToLiveSeconds = timeToLiveSeconds = clientMessage.getInt();
        String inMemoryFormat = null;
        parameters.inMemoryFormat = inMemoryFormat = clientMessage.getStringUtf8();
        RingbufferStoreConfigHolder ringbufferStoreConfig = null;
        boolean ringbufferStoreConfig_isNull = clientMessage.getBoolean();
        if (!ringbufferStoreConfig_isNull) {
            parameters.ringbufferStoreConfig = ringbufferStoreConfig = RingbufferStoreConfigCodec.decode(clientMessage);
        }
        if (clientMessage.isComplete()) {
            return parameters;
        }
        String quorumName = null;
        boolean quorumName_isNull = clientMessage.getBoolean();
        if (!quorumName_isNull) {
            parameters.quorumName = quorumName = clientMessage.getStringUtf8();
        }
        parameters.quorumNameExist = true;
        String mergePolicy = null;
        parameters.mergePolicy = mergePolicy = clientMessage.getStringUtf8();
        parameters.mergePolicyExist = true;
        int mergeBatchSize = 0;
        parameters.mergeBatchSize = mergeBatchSize = clientMessage.getInt();
        parameters.mergeBatchSizeExist = true;
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
        public static final DynamicConfigMessageType TYPE = REQUEST_TYPE;
        public String name;
        public int capacity;
        public int backupCount;
        public int asyncBackupCount;
        public int timeToLiveSeconds;
        public String inMemoryFormat;
        public RingbufferStoreConfigHolder ringbufferStoreConfig;
        public boolean quorumNameExist = false;
        public String quorumName;
        public boolean mergePolicyExist = false;
        public String mergePolicy;
        public boolean mergeBatchSizeExist = false;
        public int mergeBatchSize;

        public static int calculateDataSize(String name, int capacity, int backupCount, int asyncBackupCount, int timeToLiveSeconds, String inMemoryFormat, RingbufferStoreConfigHolder ringbufferStoreConfig) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            dataSize += 4;
            dataSize += 4;
            dataSize += 4;
            dataSize += ParameterUtil.calculateDataSize(inMemoryFormat);
            ++dataSize;
            if (ringbufferStoreConfig != null) {
                dataSize += RingbufferStoreConfigCodec.calculateDataSize(ringbufferStoreConfig);
            }
            return dataSize;
        }

        public static int calculateDataSize(String name, int capacity, int backupCount, int asyncBackupCount, int timeToLiveSeconds, String inMemoryFormat, RingbufferStoreConfigHolder ringbufferStoreConfig, String quorumName, String mergePolicy, int mergeBatchSize) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            dataSize += 4;
            dataSize += 4;
            dataSize += 4;
            dataSize += ParameterUtil.calculateDataSize(inMemoryFormat);
            ++dataSize;
            if (ringbufferStoreConfig != null) {
                dataSize += RingbufferStoreConfigCodec.calculateDataSize(ringbufferStoreConfig);
            }
            ++dataSize;
            if (quorumName != null) {
                dataSize += ParameterUtil.calculateDataSize(quorumName);
            }
            dataSize += ParameterUtil.calculateDataSize(mergePolicy);
            return dataSize += 4;
        }
    }
}

