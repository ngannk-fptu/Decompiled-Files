/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.impl.query.QueryRunner;
import com.hazelcast.map.impl.query.ResultSegment;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.ReadonlyOperation;
import java.io.IOException;

public class MapFetchWithQueryOperation
extends MapOperation
implements ReadonlyOperation {
    private Query query;
    private int fetchSize;
    private int lastTableIndex;
    private transient ResultSegment response;

    public MapFetchWithQueryOperation() {
    }

    public MapFetchWithQueryOperation(String name, int lastTableIndex, int fetchSize, Query query) {
        super(name);
        this.lastTableIndex = lastTableIndex;
        this.fetchSize = fetchSize;
        this.query = query;
    }

    @Override
    public void run() throws Exception {
        QueryRunner runner = this.mapServiceContext.getMapQueryRunner(this.query.getMapName());
        this.response = runner.runPartitionScanQueryOnPartitionChunk(this.query, this.getPartitionId(), this.lastTableIndex, this.fetchSize);
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.fetchSize = in.readInt();
        this.lastTableIndex = in.readInt();
        this.query = (Query)in.readObject();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.fetchSize);
        out.writeInt(this.lastTableIndex);
        out.writeObject(this.query);
    }

    @Override
    public int getId() {
        return 138;
    }
}

