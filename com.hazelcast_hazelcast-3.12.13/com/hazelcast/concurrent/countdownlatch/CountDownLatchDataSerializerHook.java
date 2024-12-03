/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.countdownlatch;

import com.hazelcast.concurrent.countdownlatch.CountDownLatchContainer;
import com.hazelcast.concurrent.countdownlatch.operations.AwaitOperation;
import com.hazelcast.concurrent.countdownlatch.operations.CountDownLatchBackupOperation;
import com.hazelcast.concurrent.countdownlatch.operations.CountDownLatchReplicationOperation;
import com.hazelcast.concurrent.countdownlatch.operations.CountDownOperation;
import com.hazelcast.concurrent.countdownlatch.operations.GetCountOperation;
import com.hazelcast.concurrent.countdownlatch.operations.SetCountOperation;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public final class CountDownLatchDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.cdl", -14);
    public static final int CONTAINER = 0;
    public static final int AWAIT_OPERATION = 1;
    public static final int COUNT_DOWN_LATCH_BACKUP_OPERATION = 2;
    public static final int COUNT_DOWN_LATCH_REPLICATION_OPERATION = 3;
    public static final int COUNT_DOWN_OPERATION = 4;
    public static final int GET_COUNT_OPERATION = 5;
    public static final int SET_COUNT_OPERATION = 6;

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
                        return new CountDownLatchContainer();
                    }
                    case 1: {
                        return new AwaitOperation();
                    }
                    case 2: {
                        return new CountDownLatchBackupOperation();
                    }
                    case 3: {
                        return new CountDownLatchReplicationOperation();
                    }
                    case 4: {
                        return new CountDownOperation();
                    }
                    case 5: {
                        return new GetCountOperation();
                    }
                    case 6: {
                        return new SetCountOperation();
                    }
                }
                return null;
            }
        };
    }
}

