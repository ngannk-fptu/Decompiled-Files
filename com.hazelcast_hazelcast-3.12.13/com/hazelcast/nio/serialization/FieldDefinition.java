/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

import com.hazelcast.nio.serialization.FieldType;

public interface FieldDefinition {
    public FieldType getType();

    public String getName();

    public int getIndex();

    public int getClassId();

    public int getFactoryId();

    public int getVersion();
}

