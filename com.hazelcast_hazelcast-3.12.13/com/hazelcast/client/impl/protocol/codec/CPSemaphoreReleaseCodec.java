/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPSemaphoreMessageType;
import com.hazelcast.client.impl.protocol.codec.RaftGroupIdCodec;
import com.hazelcast.client.impl.protocol.codec.UUIDCodec;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.cp.internal.RaftGroupId;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class CPSemaphoreReleaseCodec {
    public static final CPSemaphoreMessageType REQUEST_TYPE = CPSemaphoreMessageType.CPSEMAPHORE_RELEASE;
    public static final int RESPONSE_TYPE = 101;

    public static ClientMessage encodeRequest(RaftGroupId groupId, String name, long sessionId, long threadId, UUID invocationUid, int permits) {
        int requiredDataSize = RequestParameters.calculateDataSize(groupId, name, sessionId, threadId, invocationUid, permits);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("CPSemaphore.release");
        RaftGroupIdCodec.encode(groupId, clientMessage);
        clientMessage.set(name);
        clientMessage.set(sessionId);
        clientMessage.set(threadId);
        UUIDCodec.encode(invocationUid, clientMessage);
        clientMessage.set(permits);
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
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        long sessionId = 0L;
        parameters.sessionId = sessionId = clientMessage.getLong();
        long threadId = 0L;
        parameters.threadId = threadId = clientMessage.getLong();
        UUID invocationUid = null;
        parameters.invocationUid = invocationUid = UUIDCodec.decode(clientMessage);
        int permits = 0;
        parameters.permits = permits = clientMessage.getInt();
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
        public static final CPSemaphoreMessageType TYPE = REQUEST_TYPE;
        public RaftGroupId groupId;
        public String name;
        public long sessionId;
        public long threadId;
        public UUID invocationUid;
        public int permits;

        public static int calculateDataSize(RaftGroupId groupId, String name, long sessionId, long threadId, UUID invocationUid, int permits) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += RaftGroupIdCodec.calculateDataSize(groupId);
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 8;
            dataSize += 8;
            dataSize += UUIDCodec.calculateDataSize(invocationUid);
            return dataSize += 4;
        }
    }
}

