/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.impl.DefaultPortableReader;
import com.hazelcast.internal.serialization.impl.PortableSerializer;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.FieldType;
import com.hazelcast.nio.serialization.Portable;
import java.io.IOException;

public class MorphingPortableReader
extends DefaultPortableReader {
    public MorphingPortableReader(PortableSerializer serializer, BufferObjectDataInput in, ClassDefinition cd) {
        super(serializer, in, cd);
    }

    @Override
    public int readInt(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return 0;
        }
        switch (fd.getType()) {
            case INT: {
                return super.readInt(fieldName);
            }
            case BYTE: {
                return super.readByte(fieldName);
            }
            case CHAR: {
                return super.readChar(fieldName);
            }
            case SHORT: {
                return super.readShort(fieldName);
            }
        }
        throw this.createIncompatibleClassChangeError(fd, FieldType.INT);
    }

    @Override
    public long readLong(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return 0L;
        }
        switch (fd.getType()) {
            case LONG: {
                return super.readLong(fieldName);
            }
            case INT: {
                return super.readInt(fieldName);
            }
            case BYTE: {
                return super.readByte(fieldName);
            }
            case CHAR: {
                return super.readChar(fieldName);
            }
            case SHORT: {
                return super.readShort(fieldName);
            }
        }
        throw this.createIncompatibleClassChangeError(fd, FieldType.LONG);
    }

    @Override
    public String readUTF(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return null;
        }
        this.validateTypeCompatibility(fd, FieldType.UTF);
        return super.readUTF(fieldName);
    }

    @Override
    public boolean readBoolean(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return false;
        }
        this.validateTypeCompatibility(fd, FieldType.BOOLEAN);
        return super.readBoolean(fieldName);
    }

    @Override
    public byte readByte(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return 0;
        }
        this.validateTypeCompatibility(fd, FieldType.BYTE);
        return super.readByte(fieldName);
    }

    @Override
    public char readChar(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return '\u0000';
        }
        this.validateTypeCompatibility(fd, FieldType.CHAR);
        return super.readChar(fieldName);
    }

    @Override
    public double readDouble(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return 0.0;
        }
        switch (fd.getType()) {
            case DOUBLE: {
                return super.readDouble(fieldName);
            }
            case LONG: {
                return super.readLong(fieldName);
            }
            case FLOAT: {
                return super.readFloat(fieldName);
            }
            case INT: {
                return super.readInt(fieldName);
            }
            case BYTE: {
                return super.readByte(fieldName);
            }
            case CHAR: {
                return super.readChar(fieldName);
            }
            case SHORT: {
                return super.readShort(fieldName);
            }
        }
        throw this.createIncompatibleClassChangeError(fd, FieldType.DOUBLE);
    }

    @Override
    public float readFloat(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return 0.0f;
        }
        switch (fd.getType()) {
            case FLOAT: {
                return super.readFloat(fieldName);
            }
            case INT: {
                return super.readInt(fieldName);
            }
            case BYTE: {
                return super.readByte(fieldName);
            }
            case CHAR: {
                return super.readChar(fieldName);
            }
            case SHORT: {
                return super.readShort(fieldName);
            }
        }
        throw this.createIncompatibleClassChangeError(fd, FieldType.FLOAT);
    }

    @Override
    public short readShort(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return 0;
        }
        switch (fd.getType()) {
            case SHORT: {
                return super.readShort(fieldName);
            }
            case BYTE: {
                return super.readByte(fieldName);
            }
        }
        throw this.createIncompatibleClassChangeError(fd, FieldType.SHORT);
    }

    @Override
    public byte[] readByteArray(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return null;
        }
        this.validateTypeCompatibility(fd, FieldType.BYTE_ARRAY);
        return super.readByteArray(fieldName);
    }

    @Override
    public boolean[] readBooleanArray(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return null;
        }
        this.validateTypeCompatibility(fd, FieldType.BOOLEAN_ARRAY);
        return super.readBooleanArray(fieldName);
    }

    @Override
    public char[] readCharArray(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return null;
        }
        this.validateTypeCompatibility(fd, FieldType.CHAR_ARRAY);
        return super.readCharArray(fieldName);
    }

    @Override
    public int[] readIntArray(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return null;
        }
        this.validateTypeCompatibility(fd, FieldType.INT_ARRAY);
        return super.readIntArray(fieldName);
    }

    @Override
    public long[] readLongArray(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return null;
        }
        this.validateTypeCompatibility(fd, FieldType.LONG_ARRAY);
        return super.readLongArray(fieldName);
    }

    @Override
    public double[] readDoubleArray(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return null;
        }
        this.validateTypeCompatibility(fd, FieldType.DOUBLE_ARRAY);
        return super.readDoubleArray(fieldName);
    }

    @Override
    public float[] readFloatArray(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return null;
        }
        this.validateTypeCompatibility(fd, FieldType.FLOAT_ARRAY);
        return super.readFloatArray(fieldName);
    }

    @Override
    public short[] readShortArray(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return null;
        }
        this.validateTypeCompatibility(fd, FieldType.SHORT_ARRAY);
        return super.readShortArray(fieldName);
    }

    @Override
    public String[] readUTFArray(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return null;
        }
        this.validateTypeCompatibility(fd, FieldType.UTF_ARRAY);
        return super.readUTFArray(fieldName);
    }

    @Override
    public Portable readPortable(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return null;
        }
        this.validateTypeCompatibility(fd, FieldType.PORTABLE);
        return super.readPortable(fieldName);
    }

    @Override
    public Portable[] readPortableArray(String fieldName) throws IOException {
        FieldDefinition fd = this.cd.getField(fieldName);
        if (fd == null) {
            return null;
        }
        this.validateTypeCompatibility(fd, FieldType.PORTABLE_ARRAY);
        return super.readPortableArray(fieldName);
    }

    private void validateTypeCompatibility(FieldDefinition fd, FieldType expectedType) {
        if (fd.getType() != expectedType) {
            throw this.createIncompatibleClassChangeError(fd, expectedType);
        }
    }

    private IncompatibleClassChangeError createIncompatibleClassChangeError(FieldDefinition fd, FieldType expectedType) {
        return new IncompatibleClassChangeError("Incompatible to read " + (Object)((Object)expectedType) + " from " + (Object)((Object)fd.getType()) + " while reading field: " + fd.getName() + " on " + this.cd);
    }
}

