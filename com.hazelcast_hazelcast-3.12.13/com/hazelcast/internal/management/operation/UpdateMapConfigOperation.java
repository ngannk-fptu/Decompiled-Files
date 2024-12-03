/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.operation;

import com.hazelcast.config.MapConfig;
import com.hazelcast.internal.management.dto.MapConfigDTO;
import com.hazelcast.internal.management.operation.AbstractManagementOperation;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class UpdateMapConfigOperation
extends AbstractManagementOperation {
    private String mapName;
    private MapConfig mapConfig;

    public UpdateMapConfigOperation() {
    }

    public UpdateMapConfigOperation(String mapName, MapConfig mapConfig) {
        this.mapName = mapName;
        this.mapConfig = mapConfig;
    }

    @Override
    public void run() throws Exception {
        MapService service = (MapService)this.getService();
        MapConfig oldConfig = service.getMapServiceContext().getMapContainer(this.mapName).getMapConfig();
        MapConfig newConfig = new MapConfig(oldConfig);
        newConfig.setTimeToLiveSeconds(this.mapConfig.getTimeToLiveSeconds());
        newConfig.setMaxIdleSeconds(this.mapConfig.getMaxIdleSeconds());
        newConfig.setEvictionPolicy(this.mapConfig.getEvictionPolicy());
        newConfig.setEvictionPercentage(this.mapConfig.getEvictionPercentage());
        newConfig.setMinEvictionCheckMillis(this.mapConfig.getMinEvictionCheckMillis());
        newConfig.setReadBackupData(this.mapConfig.isReadBackupData());
        newConfig.setMaxSizeConfig(this.mapConfig.getMaxSizeConfig());
        MapContainer mapContainer = service.getMapServiceContext().getMapContainer(this.mapName);
        mapContainer.setMapConfig(newConfig.getAsReadOnly());
        mapContainer.initEvictor();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.mapName);
        new MapConfigDTO(this.mapConfig).writeData(out);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.mapName = in.readUTF();
        MapConfigDTO adapter = new MapConfigDTO();
        adapter.readData(in);
        this.mapConfig = adapter.getMapConfig();
    }

    @Override
    public int getId() {
        return 2;
    }
}

