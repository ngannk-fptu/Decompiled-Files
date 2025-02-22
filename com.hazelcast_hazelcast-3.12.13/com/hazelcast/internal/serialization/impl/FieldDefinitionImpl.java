/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.FieldType;

public class FieldDefinitionImpl
implements FieldDefinition {
    private final int index;
    private final String fieldName;
    private final FieldType type;
    private final int classId;
    private final int factoryId;
    private final int version;

    public FieldDefinitionImpl(int index, String fieldName, FieldType type, int version) {
        this(index, fieldName, type, 0, 0, version);
    }

    public FieldDefinitionImpl(int index, String fieldName, FieldType type, int factoryId, int classId, int version) {
        this.classId = classId;
        this.type = type;
        this.fieldName = fieldName;
        this.index = index;
        this.factoryId = factoryId;
        this.version = version;
    }

    @Override
    public FieldType getType() {
        return this.type;
    }

    @Override
    public String getName() {
        return this.fieldName;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public int getFactoryId() {
        return this.factoryId;
    }

    @Override
    public int getClassId() {
        return this.classId;
    }

    @Override
    public int getVersion() {
        return this.version;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FieldDefinitionImpl that = (FieldDefinitionImpl)o;
        if (this.index != that.index) {
            return false;
        }
        if (this.classId != that.classId) {
            return false;
        }
        if (this.factoryId != that.factoryId) {
            return false;
        }
        if (this.version != that.version) {
            return false;
        }
        if (this.fieldName != null ? !this.fieldName.equals(that.fieldName) : that.fieldName != null) {
            return false;
        }
        return this.type == that.type;
    }

    public int hashCode() {
        int result = this.index;
        result = 31 * result + (this.fieldName != null ? this.fieldName.hashCode() : 0);
        result = 31 * result + (this.type != null ? this.type.hashCode() : 0);
        result = 31 * result + this.classId;
        result = 31 * result + this.factoryId;
        result = 31 * result + this.version;
        return result;
    }

    public String toString() {
        return "FieldDefinitionImpl{index=" + this.index + ", fieldName='" + this.fieldName + '\'' + ", type=" + (Object)((Object)this.type) + ", classId=" + this.classId + ", factoryId=" + this.factoryId + ", version=" + this.version + '}';
    }
}

