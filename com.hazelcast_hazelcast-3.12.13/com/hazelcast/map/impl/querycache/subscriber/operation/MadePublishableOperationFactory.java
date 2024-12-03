/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber.operation;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.querycache.subscriber.operation.MadePublishableOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class MadePublishableOperationFactory
implements OperationFactory {
    private String mapName;
    private String cacheId;

    public MadePublishableOperationFactory() {
    }

    public MadePublishableOperationFactory(String mapName, String cacheId) {
        Preconditions.checkHasText(mapName, "mapName");
        Preconditions.checkHasText(cacheId, "cacheId");
        this.cacheId = cacheId;
        this.mapName = mapName;
    }

    @Override
    public Operation createOperation() {
        return new MadePublishableOperation(this.mapName, this.cacheId);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.mapName);
        out.writeUTF(this.cacheId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.mapName = in.readUTF();
        this.cacheId = in.readUTF();
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 125;
    }
}

