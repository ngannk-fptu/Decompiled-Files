/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.iterator.MapEntriesWithCursor;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.ReadonlyOperation;
import java.io.IOException;

public class MapFetchEntriesOperation
extends MapOperation
implements ReadonlyOperation {
    private int fetchSize;
    private int lastTableIndex;
    private transient MapEntriesWithCursor response;

    public MapFetchEntriesOperation() {
    }

    public MapFetchEntriesOperation(String name, int lastTableIndex, int fetchSize) {
        super(name);
        this.lastTableIndex = lastTableIndex;
        this.fetchSize = fetchSize;
    }

    @Override
    public void run() throws Exception {
        this.response = this.recordStore.fetchEntries(this.lastTableIndex, this.fetchSize);
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
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.fetchSize);
        out.writeInt(this.lastTableIndex);
    }

    @Override
    public int getId() {
        return 47;
    }
}

