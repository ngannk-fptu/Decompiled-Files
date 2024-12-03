/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.lock;

import com.hazelcast.cp.internal.datastructures.lock.LockEndpoint;
import com.hazelcast.cp.internal.datastructures.lock.LockInvocationKey;
import com.hazelcast.cp.internal.datastructures.lock.RaftLock;
import com.hazelcast.cp.internal.datastructures.lock.RaftLockOwnershipState;
import com.hazelcast.cp.internal.datastructures.lock.RaftLockRegistry;
import com.hazelcast.cp.internal.datastructures.lock.operation.GetLockOwnershipStateOp;
import com.hazelcast.cp.internal.datastructures.lock.operation.LockOp;
import com.hazelcast.cp.internal.datastructures.lock.operation.TryLockOp;
import com.hazelcast.cp.internal.datastructures.lock.operation.UnlockOp;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class RaftLockDataSerializerHook
implements DataSerializerHook {
    private static final int RAFT_LOCK_DS_FACTORY_ID = -1012;
    private static final String RAFT_LOCK_DS_FACTORY = "hazelcast.serialization.ds.raft.lock";
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.raft.lock", -1012);
    public static final int RAFT_LOCK_REGISTRY = 1;
    public static final int RAFT_LOCK = 2;
    public static final int LOCK_ENDPOINT = 3;
    public static final int LOCK_INVOCATION_KEY = 4;
    public static final int RAFT_LOCK_OWNERSHIP_STATE = 5;
    public static final int LOCK_OP = 6;
    public static final int TRY_LOCK_OP = 7;
    public static final int UNLOCK_OP = 8;
    public static final int GET_RAFT_LOCK_OWNERSHIP_STATE_OP = 9;

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
                        return new RaftLockRegistry();
                    }
                    case 2: {
                        return new RaftLock();
                    }
                    case 3: {
                        return new LockEndpoint();
                    }
                    case 4: {
                        return new LockInvocationKey();
                    }
                    case 5: {
                        return new RaftLockOwnershipState();
                    }
                    case 6: {
                        return new LockOp();
                    }
                    case 7: {
                        return new TryLockOp();
                    }
                    case 8: {
                        return new UnlockOp();
                    }
                    case 9: {
                        return new GetLockOwnershipStateOp();
                    }
                }
                throw new IllegalArgumentException("Undefined type: " + typeId);
            }
        };
    }
}

