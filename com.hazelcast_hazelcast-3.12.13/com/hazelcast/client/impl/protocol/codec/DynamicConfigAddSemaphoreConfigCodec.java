/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class DynamicConfigAddSemaphoreConfigCodec {
    public static final DynamicConfigMessageType REQUEST_TYPE = DynamicConfigMessageType.DYNAMICCONFIG_ADDSEMAPHORECONFIG;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String name, int initialPermits, int backupCount, int asyncBackupCount) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, initialPermits, backupCount, asyncBackupCount);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.addSemaphoreConfig");
        clientMessage.set(name);
        clientMessage.set(initialPermits);
        clientMessage.set(backupCount);
        clientMessage.set(asyncBackupCount);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeRequest(String name, int initialPermits, int backupCount, int asyncBackupCount, String quorumName) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, initialPermits, backupCount, asyncBackupCount, quorumName);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.addSemaphoreConfig");
        clientMessage.set(name);
        clientMessage.set(initialPermits);
        clientMessage.set(backupCount);
        clientMessage.set(asyncBackupCount);
        if (quorumName == null) {
            boolean quorumName_isNull = true;
            clientMessage.set(quorumName_isNull);
        } else {
            boolean quorumName_isNull = false;
            clientMessage.set(quorumName_isNull);
            clientMessage.set(quorumName);
        }
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
        int initialPermits = 0;
        parameters.initialPermits = initialPermits = clientMessage.getInt();
        int backupCount = 0;
        parameters.backupCount = backupCount = clientMessage.getInt();
        int asyncBackupCount = 0;
        parameters.asyncBackupCount = asyncBackupCount = clientMessage.getInt();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        String quorumName = null;
        boolean quorumName_isNull = clientMessage.getBoolean();
        if (!quorumName_isNull) {
            parameters.quorumName = quorumName = clientMessage.getStringUtf8();
        }
        parameters.quorumNameExist = true;
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
        public int initialPermits;
        public int backupCount;
        public int asyncBackupCount;
        public boolean quorumNameExist = false;
        public String quorumName;

        public static int calculateDataSize(String name, int initialPermits, int backupCount, int asyncBackupCount) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            dataSize += 4;
            return dataSize += 4;
        }

        public static int calculateDataSize(String name, int initialPermits, int backupCount, int asyncBackupCount, String quorumName) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            dataSize += 4;
            dataSize += 4;
            ++dataSize;
            if (quorumName != null) {
                dataSize += ParameterUtil.calculateDataSize(quorumName);
            }
            return dataSize;
        }
    }
}

