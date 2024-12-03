/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AddressCodec;
import com.hazelcast.client.impl.protocol.codec.PNCounterMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.Address;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class PNCounterGetCodec {
    public static final PNCounterMessageType REQUEST_TYPE = PNCounterMessageType.PNCOUNTER_GET;
    public static final int RESPONSE_TYPE = 127;

    public static ClientMessage encodeRequest(String name, Collection<Map.Entry<String, Long>> replicaTimestamps, Address targetReplica) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, replicaTimestamps, targetReplica);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("PNCounter.get");
        clientMessage.set(name);
        clientMessage.set(replicaTimestamps.size());
        for (Map.Entry<String, Long> replicaTimestamps_item : replicaTimestamps) {
            String replicaTimestamps_itemKey = replicaTimestamps_item.getKey();
            Long replicaTimestamps_itemVal = replicaTimestamps_item.getValue();
            clientMessage.set(replicaTimestamps_itemKey);
            clientMessage.set(replicaTimestamps_itemVal);
        }
        AddressCodec.encode(targetReplica, clientMessage);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        ArrayList<Map.Entry<String, Long>> replicaTimestamps = null;
        int replicaTimestamps_size = clientMessage.getInt();
        replicaTimestamps = new ArrayList<Map.Entry<String, Long>>(replicaTimestamps_size);
        for (int replicaTimestamps_index = 0; replicaTimestamps_index < replicaTimestamps_size; ++replicaTimestamps_index) {
            String replicaTimestamps_item_key = clientMessage.getStringUtf8();
            Long replicaTimestamps_item_val = clientMessage.getLong();
            AbstractMap.SimpleEntry<String, Long> replicaTimestamps_item = new AbstractMap.SimpleEntry<String, Long>(replicaTimestamps_item_key, replicaTimestamps_item_val);
            replicaTimestamps.add(replicaTimestamps_item);
        }
        parameters.replicaTimestamps = replicaTimestamps;
        Address targetReplica = null;
        parameters.targetReplica = targetReplica = AddressCodec.decode(clientMessage);
        return parameters;
    }

    public static ClientMessage encodeResponse(long value, Collection<Map.Entry<String, Long>> replicaTimestamps, int replicaCount) {
        int requiredDataSize = ResponseParameters.calculateDataSize(value, replicaTimestamps, replicaCount);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(127);
        clientMessage.set(value);
        clientMessage.set(replicaTimestamps.size());
        for (Map.Entry<String, Long> replicaTimestamps_item : replicaTimestamps) {
            String replicaTimestamps_itemKey = replicaTimestamps_item.getKey();
            Long replicaTimestamps_itemVal = replicaTimestamps_item.getValue();
            clientMessage.set(replicaTimestamps_itemKey);
            clientMessage.set(replicaTimestamps_itemVal);
        }
        clientMessage.set(replicaCount);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        long value = 0L;
        parameters.value = value = clientMessage.getLong();
        ArrayList<Map.Entry<String, Long>> replicaTimestamps = null;
        int replicaTimestamps_size = clientMessage.getInt();
        replicaTimestamps = new ArrayList<Map.Entry<String, Long>>(replicaTimestamps_size);
        for (int replicaTimestamps_index = 0; replicaTimestamps_index < replicaTimestamps_size; ++replicaTimestamps_index) {
            String replicaTimestamps_item_key = clientMessage.getStringUtf8();
            Long replicaTimestamps_item_val = clientMessage.getLong();
            AbstractMap.SimpleEntry<String, Long> replicaTimestamps_item = new AbstractMap.SimpleEntry<String, Long>(replicaTimestamps_item_key, replicaTimestamps_item_val);
            replicaTimestamps.add(replicaTimestamps_item);
        }
        parameters.replicaTimestamps = replicaTimestamps;
        int replicaCount = 0;
        parameters.replicaCount = replicaCount = clientMessage.getInt();
        return parameters;
    }

    public static class ResponseParameters {
        public long value;
        public List<Map.Entry<String, Long>> replicaTimestamps;
        public int replicaCount;

        public static int calculateDataSize(long value, Collection<Map.Entry<String, Long>> replicaTimestamps, int replicaCount) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 8;
            dataSize += 4;
            for (Map.Entry<String, Long> replicaTimestamps_item : replicaTimestamps) {
                String replicaTimestamps_itemKey = replicaTimestamps_item.getKey();
                Long replicaTimestamps_itemVal = replicaTimestamps_item.getValue();
                dataSize += ParameterUtil.calculateDataSize(replicaTimestamps_itemKey);
                dataSize += ParameterUtil.calculateDataSize(replicaTimestamps_itemVal);
            }
            return dataSize += 4;
        }
    }

    public static class RequestParameters {
        public static final PNCounterMessageType TYPE = REQUEST_TYPE;
        public String name;
        public List<Map.Entry<String, Long>> replicaTimestamps;
        public Address targetReplica;

        public static int calculateDataSize(String name, Collection<Map.Entry<String, Long>> replicaTimestamps, Address targetReplica) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            for (Map.Entry<String, Long> replicaTimestamps_item : replicaTimestamps) {
                String replicaTimestamps_itemKey = replicaTimestamps_item.getKey();
                Long replicaTimestamps_itemVal = replicaTimestamps_item.getValue();
                dataSize += ParameterUtil.calculateDataSize(replicaTimestamps_itemKey);
                dataSize += ParameterUtil.calculateDataSize(replicaTimestamps_itemVal);
            }
            return dataSize += AddressCodec.calculateDataSize(targetReplica);
        }
    }
}

