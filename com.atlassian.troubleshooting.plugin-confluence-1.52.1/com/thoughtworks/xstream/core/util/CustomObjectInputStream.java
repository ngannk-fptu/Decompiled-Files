/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.io.StreamException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.util.Map;

public class CustomObjectInputStream
extends ObjectInputStream {
    private FastStack callbacks = new FastStack(1);
    private final ClassLoaderReference classLoaderReference;
    private static final String DATA_HOLDER_KEY = CustomObjectInputStream.class.getName();

    public static CustomObjectInputStream getInstance(DataHolder whereFrom, StreamCallback callback) {
        return CustomObjectInputStream.getInstance(whereFrom, callback, (ClassLoader)null);
    }

    public static synchronized CustomObjectInputStream getInstance(DataHolder whereFrom, StreamCallback callback, ClassLoader classLoader) {
        return CustomObjectInputStream.getInstance(whereFrom, callback, new ClassLoaderReference(classLoader));
    }

    public static synchronized CustomObjectInputStream getInstance(DataHolder whereFrom, StreamCallback callback, ClassLoaderReference classLoaderReference) {
        try {
            CustomObjectInputStream result = (CustomObjectInputStream)whereFrom.get(DATA_HOLDER_KEY);
            if (result == null) {
                result = new CustomObjectInputStream(callback, classLoaderReference);
                whereFrom.put(DATA_HOLDER_KEY, result);
            } else {
                result.pushCallback(callback);
            }
            return result;
        }
        catch (SecurityException e) {
            throw new ObjectAccessException("Cannot create CustomObjectStream", e);
        }
        catch (IOException e) {
            throw new StreamException("Cannot create CustomObjectStream", e);
        }
    }

    public CustomObjectInputStream(StreamCallback callback, ClassLoaderReference classLoaderReference) throws IOException, SecurityException {
        this.callbacks.push(callback);
        this.classLoaderReference = classLoaderReference;
    }

    public CustomObjectInputStream(StreamCallback callback, ClassLoader classLoader) throws IOException, SecurityException {
        this(callback, new ClassLoaderReference(classLoader));
    }

    public void pushCallback(StreamCallback callback) {
        this.callbacks.push(callback);
    }

    public StreamCallback popCallback() {
        return (StreamCallback)this.callbacks.pop();
    }

    public StreamCallback peekCallback() {
        return (StreamCallback)this.callbacks.peek();
    }

    protected Class resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = this.classLoaderReference.getReference();
        if (classLoader == null) {
            return super.resolveClass(desc);
        }
        return Class.forName(desc.getName(), false, classLoader);
    }

    public void defaultReadObject() throws IOException {
        this.peekCallback().defaultReadObject();
    }

    protected Object readObjectOverride() throws IOException {
        return this.peekCallback().readFromStream();
    }

    public Object readUnshared() throws IOException, ClassNotFoundException {
        return this.readObject();
    }

    public boolean readBoolean() throws IOException {
        return (Boolean)this.peekCallback().readFromStream();
    }

    public byte readByte() throws IOException {
        return (Byte)this.peekCallback().readFromStream();
    }

    public int readUnsignedByte() throws IOException {
        return ((Byte)this.peekCallback().readFromStream()).intValue() & 0xFF;
    }

    public int readInt() throws IOException {
        return (Integer)this.peekCallback().readFromStream();
    }

    public char readChar() throws IOException {
        return ((Character)this.peekCallback().readFromStream()).charValue();
    }

    public float readFloat() throws IOException {
        return ((Float)this.peekCallback().readFromStream()).floatValue();
    }

    public double readDouble() throws IOException {
        return (Double)this.peekCallback().readFromStream();
    }

    public long readLong() throws IOException {
        return (Long)this.peekCallback().readFromStream();
    }

    public short readShort() throws IOException {
        return (Short)this.peekCallback().readFromStream();
    }

    public int readUnsignedShort() throws IOException {
        return ((Short)this.peekCallback().readFromStream()).intValue() & 0xFFFF;
    }

    public String readUTF() throws IOException {
        return (String)this.peekCallback().readFromStream();
    }

    public void readFully(byte[] buf) throws IOException {
        this.readFully(buf, 0, buf.length);
    }

    public void readFully(byte[] buf, int off, int len) throws IOException {
        byte[] b = (byte[])this.peekCallback().readFromStream();
        System.arraycopy(b, 0, buf, off, len);
    }

    public int read() throws IOException {
        return this.readUnsignedByte();
    }

    public int read(byte[] buf, int off, int len) throws IOException {
        byte[] b = (byte[])this.peekCallback().readFromStream();
        if (b.length != len) {
            throw new StreamCorruptedException("Expected " + len + " bytes from stream, got " + b.length);
        }
        System.arraycopy(b, 0, buf, off, len);
        return len;
    }

    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    public ObjectInputStream.GetField readFields() throws IOException {
        return new CustomGetField(this.peekCallback().readFieldsFromStream());
    }

    public void registerValidation(ObjectInputValidation validation, int priority) throws NotActiveException, InvalidObjectException {
        this.peekCallback().registerValidation(validation, priority);
    }

    public void close() throws IOException {
        this.peekCallback().close();
    }

    public int available() {
        throw new UnsupportedOperationException();
    }

    public String readLine() {
        throw new UnsupportedOperationException();
    }

    public int skipBytes(int len) {
        throw new UnsupportedOperationException();
    }

    public long skip(long n) {
        throw new UnsupportedOperationException();
    }

    public void mark(int readlimit) {
        throw new UnsupportedOperationException();
    }

    public void reset() {
        throw new UnsupportedOperationException();
    }

    public boolean markSupported() {
        return false;
    }

    private class CustomGetField
    extends ObjectInputStream.GetField {
        private Map fields;

        public CustomGetField(Map fields) {
            this.fields = fields;
        }

        public ObjectStreamClass getObjectStreamClass() {
            throw new UnsupportedOperationException();
        }

        private Object get(String name) {
            return this.fields.get(name);
        }

        public boolean defaulted(String name) {
            return !this.fields.containsKey(name);
        }

        public byte get(String name, byte val) {
            return this.defaulted(name) ? val : (Byte)this.get(name);
        }

        public char get(String name, char val) {
            return this.defaulted(name) ? val : ((Character)this.get(name)).charValue();
        }

        public double get(String name, double val) {
            return this.defaulted(name) ? val : (Double)this.get(name);
        }

        public float get(String name, float val) {
            return this.defaulted(name) ? val : ((Float)this.get(name)).floatValue();
        }

        public int get(String name, int val) {
            return this.defaulted(name) ? val : (Integer)this.get(name);
        }

        public long get(String name, long val) {
            return this.defaulted(name) ? val : (Long)this.get(name);
        }

        public short get(String name, short val) {
            return this.defaulted(name) ? val : (Short)this.get(name);
        }

        public boolean get(String name, boolean val) {
            return this.defaulted(name) ? val : (Boolean)this.get(name);
        }

        public Object get(String name, Object val) {
            return this.defaulted(name) ? val : this.get(name);
        }
    }

    public static interface StreamCallback {
        public Object readFromStream() throws IOException;

        public Map readFieldsFromStream() throws IOException;

        public void defaultReadObject() throws IOException;

        public void registerValidation(ObjectInputValidation var1, int var2) throws NotActiveException, InvalidObjectException;

        public void close() throws IOException;
    }
}

