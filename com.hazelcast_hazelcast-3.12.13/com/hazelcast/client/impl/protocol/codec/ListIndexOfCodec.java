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
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ListIndexOfCodec {
    public static final ListMessageType REQUEST_TYPE = ListMessageType.LIST_INDEXOF;
    public static final int RESPONSE_TYPE = 102;

    public static ClientMessage encodeRequest(String name, Data value) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, value);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("List.indexOf");
        clientMessage.set(name);
        clientMessage.set(value);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        Data value = null;
        parameters.value = value = clientMessage.getData();
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
        public static final ListMessageType TYPE = REQUEST_TYPE;
        public String name;
        public Data value;

        public static int calculateDataSize(String name, Data value) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            return dataSize += ParameterUtil.calculateDataSize(value);
        }
    }
}

