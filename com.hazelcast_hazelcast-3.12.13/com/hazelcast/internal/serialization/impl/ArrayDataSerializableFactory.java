/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.VersionedDataSerializableFactory;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.VersionAwareConstructorFunction;
import com.hazelcast.version.Version;

public final class ArrayDataSerializableFactory
implements VersionedDataSerializableFactory {
    private final ConstructorFunction<Integer, IdentifiedDataSerializable>[] constructors;
    private final int len;

    public ArrayDataSerializableFactory(ConstructorFunction<Integer, IdentifiedDataSerializable>[] ctorArray) {
        if (ctorArray == null || ctorArray.length <= 0) {
            throw new IllegalArgumentException("ConstructorFunction array cannot be null");
        }
        this.len = ctorArray.length;
        this.constructors = new ConstructorFunction[this.len];
        System.arraycopy(ctorArray, 0, this.constructors, 0, this.len);
    }

    @Override
    public IdentifiedDataSerializable create(int typeId) {
        if (typeId >= 0 && typeId < this.len) {
            ConstructorFunction<Integer, IdentifiedDataSerializable> factory = this.constructors[typeId];
            return factory != null ? factory.createNew(typeId) : null;
        }
        return null;
    }

    @Override
    public IdentifiedDataSerializable create(int typeId, Version version) {
        if (typeId >= 0 && typeId < this.len) {
            ConstructorFunction<Integer, IdentifiedDataSerializable> factory = this.constructors[typeId];
            if (factory == null) {
                return null;
            }
            if (factory instanceof VersionAwareConstructorFunction) {
                return (IdentifiedDataSerializable)((VersionAwareConstructorFunction)factory).createNew(typeId, version);
            }
            return factory.createNew(typeId);
        }
        return null;
    }
}

