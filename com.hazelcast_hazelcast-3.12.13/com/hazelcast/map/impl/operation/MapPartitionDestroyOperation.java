/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.spi.AbstractLocalOperation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;

public class MapPartitionDestroyOperation
extends AbstractLocalOperation
implements PartitionAwareOperation,
AllowedDuringPassiveState {
    private final PartitionContainer partitionContainer;
    private final MapContainer mapContainer;

    public MapPartitionDestroyOperation(PartitionContainer container, MapContainer mapContainer) {
        this.partitionContainer = container;
        this.mapContainer = mapContainer;
        this.setPartitionId(this.partitionContainer.getPartitionId());
    }

    @Override
    public void run() {
        this.partitionContainer.destroyMap(this.mapContainer);
    }

    @Override
    public boolean validatesTarget() {
        return false;
    }
}

