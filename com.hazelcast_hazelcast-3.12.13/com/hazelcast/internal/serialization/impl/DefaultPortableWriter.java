/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.impl.PortableSerializer;
import com.hazelcast.nio.BufferObjectDataOutput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.FieldType;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.Set;

public class DefaultPortableWriter
implements PortableWriter {
    private final PortableSerializer serializer;
    private final ClassDefinition cd;
    private final BufferObjectDataOutput out;
    private final int begin;
    private final int offset;
    private final Set<String> writtenFields;
    private boolean raw;

    DefaultPortableWriter(PortableSerializer serializer, BufferObjectDataOutput out, ClassDefinition cd) throws IOException {
        this.serializer = serializer;
        this.out = out;
        this.cd = cd;
        this.writtenFields = SetUtil.createHashSet(cd.getFieldCount());
        this.begin = out.position();
        out.writeZeroBytes(4);
        out.writeInt(cd.getFieldCount());
        this.offset = out.position();
        int fieldIndexesLength = (cd.getFieldCount() + 1) * 4;
        out.writeZeroBytes(fieldIndexesLength);
    }

    public int getVersion() {
        return this.cd.getVersion();
    }

    @Override
    public void writeInt(String fieldName, int value) throws IOException {
        this.setPosition(fieldName, FieldType.INT);
        this.out.writeInt(value);
    }

    @Override
    public void writeLong(String fieldName, long value) throws IOException {
        this.setPosition(fieldName, FieldType.LONG);
        this.out.writeLong(value);
    }

    @Override
    public void writeUTF(String fieldName, String str) throws IOException {
        this.setPosition(fieldName, FieldType.UTF);
        this.out.writeUTF(str);
    }

    @Override
    public void writeBoolean(String fieldName, boolean value) throws IOException {
        this.setPosition(fieldName, FieldType.BOOLEAN);
        this.out.writeBoolean(value);
    }

    @Override
    public void writeByte(String fieldName, byte value) throws IOException {
        this.setPosition(fieldName, FieldType.BYTE);
        this.out.writeByte(value);
    }

    @Override
    public void writeChar(String fieldName, int value) throws IOException {
        this.setPosition(fieldName, FieldType.CHAR);
        this.out.writeChar(value);
    }

    @Override
    public void writeDouble(String fieldName, double value) throws IOException {
        this.setPosition(fieldName, FieldType.DOUBLE);
        this.out.writeDouble(value);
    }

    @Override
    public void writeFloat(String fieldName, float value) throws IOException {
        this.setPosition(fieldName, FieldType.FLOAT);
        this.out.writeFloat(value);
    }

    @Override
    public void writeShort(String fieldName, short value) throws IOException {
        this.setPosition(fieldName, FieldType.SHORT);
        this.out.writeShort(value);
    }

    @Override
    public void writePortable(String fieldName, Portable portable) throws IOException {
        FieldDefinition fd = this.setPosition(fieldName, FieldType.PORTABLE);
        boolean isNull = portable == null;
        this.out.writeBoolean(isNull);
        this.out.writeInt(fd.getFactoryId());
        this.out.writeInt(fd.getClassId());
        if (!isNull) {
            this.checkPortableAttributes(fd, portable);
            this.serializer.writeInternal(this.out, portable);
        }
    }

    private void checkPortableAttributes(FieldDefinition fd, Portable portable) {
        if (fd.getFactoryId() != portable.getFactoryId()) {
            throw new HazelcastSerializationException("Wrong Portable type! Generic portable types are not supported!  Expected factory-id: " + fd.getFactoryId() + ", Actual factory-id: " + portable.getFactoryId());
        }
        if (fd.getClassId() != portable.getClassId()) {
            throw new HazelcastSerializationException("Wrong Portable type! Generic portable types are not supported! Expected class-id: " + fd.getClassId() + ", Actual class-id: " + portable.getClassId());
        }
    }

    @Override
    public void writeNullPortable(String fieldName, int factoryId, int classId) throws IOException {
        this.setPosition(fieldName, FieldType.PORTABLE);
        this.out.writeBoolean(true);
        this.out.writeInt(factoryId);
        this.out.writeInt(classId);
    }

    @Override
    public void writeByteArray(String fieldName, byte[] values) throws IOException {
        this.setPosition(fieldName, FieldType.BYTE_ARRAY);
        this.out.writeByteArray(values);
    }

    @Override
    public void writeBooleanArray(String fieldName, boolean[] booleans) throws IOException {
        this.setPosition(fieldName, FieldType.BOOLEAN_ARRAY);
        this.out.writeBooleanArray(booleans);
    }

    @Override
    public void writeCharArray(String fieldName, char[] values) throws IOException {
        this.setPosition(fieldName, FieldType.CHAR_ARRAY);
        this.out.writeCharArray(values);
    }

    @Override
    public void writeIntArray(String fieldName, int[] values) throws IOException {
        this.setPosition(fieldName, FieldType.INT_ARRAY);
        this.out.writeIntArray(values);
    }

    @Override
    public void writeLongArray(String fieldName, long[] values) throws IOException {
        this.setPosition(fieldName, FieldType.LONG_ARRAY);
        this.out.writeLongArray(values);
    }

    @Override
    public void writeDoubleArray(String fieldName, double[] values) throws IOException {
        this.setPosition(fieldName, FieldType.DOUBLE_ARRAY);
        this.out.writeDoubleArray(values);
    }

    @Override
    public void writeFloatArray(String fieldName, float[] values) throws IOException {
        this.setPosition(fieldName, FieldType.FLOAT_ARRAY);
        this.out.writeFloatArray(values);
    }

    @Override
    public void writeShortArray(String fieldName, short[] values) throws IOException {
        this.setPosition(fieldName, FieldType.SHORT_ARRAY);
        this.out.writeShortArray(values);
    }

    @Override
    public void writeUTFArray(String fieldName, String[] values) throws IOException {
        this.setPosition(fieldName, FieldType.UTF_ARRAY);
        this.out.writeUTFArray(values);
    }

    @Override
    public void writePortableArray(String fieldName, Portable[] portables) throws IOException {
        FieldDefinition fd = this.setPosition(fieldName, FieldType.PORTABLE_ARRAY);
        int len = portables == null ? -1 : portables.length;
        this.out.writeInt(len);
        this.out.writeInt(fd.getFactoryId());
        this.out.writeInt(fd.getClassId());
        if (len > 0) {
            int offset = this.out.position();
            this.out.writeZeroBytes(len * 4);
            for (int i = 0; i < len; ++i) {
                Portable portable = portables[i];
                this.checkPortableAttributes(fd, portable);
                int position = this.out.position();
                this.out.writeInt(offset + i * 4, position);
                this.serializer.writeInternal(this.out, portable);
            }
        }
    }

    private FieldDefinition setPosition(String fieldName, FieldType fieldType) throws IOException {
        if (this.raw) {
            throw new HazelcastSerializationException("Cannot write Portable fields after getRawDataOutput() is called!");
        }
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            throw new HazelcastSerializationException("Invalid field name: '" + fieldName + "' for ClassDefinition {id: " + this.cd.getClassId() + ", version: " + this.cd.getVersion() + "}");
        }
        if (!this.writtenFields.add(fieldName)) {
            throw new HazelcastSerializationException("Field '" + fieldName + "' has already been written!");
        }
        int pos = this.out.position();
        int index = fd.getIndex();
        this.out.writeInt(this.offset + index * 4, pos);
        this.out.writeShort(fieldName.length());
        this.out.writeBytes(fieldName);
        this.out.writeByte(fieldType.getId());
        return fd;
    }

    @Override
    public ObjectDataOutput getRawDataOutput() throws IOException {
        if (!this.raw) {
            int pos = this.out.position();
            int index = this.cd.getFieldCount();
            this.out.writeInt(this.offset + index * 4, pos);
        }
        this.raw = true;
        return this.out;
    }

    void end() throws IOException {
        int position = this.out.position();
        this.out.writeInt(this.begin, position);
    }
}

