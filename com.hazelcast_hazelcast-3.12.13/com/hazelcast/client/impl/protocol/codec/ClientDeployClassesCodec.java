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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ClientDeployClassesCodec {
    public static final ClientMessageType REQUEST_TYPE = ClientMessageType.CLIENT_DEPLOYCLASSES;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(Collection<Map.Entry<String, byte[]>> classDefinitions) {
        int requiredDataSize = RequestParameters.calculateDataSize(classDefinitions);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Client.deployClasses");
        clientMessage.set(classDefinitions.size());
        for (Map.Entry<String, byte[]> classDefinitions_item : classDefinitions) {
            String classDefinitions_itemKey = classDefinitions_item.getKey();
            byte[] classDefinitions_itemVal = classDefinitions_item.getValue();
            clientMessage.set(classDefinitions_itemKey);
            clientMessage.set(classDefinitions_itemVal.length);
            for (byte classDefinitions_itemVal_item : classDefinitions_itemVal) {
                clientMessage.set(classDefinitions_itemVal_item);
            }
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        ArrayList<Map.Entry<String, byte[]>> classDefinitions = null;
        int classDefinitions_size = clientMessage.getInt();
        classDefinitions = new ArrayList<Map.Entry<String, byte[]>>(classDefinitions_size);
        for (int classDefinitions_index = 0; classDefinitions_index < classDefinitions_size; ++classDefinitions_index) {
            String classDefinitions_item_key = clientMessage.getStringUtf8();
            int classDefinitions_item_val_size = clientMessage.getInt();
            byte[] classDefinitions_item_val = new byte[classDefinitions_item_val_size];
            for (int classDefinitions_item_val_index = 0; classDefinitions_item_val_index < classDefinitions_item_val_size; ++classDefinitions_item_val_index) {
                byte classDefinitions_item_val_item;
                classDefinitions_item_val[classDefinitions_item_val_index] = classDefinitions_item_val_item = clientMessage.getByte();
            }
            AbstractMap.SimpleEntry<String, byte[]> classDefinitions_item = new AbstractMap.SimpleEntry<String, byte[]>(classDefinitions_item_key, classDefinitions_item_val);
            classDefinitions.add(classDefinitions_item);
        }
        parameters.classDefinitions = classDefinitions;
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
        public static final ClientMessageType TYPE = REQUEST_TYPE;
        public List<Map.Entry<String, byte[]>> classDefinitions;

        public static int calculateDataSize(Collection<Map.Entry<String, byte[]>> classDefinitions) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            for (Map.Entry<String, byte[]> classDefinitions_item : classDefinitions) {
                String classDefinitions_itemKey = classDefinitions_item.getKey();
                byte[] classDefinitions_itemVal = classDefinitions_item.getValue();
                dataSize += ParameterUtil.calculateDataSize(classDefinitions_itemKey);
                dataSize += 4;
                for (byte classDefinitions_itemVal_item : classDefinitions_itemVal) {
                    ++dataSize;
                }
            }
            return dataSize;
        }
    }
}

