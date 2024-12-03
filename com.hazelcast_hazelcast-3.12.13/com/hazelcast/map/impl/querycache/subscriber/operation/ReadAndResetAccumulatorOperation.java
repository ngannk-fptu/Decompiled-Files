/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber.operation;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.Accumulator;
import com.hazelcast.map.impl.querycache.event.sequence.Sequenced;
import com.hazelcast.map.impl.querycache.utils.QueryCacheUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.PartitionAwareOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReadAndResetAccumulatorOperation
extends MapOperation
implements PartitionAwareOperation {
    private String cacheId;
    private List<Sequenced> eventDataList;

    public ReadAndResetAccumulatorOperation() {
    }

    public ReadAndResetAccumulatorOperation(String mapName, String cacheId) {
        super(mapName);
        this.cacheId = cacheId;
    }

    @Override
    public void run() throws Exception {
        QueryCacheContext context = this.getQueryCacheContext();
        Map<Integer, Accumulator> accumulators = QueryCacheUtil.getAccumulators(context, this.name, this.cacheId);
        Accumulator accumulator = accumulators.get(this.getPartitionId());
        if (accumulator == null || accumulator.isEmpty()) {
            return;
        }
        this.eventDataList = new ArrayList<Sequenced>(accumulator.size());
        for (Sequenced sequenced : accumulator) {
            this.eventDataList.add(sequenced);
        }
        accumulator.reset();
    }

    @Override
    public boolean returnsResponse() {
        return true;
    }

    @Override
    public Object getResponse() {
        return this.eventDataList;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.cacheId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.cacheId = in.readUTF();
    }

    private QueryCacheContext getQueryCacheContext() {
        return this.mapServiceContext.getQueryCacheContext();
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 127;
    }
}

