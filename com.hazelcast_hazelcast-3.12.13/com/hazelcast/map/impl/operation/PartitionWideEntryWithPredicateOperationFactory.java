/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.operation.MultipleEntryWithPredicateOperation;
import com.hazelcast.map.impl.operation.PartitionWideEntryWithPredicateOperation;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.impl.query.QueryResult;
import com.hazelcast.map.impl.query.QueryResultRow;
import com.hazelcast.map.impl.query.QueryRunner;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.impl.operations.PartitionAwareOperationFactory;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.IterationType;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.collection.InflatableSet;
import com.hazelcast.util.collection.Int2ObjectHashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PartitionWideEntryWithPredicateOperationFactory
extends PartitionAwareOperationFactory {
    private String name;
    private EntryProcessor entryProcessor;
    private Predicate predicate;
    private transient Map<Integer, List<Data>> partitionIdToKeysMap;

    public PartitionWideEntryWithPredicateOperationFactory() {
    }

    public PartitionWideEntryWithPredicateOperationFactory(String name, EntryProcessor entryProcessor, Predicate predicate) {
        this.name = name;
        this.entryProcessor = entryProcessor;
        this.predicate = predicate;
    }

    private PartitionWideEntryWithPredicateOperationFactory(String name, EntryProcessor entryProcessor, Predicate predicate, Map<Integer, List<Data>> partitionIdToKeysMap) {
        this(name, entryProcessor, predicate);
        this.partitionIdToKeysMap = partitionIdToKeysMap;
    }

    @Override
    public PartitionAwareOperationFactory createFactoryOnRunner(NodeEngine nodeEngine, int[] partitions) {
        Set<Data> keys = this.tryToObtainKeysFromIndexes(nodeEngine);
        Map<Integer, List<Data>> partitionIdToKeysMap = this.groupKeysByPartition(keys, nodeEngine.getPartitionService(), partitions);
        return new PartitionWideEntryWithPredicateOperationFactory(this.name, this.entryProcessor, this.predicate, partitionIdToKeysMap);
    }

    @Override
    public Operation createPartitionOperation(int partition) {
        if (this.partitionIdToKeysMap == null) {
            return new PartitionWideEntryWithPredicateOperation(this.name, this.entryProcessor, this.predicate);
        }
        List<Data> keyList = this.partitionIdToKeysMap.get(partition);
        assert (keyList != null) : "unexpected partition " + partition + ", expected partitions " + this.partitionIdToKeysMap.keySet();
        Set<Data> keys = keyList.isEmpty() ? Collections.emptySet() : InflatableSet.newBuilder(keyList).build();
        return new MultipleEntryWithPredicateOperation(this.name, keys, this.entryProcessor, this.predicate);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeObject(this.entryProcessor);
        out.writeObject(this.predicate);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.entryProcessor = (EntryProcessor)in.readObject();
        this.predicate = (Predicate)in.readObject();
    }

    private Set<Data> tryToObtainKeysFromIndexes(NodeEngine nodeEngine) {
        Query query;
        if (this.predicate == TruePredicate.INSTANCE) {
            return null;
        }
        MapService mapService = (MapService)nodeEngine.getService("hz:impl:mapService");
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        QueryRunner runner = mapServiceContext.getMapQueryRunner(this.name);
        QueryResult result = (QueryResult)runner.runIndexQueryOnOwnedPartitions(query = Query.of().mapName(this.name).predicate(this.predicate).iterationType(IterationType.KEY).build());
        if (result.getPartitionIds() == null) {
            return null;
        }
        InflatableSet.Builder<Data> setBuilder = InflatableSet.newBuilder(result.size());
        for (QueryResultRow row : result.getRows()) {
            setBuilder.add(row.getKey());
        }
        return setBuilder.build();
    }

    private Map<Integer, List<Data>> groupKeysByPartition(Set<Data> keys, IPartitionService partitionService, int[] partitions) {
        if (keys == null) {
            return null;
        }
        Int2ObjectHashMap<List<Data>> partitionToKeys = MapUtil.createInt2ObjectHashMap(partitions.length);
        for (int partition : partitions) {
            partitionToKeys.put(partition, (List<Data>)Collections.emptyList());
        }
        Object object = keys.iterator();
        while (object.hasNext()) {
            Data key = (Data)object.next();
            int partitionId = partitionService.getPartitionId(key);
            List<Data> keyList = partitionToKeys.get(partitionId);
            if (keyList == null) continue;
            if (keyList.isEmpty()) {
                keyList = new ArrayList<Data>();
                partitionToKeys.put(partitionId, keyList);
            }
            keyList.add(key);
        }
        return partitionToKeys;
    }

    @Override
    public Operation createOperation() {
        return new PartitionWideEntryWithPredicateOperation(this.name, this.entryProcessor, this.predicate);
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 86;
    }
}

