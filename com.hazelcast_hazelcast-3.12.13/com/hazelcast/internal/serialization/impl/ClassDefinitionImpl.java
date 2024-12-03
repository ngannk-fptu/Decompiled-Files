/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.impl.FieldDefinitionImpl;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.FieldType;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ClassDefinitionImpl
implements ClassDefinition {
    private final int factoryId;
    private final int classId;
    private int version = -1;
    private final Map<String, FieldDefinition> fieldDefinitionsMap = new LinkedHashMap<String, FieldDefinition>();

    public ClassDefinitionImpl(int factoryId, int classId, int version) {
        this.factoryId = factoryId;
        this.classId = classId;
        this.version = version;
    }

    public void addFieldDef(FieldDefinitionImpl fd) {
        this.fieldDefinitionsMap.put(fd.getName(), fd);
    }

    @Override
    public FieldDefinition getField(String name) {
        return this.fieldDefinitionsMap.get(name);
    }

    @Override
    public FieldDefinition getField(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= this.fieldDefinitionsMap.size()) {
            throw new IndexOutOfBoundsException("Index: " + fieldIndex + ", Size: " + this.fieldDefinitionsMap.size());
        }
        for (FieldDefinition fieldDefinition : this.fieldDefinitionsMap.values()) {
            if (fieldIndex != fieldDefinition.getIndex()) continue;
            return fieldDefinition;
        }
        throw new IndexOutOfBoundsException("Index: " + fieldIndex + ", Size: " + this.fieldDefinitionsMap.size());
    }

    @Override
    public boolean hasField(String fieldName) {
        return this.fieldDefinitionsMap.containsKey(fieldName);
    }

    @Override
    public Set<String> getFieldNames() {
        return new HashSet<String>(this.fieldDefinitionsMap.keySet());
    }

    @Override
    public FieldType getFieldType(String fieldName) {
        FieldDefinition fd = this.getField(fieldName);
        if (fd != null) {
            return fd.getType();
        }
        throw new IllegalArgumentException("Unknown field: " + fieldName);
    }

    @Override
    public int getFieldClassId(String fieldName) {
        FieldDefinition fd = this.getField(fieldName);
        if (fd != null) {
            return fd.getClassId();
        }
        throw new IllegalArgumentException("Unknown field: " + fieldName);
    }

    @Override
    public int getFieldCount() {
        return this.fieldDefinitionsMap.size();
    }

    @Override
    public final int getFactoryId() {
        return this.factoryId;
    }

    @Override
    public final int getClassId() {
        return this.classId;
    }

    @Override
    public final int getVersion() {
        return this.version;
    }

    void setVersionIfNotSet(int version) {
        if (this.getVersion() < 0) {
            this.version = version;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClassDefinitionImpl that = (ClassDefinitionImpl)o;
        if (this.factoryId != that.factoryId) {
            return false;
        }
        if (this.classId != that.classId) {
            return false;
        }
        if (this.version != that.version) {
            return false;
        }
        return this.fieldDefinitionsMap.equals(that.fieldDefinitionsMap);
    }

    public int hashCode() {
        int result = this.classId;
        result = 31 * result + this.version;
        return result;
    }

    public String toString() {
        return "ClassDefinition{factoryId=" + this.factoryId + ", classId=" + this.classId + ", version=" + this.version + ", fieldDefinitions=" + this.fieldDefinitionsMap.values() + '}';
    }
}

