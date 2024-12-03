/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl;

import com.hazelcast.durableexecutor.impl.operations.DisposeResultBackupOperation;
import com.hazelcast.durableexecutor.impl.operations.DisposeResultOperation;
import com.hazelcast.durableexecutor.impl.operations.PutResultBackupOperation;
import com.hazelcast.durableexecutor.impl.operations.PutResultOperation;
import com.hazelcast.durableexecutor.impl.operations.ReplicationOperation;
import com.hazelcast.durableexecutor.impl.operations.RetrieveAndDisposeResultOperation;
import com.hazelcast.durableexecutor.impl.operations.RetrieveResultOperation;
import com.hazelcast.durableexecutor.impl.operations.ShutdownOperation;
import com.hazelcast.durableexecutor.impl.operations.TaskBackupOperation;
import com.hazelcast.durableexecutor.impl.operations.TaskOperation;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class DurableExecutorDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.durable.executor", -34);
    public static final int DISPOSE_RESULT_BACKUP = 0;
    public static final int DISPOSE_RESULT = 1;
    public static final int PUT_RESULT = 2;
    public static final int REPLICATION = 3;
    public static final int RETRIEVE_DISPOSE_RESULT = 4;
    public static final int RETRIEVE_RESULT = 5;
    public static final int SHUTDOWN = 6;
    public static final int TASK_BACKUP = 7;
    public static final int TASK = 8;
    public static final int PUT_RESULT_BACKUP = 9;

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
                        return new DisposeResultBackupOperation();
                    }
                    case 1: {
                        return new DisposeResultOperation();
                    }
                    case 2: {
                        return new PutResultOperation();
                    }
                    case 9: {
                        return new PutResultBackupOperation();
                    }
                    case 3: {
                        return new ReplicationOperation();
                    }
                    case 4: {
                        return new RetrieveAndDisposeResultOperation();
                    }
                    case 5: {
                        return new RetrieveResultOperation();
                    }
                    case 6: {
                        return new ShutdownOperation();
                    }
                    case 7: {
                        return new TaskBackupOperation();
                    }
                    case 8: {
                        return new TaskOperation();
                    }
                }
                return null;
            }
        };
    }
}

