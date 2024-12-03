/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.impl.VersionedObjectDataOutput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;
import java.nio.ByteOrder;

final class EmptyObjectDataOutput
extends VersionedObjectDataOutput
implements ObjectDataOutput {
    EmptyObjectDataOutput() {
    }

    @Override
    public void writeObject(Object object) throws IOException {
    }

    @Override
    public void writeData(Data data) throws IOException {
    }

    @Override
    public void write(int b) throws IOException {
    }

    @Override
    public void write(byte[] b) throws IOException {
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
    }

    @Override
    public void writeByte(int v) throws IOException {
    }

    @Override
    public void writeShort(int v) throws IOException {
    }

    @Override
    public void writeChar(int v) throws IOException {
    }

    @Override
    public void writeInt(int v) throws IOException {
    }

    @Override
    public void writeLong(long v) throws IOException {
    }

    @Override
    public void writeFloat(float v) throws IOException {
    }

    @Override
    public void writeDouble(double v) throws IOException {
    }

    @Override
    public void writeBytes(String s) throws IOException {
    }

    @Override
    public void writeChars(String s) throws IOException {
    }

    @Override
    public void writeUTF(String s) throws IOException {
    }

    @Override
    public void writeByteArray(byte[] value) throws IOException {
    }

    @Override
    public void writeBooleanArray(boolean[] booleans) throws IOException {
    }

    @Override
    public void writeCharArray(char[] chars) throws IOException {
    }

    @Override
    public void writeIntArray(int[] ints) throws IOException {
    }

    @Override
    public void writeLongArray(long[] longs) throws IOException {
    }

    @Override
    public void writeDoubleArray(double[] values) throws IOException {
    }

    @Override
    public void writeFloatArray(float[] values) throws IOException {
    }

    @Override
    public void writeShortArray(short[] values) throws IOException {
    }

    @Override
    public void writeUTFArray(String[] values) throws IOException {
    }

    @Override
    public byte[] toByteArray() {
        return this.toByteArray(0);
    }

    @Override
    public byte[] toByteArray(int padding) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public ByteOrder getByteOrder() {
        return ByteOrder.BIG_ENDIAN;
    }

    @Override
    public SerializationService getSerializationService() {
        return null;
    }
}

