/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference;

import com.hazelcast.concurrent.atomicreference.operations.AlterAndGetOperation;
import com.hazelcast.concurrent.atomicreference.operations.AlterOperation;
import com.hazelcast.concurrent.atomicreference.operations.ApplyOperation;
import com.hazelcast.concurrent.atomicreference.operations.AtomicReferenceReplicationOperation;
import com.hazelcast.concurrent.atomicreference.operations.CompareAndSetOperation;
import com.hazelcast.concurrent.atomicreference.operations.ContainsOperation;
import com.hazelcast.concurrent.atomicreference.operations.GetAndAlterOperation;
import com.hazelcast.concurrent.atomicreference.operations.GetAndSetOperation;
import com.hazelcast.concurrent.atomicreference.operations.GetOperation;
import com.hazelcast.concurrent.atomicreference.operations.IsNullOperation;
import com.hazelcast.concurrent.atomicreference.operations.MergeBackupOperation;
import com.hazelcast.concurrent.atomicreference.operations.MergeOperation;
import com.hazelcast.concurrent.atomicreference.operations.SetAndGetOperation;
import com.hazelcast.concurrent.atomicreference.operations.SetBackupOperation;
import com.hazelcast.concurrent.atomicreference.operations.SetOperation;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public final class AtomicReferenceDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.atomic_reference", -21);
    public static final int ALTER_AND_GET = 0;
    public static final int ALTER = 1;
    public static final int APPLY = 2;
    public static final int COMPARE_AND_SET = 3;
    public static final int CONTAINS = 4;
    public static final int GET_AND_ALTER = 5;
    public static final int GET_AND_SET = 6;
    public static final int GET = 7;
    public static final int IS_NULL = 8;
    public static final int SET_AND_GET = 9;
    public static final int SET_BACKUP = 10;
    public static final int SET = 11;
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
                        return new AlterAndGetOperation();
                    }
                    case 1: {
                        return new AlterOperation();
                    }
                    case 2: {
                        return new ApplyOperation();
                    }
                    case 3: {
                        return new CompareAndSetOperation();
                    }
                    case 4: {
                        return new ContainsOperation();
                    }
                    case 5: {
                        return new GetAndAlterOperation();
                    }
                    case 6: {
                        return new GetAndSetOperation();
                    }
                    case 7: {
                        return new GetOperation();
                    }
                    case 8: {
                        return new IsNullOperation();
                    }
                    case 9: {
                        return new SetAndGetOperation();
                    }
                    case 10: {
                        return new SetBackupOperation();
                    }
                    case 11: {
                        return new SetOperation();
                    }
                    case 12: {
                        return new AtomicReferenceReplicationOperation();
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

