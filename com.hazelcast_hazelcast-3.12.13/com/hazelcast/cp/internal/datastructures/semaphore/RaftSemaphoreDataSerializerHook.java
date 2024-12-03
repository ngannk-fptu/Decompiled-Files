/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore;

import com.hazelcast.cp.internal.datastructures.semaphore.AcquireInvocationKey;
import com.hazelcast.cp.internal.datastructures.semaphore.RaftSemaphore;
import com.hazelcast.cp.internal.datastructures.semaphore.RaftSemaphoreRegistry;
import com.hazelcast.cp.internal.datastructures.semaphore.SemaphoreEndpoint;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.AcquirePermitsOp;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.AvailablePermitsOp;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.ChangePermitsOp;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.DrainPermitsOp;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.InitSemaphoreOp;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.ReleasePermitsOp;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class RaftSemaphoreDataSerializerHook
implements DataSerializerHook {
    private static final int DS_FACTORY_ID = -1013;
    private static final String DS_FACTORY = "hazelcast.serialization.ds.raft.sema";
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.raft.sema", -1013);
    public static final int RAFT_SEMAPHORE_REGISTRY = 1;
    public static final int RAFT_SEMAPHORE = 2;
    public static final int ACQUIRE_INVOCATION_KEY = 3;
    public static final int SEMAPHORE_ENDPOINT = 4;
    public static final int ACQUIRE_PERMITS_OP = 5;
    public static final int AVAILABLE_PERMITS_OP = 6;
    public static final int CHANGE_PERMITS_OP = 7;
    public static final int DRAIN_PERMITS_OP = 8;
    public static final int INIT_SEMAPHORE_OP = 9;
    public static final int RELEASE_PERMITS_OP = 10;

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
                        return new RaftSemaphoreRegistry();
                    }
                    case 2: {
                        return new RaftSemaphore();
                    }
                    case 3: {
                        return new AcquireInvocationKey();
                    }
                    case 4: {
                        return new SemaphoreEndpoint();
                    }
                    case 5: {
                        return new AcquirePermitsOp();
                    }
                    case 6: {
                        return new AvailablePermitsOp();
                    }
                    case 7: {
                        return new ChangePermitsOp();
                    }
                    case 8: {
                        return new DrainPermitsOp();
                    }
                    case 9: {
                        return new InitSemaphoreOp();
                    }
                    case 10: {
                        return new ReleasePermitsOp();
                    }
                }
                throw new IllegalArgumentException("Undefined type: " + typeId);
            }
        };
    }
}

