/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl;

import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.ringbuffer.impl.ReadResultSetImpl;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.operations.AddAllBackupOperation;
import com.hazelcast.ringbuffer.impl.operations.AddAllOperation;
import com.hazelcast.ringbuffer.impl.operations.AddBackupOperation;
import com.hazelcast.ringbuffer.impl.operations.AddOperation;
import com.hazelcast.ringbuffer.impl.operations.GenericOperation;
import com.hazelcast.ringbuffer.impl.operations.MergeBackupOperation;
import com.hazelcast.ringbuffer.impl.operations.MergeOperation;
import com.hazelcast.ringbuffer.impl.operations.ReadManyOperation;
import com.hazelcast.ringbuffer.impl.operations.ReadOneOperation;
import com.hazelcast.ringbuffer.impl.operations.ReplicationOperation;

public class RingbufferDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.ringbuffer", -29);
    public static final int GENERIC_OPERATION = 1;
    public static final int ADD_BACKUP_OPERATION = 2;
    public static final int ADD_OPERATION = 3;
    public static final int READ_ONE_OPERATION = 4;
    public static final int REPLICATION_OPERATION = 5;
    public static final int READ_MANY_OPERATION = 6;
    public static final int ADD_ALL_OPERATION = 7;
    public static final int ADD_ALL_BACKUP_OPERATION = 8;
    public static final int READ_RESULT_SET = 9;
    public static final int RINGBUFFER_CONTAINER = 10;
    public static final int MERGE_OPERATION = 11;
    public static final int MERGE_BACKUP_OPERATION = 12;

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
                    case 2: {
                        return new AddBackupOperation();
                    }
                    case 3: {
                        return new AddOperation();
                    }
                    case 4: {
                        return new ReadOneOperation();
                    }
                    case 5: {
                        return new ReplicationOperation();
                    }
                    case 1: {
                        return new GenericOperation();
                    }
                    case 6: {
                        return new ReadManyOperation();
                    }
                    case 7: {
                        return new AddAllOperation();
                    }
                    case 8: {
                        return new AddAllBackupOperation();
                    }
                    case 9: {
                        return new ReadResultSetImpl();
                    }
                    case 10: {
                        return new RingbufferContainer();
                    }
                    case 11: {
                        return new MergeOperation();
                    }
                    case 12: {
                        return new MergeBackupOperation();
                    }
                }
                return null;
            }
        };
    }
}

