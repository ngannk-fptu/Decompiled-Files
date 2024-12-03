/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPSessionMessageType;
import com.hazelcast.client.impl.protocol.codec.RaftGroupIdCodec;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.cp.internal.RaftGroupId;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class CPSessionCreateSessionCodec {
    public static final CPSessionMessageType REQUEST_TYPE = CPSessionMessageType.CPSESSION_CREATESESSION;
    public static final int RESPONSE_TYPE = 130;

    public static ClientMessage encodeRequest(RaftGroupId groupId, String endpointName) {
        int requiredDataSize = RequestParameters.calculateDataSize(groupId, endpointName);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("CPSession.createSession");
        RaftGroupIdCodec.encode(groupId, clientMessage);
        clientMessage.set(endpointName);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        RaftGroupId groupId = null;
        parameters.groupId = groupId = RaftGroupIdCodec.decode(clientMessage);
        String endpointName = null;
        parameters.endpointName = endpointName = clientMessage.getStringUtf8();
        return parameters;
    }

    public static ClientMessage encodeResponse(long sessionId, long ttlMillis, long heartbeatMillis) {
        int requiredDataSize = ResponseParameters.calculateDataSize(sessionId, ttlMillis, heartbeatMillis);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(130);
        clientMessage.set(sessionId);
        clientMessage.set(ttlMillis);
        clientMessage.set(heartbeatMillis);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        long sessionId = 0L;
        parameters.sessionId = sessionId = clientMessage.getLong();
        long ttlMillis = 0L;
        parameters.ttlMillis = ttlMillis = clientMessage.getLong();
        long heartbeatMillis = 0L;
        parameters.heartbeatMillis = heartbeatMillis = clientMessage.getLong();
        return parameters;
    }

    public static class ResponseParameters {
        public long sessionId;
        public long ttlMillis;
        public long heartbeatMillis;

        public static int calculateDataSize(long sessionId, long ttlMillis, long heartbeatMillis) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 8;
            dataSize += 8;
            return dataSize += 8;
        }
    }

    public static class RequestParameters {
        public static final CPSessionMessageType TYPE = REQUEST_TYPE;
        public RaftGroupId groupId;
        public String endpointName;

        public static int calculateDataSize(RaftGroupId groupId, String endpointName) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += RaftGroupIdCodec.calculateDataSize(groupId);
            return dataSize += ParameterUtil.calculateDataSize(endpointName);
        }
    }
}

