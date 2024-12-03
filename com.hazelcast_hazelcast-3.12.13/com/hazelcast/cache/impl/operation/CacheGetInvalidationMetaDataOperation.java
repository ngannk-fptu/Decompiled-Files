/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.CacheEventHandler;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.internal.nearcache.impl.invalidation.MetaDataGenerator;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CacheGetInvalidationMetaDataOperation
extends Operation
implements IdentifiedDataSerializable,
ReadonlyOperation {
    private List<String> names;
    private MetaDataResponse response;

    public CacheGetInvalidationMetaDataOperation() {
    }

    public CacheGetInvalidationMetaDataOperation(List<String> names) {
        Preconditions.checkTrue(CollectionUtil.isNotEmpty(names), "names cannot be null or empty");
        this.names = names;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public void run() {
        List<Integer> ownedPartitions = this.getOwnedPartitions();
        this.response = new MetaDataResponse();
        this.response.partitionUuidList = this.getPartitionUuidList(ownedPartitions);
        this.response.namePartitionSequenceList = this.getNamePartitionSequenceList(ownedPartitions);
    }

    private List<Integer> getOwnedPartitions() {
        IPartitionService partitionService = this.getNodeEngine().getPartitionService();
        Map<Address, List<Integer>> memberPartitionsMap = partitionService.getMemberPartitionsMap();
        List<Integer> ownedPartitions = memberPartitionsMap.get(this.getNodeEngine().getThisAddress());
        return ownedPartitions == null ? Collections.emptyList() : ownedPartitions;
    }

    private Map<Integer, UUID> getPartitionUuidList(List<Integer> ownedPartitionIds) {
        MetaDataGenerator metaDataGenerator = this.getPartitionMetaDataGenerator();
        HashMap<Integer, UUID> partitionUuids = new HashMap<Integer, UUID>(ownedPartitionIds.size());
        for (Integer partitionId : ownedPartitionIds) {
            UUID uuid = metaDataGenerator.getOrCreateUuid(partitionId);
            partitionUuids.put(partitionId, uuid);
        }
        return partitionUuids;
    }

    private Map<String, List<Map.Entry<Integer, Long>>> getNamePartitionSequenceList(List<Integer> ownedPartitionIds) {
        MetaDataGenerator metaDataGenerator = this.getPartitionMetaDataGenerator();
        HashMap<String, List<Map.Entry<Integer, Long>>> sequences = new HashMap<String, List<Map.Entry<Integer, Long>>>(ownedPartitionIds.size());
        for (String name : this.names) {
            ArrayList<AbstractMap.SimpleEntry<Integer, Long>> mapSequences = new ArrayList<AbstractMap.SimpleEntry<Integer, Long>>();
            for (Integer partitionId : ownedPartitionIds) {
                long partitionSequence = metaDataGenerator.currentSequence(name, partitionId);
                if (partitionSequence == 0L) continue;
                mapSequences.add(new AbstractMap.SimpleEntry<Integer, Long>(partitionId, partitionSequence));
            }
            sequences.put(name, mapSequences);
        }
        return sequences;
    }

    private MetaDataGenerator getPartitionMetaDataGenerator() {
        CacheService cacheService = (CacheService)this.getService();
        CacheEventHandler cacheEventHandler = cacheService.getCacheEventHandler();
        return cacheEventHandler.getMetaDataGenerator();
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    public void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.names.size());
        for (String mapName : this.names) {
            out.writeUTF(mapName);
        }
    }

    @Override
    public void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        ArrayList<String> mapNames = new ArrayList<String>(size);
        for (int i = 0; i < size; ++i) {
            mapNames.add(in.readUTF());
        }
        this.names = mapNames;
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 50;
    }

    public static class MetaDataResponse
    implements IdentifiedDataSerializable {
        private Map<String, List<Map.Entry<Integer, Long>>> namePartitionSequenceList;
        private Map<Integer, UUID> partitionUuidList;

        public Map<String, List<Map.Entry<Integer, Long>>> getNamePartitionSequenceList() {
            return this.namePartitionSequenceList;
        }

        public Map<Integer, UUID> getPartitionUuidList() {
            return this.partitionUuidList;
        }

        @Override
        public int getFactoryId() {
            return CacheDataSerializerHook.F_ID;
        }

        @Override
        public int getId() {
            return 51;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeInt(this.namePartitionSequenceList.size());
            for (Map.Entry<String, List<Map.Entry<Integer, Long>>> entry : this.namePartitionSequenceList.entrySet()) {
                out.writeUTF(entry.getKey());
                out.writeInt(entry.getValue().size());
                for (Map.Entry<Integer, Long> seqEntry : entry.getValue()) {
                    out.writeInt(seqEntry.getKey());
                    out.writeLong(seqEntry.getValue());
                }
            }
            out.writeInt(this.partitionUuidList.size());
            for (Map.Entry<Object, Object> entry : this.partitionUuidList.entrySet()) {
                out.writeInt((Integer)entry.getKey());
                out.writeLong(((UUID)entry.getValue()).getMostSignificantBits());
                out.writeLong(((UUID)entry.getValue()).getLeastSignificantBits());
            }
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            int size1 = in.readInt();
            this.namePartitionSequenceList = new HashMap<String, List<Map.Entry<Integer, Long>>>(size1);
            for (int i = 0; i < size1; ++i) {
                String name = in.readUTF();
                int size2 = in.readInt();
                ArrayList<AbstractMap.SimpleEntry<Integer, Long>> innerList = new ArrayList<AbstractMap.SimpleEntry<Integer, Long>>(size2);
                for (int j = 0; j < size2; ++j) {
                    int partition = in.readInt();
                    long seq = in.readLong();
                    innerList.add(new AbstractMap.SimpleEntry<Integer, Long>(partition, seq));
                }
                this.namePartitionSequenceList.put(name, innerList);
            }
            int size3 = in.readInt();
            this.partitionUuidList = new HashMap<Integer, UUID>(size3);
            for (int i = 0; i < size3; ++i) {
                int partition = in.readInt();
                UUID uuid = new UUID(in.readLong(), in.readLong());
                this.partitionUuidList.put(partition, uuid);
            }
        }
    }
}

