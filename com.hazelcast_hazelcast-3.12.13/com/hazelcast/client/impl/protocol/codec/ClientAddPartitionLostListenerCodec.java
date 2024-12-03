/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AddressCodec;
import com.hazelcast.client.impl.protocol.codec.ClientMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.Address;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ClientAddPartitionLostListenerCodec {
    public static final ClientMessageType REQUEST_TYPE = ClientMessageType.CLIENT_ADDPARTITIONLOSTLISTENER;
    public static final int RESPONSE_TYPE = 104;

    public static ClientMessage encodeRequest(boolean localOnly) {
        int requiredDataSize = RequestParameters.calculateDataSize(localOnly);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Client.addPartitionLostListener");
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

    public static ClientMessage encodePartitionLostEvent(int partitionId, int lostBackupCount, Address source) {
        int dataSize = ClientMessage.HEADER_SIZE;
        dataSize += 4;
        dataSize += 4;
        ++dataSize;
        if (source != null) {
            dataSize += AddressCodec.calculateDataSize(source);
        }
        ClientMessage clientMessage = ClientMessage.createForEncode(dataSize);
        clientMessage.setMessageType(206);
        clientMessage.addFlag((short)1);
        clientMessage.set(partitionId);
        clientMessage.set(lostBackupCount);
        if (source == null) {
            boolean source_isNull = true;
            clientMessage.set(source_isNull);
        } else {
            boolean source_isNull = false;
            clientMessage.set(source_isNull);
            AddressCodec.encode(source, clientMessage);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static abstract class AbstractEventHandler {
        public void handle(ClientMessage clientMessage) {
            int messageType = clientMessage.getMessageType();
            if (messageType == 206) {
                int partitionId = 0;
                partitionId = clientMessage.getInt();
                int lostBackupCount = 0;
                lostBackupCount = clientMessage.getInt();
                Address source = null;
                boolean source_isNull = clientMessage.getBoolean();
                if (!source_isNull) {
                    source = AddressCodec.decode(clientMessage);
                }
                this.handlePartitionLostEventV10(partitionId, lostBackupCount, source);
                return;
            }
            Logger.getLogger(super.getClass()).warning("Unknown message type received on event handler :" + messageType);
        }

        public abstract void handlePartitionLostEventV10(int var1, int var2, Address var3);
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

