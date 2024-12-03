/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.flakeidgen.impl;

import com.hazelcast.flakeidgen.impl.NewIdBatchOperation;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public final class FlakeIdGeneratorDataSerializerHook
implements DataSerializerHook {
    static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.flake_id_generator", -46);
    static final int NEW_ID_BATCH_OPERATION = 0;

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
                        return new NewIdBatchOperation();
                    }
                }
                return null;
            }
        };
    }
}

