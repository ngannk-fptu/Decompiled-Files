/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapMessageType;
import com.hazelcast.client.impl.protocol.codec.UUIDCodec;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class MapAssignAndGetUuidsCodec {
    public static final MapMessageType REQUEST_TYPE = MapMessageType.MAP_ASSIGNANDGETUUIDS;
    public static final int RESPONSE_TYPE = 123;

    public static ClientMessage encodeRequest() {
        int requiredDataSize = RequestParameters.calculateDataSize();
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Map.assignAndGetUuids");
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        return parameters;
    }

    public static ClientMessage encodeResponse(Collection<Map.Entry<Integer, UUID>> partitionUuidList) {
        int requiredDataSize = ResponseParameters.calculateDataSize(partitionUuidList);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(123);
        clientMessage.set(partitionUuidList.size());
        for (Map.Entry<Integer, UUID> partitionUuidList_item : partitionUuidList) {
            Integer partitionUuidList_itemKey = partitionUuidList_item.getKey();
            UUID partitionUuidList_itemVal = partitionUuidList_item.getValue();
            clientMessage.set(partitionUuidList_itemKey);
            UUIDCodec.encode(partitionUuidList_itemVal, clientMessage);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        ArrayList<Map.Entry<Integer, UUID>> partitionUuidList = null;
        int partitionUuidList_size = clientMessage.getInt();
        partitionUuidList = new ArrayList<Map.Entry<Integer, UUID>>(partitionUuidList_size);
        for (int partitionUuidList_index = 0; partitionUuidList_index < partitionUuidList_size; ++partitionUuidList_index) {
            Integer partitionUuidList_item_key = clientMessage.getInt();
            UUID partitionUuidList_item_val = UUIDCodec.decode(clientMessage);
            AbstractMap.SimpleEntry<Integer, UUID> partitionUuidList_item = new AbstractMap.SimpleEntry<Integer, UUID>(partitionUuidList_item_key, partitionUuidList_item_val);
            partitionUuidList.add(partitionUuidList_item);
        }
        parameters.partitionUuidList = partitionUuidList;
        return parameters;
    }

    public static class ResponseParameters {
        public List<Map.Entry<Integer, UUID>> partitionUuidList;

        public static int calculateDataSize(Collection<Map.Entry<Integer, UUID>> partitionUuidList) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            for (Map.Entry<Integer, UUID> partitionUuidList_item : partitionUuidList) {
                Integer partitionUuidList_itemKey = partitionUuidList_item.getKey();
                UUID partitionUuidList_itemVal = partitionUuidList_item.getValue();
                dataSize += ParameterUtil.calculateDataSize(partitionUuidList_itemKey);
                dataSize += UUIDCodec.calculateDataSize(partitionUuidList_itemVal);
            }
            return dataSize;
        }
    }

    public static class RequestParameters {
        public static final MapMessageType TYPE = REQUEST_TYPE;

        public static int calculateDataSize() {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize;
        }
    }
}

