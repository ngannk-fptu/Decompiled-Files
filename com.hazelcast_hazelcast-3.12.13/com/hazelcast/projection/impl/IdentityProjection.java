/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.projection.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.projection.Projection;
import com.hazelcast.projection.impl.ProjectionDataSerializerHook;

public final class IdentityProjection<I>
extends Projection<I, I>
implements IdentifiedDataSerializable {
    public static final IdentityProjection<Object> INSTANCE = new IdentityProjection();

    private IdentityProjection() {
    }

    @Override
    public I transform(I input) {
        return input;
    }

    @Override
    public int getFactoryId() {
        return ProjectionDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public void writeData(ObjectDataOutput out) {
    }

    @Override
    public void readData(ObjectDataInput in) {
    }
}

