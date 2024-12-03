/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.countdownlatch;

import com.hazelcast.cp.internal.datastructures.countdownlatch.AwaitInvocationKey;
import com.hazelcast.cp.internal.datastructures.countdownlatch.RaftCountDownLatch;
import com.hazelcast.cp.internal.datastructures.countdownlatch.RaftCountDownLatchRegistry;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.AwaitOp;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.CountDownOp;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.GetCountOp;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.GetRoundOp;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.TrySetCountOp;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class RaftCountDownLatchDataSerializerHook
implements DataSerializerHook {
    private static final int RAFT_COUNT_DOWN_LATCH_DS_FACTORY_ID = -1015;
    private static final String RAFT_COUNT_DOWN_LATCH_DS_FACTORY = "hazelcast.serialization.ds.raft.countdownlatch";
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.raft.countdownlatch", -1015);
    public static final int COUNT_DOWN_LATCH_REGISTRY = 1;
    public static final int COUNT_DOWN_LATCH = 2;
    public static final int AWAIT_INVOCATION_KEY = 3;
    public static final int AWAIT_OP = 4;
    public static final int COUNT_DOWN_OP = 5;
    public static final int GET_COUNT_OP = 6;
    public static final int GET_ROUND_OP = 7;
    public static final int TRY_SET_COUNT_OP = 8;

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
                        return new RaftCountDownLatchRegistry();
                    }
                    case 2: {
                        return new RaftCountDownLatch();
                    }
                    case 3: {
                        return new AwaitInvocationKey();
                    }
                    case 4: {
                        return new AwaitOp();
                    }
                    case 5: {
                        return new CountDownOp();
                    }
                    case 6: {
                        return new GetCountOp();
                    }
                    case 7: {
                        return new GetRoundOp();
                    }
                    case 8: {
                        return new TrySetCountOp();
                    }
                }
                throw new IllegalArgumentException("Undefined type: " + typeId);
            }
        };
    }
}

