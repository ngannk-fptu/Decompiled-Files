/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.spi.AbstractWaitNotifyKey;

public class MapFlushWaitNotifyKey
extends AbstractWaitNotifyKey {
    private final int partitionId;
    private final long happenedFlushCount;

    public MapFlushWaitNotifyKey(String mapName, int partitionId, long happenedFlushCount) {
        super("hz:impl:mapService", mapName);
        this.happenedFlushCount = happenedFlushCount;
        this.partitionId = partitionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        MapFlushWaitNotifyKey that = (MapFlushWaitNotifyKey)o;
        if (this.partitionId != that.partitionId) {
            return false;
        }
        return this.happenedFlushCount == that.happenedFlushCount;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.partitionId;
        result = 31 * result + (int)(this.happenedFlushCount ^ this.happenedFlushCount >>> 32);
        return result;
    }
}

