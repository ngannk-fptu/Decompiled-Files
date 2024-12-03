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
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class MapAddNearCacheEntryListenerCodec {
    public static final MapMessageType REQUEST_TYPE = MapMessageType.MAP_ADDNEARCACHEENTRYLISTENER;
    public static final int RESPONSE_TYPE = 104;

    public static ClientMessage encodeRequest(String name, int listenerFlags, boolean localOnly) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, listenerFlags, localOnly);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Map.addNearCacheEntryListener");
        clientMessage.set(name);
        clientMessage.set(listenerFlags);
        clientMessage.set(localOnly);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        int listenerFlags = 0;
        parameters.listenerFlags = listenerFlags = clientMessage.getInt();
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

    public static ClientMessage encodeIMapInvalidationEvent(Data key, String sourceUuid, UUID partitionUuid, long sequence) {
        int dataSize = ClientMessage.HEADER_SIZE;
        ++dataSize;
        if (key != null) {
            dataSize += ParameterUtil.calculateDataSize(key);
        }
        dataSize += ParameterUtil.calculateDataSize(sourceUuid);
        dataSize += UUIDCodec.calculateDataSize(partitionUuid);
        ClientMessage clientMessage = ClientMessage.createForEncode(dataSize += 8);
        clientMessage.setMessageType(215);
        clientMessage.addFlag((short)1);
        if (key == null) {
            boolean key_isNull = true;
            clientMessage.set(key_isNull);
        } else {
            boolean key_isNull = false;
            clientMessage.set(key_isNull);
            clientMessage.set(key);
        }
        clientMessage.set(sourceUuid);
        UUIDCodec.encode(partitionUuid, clientMessage);
        clientMessage.set(sequence);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeIMapBatchInvalidationEvent(Collection<Data> keys, Collection<String> sourceUuids, Collection<UUID> partitionUuids, Collection<Long> sequences) {
        int dataSize = ClientMessage.HEADER_SIZE;
        dataSize += 4;
        for (Data keys_item : keys) {
            dataSize += ParameterUtil.calculateDataSize(keys_item);
        }
        dataSize += 4;
        for (String sourceUuids_item : sourceUuids) {
            dataSize += ParameterUtil.calculateDataSize(sourceUuids_item);
        }
        dataSize += 4;
        for (UUID partitionUuids_item : partitionUuids) {
            dataSize += UUIDCodec.calculateDataSize(partitionUuids_item);
        }
        dataSize += 4;
        for (Long sequences_item : sequences) {
            dataSize += ParameterUtil.calculateDataSize(sequences_item);
        }
        ClientMessage clientMessage = ClientMessage.createForEncode(dataSize);
        clientMessage.setMessageType(216);
        clientMessage.addFlag((short)1);
        clientMessage.set(keys.size());
        for (Data keys_item : keys) {
            clientMessage.set(keys_item);
        }
        clientMessage.set(sourceUuids.size());
        for (String sourceUuids_item : sourceUuids) {
            clientMessage.set(sourceUuids_item);
        }
        clientMessage.set(partitionUuids.size());
        for (UUID partitionUuids_item : partitionUuids) {
            UUIDCodec.encode(partitionUuids_item, clientMessage);
        }
        clientMessage.set(sequences.size());
        for (Long sequences_item : sequences) {
            clientMessage.set(sequences_item);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static abstract class AbstractEventHandler {
        public void handle(ClientMessage clientMessage) {
            int messageType = clientMessage.getMessageType();
            if (messageType == 215) {
                Data key = null;
                boolean key_isNull = clientMessage.getBoolean();
                if (!key_isNull) {
                    key = clientMessage.getData();
                }
                if (clientMessage.isComplete()) {
                    this.handleIMapInvalidationEventV10(key);
                    return;
                }
                String sourceUuid = null;
                sourceUuid = clientMessage.getStringUtf8();
                UUID partitionUuid = null;
                partitionUuid = UUIDCodec.decode(clientMessage);
                long sequence = 0L;
                sequence = clientMessage.getLong();
                this.handleIMapInvalidationEventV14(key, sourceUuid, partitionUuid, sequence);
                return;
            }
            if (messageType == 216) {
                ArrayList<Data> keys = null;
                int keys_size = clientMessage.getInt();
                keys = new ArrayList<Data>(keys_size);
                for (int keys_index = 0; keys_index < keys_size; ++keys_index) {
                    Data keys_item = clientMessage.getData();
                    keys.add(keys_item);
                }
                if (clientMessage.isComplete()) {
                    this.handleIMapBatchInvalidationEventV10(keys);
                    return;
                }
                ArrayList<String> sourceUuids = null;
                int sourceUuids_size = clientMessage.getInt();
                sourceUuids = new ArrayList<String>(sourceUuids_size);
                for (int sourceUuids_index = 0; sourceUuids_index < sourceUuids_size; ++sourceUuids_index) {
                    String sourceUuids_item = clientMessage.getStringUtf8();
                    sourceUuids.add(sourceUuids_item);
                }
                ArrayList<UUID> partitionUuids = null;
                int partitionUuids_size = clientMessage.getInt();
                partitionUuids = new ArrayList<UUID>(partitionUuids_size);
                for (int partitionUuids_index = 0; partitionUuids_index < partitionUuids_size; ++partitionUuids_index) {
                    UUID partitionUuids_item = UUIDCodec.decode(clientMessage);
                    partitionUuids.add(partitionUuids_item);
                }
                ArrayList<Long> sequences = null;
                int sequences_size = clientMessage.getInt();
                sequences = new ArrayList<Long>(sequences_size);
                for (int sequences_index = 0; sequences_index < sequences_size; ++sequences_index) {
                    Long sequences_item = clientMessage.getLong();
                    sequences.add(sequences_item);
                }
                this.handleIMapBatchInvalidationEventV14(keys, sourceUuids, partitionUuids, sequences);
                return;
            }
            Logger.getLogger(super.getClass()).warning("Unknown message type received on event handler :" + messageType);
        }

        public abstract void handleIMapInvalidationEventV10(Data var1);

        public abstract void handleIMapInvalidationEventV14(Data var1, String var2, UUID var3, long var4);

        public abstract void handleIMapBatchInvalidationEventV10(Collection<Data> var1);

        public abstract void handleIMapBatchInvalidationEventV14(Collection<Data> var1, Collection<String> var2, Collection<UUID> var3, Collection<Long> var4);
    }

    public static class ResponseParameters {
        public String response;

        public static int calculateDataSize(String response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += ParameterUtil.calculateDataSize(response);
        }
    }

    public static class RequestParameters {
        public static final MapMessageType TYPE = REQUEST_TYPE;
        public String name;
        public int listenerFlags;
        public boolean localOnly;

        public static int calculateDataSize(String name, int listenerFlags, boolean localOnly) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            return ++dataSize;
        }
    }
}

