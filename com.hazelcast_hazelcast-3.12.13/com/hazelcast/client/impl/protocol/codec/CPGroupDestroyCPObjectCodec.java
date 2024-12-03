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
public final class CPGroupDestroyCPObjectCodec {
    public static final CPGroupMessageType REQUEST_TYPE = CPGroupMessageType.CPGROUP_DESTROYCPOBJECT;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(RaftGroupId groupId, String serviceName, String objectName) {
        int requiredDataSize = RequestParameters.calculateDataSize(groupId, serviceName, objectName);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("CPGroup.destroyCPObject");
        RaftGroupIdCodec.encode(groupId, clientMessage);
        clientMessage.set(serviceName);
        clientMessage.set(objectName);
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
        String serviceName = null;
        parameters.serviceName = serviceName = clientMessage.getStringUtf8();
        String objectName = null;
        parameters.objectName = objectName = clientMessage.getStringUtf8();
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
        public static final CPGroupMessageType TYPE = REQUEST_TYPE;
        public RaftGroupId groupId;
        public String serviceName;
        public String objectName;

        public static int calculateDataSize(RaftGroupId groupId, String serviceName, String objectName) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += RaftGroupIdCodec.calculateDataSize(groupId);
            dataSize += ParameterUtil.calculateDataSize(serviceName);
            return dataSize += ParameterUtil.calculateDataSize(objectName);
        }
    }
}

