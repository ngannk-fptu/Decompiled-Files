/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.concurrent.lock.LockResourceImpl;
import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.concurrent.lock.WaitersInfo;
import com.hazelcast.concurrent.lock.operations.AwaitBackupOperation;
import com.hazelcast.concurrent.lock.operations.AwaitOperation;
import com.hazelcast.concurrent.lock.operations.BeforeAwaitBackupOperation;
import com.hazelcast.concurrent.lock.operations.BeforeAwaitOperation;
import com.hazelcast.concurrent.lock.operations.GetLockCountOperation;
import com.hazelcast.concurrent.lock.operations.GetRemainingLeaseTimeOperation;
import com.hazelcast.concurrent.lock.operations.IsLockedOperation;
import com.hazelcast.concurrent.lock.operations.LockBackupOperation;
import com.hazelcast.concurrent.lock.operations.LockOperation;
import com.hazelcast.concurrent.lock.operations.LockReplicationOperation;
import com.hazelcast.concurrent.lock.operations.SignalBackupOperation;
import com.hazelcast.concurrent.lock.operations.SignalOperation;
import com.hazelcast.concurrent.lock.operations.UnlockBackupOperation;
import com.hazelcast.concurrent.lock.operations.UnlockIfLeaseExpiredOperation;
import com.hazelcast.concurrent.lock.operations.UnlockOperation;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public final class LockDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.lock", -15);
    public static final int LOCK_RESOURCE = 0;
    public static final int LOCK_STORE = 1;
    public static final int WAITERS_INFO = 2;
    public static final int AWAIT_BACKUP = 3;
    public static final int AWAIT = 4;
    public static final int BEFORE_AWAIT_BACKUP = 5;
    public static final int BEFORE_AWAIT = 6;
    public static final int GET_LOCK_COUNT = 7;
    public static final int GET_REMAINING_LEASETIME = 8;
    public static final int IS_LOCKED = 9;
    public static final int LOCK_BACKUP = 10;
    public static final int LOCK = 11;
    public static final int LOCK_REPLICATION = 12;
    public static final int SIGNAL_BACKUP = 13;
    public static final int SIGNAL = 14;
    public static final int UNLOCK_BACKUP = 15;
    public static final int UNLOCK = 16;
    public static final int UNLOCK_IF_LEASE_EXPIRED = 17;

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
                    case 3: {
                        return new AwaitBackupOperation();
                    }
                    case 4: {
                        return new AwaitOperation();
                    }
                    case 5: {
                        return new BeforeAwaitBackupOperation();
                    }
                    case 6: {
                        return new BeforeAwaitOperation();
                    }
                    case 7: {
                        return new GetLockCountOperation();
                    }
                    case 8: {
                        return new GetRemainingLeaseTimeOperation();
                    }
                    case 9: {
                        return new IsLockedOperation();
                    }
                    case 11: {
                        return new LockOperation();
                    }
                    case 10: {
                        return new LockBackupOperation();
                    }
                    case 12: {
                        return new LockReplicationOperation();
                    }
                    case 13: {
                        return new SignalBackupOperation();
                    }
                    case 14: {
                        return new SignalOperation();
                    }
                    case 15: {
                        return new UnlockBackupOperation();
                    }
                    case 16: {
                        return new UnlockOperation();
                    }
                    case 1: {
                        return new LockStoreImpl();
                    }
                    case 2: {
                        return new WaitersInfo();
                    }
                    case 0: {
                        return new LockResourceImpl();
                    }
                    case 17: {
                        return new UnlockIfLeaseExpiredOperation();
                    }
                }
                return null;
            }
        };
    }
}

