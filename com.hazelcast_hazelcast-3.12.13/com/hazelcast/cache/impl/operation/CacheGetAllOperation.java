/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cache.impl.operation.PartitionWideCacheOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.cache.expiry.ExpiryPolicy;

public class CacheGetAllOperation
extends PartitionWideCacheOperation
implements ReadonlyOperation {
    private Set<Data> keys;
    private ExpiryPolicy expiryPolicy;

    public CacheGetAllOperation(String name, Set<Data> keys, ExpiryPolicy expiryPolicy) {
        super(name);
        this.keys = keys;
        this.expiryPolicy = expiryPolicy;
    }

    public CacheGetAllOperation() {
        this.keys = new HashSet<Data>();
    }

    @Override
    public void run() {
        ICacheService service = (ICacheService)this.getService();
        ICacheRecordStore cache = service.getOrCreateRecordStore(this.name, this.getPartitionId());
        int partitionId = this.getPartitionId();
        HashSet<Data> partitionKeySet = new HashSet<Data>();
        for (Data key : this.keys) {
            if (partitionId != this.getNodeEngine().getPartitionService().getPartitionId(key)) continue;
            partitionKeySet.add(key);
        }
        this.response = cache.getAll(partitionKeySet, this.expiryPolicy);
    }

    @Override
    public int getId() {
        return 17;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", keys=").append(this.keys.toString());
        sb.append(", expiryPolicy=").append(this.expiryPolicy);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.expiryPolicy);
        if (this.keys == null) {
            out.writeInt(-1);
        } else {
            out.writeInt(this.keys.size());
            for (Data key : this.keys) {
                out.writeData(key);
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.expiryPolicy = (ExpiryPolicy)in.readObject();
        int size = in.readInt();
        if (size > -1) {
            this.keys = SetUtil.createHashSet(size);
            for (int i = 0; i < size; ++i) {
                Data key = in.readData();
                this.keys.add(key);
            }
        }
    }
}

