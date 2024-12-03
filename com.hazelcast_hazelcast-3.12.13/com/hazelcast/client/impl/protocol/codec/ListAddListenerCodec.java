/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ListAddListenerCodec {
    public static final ListMessageType REQUEST_TYPE = ListMessageType.LIST_ADDLISTENER;
    public static final int RESPONSE_TYPE = 104;

    public static ClientMessage encodeRequest(String name, boolean includeValue, boolean localOnly) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, includeValue, localOnly);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("List.addListener");
        clientMessage.set(name);
        clientMessage.set(includeValue);
        clientMessage.set(localOnly);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        boolean includeValue = false;
        parameters.includeValue = includeValue = clientMessage.getBoolean();
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

    public static ClientMessage encodeItemEvent(Data item, String uuid, int eventType) {
        int dataSize = ClientMessage.HEADER_SIZE;
        ++dataSize;
        if (item != null) {
            dataSize += ParameterUtil.calculateDataSize(item);
        }
        dataSize += ParameterUtil.calculateDataSize(uuid);
        ClientMessage clientMessage = ClientMessage.createForEncode(dataSize += 4);
        clientMessage.setMessageType(204);
        clientMessage.addFlag((short)1);
        if (item == null) {
            boolean item_isNull = true;
            clientMessage.set(item_isNull);
        } else {
            boolean item_isNull = false;
            clientMessage.set(item_isNull);
            clientMessage.set(item);
        }
        clientMessage.set(uuid);
        clientMessage.set(eventType);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static abstract class AbstractEventHandler {
        public void handle(ClientMessage clientMessage) {
            int messageType = clientMessage.getMessageType();
            if (messageType == 204) {
                Data item = null;
                boolean item_isNull = clientMessage.getBoolean();
                if (!item_isNull) {
                    item = clientMessage.getData();
                }
                String uuid = null;
                uuid = clientMessage.getStringUtf8();
                int eventType = 0;
                eventType = clientMessage.getInt();
                this.handleItemEventV10(item, uuid, eventType);
                return;
            }
            Logger.getLogger(super.getClass()).warning("Unknown message type received on event handler :" + messageType);
        }

        public abstract void handleItemEventV10(Data var1, String var2, int var3);
    }

    public static class ResponseParameters {
        public String response;

        public static int calculateDataSize(String response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += ParameterUtil.calculateDataSize(response);
        }
    }

    public static class RequestParameters {
        public static final ListMessageType TYPE = REQUEST_TYPE;
        public String name;
        public boolean includeValue;
        public boolean localOnly;

        public static int calculateDataSize(String name, boolean includeValue, boolean localOnly) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            ++dataSize;
            return ++dataSize;
        }
    }
}

