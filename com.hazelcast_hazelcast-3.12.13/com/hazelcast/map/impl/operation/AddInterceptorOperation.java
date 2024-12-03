/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.MapInterceptor;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NamedOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class AddInterceptorOperation
extends Operation
implements MutatingOperation,
NamedOperation,
IdentifiedDataSerializable {
    private MapService mapService;
    private String id;
    private MapInterceptor mapInterceptor;
    private String mapName;

    public AddInterceptorOperation() {
    }

    public AddInterceptorOperation(String id, MapInterceptor mapInterceptor, String mapName) {
        this.id = id;
        this.mapInterceptor = mapInterceptor;
        this.mapName = mapName;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public void run() {
        this.mapService = (MapService)this.getService();
        MapContainer mapContainer = this.mapService.getMapServiceContext().getMapContainer(this.mapName);
        mapContainer.getInterceptorRegistry().register(this.id, this.mapInterceptor);
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
        this.mapInterceptor = (MapInterceptor)in.readObject();
    }

    @Override
    public void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.mapName);
        out.writeUTF(this.id);
        out.writeObject(this.mapInterceptor);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", name=").append(this.mapName);
    }

    @Override
    public String getName() {
        return this.mapName;
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 95;
    }
}

