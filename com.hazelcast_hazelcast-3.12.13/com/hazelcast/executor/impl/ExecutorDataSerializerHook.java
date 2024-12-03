/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.executor.impl;

import com.hazelcast.executor.impl.RunnableAdapter;
import com.hazelcast.executor.impl.operations.CallableTaskOperation;
import com.hazelcast.executor.impl.operations.CancellationOperation;
import com.hazelcast.executor.impl.operations.MemberCallableTaskOperation;
import com.hazelcast.executor.impl.operations.ShutdownOperation;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class ExecutorDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.executor", -13);
    public static final int CALLABLE_TASK = 0;
    public static final int MEMBER_CALLABLE_TASK = 1;
    public static final int RUNNABLE_ADAPTER = 2;
    public static final int CANCELLATION = 3;
    public static final int SHUTDOWN = 4;

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
                        return new CallableTaskOperation();
                    }
                    case 1: {
                        return new MemberCallableTaskOperation();
                    }
                    case 2: {
                        return new RunnableAdapter();
                    }
                    case 3: {
                        return new CancellationOperation();
                    }
                    case 4: {
                        return new ShutdownOperation();
                    }
                }
                return null;
            }
        };
    }
}

