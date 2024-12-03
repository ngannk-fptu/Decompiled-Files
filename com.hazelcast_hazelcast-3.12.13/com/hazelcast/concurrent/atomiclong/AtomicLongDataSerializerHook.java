/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong;

import com.hazelcast.concurrent.atomiclong.operations.AddAndGetOperation;
import com.hazelcast.concurrent.atomiclong.operations.AddBackupOperation;
import com.hazelcast.concurrent.atomiclong.operations.AlterAndGetOperation;
import com.hazelcast.concurrent.atomiclong.operations.AlterOperation;
import com.hazelcast.concurrent.atomiclong.operations.ApplyOperation;
import com.hazelcast.concurrent.atomiclong.operations.AtomicLongReplicationOperation;
import com.hazelcast.concurrent.atomiclong.operations.CompareAndSetOperation;
import com.hazelcast.concurrent.atomiclong.operations.GetAndAddOperation;
import com.hazelcast.concurrent.atomiclong.operations.GetAndAlterOperation;
import com.hazelcast.concurrent.atomiclong.operations.GetAndSetOperation;
import com.hazelcast.concurrent.atomiclong.operations.GetOperation;
import com.hazelcast.concurrent.atomiclong.operations.MergeBackupOperation;
import com.hazelcast.concurrent.atomiclong.operations.MergeOperation;
import com.hazelcast.concurrent.atomiclong.operations.SetBackupOperation;
import com.hazelcast.concurrent.atomiclong.operations.SetOperation;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public final class AtomicLongDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.atomic_long", -17);
    public static final int ADD_BACKUP = 0;
    public static final int ADD_AND_GET = 1;
    public static final int ALTER = 2;
    public static final int ALTER_AND_GET = 3;
    public static final int APPLY = 4;
    public static final int COMPARE_AND_SET = 5;
    public static final int GET = 6;
    public static final int GET_AND_SET = 7;
    public static final int GET_AND_ALTER = 8;
    public static final int GET_AND_ADD = 9;
    public static final int SET_OPERATION = 10;
    public static final int SET_BACKUP = 11;
    public static final int REPLICATION = 12;
    public static final int MERGE = 13;
    public static final int MERGE_BACKUP = 14;

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
                        return new AddBackupOperation();
                    }
                    case 1: {
                        return new AddAndGetOperation();
                    }
                    case 2: {
                        return new AlterOperation();
                    }
                    case 3: {
                        return new AlterAndGetOperation();
                    }
                    case 4: {
                        return new ApplyOperation();
                    }
                    case 5: {
                        return new CompareAndSetOperation();
                    }
                    case 6: {
                        return new GetOperation();
                    }
                    case 7: {
                        return new GetAndSetOperation();
                    }
                    case 8: {
                        return new GetAndAlterOperation();
                    }
                    case 9: {
                        return new GetAndAddOperation();
                    }
                    case 10: {
                        return new SetOperation();
                    }
                    case 11: {
                        return new SetBackupOperation();
                    }
                    case 12: {
                        return new AtomicLongReplicationOperation();
                    }
                    case 13: {
                        return new MergeOperation();
                    }
                    case 14: {
                        return new MergeBackupOperation();
                    }
                }
                return null;
            }
        };
    }
}

