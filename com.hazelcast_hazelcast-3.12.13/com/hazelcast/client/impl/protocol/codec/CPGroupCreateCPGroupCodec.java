/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPGroupMessageType;
import com.hazelcast.client.impl.protocol.codec.RaftGroupIdCodec;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.cp.internal.RaftGroupId;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class CPGroupCreateCPGroupCodec {
    public static final CPGroupMessageType REQUEST_TYPE = CPGroupMessageType.CPGROUP_CREATECPGROUP;
    public static final int RESPONSE_TYPE = 128;

    public static ClientMessage encodeRequest(String proxyName) {
        int requiredDataSize = RequestParameters.calculateDataSize(proxyName);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("CPGroup.createCPGroup");
        clientMessage.set(proxyName);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        String proxyName = null;
        parameters.proxyName = proxyName = clientMessage.getStringUtf8();
        return parameters;
    }

    public static ClientMessage encodeResponse(RaftGroupId groupId) {
        int requiredDataSize = ResponseParameters.calculateDataSize(groupId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(128);
        RaftGroupIdCodec.encode(groupId, clientMessage);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        RaftGroupId groupId = null;
        parameters.groupId = groupId = RaftGroupIdCodec.decode(clientMessage);
        return parameters;
    }

    public static class ResponseParameters {
        public RaftGroupId groupId;

        public static int calculateDataSize(RaftGroupId groupId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += RaftGroupIdCodec.calculateDataSize(groupId);
        }
    }

    public static class RequestParameters {
        public static final CPGroupMessageType TYPE = REQUEST_TYPE;
        public String proxyName;

        public static int calculateDataSize(String proxyName) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += ParameterUtil.calculateDataSize(proxyName);
        }
    }
}

