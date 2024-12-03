/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheMessageType;
import com.hazelcast.client.impl.protocol.codec.UUIDCodec;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class CacheAddInvalidationListenerCodec {
    public static final CacheMessageType REQUEST_TYPE = CacheMessageType.CACHE_ADDINVALIDATIONLISTENER;
    public static final int RESPONSE_TYPE = 104;

    public static ClientMessage encodeRequest(String name, boolean localOnly) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, localOnly);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Cache.addInvalidationListener");
        clientMessage.set(name);
        clientMessage.set(localOnly);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
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

    public static ClientMessage encodeCacheInvalidationEvent(String name, Data key, String sourceUuid, UUID partitionUuid, long sequence) {
        int dataSize = ClientMessage.HEADER_SIZE;
        dataSize += ParameterUtil.calculateDataSize(name);
        ++dataSize;
        if (key != null) {
            dataSize += ParameterUtil.calculateDataSize(key);
        }
        ++dataSize;
        if (sourceUuid != null) {
            dataSize += ParameterUtil.calculateDataSize(sourceUuid);
        }
        dataSize += UUIDCodec.calculateDataSize(partitionUuid);
        ClientMessage clientMessage = ClientMessage.createForEncode(dataSize += 8);
        clientMessage.setMessageType(208);
        clientMessage.addFlag((short)1);
        clientMessage.set(name);
        if (key == null) {
            boolean key_isNull = true;
            clientMessage.set(key_isNull);
        } else {
            boolean key_isNull = false;
            clientMessage.set(key_isNull);
            clientMessage.set(key);
        }
        if (sourceUuid == null) {
            boolean sourceUuid_isNull = true;
            clientMessage.set(sourceUuid_isNull);
        } else {
            boolean sourceUuid_isNull = false;
            clientMessage.set(sourceUuid_isNull);
            clientMessage.set(sourceUuid);
        }
        UUIDCodec.encode(partitionUuid, clientMessage);
        clientMessage.set(sequence);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeCacheBatchInvalidationEvent(String name, Collection<Data> keys, Collection<String> sourceUuids, Collection<UUID> partitionUuids, Collection<Long> sequences) {
        int dataSize = ClientMessage.HEADER_SIZE;
        dataSize += ParameterUtil.calculateDataSize(name);
        dataSize += 4;
        for (Data data : keys) {
            dataSize += ParameterUtil.calculateDataSize(data);
        }
        ++dataSize;
        if (sourceUuids != null) {
            dataSize += 4;
            for (String string : sourceUuids) {
                dataSize += ParameterUtil.calculateDataSize(string);
            }
        }
        dataSize += 4;
        for (UUID uUID : partitionUuids) {
            dataSize += UUIDCodec.calculateDataSize(uUID);
        }
        dataSize += 4;
        for (Long l : sequences) {
            dataSize += ParameterUtil.calculateDataSize(l);
        }
        ClientMessage clientMessage = ClientMessage.createForEncode(dataSize);
        clientMessage.setMessageType(211);
        clientMessage.addFlag((short)1);
        clientMessage.set(name);
        clientMessage.set(keys.size());
        for (Data keys_item : keys) {
            clientMessage.set(keys_item);
        }
        if (sourceUuids == null) {
            boolean bl = true;
            clientMessage.set(bl);
        } else {
            boolean bl = false;
            clientMessage.set(bl);
            clientMessage.set(sourceUuids.size());
            for (String sourceUuids_item : sourceUuids) {
                clientMessage.set(sourceUuids_item);
            }
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
            if (messageType == 208) {
                String name = null;
                name = clientMessage.getStringUtf8();
                Data key = null;
                boolean key_isNull = clientMessage.getBoolean();
                if (!key_isNull) {
                    key = clientMessage.getData();
                }
                String sourceUuid = null;
                boolean sourceUuid_isNull = clientMessage.getBoolean();
                if (!sourceUuid_isNull) {
                    sourceUuid = clientMessage.getStringUtf8();
                }
                if (clientMessage.isComplete()) {
                    this.handleCacheInvalidationEventV10(name, key, sourceUuid);
                    return;
                }
                UUID partitionUuid = null;
                partitionUuid = UUIDCodec.decode(clientMessage);
                long sequence = 0L;
                sequence = clientMessage.getLong();
                this.handleCacheInvalidationEventV14(name, key, sourceUuid, partitionUuid, sequence);
                return;
            }
            if (messageType == 211) {
                String name = null;
                name = clientMessage.getStringUtf8();
                ArrayList<Data> keys = null;
                int keys_size = clientMessage.getInt();
                keys = new ArrayList<Data>(keys_size);
                for (int keys_index = 0; keys_index < keys_size; ++keys_index) {
                    Data keys_item = clientMessage.getData();
                    keys.add(keys_item);
                }
                ArrayList<String> sourceUuids = null;
                boolean sourceUuids_isNull = clientMessage.getBoolean();
                if (!sourceUuids_isNull) {
                    int sourceUuids_size = clientMessage.getInt();
                    sourceUuids = new ArrayList<String>(sourceUuids_size);
                    for (int sourceUuids_index = 0; sourceUuids_index < sourceUuids_size; ++sourceUuids_index) {
                        String sourceUuids_item = clientMessage.getStringUtf8();
                        sourceUuids.add(sourceUuids_item);
                    }
                }
                if (clientMessage.isComplete()) {
                    this.handleCacheBatchInvalidationEventV10(name, keys, sourceUuids);
                    return;
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
                this.handleCacheBatchInvalidationEventV14(name, keys, sourceUuids, partitionUuids, sequences);
                return;
            }
            Logger.getLogger(super.getClass()).warning("Unknown message type received on event handler :" + messageType);
        }

        public abstract void handleCacheInvalidationEventV10(String var1, Data var2, String var3);

        public abstract void handleCacheInvalidationEventV14(String var1, Data var2, String var3, UUID var4, long var5);

        public abstract void handleCacheBatchInvalidationEventV10(String var1, Collection<Data> var2, Collection<String> var3);

        public abstract void handleCacheBatchInvalidationEventV14(String var1, Collection<Data> var2, Collection<String> var3, Collection<UUID> var4, Collection<Long> var5);
    }

    public static class ResponseParameters {
        public String response;

        public static int calculateDataSize(String response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += ParameterUtil.calculateDataSize(response);
        }
    }

    public static class RequestParameters {
        public static final CacheMessageType TYPE = REQUEST_TYPE;
        public String name;
        public boolean localOnly;

        public static int calculateDataSize(String name, boolean localOnly) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            return ++dataSize;
        }
    }
}

