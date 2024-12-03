/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.impl.query.QueryRunner;
import com.hazelcast.map.impl.query.Result;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ReadonlyOperation;
import java.io.IOException;

public class QueryPartitionOperation
extends MapOperation
implements PartitionAwareOperation,
ReadonlyOperation {
    private Query query;
    private Result result;

    public QueryPartitionOperation() {
    }

    public QueryPartitionOperation(Query query) {
        super(query.getMapName());
        this.query = query;
    }

    @Override
    public void run() throws Exception {
        QueryRunner queryRunner = this.mapServiceContext.getMapQueryRunner(this.getName());
        this.result = queryRunner.runPartitionScanQueryOnGivenOwnedPartition(this.query, this.getPartitionId());
        Indexes indexes = this.mapServiceContext.getMapContainer(this.getName()).getIndexes();
        indexes.getIndexesStats().incrementQueryCount();
    }

    @Override
    public Object getResponse() {
        return this.result;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.query);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.query = (Query)in.readObject();
    }

    @Override
    public int getId() {
        return 59;
    }
}

