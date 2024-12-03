/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong;

import com.hazelcast.cp.internal.datastructures.atomiclong.RaftAtomicLongSnapshot;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.AddAndGetOp;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.AlterOp;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.ApplyOp;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.CompareAndSetOp;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.GetAndAddOp;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.GetAndSetOp;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.LocalGetOp;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public final class RaftAtomicLongDataSerializerHook
implements DataSerializerHook {
    private static final int RAFT_ATOMIC_LONG_DS_FACTORY_ID = -1011;
    private static final String RAFT_ATOMIC_LONG_DS_FACTORY = "hazelcast.serialization.ds.raft.atomiclong";
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.raft.atomiclong", -1011);
    public static final int ADD_AND_GET_OP = 1;
    public static final int COMPARE_AND_SET_OP = 2;
    public static final int GET_AND_ADD_OP = 3;
    public static final int GET_AND_SET_OP = 4;
    public static final int ALTER_OP = 5;
    public static final int APPLY_OP = 6;
    public static final int LOCAL_GET_OP = 7;
    public static final int SNAPSHOT = 8;

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
                        return new AddAndGetOp();
                    }
                    case 2: {
                        return new CompareAndSetOp();
                    }
                    case 3: {
                        return new GetAndAddOp();
                    }
                    case 4: {
                        return new GetAndSetOp();
                    }
                    case 5: {
                        return new AlterOp();
                    }
                    case 6: {
                        return new ApplyOp();
                    }
                    case 7: {
                        return new LocalGetOp();
                    }
                    case 8: {
                        return new RaftAtomicLongSnapshot();
                    }
                }
                throw new IllegalArgumentException("Undefined type: " + typeId);
            }
        };
    }
}

