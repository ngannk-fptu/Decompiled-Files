/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.operation.CacheOperation;
import com.hazelcast.cache.impl.operation.CachePutAllBackupOperation;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.TargetAware;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.util.MapUtil;
import com.hazelcast.version.Version;
import com.hazelcast.wan.impl.CallerProvenance;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CacheMergeOperation
extends CacheOperation
implements BackupAwareOperation,
TargetAware {
    private List<SplitBrainMergeTypes.CacheMergeTypes> mergingEntries;
    private SplitBrainMergePolicy<Data, SplitBrainMergeTypes.CacheMergeTypes> mergePolicy;
    private transient boolean hasBackups;
    private transient Map<Data, CacheRecord> backupRecords;
    private transient Address target;

    public CacheMergeOperation() {
    }

    public CacheMergeOperation(String name, List<SplitBrainMergeTypes.CacheMergeTypes> mergingEntries, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.CacheMergeTypes> mergePolicy) {
        super(name);
        this.mergingEntries = mergingEntries;
        this.mergePolicy = mergePolicy;
    }

    @Override
    protected void beforeRunInternal() {
        boolean bl = this.hasBackups = this.getSyncBackupCount() + this.getAsyncBackupCount() > 0;
        if (this.hasBackups) {
            this.backupRecords = MapUtil.createHashMap(this.mergingEntries.size());
        }
    }

    @Override
    public void run() {
        for (SplitBrainMergeTypes.CacheMergeTypes mergingEntry : this.mergingEntries) {
            this.merge(mergingEntry);
        }
    }

    private void merge(SplitBrainMergeTypes.CacheMergeTypes mergingEntry) {
        Data dataKey = (Data)mergingEntry.getKey();
        CacheRecord backupRecord = this.recordStore.merge(mergingEntry, this.mergePolicy, CallerProvenance.NOT_WAN);
        if (backupRecord != null) {
            this.backupRecords.put(dataKey, backupRecord);
        }
        if (this.recordStore.isWanReplicationEnabled()) {
            if (backupRecord != null) {
                this.publishWanUpdate(dataKey, backupRecord);
            } else {
                this.publishWanRemove(dataKey);
            }
        }
    }

    @Override
    public Object getResponse() {
        return !this.backupRecords.isEmpty();
    }

    @Override
    public boolean shouldBackup() {
        return this.hasBackups && !this.backupRecords.isEmpty();
    }

    @Override
    public Operation getBackupOperation() {
        return new CachePutAllBackupOperation(this.name, this.backupRecords);
    }

    @Override
    public void setTarget(Address address) {
        this.target = address;
    }

    @Override
    protected boolean requiresExplicitServiceName() {
        MemberImpl member = this.getNodeEngine().getClusterService().getMember(this.target);
        if (member == null) {
            return false;
        }
        Version memberVersion = member.getVersion().asVersion();
        return memberVersion.isLessThan(Versions.V3_11);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.mergingEntries.size());
        for (SplitBrainMergeTypes.CacheMergeTypes mergingEntry : this.mergingEntries) {
            out.writeObject(mergingEntry);
        }
        out.writeObject(this.mergePolicy);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.mergingEntries = new ArrayList<SplitBrainMergeTypes.CacheMergeTypes>(size);
        for (int i = 0; i < size; ++i) {
            SplitBrainMergeTypes.CacheMergeTypes mergingEntry = (SplitBrainMergeTypes.CacheMergeTypes)in.readObject();
            this.mergingEntries.add(mergingEntry);
        }
        this.mergePolicy = (SplitBrainMergePolicy)in.readObject();
    }

    @Override
    public int getId() {
        return 65;
    }
}

