/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.PortableContext;
import com.hazelcast.internal.serialization.impl.EmptyObjectDataOutput;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableWriter;
import java.io.IOException;

final class ClassDefinitionWriter
implements PortableWriter {
    private final PortableContext context;
    private final ClassDefinitionBuilder builder;

    ClassDefinitionWriter(PortableContext context, int factoryId, int classId, int version) {
        this.context = context;
        this.builder = new ClassDefinitionBuilder(factoryId, classId, version);
    }

    ClassDefinitionWriter(PortableContext context, ClassDefinitionBuilder builder) {
        this.context = context;
        this.builder = builder;
    }

    @Override
    public void writeInt(String fieldName, int value) {
        this.builder.addIntField(fieldName);
    }

    @Override
    public void writeLong(String fieldName, long value) {
        this.builder.addLongField(fieldName);
    }

    @Override
    public void writeUTF(String fieldName, String str) {
        this.builder.addUTFField(fieldName);
    }

    @Override
    public void writeBoolean(String fieldName, boolean value) throws IOException {
        this.builder.addBooleanField(fieldName);
    }

    @Override
    public void writeByte(String fieldName, byte value) throws IOException {
        this.builder.addByteField(fieldName);
    }

    @Override
    public void writeChar(String fieldName, int value) throws IOException {
        this.builder.addCharField(fieldName);
    }

    @Override
    public void writeDouble(String fieldName, double value) throws IOException {
        this.builder.addDoubleField(fieldName);
    }

    @Override
    public void writeFloat(String fieldName, float value) throws IOException {
        this.builder.addFloatField(fieldName);
    }

    @Override
    public void writeShort(String fieldName, short value) throws IOException {
        this.builder.addShortField(fieldName);
    }

    @Override
    public void writeByteArray(String fieldName, byte[] bytes) throws IOException {
        this.builder.addByteArrayField(fieldName);
    }

    @Override
    public void writeBooleanArray(String fieldName, boolean[] booleans) throws IOException {
        this.builder.addBooleanArrayField(fieldName);
    }

    @Override
    public void writeCharArray(String fieldName, char[] chars) throws IOException {
        this.builder.addCharArrayField(fieldName);
    }

    @Override
    public void writeIntArray(String fieldName, int[] ints) throws IOException {
        this.builder.addIntArrayField(fieldName);
    }

    @Override
    public void writeLongArray(String fieldName, long[] longs) throws IOException {
        this.builder.addLongArrayField(fieldName);
    }

    @Override
    public void writeDoubleArray(String fieldName, double[] values) throws IOException {
        this.builder.addDoubleArrayField(fieldName);
    }

    @Override
    public void writeFloatArray(String fieldName, float[] values) throws IOException {
        this.builder.addFloatArrayField(fieldName);
    }

    @Override
    public void writeShortArray(String fieldName, short[] values) throws IOException {
        this.builder.addShortArrayField(fieldName);
    }

    @Override
    public void writeUTFArray(String fieldName, String[] values) throws IOException {
        this.builder.addUTFArrayField(fieldName);
    }

    @Override
    public void writePortable(String fieldName, Portable portable) throws IOException {
        if (portable == null) {
            throw new HazelcastSerializationException("Cannot write null portable without explicitly registering class definition!");
        }
        int version = SerializationUtil.getPortableVersion(portable, this.context.getVersion());
        ClassDefinition nestedClassDef = this.createNestedClassDef(portable, new ClassDefinitionBuilder(portable.getFactoryId(), portable.getClassId(), version));
        this.builder.addPortableField(fieldName, nestedClassDef);
    }

    @Override
    public void writeNullPortable(String fieldName, int factoryId, int classId) throws IOException {
        ClassDefinition nestedClassDef = this.context.lookupClassDefinition(factoryId, classId, this.context.getVersion());
        if (nestedClassDef == null) {
            throw new HazelcastSerializationException("Cannot write null portable without explicitly registering class definition!");
        }
        this.builder.addPortableField(fieldName, nestedClassDef);
    }

    @Override
    public void writePortableArray(String fieldName, Portable[] portables) throws IOException {
        if (portables == null || portables.length == 0) {
            throw new HazelcastSerializationException("Cannot write null portable array without explicitly registering class definition!");
        }
        Portable p = portables[0];
        int classId = p.getClassId();
        for (int i = 1; i < portables.length; ++i) {
            if (portables[i].getClassId() == classId) continue;
            throw new IllegalArgumentException("Detected different class-ids in portable array!");
        }
        int version = SerializationUtil.getPortableVersion(p, this.context.getVersion());
        ClassDefinition nestedClassDef = this.createNestedClassDef(p, new ClassDefinitionBuilder(p.getFactoryId(), classId, version));
        this.builder.addPortableArrayField(fieldName, nestedClassDef);
    }

    @Override
    public ObjectDataOutput getRawDataOutput() {
        return new EmptyObjectDataOutput();
    }

    private ClassDefinition createNestedClassDef(Portable portable, ClassDefinitionBuilder nestedBuilder) throws IOException {
        ClassDefinitionWriter writer = new ClassDefinitionWriter(this.context, nestedBuilder);
        portable.writePortable(writer);
        return this.context.registerClassDefinition(nestedBuilder.build());
    }

    ClassDefinition registerAndGet() {
        ClassDefinition cd = this.builder.build();
        return this.context.registerClassDefinition(cd);
    }
}

