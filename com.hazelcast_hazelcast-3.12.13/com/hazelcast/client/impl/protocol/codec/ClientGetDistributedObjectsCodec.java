/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.client.DistributedObjectInfo;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientMessageType;
import com.hazelcast.client.impl.protocol.codec.DistributedObjectInfoCodec;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ClientGetDistributedObjectsCodec {
    public static final ClientMessageType REQUEST_TYPE = ClientMessageType.CLIENT_GETDISTRIBUTEDOBJECTS;
    public static final int RESPONSE_TYPE = 110;

    public static ClientMessage encodeRequest() {
        int requiredDataSize = RequestParameters.calculateDataSize();
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Client.getDistributedObjects");
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        return parameters;
    }

    public static ClientMessage encodeResponse(Collection<DistributedObjectInfo> response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(110);
        clientMessage.set(response.size());
        for (DistributedObjectInfo response_item : response) {
            DistributedObjectInfoCodec.encode(response_item, clientMessage);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        ArrayList<DistributedObjectInfo> response = null;
        int response_size = clientMessage.getInt();
        response = new ArrayList<DistributedObjectInfo>(response_size);
        for (int response_index = 0; response_index < response_size; ++response_index) {
            DistributedObjectInfo response_item = DistributedObjectInfoCodec.decode(clientMessage);
            response.add(response_item);
        }
        parameters.response = response;
        return parameters;
    }

    public static class ResponseParameters {
        public List<DistributedObjectInfo> response;

        public static int calculateDataSize(Collection<DistributedObjectInfo> response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            for (DistributedObjectInfo response_item : response) {
                dataSize += DistributedObjectInfoCodec.calculateDataSize(response_item);
            }
            return dataSize;
        }
    }

    public static class RequestParameters {
        public static final ClientMessageType TYPE = REQUEST_TYPE;

        public static int calculateDataSize() {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize;
        }
    }
}

