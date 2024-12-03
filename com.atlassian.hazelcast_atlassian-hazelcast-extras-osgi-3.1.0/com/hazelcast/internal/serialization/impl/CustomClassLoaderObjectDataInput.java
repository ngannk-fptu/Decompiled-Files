/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.internal.serialization.InternalSerializationService
 *  com.hazelcast.internal.serialization.impl.VersionedObjectDataInput
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.serialization.Data
 *  com.hazelcast.version.Version
 *  javax.annotation.Nonnull
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.VersionedObjectDataInput;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.version.Version;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import javax.annotation.Nonnull;

public class CustomClassLoaderObjectDataInput
extends VersionedObjectDataInput {
    private final ClassLoader classLoader;
    private final ObjectDataInput delegate;
    private final InputStream delegateInput;

    public CustomClassLoaderObjectDataInput(ClassLoader classLoader, ObjectDataInput delegate) {
        this.classLoader = classLoader;
        this.delegate = delegate;
        this.delegateInput = (InputStream)delegate;
    }

    public int available() throws IOException {
        return this.delegateInput.available();
    }

    public void close() throws IOException {
        this.delegateInput.close();
    }

    public ByteOrder getByteOrder() {
        return this.delegate.getByteOrder();
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public InternalSerializationService getSerializationService() {
        return this.delegate.getSerializationService();
    }

    public Version getVersion() {
        return this.delegate.getVersion();
    }

    public void setVersion(Version version) {
        if (this.delegate instanceof VersionedObjectDataInput) {
            ((VersionedObjectDataInput)this.delegate).setVersion(version);
        }
    }

    public void mark(int readlimit) {
        this.delegateInput.mark(readlimit);
    }

    public boolean markSupported() {
        return this.delegateInput.markSupported();
    }

    public int read() throws IOException {
        return this.delegateInput.read();
    }

    public int read(@Nonnull byte[] b) throws IOException {
        return this.delegateInput.read(b);
    }

    public int read(@Nonnull byte[] b, int off, int len) throws IOException {
        return this.delegateInput.read(b, off, len);
    }

    public boolean readBoolean() throws IOException {
        return this.delegate.readBoolean();
    }

    public boolean[] readBooleanArray() throws IOException {
        return this.delegate.readBooleanArray();
    }

    public byte readByte() throws IOException {
        return this.delegate.readByte();
    }

    public byte[] readByteArray() throws IOException {
        return this.delegate.readByteArray();
    }

    public char readChar() throws IOException {
        return this.delegate.readChar();
    }

    public char[] readCharArray() throws IOException {
        return this.delegate.readCharArray();
    }

    public Data readData() throws IOException {
        return this.delegate.readData();
    }

    public <T> T readDataAsObject() throws IOException {
        return (T)this.delegate.readDataAsObject();
    }

    public double readDouble() throws IOException {
        return this.delegate.readDouble();
    }

    public double[] readDoubleArray() throws IOException {
        return this.delegate.readDoubleArray();
    }

    public float readFloat() throws IOException {
        return this.delegate.readFloat();
    }

    public float[] readFloatArray() throws IOException {
        return this.delegate.readFloatArray();
    }

    public void readFully(@Nonnull byte[] b) throws IOException {
        this.delegate.readFully(b);
    }

    public void readFully(@Nonnull byte[] b, int off, int len) throws IOException {
        this.delegate.readFully(b, off, len);
    }

    public int readInt() throws IOException {
        return this.delegate.readInt();
    }

    public int[] readIntArray() throws IOException {
        return this.delegate.readIntArray();
    }

    public String readLine() throws IOException {
        return this.delegate.readLine();
    }

    public long readLong() throws IOException {
        return this.delegate.readLong();
    }

    public long[] readLongArray() throws IOException {
        return this.delegate.readLongArray();
    }

    public <T> T readObject() throws IOException {
        return (T)this.delegate.readObject();
    }

    public <T> T readObject(Class aClass) throws IOException {
        return (T)this.delegate.readObject(aClass);
    }

    public short readShort() throws IOException {
        return this.delegate.readShort();
    }

    public short[] readShortArray() throws IOException {
        return this.delegate.readShortArray();
    }

    @Nonnull
    public String readUTF() throws IOException {
        return this.delegate.readUTF();
    }

    public String[] readUTFArray() throws IOException {
        return this.delegate.readUTFArray();
    }

    public int readUnsignedByte() throws IOException {
        return this.delegate.readUnsignedByte();
    }

    public int readUnsignedShort() throws IOException {
        return this.delegate.readUnsignedShort();
    }

    public void reset() throws IOException {
        this.delegateInput.reset();
    }

    public long skip(long n) throws IOException {
        return this.delegateInput.skip(n);
    }

    public int skipBytes(int n) throws IOException {
        return this.delegate.skipBytes(n);
    }
}

