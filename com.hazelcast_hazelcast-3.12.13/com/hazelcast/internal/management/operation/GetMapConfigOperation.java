/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.operation;

import com.hazelcast.config.MapConfig;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.spi.AbstractLocalOperation;

public class GetMapConfigOperation
extends AbstractLocalOperation {
    private String mapName;
    private MapConfig mapConfig;

    public GetMapConfigOperation(String mapName) {
        this.mapName = mapName;
    }

    @Override
    public void run() throws Exception {
        MapService service = (MapService)this.getService();
        this.mapConfig = service.getMapServiceContext().getMapContainer(this.mapName).getMapConfig();
    }

    @Override
    public Object getResponse() {
        return this.mapConfig;
    }
}

