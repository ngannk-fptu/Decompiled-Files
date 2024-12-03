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
public final class DynamicConfigAddFlakeIdGeneratorConfigCodec {
    public static final DynamicConfigMessageType REQUEST_TYPE = DynamicConfigMessageType.DYNAMICCONFIG_ADDFLAKEIDGENERATORCONFIG;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String name, int prefetchCount, long prefetchValidity, long idOffset, boolean statisticsEnabled, long nodeIdOffset) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, prefetchCount, prefetchValidity, idOffset, statisticsEnabled, nodeIdOffset);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.addFlakeIdGeneratorConfig");
        clientMessage.set(name);
        clientMessage.set(prefetchCount);
        clientMessage.set(prefetchValidity);
        clientMessage.set(idOffset);
        clientMessage.set(statisticsEnabled);
        clientMessage.set(nodeIdOffset);
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
        int prefetchCount = 0;
        parameters.prefetchCount = prefetchCount = clientMessage.getInt();
        long prefetchValidity = 0L;
        parameters.prefetchValidity = prefetchValidity = clientMessage.getLong();
        long idOffset = 0L;
        parameters.idOffset = idOffset = clientMessage.getLong();
        boolean statisticsEnabled = false;
        parameters.statisticsEnabled = statisticsEnabled = clientMessage.getBoolean();
        long nodeIdOffset = 0L;
        parameters.nodeIdOffset = nodeIdOffset = clientMessage.getLong();
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
        public int prefetchCount;
        public long prefetchValidity;
        public long idOffset;
        public boolean statisticsEnabled;
        public long nodeIdOffset;

        public static int calculateDataSize(String name, int prefetchCount, long prefetchValidity, long idOffset, boolean statisticsEnabled, long nodeIdOffset) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            dataSize += 8;
            dataSize += 8;
            ++dataSize;
            return dataSize += 8;
        }
    }
}

