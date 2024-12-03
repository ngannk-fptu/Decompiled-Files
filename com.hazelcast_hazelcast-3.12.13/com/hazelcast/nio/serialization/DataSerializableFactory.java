/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public interface DataSerializableFactory {
    public IdentifiedDataSerializable create(int var1);
}

