/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber.operation;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.Accumulator;
import com.hazelcast.map.impl.querycache.utils.QueryCacheUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class SetReadCursorOperation
extends MapOperation
implements PartitionAwareOperation {
    private long sequence;
    private String cacheId;
    private transient boolean result;

    public SetReadCursorOperation() {
    }

    public SetReadCursorOperation(String mapName, String cacheId, long sequence, int ignored) {
        super(Preconditions.checkHasText(mapName, "mapName"));
        Preconditions.checkPositive(sequence, "sequence");
        this.cacheId = Preconditions.checkHasText(cacheId, "cacheId");
        this.sequence = sequence;
    }

    @Override
    public void run() throws Exception {
        this.result = this.setReadCursor();
    }

    @Override
    public Object getResponse() {
        return this.result;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.cacheId);
        out.writeLong(this.sequence);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.cacheId = in.readUTF();
        this.sequence = in.readLong();
    }

    private boolean setReadCursor() {
        QueryCacheContext context = this.getContext();
        Accumulator accumulator = QueryCacheUtil.getAccumulatorOrNull(context, this.name, this.cacheId, this.getPartitionId());
        if (accumulator == null) {
            return false;
        }
        return accumulator.setHead(this.sequence);
    }

    private QueryCacheContext getContext() {
        return this.mapServiceContext.getQueryCacheContext();
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 128;
    }
}

