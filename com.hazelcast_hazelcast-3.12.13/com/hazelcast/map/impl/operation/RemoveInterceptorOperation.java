/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NamedOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class RemoveInterceptorOperation
extends Operation
implements MutatingOperation,
NamedOperation,
IdentifiedDataSerializable {
    private MapService mapService;
    private String mapName;
    private String id;

    public RemoveInterceptorOperation() {
    }

    public RemoveInterceptorOperation(String mapName, String id) {
        this.mapName = mapName;
        this.id = id;
    }

    @Override
    public void run() {
        this.mapService = (MapService)this.getService();
        MapServiceContext mapServiceContext = this.mapService.getMapServiceContext();
        MapContainer mapContainer = mapServiceContext.getMapContainer(this.mapName);
        mapContainer.getInterceptorRegistry().deregister(this.id);
    }

    @Override
    public Object getResponse() {
        return true;
    }

    @Override
    public void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.mapName = in.readUTF();
        this.id = in.readUTF();
    }

    @Override
    public void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.mapName);
        out.writeUTF(this.id);
    }

    @Override
    public String getName() {
        return this.mapName;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", mapName=").append(this.mapName);
        sb.append(", id=").append(this.id);
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 101;
    }
}

