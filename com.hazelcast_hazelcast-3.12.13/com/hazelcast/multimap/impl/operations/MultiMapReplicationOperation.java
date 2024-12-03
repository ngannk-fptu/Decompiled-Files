/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.multimap.impl.MultiMapDataSerializerHook;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.multimap.impl.MultiMapValue;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MultiMapReplicationOperation
extends Operation
implements IdentifiedDataSerializable {
    private Map<String, Map<Data, MultiMapValue>> map;

    public MultiMapReplicationOperation() {
    }

    public MultiMapReplicationOperation(Map<String, Map<Data, MultiMapValue>> map) {
        this.map = map;
    }

    @Override
    public void run() throws Exception {
        MultiMapService service = (MultiMapService)this.getService();
        service.insertMigratedData(this.getPartitionId(), this.map);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.map.size());
        for (Map.Entry<String, Map<Data, MultiMapValue>> entry : this.map.entrySet()) {
            String name = entry.getKey();
            out.writeUTF(name);
            Map<Data, MultiMapValue> collections = entry.getValue();
            out.writeInt(collections.size());
            for (Map.Entry<Data, MultiMapValue> collectionEntry : collections.entrySet()) {
                Data key = collectionEntry.getKey();
                out.writeData(key);
                MultiMapValue multiMapValue = collectionEntry.getValue();
                Collection<MultiMapRecord> coll = multiMapValue.getCollection(false);
                out.writeInt(coll.size());
                String collectionType = MultiMapConfig.ValueCollectionType.SET.name();
                if (coll instanceof List) {
                    collectionType = MultiMapConfig.ValueCollectionType.LIST.name();
                }
                out.writeUTF(collectionType);
                for (MultiMapRecord record : coll) {
                    record.writeData(out);
                }
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int mapSize = in.readInt();
        this.map = MapUtil.createHashMap(mapSize);
        for (int i = 0; i < mapSize; ++i) {
            String name = in.readUTF();
            int collectionSize = in.readInt();
            Map<Data, MultiMapValue> collections = MapUtil.createHashMap(collectionSize);
            for (int j = 0; j < collectionSize; ++j) {
                Data key = in.readData();
                int collSize = in.readInt();
                String collectionType = in.readUTF();
                Collection coll = collectionType.equals(MultiMapConfig.ValueCollectionType.SET.name()) ? SetUtil.createHashSet(collSize) : new LinkedList();
                for (int k = 0; k < collSize; ++k) {
                    MultiMapRecord record = new MultiMapRecord();
                    record.readData(in);
                    coll.add((MultiMapRecord)record);
                }
                collections.put(key, new MultiMapValue(coll));
            }
            this.map.put(name, collections);
        }
    }

    @Override
    public int getFactoryId() {
        return MultiMapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 45;
    }
}

