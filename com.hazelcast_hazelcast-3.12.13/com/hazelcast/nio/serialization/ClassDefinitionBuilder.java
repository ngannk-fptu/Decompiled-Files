/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

import com.hazelcast.internal.serialization.impl.ClassDefinitionImpl;
import com.hazelcast.internal.serialization.impl.FieldDefinitionImpl;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.FieldType;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.spi.annotation.PrivateApi;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ClassDefinitionBuilder {
    private final int factoryId;
    private final int classId;
    private final int version;
    private final List<FieldDefinitionImpl> fieldDefinitions = new ArrayList<FieldDefinitionImpl>();
    private final Set<String> addedFieldNames = new HashSet<String>();
    private int index;
    private boolean done;

    public ClassDefinitionBuilder(int factoryId, int classId) {
        this.factoryId = factoryId;
        this.classId = classId;
        this.version = 0;
    }

    public ClassDefinitionBuilder(int factoryId, int classId, int version) {
        this.factoryId = factoryId;
        this.classId = classId;
        this.version = version;
    }

    public ClassDefinitionBuilder addIntField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.INT, this.version));
        return this;
    }

    public ClassDefinitionBuilder addLongField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.LONG, this.version));
        return this;
    }

    public ClassDefinitionBuilder addUTFField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.UTF, this.version));
        return this;
    }

    public ClassDefinitionBuilder addBooleanField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.BOOLEAN, this.version));
        return this;
    }

    public ClassDefinitionBuilder addByteField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.BYTE, this.version));
        return this;
    }

    public ClassDefinitionBuilder addBooleanArrayField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.BOOLEAN_ARRAY, this.version));
        return this;
    }

    public ClassDefinitionBuilder addCharField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.CHAR, this.version));
        return this;
    }

    public ClassDefinitionBuilder addDoubleField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.DOUBLE, this.version));
        return this;
    }

    public ClassDefinitionBuilder addFloatField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.FLOAT, this.version));
        return this;
    }

    public ClassDefinitionBuilder addShortField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.SHORT, this.version));
        return this;
    }

    public ClassDefinitionBuilder addByteArrayField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.BYTE_ARRAY, this.version));
        return this;
    }

    public ClassDefinitionBuilder addCharArrayField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.CHAR_ARRAY, this.version));
        return this;
    }

    public ClassDefinitionBuilder addIntArrayField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.INT_ARRAY, this.version));
        return this;
    }

    public ClassDefinitionBuilder addLongArrayField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.LONG_ARRAY, this.version));
        return this;
    }

    public ClassDefinitionBuilder addDoubleArrayField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.DOUBLE_ARRAY, this.version));
        return this;
    }

    public ClassDefinitionBuilder addFloatArrayField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.FLOAT_ARRAY, this.version));
        return this;
    }

    public ClassDefinitionBuilder addShortArrayField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.SHORT_ARRAY, this.version));
        return this;
    }

    public ClassDefinitionBuilder addUTFArrayField(String fieldName) {
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.UTF_ARRAY, this.version));
        return this;
    }

    public ClassDefinitionBuilder addPortableField(String fieldName, ClassDefinition def) {
        if (def.getClassId() == 0) {
            throw new IllegalArgumentException("Portable class ID cannot be zero!");
        }
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.PORTABLE, def.getFactoryId(), def.getClassId(), def.getVersion()));
        return this;
    }

    public ClassDefinitionBuilder addPortableArrayField(String fieldName, ClassDefinition classDefinition) {
        if (classDefinition.getClassId() == 0) {
            throw new IllegalArgumentException("Portable class ID cannot be zero!");
        }
        this.check(fieldName);
        this.fieldDefinitions.add(new FieldDefinitionImpl(this.index++, fieldName, FieldType.PORTABLE_ARRAY, classDefinition.getFactoryId(), classDefinition.getClassId(), classDefinition.getVersion()));
        return this;
    }

    @PrivateApi
    public ClassDefinitionBuilder addField(FieldDefinitionImpl fieldDefinition) {
        if (this.index != fieldDefinition.getIndex()) {
            throw new IllegalArgumentException("Invalid field index");
        }
        this.check(fieldDefinition.getName());
        ++this.index;
        this.fieldDefinitions.add(fieldDefinition);
        return this;
    }

    public ClassDefinition build() {
        this.done = true;
        ClassDefinitionImpl cd = new ClassDefinitionImpl(this.factoryId, this.classId, this.version);
        for (FieldDefinitionImpl fd : this.fieldDefinitions) {
            cd.addFieldDef(fd);
        }
        return cd;
    }

    private void check(String fieldName) {
        if (!this.addedFieldNames.add(fieldName)) {
            throw new HazelcastSerializationException("Field with field name : " + fieldName + " already exists");
        }
        if (this.done) {
            throw new HazelcastSerializationException("ClassDefinition is already built for " + this.classId);
        }
    }

    public int getFactoryId() {
        return this.factoryId;
    }

    public int getClassId() {
        return this.classId;
    }

    public int getVersion() {
        return this.version;
    }
}

