/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.FieldType;
import java.util.Set;

public interface ClassDefinition {
    public FieldDefinition getField(String var1);

    public FieldDefinition getField(int var1);

    public boolean hasField(String var1);

    public Set<String> getFieldNames();

    public FieldType getFieldType(String var1);

    public int getFieldClassId(String var1);

    public int getFieldCount();

    public int getFactoryId();

    public int getClassId();

    public int getVersion();
}

