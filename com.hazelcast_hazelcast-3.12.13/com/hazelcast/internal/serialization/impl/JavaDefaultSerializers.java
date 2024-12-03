/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.ClassNameFilter;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.StreamSerializer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class JavaDefaultSerializers {
    private JavaDefaultSerializers() {
    }

    private static final class ExtendedGZipOutputStream
    extends GZIPOutputStream {
        private ExtendedGZipOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        private void closeDeflater() {
            this.def.end();
        }
    }

    private static final class ExtendedGZipInputStream
    extends GZIPInputStream {
        private static final int GZIP_TRAILER_SIZE = 8;

        private ExtendedGZipInputStream(InputStream in) throws IOException {
            super(in);
            assert (in instanceof BufferObjectDataInput) : "Unexpected input: " + in;
        }

        private void pushBackUnconsumedBytes() {
            int remaining = this.inf.getRemaining();
            BufferObjectDataInput bufferedInput = (BufferObjectDataInput)((Object)this.in);
            int position = bufferedInput.position();
            int rewindBack = Math.max(0, remaining - 8);
            int newPosition = position - rewindBack;
            bufferedInput.position(newPosition);
        }

        private void closeInflater() {
            this.inf.end();
        }
    }

    private static abstract class SingletonSerializer<T>
    implements StreamSerializer<T> {
        private SingletonSerializer() {
        }

        @Override
        public void destroy() {
        }
    }

    public static final class HazelcastJsonValueSerializer
    extends SingletonSerializer<HazelcastJsonValue> {
        @Override
        public void write(ObjectDataOutput out, HazelcastJsonValue object) throws IOException {
            out.writeUTF(object.toString());
        }

        @Override
        public HazelcastJsonValue read(ObjectDataInput in) throws IOException {
            return new HazelcastJsonValue(in.readUTF());
        }

        @Override
        public int getTypeId() {
            return -130;
        }
    }

    public static final class EnumSerializer
    extends SingletonSerializer<Enum> {
        @Override
        public int getTypeId() {
            return -25;
        }

        @Override
        public void write(ObjectDataOutput out, Enum obj) throws IOException {
            String name = obj.getDeclaringClass().getName();
            out.writeUTF(name);
            out.writeUTF(obj.name());
        }

        @Override
        public Enum read(ObjectDataInput in) throws IOException {
            Class<?> clazz;
            String clazzName = in.readUTF();
            try {
                clazz = ClassLoaderUtil.loadClass(in.getClassLoader(), clazzName);
            }
            catch (ClassNotFoundException e) {
                throw new HazelcastSerializationException("Failed to deserialize enum: " + clazzName, e);
            }
            String name = in.readUTF();
            return Enum.valueOf(clazz, name);
        }
    }

    public static final class ClassSerializer
    extends SingletonSerializer<Class> {
        @Override
        public int getTypeId() {
            return -21;
        }

        @Override
        public Class read(ObjectDataInput in) throws IOException {
            try {
                return ClassLoaderUtil.loadClass(in.getClassLoader(), in.readUTF());
            }
            catch (ClassNotFoundException e) {
                throw new HazelcastSerializationException(e);
            }
        }

        @Override
        public void write(ObjectDataOutput out, Class obj) throws IOException {
            out.writeUTF(obj.getName());
        }
    }

    public static final class DateSerializer
    extends SingletonSerializer<Date> {
        @Override
        public int getTypeId() {
            return -22;
        }

        @Override
        public Date read(ObjectDataInput in) throws IOException {
            return new Date(in.readLong());
        }

        @Override
        public void write(ObjectDataOutput out, Date obj) throws IOException {
            out.writeLong(obj.getTime());
        }
    }

    public static final class BigDecimalSerializer
    extends SingletonSerializer<BigDecimal> {
        final BigIntegerSerializer bigIntegerSerializer = new BigIntegerSerializer();

        @Override
        public int getTypeId() {
            return -24;
        }

        @Override
        public BigDecimal read(ObjectDataInput in) throws IOException {
            BigInteger bigInt = this.bigIntegerSerializer.read(in);
            int scale = in.readInt();
            return new BigDecimal(bigInt, scale);
        }

        @Override
        public void write(ObjectDataOutput out, BigDecimal obj) throws IOException {
            BigInteger bigInt = obj.unscaledValue();
            int scale = obj.scale();
            this.bigIntegerSerializer.write(out, bigInt);
            out.writeInt(scale);
        }
    }

    public static final class BigIntegerSerializer
    extends SingletonSerializer<BigInteger> {
        @Override
        public int getTypeId() {
            return -23;
        }

        @Override
        public BigInteger read(ObjectDataInput in) throws IOException {
            byte[] bytes = new byte[in.readInt()];
            in.readFully(bytes);
            return new BigInteger(bytes);
        }

        @Override
        public void write(ObjectDataOutput out, BigInteger obj) throws IOException {
            byte[] bytes = obj.toByteArray();
            out.writeInt(bytes.length);
            out.write(bytes);
        }
    }

    public static final class ExternalizableSerializer
    extends SingletonSerializer<Externalizable> {
        private final boolean gzipEnabled;
        private final ClassNameFilter classFilter;

        public ExternalizableSerializer(boolean gzipEnabled, ClassNameFilter classFilter) {
            this.gzipEnabled = gzipEnabled;
            this.classFilter = classFilter;
        }

        @Override
        public int getTypeId() {
            return -101;
        }

        @Override
        public Externalizable read(ObjectDataInput in) throws IOException {
            String className = in.readUTF();
            try {
                if (this.gzipEnabled) {
                    return this.readGzipped((InputStream)((Object)in), className, in.getClassLoader());
                }
                return this.read((InputStream)((Object)in), className, in.getClassLoader());
            }
            catch (Exception e) {
                throw new HazelcastSerializationException("Problem while reading Externalizable class: " + className + ", exception: " + e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Externalizable readGzipped(InputStream in, String className, ClassLoader classLoader) throws Exception {
            ExtendedGZipInputStream gzip = new ExtendedGZipInputStream(in);
            try {
                Externalizable external = this.read(gzip, className, classLoader);
                gzip.pushBackUnconsumedBytes();
                Externalizable externalizable = external;
                return externalizable;
            }
            finally {
                gzip.closeInflater();
            }
        }

        private Externalizable read(InputStream in, String className, ClassLoader classLoader) throws Exception {
            if (this.classFilter != null) {
                this.classFilter.filter(className);
            }
            Externalizable ds = (Externalizable)ClassLoaderUtil.newInstance(classLoader, className);
            ObjectInputStream objectInputStream = IOUtil.newObjectInputStream(classLoader, this.classFilter, in);
            ds.readExternal(objectInputStream);
            return ds;
        }

        @Override
        public void write(ObjectDataOutput out, Externalizable obj) throws IOException {
            out.writeUTF(obj.getClass().getName());
            if (this.gzipEnabled) {
                this.writeGzipped((OutputStream)((Object)out), obj);
            } else {
                this.write((OutputStream)((Object)out), obj);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void writeGzipped(OutputStream out, Externalizable obj) throws IOException {
            ExtendedGZipOutputStream gzip = new ExtendedGZipOutputStream(out);
            try {
                this.write(gzip, obj);
                gzip.finish();
            }
            finally {
                gzip.closeDeflater();
            }
        }

        private void write(OutputStream outputStream, Externalizable obj) throws IOException {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            obj.writeExternal(objectOutputStream);
            objectOutputStream.flush();
        }
    }

    public static final class JavaSerializer
    extends SingletonSerializer<Object> {
        private final boolean shared;
        private final boolean gzipEnabled;
        private final ClassNameFilter classFilter;

        public JavaSerializer(boolean shared, boolean gzipEnabled, ClassNameFilter classFilter) {
            this.shared = shared;
            this.gzipEnabled = gzipEnabled;
            this.classFilter = classFilter;
        }

        @Override
        public int getTypeId() {
            return -100;
        }

        @Override
        public Object read(ObjectDataInput in) throws IOException {
            if (this.gzipEnabled) {
                return this.readGzipped((InputStream)((Object)in), in.getClassLoader());
            }
            return this.read((InputStream)((Object)in), in.getClassLoader());
        }

        private Object read(InputStream in, ClassLoader classLoader) throws IOException {
            try {
                ObjectInputStream objectInputStream = IOUtil.newObjectInputStream(classLoader, this.classFilter, in);
                if (this.shared) {
                    return objectInputStream.readObject();
                }
                return objectInputStream.readUnshared();
            }
            catch (ClassNotFoundException e) {
                throw new HazelcastSerializationException(e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Object readGzipped(InputStream in, ClassLoader classLoader) throws IOException {
            ExtendedGZipInputStream gzip = new ExtendedGZipInputStream(in);
            try {
                Object obj = this.read(gzip, classLoader);
                gzip.pushBackUnconsumedBytes();
                Object object = obj;
                return object;
            }
            finally {
                gzip.closeInflater();
            }
        }

        @Override
        @SuppressFBWarnings(value={"OS_OPEN_STREAM"})
        public void write(ObjectDataOutput out, Object obj) throws IOException {
            if (this.gzipEnabled) {
                this.writeGzipped((OutputStream)((Object)out), obj);
            } else {
                this.write((OutputStream)((Object)out), obj);
            }
        }

        private void write(OutputStream out, Object obj) throws IOException {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
            if (this.shared) {
                objectOutputStream.writeObject(obj);
            } else {
                objectOutputStream.writeUnshared(obj);
            }
            objectOutputStream.flush();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void writeGzipped(OutputStream out, Object obj) throws IOException {
            ExtendedGZipOutputStream gzip = new ExtendedGZipOutputStream(out);
            try {
                this.write(gzip, obj);
                gzip.finish();
            }
            finally {
                gzip.closeDeflater();
            }
        }
    }
}

