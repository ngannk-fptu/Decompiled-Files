/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization.impl;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.version.Version;

public interface VersionedDataSerializableFactory
extends DataSerializableFactory {
    public IdentifiedDataSerializable create(int var1, Version var2);
}

