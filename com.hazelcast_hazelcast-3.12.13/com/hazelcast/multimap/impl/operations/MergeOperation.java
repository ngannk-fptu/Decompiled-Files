/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapMergeContainer;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.MultiMapValue;
import com.hazelcast.multimap.impl.operations.AbstractMultiMapOperation;
import com.hazelcast.multimap.impl.operations.MergeBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MergeOperation
extends AbstractMultiMapOperation
implements BackupAwareOperation {
    private List<MultiMapMergeContainer> mergeContainers;
    private SplitBrainMergePolicy<Collection<Object>, SplitBrainMergeTypes.MultiMapMergeTypes> mergePolicy;
    private transient Map<Data, Collection<MultiMapRecord>> resultMap;

    public MergeOperation() {
    }

    public MergeOperation(String name, List<MultiMapMergeContainer> mergeContainers, SplitBrainMergePolicy<Collection<Object>, SplitBrainMergeTypes.MultiMapMergeTypes> mergePolicy) {
        super(name);
        this.mergeContainers = mergeContainers;
        this.mergePolicy = mergePolicy;
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainerWithoutAccess();
        this.resultMap = MapUtil.createHashMap(this.mergeContainers.size());
        for (MultiMapMergeContainer mergeContainer : this.mergeContainers) {
            Data key = mergeContainer.getKey();
            if (!container.canAcquireLock(key, this.getCallerUuid(), -1L)) {
                Object valueKey = this.getNodeEngine().getSerializationService().toObject(key);
                this.getLogger().info("Skipped merging of locked key '" + valueKey + "' on MultiMap '" + this.name + "'");
                continue;
            }
            MultiMapValue result = container.merge(mergeContainer, this.mergePolicy);
            if (result == null) continue;
            this.resultMap.put(key, result.getCollection(false));
        }
        this.response = !this.resultMap.isEmpty();
    }

    @Override
    public boolean shouldBackup() {
        return !this.resultMap.isEmpty();
    }

    @Override
    public Operation getBackupOperation() {
        return new MergeBackupOperation(this.name, this.resultMap);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.mergeContainers.size());
        for (MultiMapMergeContainer container : this.mergeContainers) {
            out.writeObject(container);
        }
        out.writeObject(this.mergePolicy);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.mergeContainers = new ArrayList<MultiMapMergeContainer>(size);
        for (int i = 0; i < size; ++i) {
            MultiMapMergeContainer container = (MultiMapMergeContainer)in.readObject();
            this.mergeContainers.add(container);
        }
        this.mergePolicy = (SplitBrainMergePolicy)in.readObject();
    }

    @Override
    public int getId() {
        return 49;
    }
}

