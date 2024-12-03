/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore;

import com.hazelcast.concurrent.semaphore.SemaphoreContainer;
import com.hazelcast.concurrent.semaphore.operations.AcquireBackupOperation;
import com.hazelcast.concurrent.semaphore.operations.AcquireOperation;
import com.hazelcast.concurrent.semaphore.operations.AvailableOperation;
import com.hazelcast.concurrent.semaphore.operations.DrainBackupOperation;
import com.hazelcast.concurrent.semaphore.operations.DrainOperation;
import com.hazelcast.concurrent.semaphore.operations.IncreaseBackupOperation;
import com.hazelcast.concurrent.semaphore.operations.IncreaseOperation;
import com.hazelcast.concurrent.semaphore.operations.InitBackupOperation;
import com.hazelcast.concurrent.semaphore.operations.InitOperation;
import com.hazelcast.concurrent.semaphore.operations.ReduceBackupOperation;
import com.hazelcast.concurrent.semaphore.operations.ReduceOperation;
import com.hazelcast.concurrent.semaphore.operations.ReleaseBackupOperation;
import com.hazelcast.concurrent.semaphore.operations.ReleaseOperation;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreDetachMemberBackupOperation;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreDetachMemberOperation;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreReplicationOperation;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class SemaphoreDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.semaphore", -16);
    public static final int CONTAINER = 0;
    public static final int ACQUIRE_BACKUP_OPERATION = 1;
    public static final int ACQUIRE_OPERATION = 2;
    public static final int AVAILABLE_OPERATION = 3;
    public static final int DETACH_MEMBER_BACKUP_OPERATION = 4;
    public static final int DRAIN_BACKUP_OPERATION = 5;
    public static final int DRAIN_OPERATION = 6;
    public static final int INIT_BACKUP_OPERATION = 7;
    public static final int INIT_OPERATION = 8;
    public static final int REDUCE_BACKUP_OPERATION = 9;
    public static final int REDUCE_OPERATION = 10;
    public static final int RELEASE_BACKUP_OPERATION = 11;
    public static final int RELEASE_OPERATION = 12;
    public static final int DETACH_MEMBER_OPERATION = 13;
    public static final int SEMAPHORE_REPLICATION_OPERATION = 14;
    public static final int INCREASE_OPERATION = 15;
    public static final int INCREASE_BACKUP_OPERATION = 16;

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
                        return new SemaphoreContainer();
                    }
                    case 1: {
                        return new AcquireBackupOperation();
                    }
                    case 2: {
                        return new AcquireOperation();
                    }
                    case 3: {
                        return new AvailableOperation();
                    }
                    case 4: {
                        return new SemaphoreDetachMemberBackupOperation();
                    }
                    case 5: {
                        return new DrainBackupOperation();
                    }
                    case 6: {
                        return new DrainOperation();
                    }
                    case 7: {
                        return new InitBackupOperation();
                    }
                    case 8: {
                        return new InitOperation();
                    }
                    case 9: {
                        return new ReduceBackupOperation();
                    }
                    case 10: {
                        return new ReduceOperation();
                    }
                    case 11: {
                        return new ReleaseBackupOperation();
                    }
                    case 12: {
                        return new ReleaseOperation();
                    }
                    case 13: {
                        return new SemaphoreDetachMemberOperation();
                    }
                    case 14: {
                        return new SemaphoreReplicationOperation();
                    }
                    case 15: {
                        return new IncreaseOperation();
                    }
                    case 16: {
                        return new IncreaseBackupOperation();
                    }
                }
                return null;
            }
        };
    }
}

