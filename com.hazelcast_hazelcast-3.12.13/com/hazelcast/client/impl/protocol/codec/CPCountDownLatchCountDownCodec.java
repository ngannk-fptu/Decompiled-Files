/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPCountDownLatchMessageType;
import com.hazelcast.client.impl.protocol.codec.RaftGroupIdCodec;
import com.hazelcast.client.impl.protocol.codec.UUIDCodec;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.cp.internal.RaftGroupId;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class CPCountDownLatchCountDownCodec {
    public static final CPCountDownLatchMessageType REQUEST_TYPE = CPCountDownLatchMessageType.CPCOUNTDOWNLATCH_COUNTDOWN;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(RaftGroupId groupId, String name, UUID invocationUid, int expectedRound) {
        int requiredDataSize = RequestParameters.calculateDataSize(groupId, name, invocationUid, expectedRound);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("CPCountDownLatch.countDown");
        RaftGroupIdCodec.encode(groupId, clientMessage);
        clientMessage.set(name);
        UUIDCodec.encode(invocationUid, clientMessage);
        clientMessage.set(expectedRound);
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
        UUID invocationUid = null;
        parameters.invocationUid = invocationUid = UUIDCodec.decode(clientMessage);
        int expectedRound = 0;
        parameters.expectedRound = expectedRound = clientMessage.getInt();
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
        public static final CPCountDownLatchMessageType TYPE = REQUEST_TYPE;
        public RaftGroupId groupId;
        public String name;
        public UUID invocationUid;
        public int expectedRound;

        public static int calculateDataSize(RaftGroupId groupId, String name, UUID invocationUid, int expectedRound) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += RaftGroupIdCodec.calculateDataSize(groupId);
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += UUIDCodec.calculateDataSize(invocationUid);
            return dataSize += 4;
        }
    }
}

