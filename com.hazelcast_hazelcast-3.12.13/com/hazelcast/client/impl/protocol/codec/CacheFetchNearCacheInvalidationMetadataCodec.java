/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AddressCodec;
import com.hazelcast.client.impl.protocol.codec.CacheMessageType;
import com.hazelcast.client.impl.protocol.codec.UUIDCodec;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.Address;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class CacheFetchNearCacheInvalidationMetadataCodec {
    public static final CacheMessageType REQUEST_TYPE = CacheMessageType.CACHE_FETCHNEARCACHEINVALIDATIONMETADATA;
    public static final int RESPONSE_TYPE = 122;

    public static ClientMessage encodeRequest(Collection<String> names, Address address) {
        int requiredDataSize = RequestParameters.calculateDataSize(names, address);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Cache.fetchNearCacheInvalidationMetadata");
        clientMessage.set(names.size());
        for (String names_item : names) {
            clientMessage.set(names_item);
        }
        AddressCodec.encode(address, clientMessage);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        ArrayList<String> names = null;
        int names_size = clientMessage.getInt();
        names = new ArrayList<String>(names_size);
        for (int names_index = 0; names_index < names_size; ++names_index) {
            String names_item = clientMessage.getStringUtf8();
            names.add(names_item);
        }
        parameters.names = names;
        Address address = null;
        parameters.address = address = AddressCodec.decode(clientMessage);
        return parameters;
    }

    public static ClientMessage encodeResponse(Collection<Map.Entry<String, List<Map.Entry<Integer, Long>>>> namePartitionSequenceList, Collection<Map.Entry<Integer, UUID>> partitionUuidList) {
        int requiredDataSize = ResponseParameters.calculateDataSize(namePartitionSequenceList, partitionUuidList);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(122);
        clientMessage.set(namePartitionSequenceList.size());
        for (Map.Entry<String, List<Map.Entry<Integer, Long>>> entry : namePartitionSequenceList) {
            String namePartitionSequenceList_itemKey = entry.getKey();
            List<Map.Entry<Integer, Long>> namePartitionSequenceList_itemVal = entry.getValue();
            clientMessage.set(namePartitionSequenceList_itemKey);
            clientMessage.set(namePartitionSequenceList_itemVal.size());
            for (Map.Entry<Integer, Long> namePartitionSequenceList_itemVal_item : namePartitionSequenceList_itemVal) {
                Integer namePartitionSequenceList_itemVal_itemKey = namePartitionSequenceList_itemVal_item.getKey();
                Long namePartitionSequenceList_itemVal_itemVal = namePartitionSequenceList_itemVal_item.getValue();
                clientMessage.set(namePartitionSequenceList_itemVal_itemKey);
                clientMessage.set(namePartitionSequenceList_itemVal_itemVal);
            }
        }
        clientMessage.set(partitionUuidList.size());
        for (Map.Entry<Object, Object> entry : partitionUuidList) {
            Integer partitionUuidList_itemKey = (Integer)entry.getKey();
            UUID partitionUuidList_itemVal = (UUID)entry.getValue();
            clientMessage.set(partitionUuidList_itemKey);
            UUIDCodec.encode(partitionUuidList_itemVal, clientMessage);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        ArrayList<Map.Entry<String, List<Map.Entry<Integer, Long>>>> namePartitionSequenceList = null;
        int namePartitionSequenceList_size = clientMessage.getInt();
        namePartitionSequenceList = new ArrayList<Map.Entry<String, List<Map.Entry<Integer, Long>>>>(namePartitionSequenceList_size);
        for (int namePartitionSequenceList_index = 0; namePartitionSequenceList_index < namePartitionSequenceList_size; ++namePartitionSequenceList_index) {
            String namePartitionSequenceList_item_key = clientMessage.getStringUtf8();
            int namePartitionSequenceList_item_val_size = clientMessage.getInt();
            ArrayList<AbstractMap.SimpleEntry<Integer, Long>> namePartitionSequenceList_item_val = new ArrayList<AbstractMap.SimpleEntry<Integer, Long>>(namePartitionSequenceList_item_val_size);
            for (int namePartitionSequenceList_item_val_index = 0; namePartitionSequenceList_item_val_index < namePartitionSequenceList_item_val_size; ++namePartitionSequenceList_item_val_index) {
                Integer namePartitionSequenceList_item_val_item_key = clientMessage.getInt();
                Long namePartitionSequenceList_item_val_item_val = clientMessage.getLong();
                AbstractMap.SimpleEntry<Integer, Long> namePartitionSequenceList_item_val_item = new AbstractMap.SimpleEntry<Integer, Long>(namePartitionSequenceList_item_val_item_key, namePartitionSequenceList_item_val_item_val);
                namePartitionSequenceList_item_val.add(namePartitionSequenceList_item_val_item);
            }
            AbstractMap.SimpleEntry namePartitionSequenceList_item = new AbstractMap.SimpleEntry(namePartitionSequenceList_item_key, namePartitionSequenceList_item_val);
            namePartitionSequenceList.add(namePartitionSequenceList_item);
        }
        parameters.namePartitionSequenceList = namePartitionSequenceList;
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
        public List<Map.Entry<String, List<Map.Entry<Integer, Long>>>> namePartitionSequenceList;
        public List<Map.Entry<Integer, UUID>> partitionUuidList;

        public static int calculateDataSize(Collection<Map.Entry<String, List<Map.Entry<Integer, Long>>>> namePartitionSequenceList, Collection<Map.Entry<Integer, UUID>> partitionUuidList) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            for (Map.Entry<String, List<Map.Entry<Integer, Long>>> entry : namePartitionSequenceList) {
                String namePartitionSequenceList_itemKey = entry.getKey();
                List<Map.Entry<Integer, Long>> namePartitionSequenceList_itemVal = entry.getValue();
                dataSize += ParameterUtil.calculateDataSize(namePartitionSequenceList_itemKey);
                dataSize += 4;
                for (Map.Entry<Integer, Long> namePartitionSequenceList_itemVal_item : namePartitionSequenceList_itemVal) {
                    Integer namePartitionSequenceList_itemVal_itemKey = namePartitionSequenceList_itemVal_item.getKey();
                    Long namePartitionSequenceList_itemVal_itemVal = namePartitionSequenceList_itemVal_item.getValue();
                    dataSize += ParameterUtil.calculateDataSize(namePartitionSequenceList_itemVal_itemKey);
                    dataSize += ParameterUtil.calculateDataSize(namePartitionSequenceList_itemVal_itemVal);
                }
            }
            dataSize += 4;
            for (Map.Entry<Object, Object> entry : partitionUuidList) {
                Integer partitionUuidList_itemKey = (Integer)entry.getKey();
                UUID partitionUuidList_itemVal = (UUID)entry.getValue();
                dataSize += ParameterUtil.calculateDataSize(partitionUuidList_itemKey);
                dataSize += UUIDCodec.calculateDataSize(partitionUuidList_itemVal);
            }
            return dataSize;
        }
    }

    public static class RequestParameters {
        public static final CacheMessageType TYPE = REQUEST_TYPE;
        public List<String> names;
        public Address address;

        public static int calculateDataSize(Collection<String> names, Address address) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            for (String names_item : names) {
                dataSize += ParameterUtil.calculateDataSize(names_item);
            }
            return dataSize += AddressCodec.calculateDataSize(address);
        }
    }
}

