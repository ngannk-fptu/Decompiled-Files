/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.FlakeIdGeneratorMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class FlakeIdGeneratorNewIdBatchCodec {
    public static final FlakeIdGeneratorMessageType REQUEST_TYPE = FlakeIdGeneratorMessageType.FLAKEIDGENERATOR_NEWIDBATCH;
    public static final int RESPONSE_TYPE = 126;

    public static ClientMessage encodeRequest(String name, int batchSize) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, batchSize);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("FlakeIdGenerator.newIdBatch");
        clientMessage.set(name);
        clientMessage.set(batchSize);
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
        int batchSize = 0;
        parameters.batchSize = batchSize = clientMessage.getInt();
        return parameters;
    }

    public static ClientMessage encodeResponse(long base, long increment, int batchSize) {
        int requiredDataSize = ResponseParameters.calculateDataSize(base, increment, batchSize);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(126);
        clientMessage.set(base);
        clientMessage.set(increment);
        clientMessage.set(batchSize);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        long base = 0L;
        parameters.base = base = clientMessage.getLong();
        long increment = 0L;
        parameters.increment = increment = clientMessage.getLong();
        int batchSize = 0;
        parameters.batchSize = batchSize = clientMessage.getInt();
        return parameters;
    }

    public static class ResponseParameters {
        public long base;
        public long increment;
        public int batchSize;

        public static int calculateDataSize(long base, long increment, int batchSize) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 8;
            dataSize += 8;
            return dataSize += 4;
        }
    }

    public static class RequestParameters {
        public static final FlakeIdGeneratorMessageType TYPE = REQUEST_TYPE;
        public String name;
        public int batchSize;

        public static int calculateDataSize(String name, int batchSize) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            return dataSize += 4;
        }
    }
}

