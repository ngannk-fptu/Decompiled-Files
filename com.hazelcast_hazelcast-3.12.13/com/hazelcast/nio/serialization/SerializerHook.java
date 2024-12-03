/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

import com.hazelcast.nio.serialization.Serializer;

public interface SerializerHook<T> {
    public Class<T> getSerializationType();

    public Serializer createSerializer();

    public boolean isOverwritable();
}

