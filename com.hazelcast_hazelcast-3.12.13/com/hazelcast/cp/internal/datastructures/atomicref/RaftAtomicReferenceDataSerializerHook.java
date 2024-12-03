/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomicref;

import com.hazelcast.cp.internal.datastructures.atomicref.RaftAtomicRefSnapshot;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.ApplyOp;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.CompareAndSetOp;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.ContainsOp;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.GetOp;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.SetOp;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public final class RaftAtomicReferenceDataSerializerHook
implements DataSerializerHook {
    private static final int RAFT_ATOMIC_REF_DS_FACTORY_ID = -1014;
    private static final String RAFT_ATOMIC_REF_DS_FACTORY = "hazelcast.serialization.ds.raft.atomicref";
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.raft.atomicref", -1014);
    public static final int SNAPSHOT = 1;
    public static final int APPLY_OP = 2;
    public static final int COMPARE_AND_SET_OP = 3;
    public static final int CONTAINS_OP = 4;
    public static final int GET_OP = 5;
    public static final int SET_OP = 6;

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
                        return new RaftAtomicRefSnapshot();
                    }
                    case 2: {
                        return new ApplyOp();
                    }
                    case 3: {
                        return new CompareAndSetOp();
                    }
                    case 4: {
                        return new ContainsOp();
                    }
                    case 5: {
                        return new GetOp();
                    }
                    case 6: {
                        return new SetOp();
                    }
                }
                throw new IllegalArgumentException("Undefined type: " + typeId);
            }
        };
    }
}

