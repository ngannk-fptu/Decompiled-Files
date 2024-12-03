/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt;

import com.hazelcast.crdt.pncounter.PNCounterImpl;
import com.hazelcast.crdt.pncounter.PNCounterReplicationOperation;
import com.hazelcast.crdt.pncounter.operations.AddOperation;
import com.hazelcast.crdt.pncounter.operations.CRDTTimestampedLong;
import com.hazelcast.crdt.pncounter.operations.GetOperation;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public final class CRDTDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.pn_counter", -48);
    public static final int PN_COUNTER_REPLICATION = 1;
    public static final int PN_COUNTER = 2;
    public static final int PN_COUNTER_ADD_OPERATION = 3;
    public static final int PN_COUNTER_GET_OPERATION = 4;
    public static final int CRDT_TIMESTAMPED_LONG = 5;

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
                        return new PNCounterReplicationOperation();
                    }
                    case 2: {
                        return new PNCounterImpl();
                    }
                    case 3: {
                        return new AddOperation();
                    }
                    case 4: {
                        return new GetOperation();
                    }
                    case 5: {
                        return new CRDTTimestampedLong();
                    }
                }
                return null;
            }
        };
    }
}

