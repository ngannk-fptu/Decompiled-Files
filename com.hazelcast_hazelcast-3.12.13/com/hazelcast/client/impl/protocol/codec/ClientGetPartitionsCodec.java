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
import com.hazelcast.nio.Address;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ClientGetPartitionsCodec {
    public static final ClientMessageType REQUEST_TYPE = ClientMessageType.CLIENT_GETPARTITIONS;
    public static final int RESPONSE_TYPE = 108;

    public static ClientMessage encodeRequest() {
        int requiredDataSize = RequestParameters.calculateDataSize();
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Client.getPartitions");
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        return parameters;
    }

    public static ClientMessage encodeResponse(Collection<Map.Entry<Address, List<Integer>>> partitions) {
        int requiredDataSize = ResponseParameters.calculateDataSize(partitions);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(108);
        clientMessage.set(partitions.size());
        for (Map.Entry<Address, List<Integer>> partitions_item : partitions) {
            Address partitions_itemKey = partitions_item.getKey();
            List<Integer> partitions_itemVal = partitions_item.getValue();
            AddressCodec.encode(partitions_itemKey, clientMessage);
            clientMessage.set(partitions_itemVal.size());
            for (Integer partitions_itemVal_item : partitions_itemVal) {
                clientMessage.set(partitions_itemVal_item);
            }
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeResponse(Collection<Map.Entry<Address, List<Integer>>> partitions, int partitionStateVersion) {
        int requiredDataSize = ResponseParameters.calculateDataSize(partitions, partitionStateVersion);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(108);
        clientMessage.set(partitions.size());
        for (Map.Entry<Address, List<Integer>> partitions_item : partitions) {
            Address partitions_itemKey = partitions_item.getKey();
            List<Integer> partitions_itemVal = partitions_item.getValue();
            AddressCodec.encode(partitions_itemKey, clientMessage);
            clientMessage.set(partitions_itemVal.size());
            for (Integer partitions_itemVal_item : partitions_itemVal) {
                clientMessage.set(partitions_itemVal_item);
            }
        }
        clientMessage.set(partitionStateVersion);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        ArrayList<Map.Entry<Address, List<Integer>>> partitions = null;
        int partitions_size = clientMessage.getInt();
        partitions = new ArrayList<Map.Entry<Address, List<Integer>>>(partitions_size);
        for (int partitions_index = 0; partitions_index < partitions_size; ++partitions_index) {
            Address partitions_item_key = AddressCodec.decode(clientMessage);
            int partitions_item_val_size = clientMessage.getInt();
            ArrayList<Integer> partitions_item_val = new ArrayList<Integer>(partitions_item_val_size);
            for (int partitions_item_val_index = 0; partitions_item_val_index < partitions_item_val_size; ++partitions_item_val_index) {
                Integer partitions_item_val_item = clientMessage.getInt();
                partitions_item_val.add(partitions_item_val_item);
            }
            AbstractMap.SimpleEntry partitions_item = new AbstractMap.SimpleEntry(partitions_item_key, partitions_item_val);
            partitions.add(partitions_item);
        }
        parameters.partitions = partitions;
        if (clientMessage.isComplete()) {
            return parameters;
        }
        int partitionStateVersion = 0;
        parameters.partitionStateVersion = partitionStateVersion = clientMessage.getInt();
        parameters.partitionStateVersionExist = true;
        return parameters;
    }

    public static class ResponseParameters {
        public List<Map.Entry<Address, List<Integer>>> partitions;
        public boolean partitionStateVersionExist = false;
        public int partitionStateVersion;

        public static int calculateDataSize(Collection<Map.Entry<Address, List<Integer>>> partitions) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            for (Map.Entry<Address, List<Integer>> partitions_item : partitions) {
                Address partitions_itemKey = partitions_item.getKey();
                List<Integer> partitions_itemVal = partitions_item.getValue();
                dataSize += AddressCodec.calculateDataSize(partitions_itemKey);
                dataSize += 4;
                for (Integer partitions_itemVal_item : partitions_itemVal) {
                    dataSize += ParameterUtil.calculateDataSize(partitions_itemVal_item);
                }
            }
            return dataSize;
        }

        public static int calculateDataSize(Collection<Map.Entry<Address, List<Integer>>> partitions, int partitionStateVersion) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            for (Map.Entry<Address, List<Integer>> partitions_item : partitions) {
                Address partitions_itemKey = partitions_item.getKey();
                List<Integer> partitions_itemVal = partitions_item.getValue();
                dataSize += AddressCodec.calculateDataSize(partitions_itemKey);
                dataSize += 4;
                for (Integer partitions_itemVal_item : partitions_itemVal) {
                    dataSize += ParameterUtil.calculateDataSize(partitions_itemVal_item);
                }
            }
            return dataSize += 4;
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

