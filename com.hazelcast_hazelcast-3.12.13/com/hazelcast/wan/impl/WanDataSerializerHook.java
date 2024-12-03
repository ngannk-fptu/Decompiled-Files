/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan.impl;

import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.map.impl.wan.MapReplicationRemove;
import com.hazelcast.map.impl.wan.MapReplicationUpdate;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.wan.WanReplicationEvent;

public class WanDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.wan_replication", -31);
    public static final int WAN_REPLICATION_EVENT = 0;
    public static final int MAP_REPLICATION_UPDATE = 1;
    public static final int MAP_REPLICATION_REMOVE = 2;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        return new DataSerializableFactory(){

            @Override
            public IdentifiedDataSerializable create(int typeId) {
                switch (typeId) {
                    case 0: {
                        return new WanReplicationEvent();
                    }
                    case 1: {
                        return new MapReplicationUpdate();
                    }
                    case 2: {
                        return new MapReplicationRemove();
                    }
                }
                throw new IllegalArgumentException("Unknown type-id: " + typeId);
            }
        };
    }
}

