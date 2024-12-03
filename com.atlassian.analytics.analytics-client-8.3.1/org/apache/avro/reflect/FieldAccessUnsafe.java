/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.reflect;

import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.AvroEncode;
import org.apache.avro.reflect.CustomEncoding;
import org.apache.avro.reflect.FieldAccess;
import org.apache.avro.reflect.FieldAccessor;
import org.apache.avro.reflect.Stringable;
import sun.misc.Unsafe;

class FieldAccessUnsafe
extends FieldAccess {
    private static final Unsafe UNSAFE;

    FieldAccessUnsafe() {
    }

    @Override
    protected FieldAccessor getAccessor(Field field) {
        AvroEncode enc = field.getAnnotation(AvroEncode.class);
        if (enc != null) {
            try {
                return new UnsafeCustomEncodedField(field, enc.using().getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
            }
            catch (Exception e) {
                throw new AvroRuntimeException("Could not instantiate custom Encoding");
            }
        }
        Class<?> c = field.getType();
        if (c == Integer.TYPE) {
            return new UnsafeIntField(field);
        }
        if (c == Long.TYPE) {
            return new UnsafeLongField(field);
        }
        if (c == Byte.TYPE) {
            return new UnsafeByteField(field);
        }
        if (c == Float.TYPE) {
            return new UnsafeFloatField(field);
        }
        if (c == Double.TYPE) {
            return new UnsafeDoubleField(field);
        }
        if (c == Character.TYPE) {
            return new UnsafeCharField(field);
        }
        if (c == Boolean.TYPE) {
            return new UnsafeBooleanField(field);
        }
        if (c == Short.TYPE) {
            return new UnsafeShortField(field);
        }
        return new UnsafeObjectField(field);
    }

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe)theUnsafe.get(null);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static final class UnsafeCustomEncodedField
    extends UnsafeCachedField {
        private CustomEncoding<?> encoding;

        UnsafeCustomEncodedField(Field f, CustomEncoding<?> encoding) {
            super(f);
            this.encoding = encoding;
        }

        @Override
        protected Object get(Object object) throws IllegalAccessException {
            return UNSAFE.getObject(object, this.offset);
        }

        @Override
        protected void set(Object object, Object value) throws IllegalAccessException, IOException {
            UNSAFE.putObject(object, this.offset, value);
        }

        @Override
        protected void read(Object object, Decoder in) throws IOException {
            UNSAFE.putObject(object, this.offset, this.encoding.read(in));
        }

        @Override
        protected void write(Object object, Encoder out) throws IOException {
            this.encoding.write(UNSAFE.getObject(object, this.offset), out);
        }

        @Override
        protected boolean isCustomEncoded() {
            return true;
        }
    }

    static final class UnsafeObjectField
    extends UnsafeCachedField {
        protected UnsafeObjectField(Field f) {
            super(f);
        }

        @Override
        protected void set(Object object, Object value) {
            UNSAFE.putObject(object, this.offset, value);
        }

        @Override
        protected Object get(Object object) {
            return UNSAFE.getObject(object, this.offset);
        }

        @Override
        protected boolean supportsIO() {
            return false;
        }
    }

    static final class UnsafeDoubleField
    extends UnsafeCachedField {
        protected UnsafeDoubleField(Field f) {
            super(f);
        }

        @Override
        protected void set(Object object, Object value) {
            UNSAFE.putDouble(object, this.offset, (Double)value);
        }

        @Override
        protected Object get(Object object) {
            return UNSAFE.getDouble(object, this.offset);
        }

        @Override
        protected void read(Object object, Decoder in) throws IOException {
            UNSAFE.putDouble(object, this.offset, in.readDouble());
        }

        @Override
        protected void write(Object object, Encoder out) throws IOException {
            out.writeDouble(UNSAFE.getDouble(object, this.offset));
        }
    }

    static final class UnsafeLongField
    extends UnsafeCachedField {
        protected UnsafeLongField(Field f) {
            super(f);
        }

        @Override
        protected void set(Object object, Object value) {
            UNSAFE.putLong(object, this.offset, (Long)value);
        }

        @Override
        protected Object get(Object object) {
            return UNSAFE.getLong(object, this.offset);
        }

        @Override
        protected void read(Object object, Decoder in) throws IOException {
            UNSAFE.putLong(object, this.offset, in.readLong());
        }

        @Override
        protected void write(Object object, Encoder out) throws IOException {
            out.writeLong(UNSAFE.getLong(object, this.offset));
        }
    }

    static final class UnsafeCharField
    extends UnsafeCachedField {
        protected UnsafeCharField(Field f) {
            super(f);
        }

        @Override
        protected void set(Object object, Object value) {
            UNSAFE.putChar(object, this.offset, ((Character)value).charValue());
        }

        @Override
        protected Object get(Object object) {
            return Character.valueOf(UNSAFE.getChar(object, this.offset));
        }

        @Override
        protected void read(Object object, Decoder in) throws IOException {
            UNSAFE.putChar(object, this.offset, (char)in.readInt());
        }

        @Override
        protected void write(Object object, Encoder out) throws IOException {
            out.writeInt(UNSAFE.getChar(object, this.offset));
        }
    }

    static final class UnsafeBooleanField
    extends UnsafeCachedField {
        protected UnsafeBooleanField(Field f) {
            super(f);
        }

        @Override
        protected void set(Object object, Object value) {
            UNSAFE.putBoolean(object, this.offset, (Boolean)value);
        }

        @Override
        protected Object get(Object object) {
            return UNSAFE.getBoolean(object, this.offset);
        }

        @Override
        protected void read(Object object, Decoder in) throws IOException {
            UNSAFE.putBoolean(object, this.offset, in.readBoolean());
        }

        @Override
        protected void write(Object object, Encoder out) throws IOException {
            out.writeBoolean(UNSAFE.getBoolean(object, this.offset));
        }
    }

    static final class UnsafeByteField
    extends UnsafeCachedField {
        protected UnsafeByteField(Field f) {
            super(f);
        }

        @Override
        protected void set(Object object, Object value) {
            UNSAFE.putByte(object, this.offset, (Byte)value);
        }

        @Override
        protected Object get(Object object) {
            return UNSAFE.getByte(object, this.offset);
        }

        @Override
        protected void read(Object object, Decoder in) throws IOException {
            UNSAFE.putByte(object, this.offset, (byte)in.readInt());
        }

        @Override
        protected void write(Object object, Encoder out) throws IOException {
            out.writeInt(UNSAFE.getByte(object, this.offset));
        }
    }

    static final class UnsafeShortField
    extends UnsafeCachedField {
        protected UnsafeShortField(Field f) {
            super(f);
        }

        @Override
        protected void set(Object object, Object value) {
            UNSAFE.putShort(object, this.offset, (Short)value);
        }

        @Override
        protected Object get(Object object) {
            return UNSAFE.getShort(object, this.offset);
        }

        @Override
        protected void read(Object object, Decoder in) throws IOException {
            UNSAFE.putShort(object, this.offset, (short)in.readInt());
        }

        @Override
        protected void write(Object object, Encoder out) throws IOException {
            out.writeInt(UNSAFE.getShort(object, this.offset));
        }
    }

    static final class UnsafeFloatField
    extends UnsafeCachedField {
        protected UnsafeFloatField(Field f) {
            super(f);
        }

        @Override
        protected void set(Object object, Object value) {
            UNSAFE.putFloat(object, this.offset, ((Float)value).floatValue());
        }

        @Override
        protected Object get(Object object) {
            return Float.valueOf(UNSAFE.getFloat(object, this.offset));
        }

        @Override
        protected void read(Object object, Decoder in) throws IOException {
            UNSAFE.putFloat(object, this.offset, in.readFloat());
        }

        @Override
        protected void write(Object object, Encoder out) throws IOException {
            out.writeFloat(UNSAFE.getFloat(object, this.offset));
        }
    }

    static final class UnsafeIntField
    extends UnsafeCachedField {
        UnsafeIntField(Field f) {
            super(f);
        }

        @Override
        protected void set(Object object, Object value) {
            UNSAFE.putInt(object, this.offset, (Integer)value);
        }

        @Override
        protected Object get(Object object) {
            return UNSAFE.getInt(object, this.offset);
        }

        @Override
        protected void read(Object object, Decoder in) throws IOException {
            UNSAFE.putInt(object, this.offset, in.readInt());
        }

        @Override
        protected void write(Object object, Encoder out) throws IOException {
            out.writeInt(UNSAFE.getInt(object, this.offset));
        }
    }

    static abstract class UnsafeCachedField
    extends FieldAccessor {
        protected final long offset;
        protected Field field;
        protected final boolean isStringable;

        UnsafeCachedField(Field f) {
            this.offset = UNSAFE.objectFieldOffset(f);
            this.field = f;
            this.isStringable = f.isAnnotationPresent(Stringable.class);
        }

        @Override
        protected Field getField() {
            return this.field;
        }

        @Override
        protected boolean supportsIO() {
            return true;
        }

        @Override
        protected boolean isStringable() {
            return this.isStringable;
        }
    }
}

