/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.logging.Logger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ClientAddDistributedObjectListenerCodec {
    public static final ClientMessageType REQUEST_TYPE = ClientMessageType.CLIENT_ADDDISTRIBUTEDOBJECTLISTENER;
    public static final int RESPONSE_TYPE = 104;

    public static ClientMessage encodeRequest(boolean localOnly) {
        int requiredDataSize = RequestParameters.calculateDataSize(localOnly);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Client.addDistributedObjectListener");
        clientMessage.set(localOnly);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        boolean localOnly = false;
        parameters.localOnly = localOnly = clientMessage.getBoolean();
        return parameters;
    }

    public static ClientMessage encodeResponse(String response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(104);
        clientMessage.set(response);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        String response = null;
        parameters.response = response = clientMessage.getStringUtf8();
        return parameters;
    }

    public static ClientMessage encodeDistributedObjectEvent(String name, String serviceName, String eventType) {
        int dataSize = ClientMessage.HEADER_SIZE;
        dataSize += ParameterUtil.calculateDataSize(name);
        dataSize += ParameterUtil.calculateDataSize(serviceName);
        ClientMessage clientMessage = ClientMessage.createForEncode(dataSize += ParameterUtil.calculateDataSize(eventType));
        clientMessage.setMessageType(207);
        clientMessage.addFlag((short)1);
        clientMessage.set(name);
        clientMessage.set(serviceName);
        clientMessage.set(eventType);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static abstract class AbstractEventHandler {
        public void handle(ClientMessage clientMessage) {
            int messageType = clientMessage.getMessageType();
            if (messageType == 207) {
                String name = null;
                name = clientMessage.getStringUtf8();
                String serviceName = null;
                serviceName = clientMessage.getStringUtf8();
                String eventType = null;
                eventType = clientMessage.getStringUtf8();
                this.handleDistributedObjectEventV10(name, serviceName, eventType);
                return;
            }
            Logger.getLogger(super.getClass()).warning("Unknown message type received on event handler :" + messageType);
        }

        public abstract void handleDistributedObjectEventV10(String var1, String var2, String var3);
    }

    public static class ResponseParameters {
        public String response;

        public static int calculateDataSize(String response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += ParameterUtil.calculateDataSize(response);
        }
    }

    public static class RequestParameters {
        public static final ClientMessageType TYPE = REQUEST_TYPE;
        public boolean localOnly;

        public static int calculateDataSize(boolean localOnly) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return ++dataSize;
        }
    }
}

