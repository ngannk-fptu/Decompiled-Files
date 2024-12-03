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
public final class CPSemaphoreDrainCodec {
    public static final CPSemaphoreMessageType REQUEST_TYPE = CPSemaphoreMessageType.CPSEMAPHORE_DRAIN;
    public static final int RESPONSE_TYPE = 102;

    public static ClientMessage encodeRequest(RaftGroupId groupId, String name, long sessionId, long threadId, UUID invocationUid) {
        int requiredDataSize = RequestParameters.calculateDataSize(groupId, name, sessionId, threadId, invocationUid);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("CPSemaphore.drain");
        RaftGroupIdCodec.encode(groupId, clientMessage);
        clientMessage.set(name);
        clientMessage.set(sessionId);
        clientMessage.set(threadId);
        UUIDCodec.encode(invocationUid, clientMessage);
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
        return parameters;
    }

    public static ClientMessage encodeResponse(int response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(102);
        clientMessage.set(response);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        int response = 0;
        parameters.response = response = clientMessage.getInt();
        return parameters;
    }

    public static class ResponseParameters {
        public int response;

        public static int calculateDataSize(int response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += 4;
        }
    }

    public static class RequestParameters {
        public static final CPSemaphoreMessageType TYPE = REQUEST_TYPE;
        public RaftGroupId groupId;
        public String name;
        public long sessionId;
        public long threadId;
        public UUID invocationUid;

        public static int calculateDataSize(RaftGroupId groupId, String name, long sessionId, long threadId, UUID invocationUid) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += RaftGroupIdCodec.calculateDataSize(groupId);
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 8;
            dataSize += 8;
            return dataSize += UUIDCodec.calculateDataSize(invocationUid);
        }
    }
}

