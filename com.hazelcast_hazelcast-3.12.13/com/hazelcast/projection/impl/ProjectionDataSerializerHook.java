/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.projection.impl;

import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.ArrayDataSerializableFactory;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.projection.impl.IdentityProjection;
import com.hazelcast.projection.impl.MultiAttributeProjection;
import com.hazelcast.projection.impl.SingleAttributeProjection;
import com.hazelcast.util.ConstructorFunction;

public final class ProjectionDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.projection", -42);
    public static final int SINGLE_ATTRIBUTE = 0;
    public static final int MULTI_ATTRIBUTE = 1;
    public static final int IDENTITY_PROJECTION = 2;
    private static final int LEN = 3;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        ConstructorFunction[] constructors = new ConstructorFunction[]{new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SingleAttributeProjection();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MultiAttributeProjection();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return IdentityProjection.INSTANCE;
            }
        }};
        return new ArrayDataSerializableFactory(constructors);
    }
}

