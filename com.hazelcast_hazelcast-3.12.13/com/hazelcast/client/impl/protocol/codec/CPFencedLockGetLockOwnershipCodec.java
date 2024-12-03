/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPFencedLockMessageType;
import com.hazelcast.client.impl.protocol.codec.RaftGroupIdCodec;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.cp.internal.RaftGroupId;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class CPFencedLockGetLockOwnershipCodec {
    public static final CPFencedLockMessageType REQUEST_TYPE = CPFencedLockMessageType.CPFENCEDLOCK_GETLOCKOWNERSHIP;
    public static final int RESPONSE_TYPE = 129;

    public static ClientMessage encodeRequest(RaftGroupId groupId, String name) {
        int requiredDataSize = RequestParameters.calculateDataSize(groupId, name);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("CPFencedLock.getLockOwnership");
        RaftGroupIdCodec.encode(groupId, clientMessage);
        clientMessage.set(name);
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
        return parameters;
    }

    public static ClientMessage encodeResponse(long fence, int lockCount, long sessionId, long threadId) {
        int requiredDataSize = ResponseParameters.calculateDataSize(fence, lockCount, sessionId, threadId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(129);
        clientMessage.set(fence);
        clientMessage.set(lockCount);
        clientMessage.set(sessionId);
        clientMessage.set(threadId);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        long fence = 0L;
        parameters.fence = fence = clientMessage.getLong();
        int lockCount = 0;
        parameters.lockCount = lockCount = clientMessage.getInt();
        long sessionId = 0L;
        parameters.sessionId = sessionId = clientMessage.getLong();
        long threadId = 0L;
        parameters.threadId = threadId = clientMessage.getLong();
        return parameters;
    }

    public static class ResponseParameters {
        public long fence;
        public int lockCount;
        public long sessionId;
        public long threadId;

        public static int calculateDataSize(long fence, int lockCount, long sessionId, long threadId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 8;
            dataSize += 4;
            dataSize += 8;
            return dataSize += 8;
        }
    }

    public static class RequestParameters {
        public static final CPFencedLockMessageType TYPE = REQUEST_TYPE;
        public RaftGroupId groupId;
        public String name;

        public static int calculateDataSize(RaftGroupId groupId, String name) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += RaftGroupIdCodec.calculateDataSize(groupId);
            return dataSize += ParameterUtil.calculateDataSize(name);
        }
    }
}

