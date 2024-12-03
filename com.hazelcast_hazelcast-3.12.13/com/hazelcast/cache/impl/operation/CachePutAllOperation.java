/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.operation.CacheOperation;
import com.hazelcast.cache.impl.operation.CachePutAllBackupOperation;
import com.hazelcast.cache.impl.operation.MutableOperation;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.spi.impl.operationservice.TargetAware;
import com.hazelcast.util.MapUtil;
import com.hazelcast.version.Version;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.cache.expiry.ExpiryPolicy;

public class CachePutAllOperation
extends CacheOperation
implements BackupAwareOperation,
MutableOperation,
MutatingOperation,
TargetAware {
    private List<Map.Entry<Data, Data>> entries;
    private ExpiryPolicy expiryPolicy;
    private int completionId;
    private transient Map<Data, CacheRecord> backupRecords;
    private transient Address target;

    public CachePutAllOperation() {
    }

    public CachePutAllOperation(String cacheNameWithPrefix, List<Map.Entry<Data, Data>> entries, ExpiryPolicy expiryPolicy, int completionId) {
        super(cacheNameWithPrefix);
        this.entries = entries;
        this.expiryPolicy = expiryPolicy;
        this.completionId = completionId;
    }

    @Override
    public int getCompletionId() {
        return this.completionId;
    }

    @Override
    public void setCompletionId(int completionId) {
        this.completionId = completionId;
    }

    @Override
    public void run() throws Exception {
        String callerUuid = this.getCallerUuid();
        this.backupRecords = MapUtil.createHashMap(this.entries.size());
        for (Map.Entry<Data, Data> entry : this.entries) {
            Data value;
            Data key = entry.getKey();
            CacheRecord backupRecord = this.recordStore.put(key, value = entry.getValue(), this.expiryPolicy, callerUuid, this.completionId);
            if (backupRecord == null) continue;
            this.backupRecords.put(key, backupRecord);
            this.publishWanUpdate(key, backupRecord);
        }
    }

    @Override
    public boolean shouldBackup() {
        return !this.backupRecords.isEmpty();
    }

    @Override
    public Operation getBackupOperation() {
        return new CachePutAllBackupOperation(this.name, this.backupRecords);
    }

    @Override
    public int getId() {
        return 37;
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
        out.writeObject(this.expiryPolicy);
        out.writeInt(this.completionId);
        out.writeInt(this.entries.size());
        for (Map.Entry<Data, Data> entry : this.entries) {
            out.writeData(entry.getKey());
            out.writeData(entry.getValue());
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.expiryPolicy = (ExpiryPolicy)in.readObject();
        this.completionId = in.readInt();
        int size = in.readInt();
        this.entries = new ArrayList<Map.Entry<Data, Data>>(size);
        for (int i = 0; i < size; ++i) {
            Data key = in.readData();
            Data value = in.readData();
            this.entries.add(new AbstractMap.SimpleImmutableEntry<Data, Data>(key, value));
        }
    }
}

