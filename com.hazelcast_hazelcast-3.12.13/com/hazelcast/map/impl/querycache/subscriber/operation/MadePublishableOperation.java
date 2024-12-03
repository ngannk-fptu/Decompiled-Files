/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber.operation;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.publisher.PartitionAccumulatorRegistry;
import com.hazelcast.map.impl.querycache.utils.QueryCacheUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class MadePublishableOperation
extends MapOperation {
    private final ILogger logger = Logger.getLogger(this.getClass());
    private String cacheId;
    private transient boolean done;

    public MadePublishableOperation() {
    }

    public MadePublishableOperation(String mapName, String cacheId) {
        super(mapName);
        this.cacheId = cacheId;
    }

    @Override
    public void run() throws Exception {
        this.setPublishable();
    }

    private void setPublishable() {
        PartitionAccumulatorRegistry registry = QueryCacheUtil.getAccumulatorRegistryOrNull(this.getContext(), this.name, this.cacheId);
        if (registry == null) {
            return;
        }
        AccumulatorInfo info = registry.getInfo();
        info.setPublishable(true);
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Accumulator was made publishable for map=" + this.getName());
        }
        this.done = true;
    }

    private QueryCacheContext getContext() {
        MapService service = (MapService)this.getService();
        MapServiceContext mapServiceContext = service.getMapServiceContext();
        return mapServiceContext.getQueryCacheContext();
    }

    @Override
    public Object getResponse() {
        return this.done;
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

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 124;
    }
}

