/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures;

import com.hazelcast.cp.internal.datastructures.spi.blocking.WaitKeyContainer;
import com.hazelcast.cp.internal.datastructures.spi.blocking.operation.ExpireWaitKeysOp;
import com.hazelcast.cp.internal.datastructures.spi.operation.DestroyRaftObjectOp;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class RaftDataServiceDataSerializerHook
implements DataSerializerHook {
    private static final int FACTORY_ID = -1010;
    private static final String RAFT_DS_FACTORY = "hazelcast.serialization.ds.raft.data";
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.raft.data", -1010);
    public static final int WAIT_KEY_CONTAINER = 1;
    public static final int EXPIRE_WAIT_KEYS_OP = 2;
    public static final int DESTROY_RAFT_OBJECT_OP = 3;

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
                    case 1: {
                        return new WaitKeyContainer();
                    }
                    case 2: {
                        return new ExpireWaitKeysOp();
                    }
                    case 3: {
                        return new DestroyRaftObjectOp();
                    }
                }
                throw new IllegalArgumentException("Undefined type: " + typeId);
            }
        };
    }
}

