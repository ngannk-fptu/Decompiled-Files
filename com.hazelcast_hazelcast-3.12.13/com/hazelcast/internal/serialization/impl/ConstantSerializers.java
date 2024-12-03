/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.ByteArraySerializer;
import com.hazelcast.nio.serialization.StreamSerializer;
import java.io.IOException;

public final class ConstantSerializers {
    private ConstantSerializers() {
    }

    private static abstract class SingletonSerializer<T>
    implements StreamSerializer<T> {
        private SingletonSerializer() {
        }

        @Override
        public void destroy() {
        }
    }

    public static final class StringArraySerializer
    extends SingletonSerializer<String[]> {
        @Override
        public int getTypeId() {
            return -20;
        }

        @Override
        public String[] read(ObjectDataInput in) throws IOException {
            return in.readUTFArray();
        }

        @Override
        public void write(ObjectDataOutput out, String[] obj) throws IOException {
            out.writeUTFArray(obj);
        }
    }

    public static final class DoubleArraySerializer
    extends SingletonSerializer<double[]> {
        @Override
        public int getTypeId() {
            return -19;
        }

        @Override
        public double[] read(ObjectDataInput in) throws IOException {
            return in.readDoubleArray();
        }

        @Override
        public void write(ObjectDataOutput out, double[] obj) throws IOException {
            out.writeDoubleArray(obj);
        }
    }

    public static final class FloatArraySerializer
    extends SingletonSerializer<float[]> {
        @Override
        public int getTypeId() {
            return -18;
        }

        @Override
        public float[] read(ObjectDataInput in) throws IOException {
            return in.readFloatArray();
        }

        @Override
        public void write(ObjectDataOutput out, float[] obj) throws IOException {
            out.writeFloatArray(obj);
        }
    }

    public static final class LongArraySerializer
    extends SingletonSerializer<long[]> {
        @Override
        public int getTypeId() {
            return -17;
        }

        @Override
        public long[] read(ObjectDataInput in) throws IOException {
            return in.readLongArray();
        }

        @Override
        public void write(ObjectDataOutput out, long[] obj) throws IOException {
            out.writeLongArray(obj);
        }
    }

    public static final class IntegerArraySerializer
    extends SingletonSerializer<int[]> {
        @Override
        public int getTypeId() {
            return -16;
        }

        @Override
        public int[] read(ObjectDataInput in) throws IOException {
            return in.readIntArray();
        }

        @Override
        public void write(ObjectDataOutput out, int[] obj) throws IOException {
            out.writeIntArray(obj);
        }
    }

    public static final class ShortArraySerializer
    extends SingletonSerializer<short[]> {
        @Override
        public int getTypeId() {
            return -15;
        }

        @Override
        public short[] read(ObjectDataInput in) throws IOException {
            return in.readShortArray();
        }

        @Override
        public void write(ObjectDataOutput out, short[] obj) throws IOException {
            out.writeShortArray(obj);
        }
    }

    public static final class CharArraySerializer
    extends SingletonSerializer<char[]> {
        @Override
        public int getTypeId() {
            return -14;
        }

        @Override
        public char[] read(ObjectDataInput in) throws IOException {
            return in.readCharArray();
        }

        @Override
        public void write(ObjectDataOutput out, char[] obj) throws IOException {
            out.writeCharArray(obj);
        }
    }

    public static final class BooleanArraySerializer
    extends SingletonSerializer<boolean[]> {
        @Override
        public int getTypeId() {
            return -13;
        }

        @Override
        public boolean[] read(ObjectDataInput in) throws IOException {
            return in.readBooleanArray();
        }

        @Override
        public void write(ObjectDataOutput out, boolean[] obj) throws IOException {
            out.writeBooleanArray(obj);
        }
    }

    public static final class TheByteArraySerializer
    implements ByteArraySerializer<byte[]> {
        @Override
        public int getTypeId() {
            return -12;
        }

        @Override
        public byte[] write(byte[] object) throws IOException {
            return object;
        }

        @Override
        public byte[] read(byte[] buffer) throws IOException {
            return buffer;
        }

        @Override
        public void destroy() {
        }
    }

    public static final class StringSerializer
    extends SingletonSerializer<String> {
        @Override
        public int getTypeId() {
            return -11;
        }

        @Override
        public String read(ObjectDataInput in) throws IOException {
            return in.readUTF();
        }

        @Override
        public void write(ObjectDataOutput out, String obj) throws IOException {
            out.writeUTF(obj);
        }
    }

    public static final class DoubleSerializer
    extends SingletonSerializer<Double> {
        @Override
        public int getTypeId() {
            return -10;
        }

        @Override
        public Double read(ObjectDataInput in) throws IOException {
            return in.readDouble();
        }

        @Override
        public void write(ObjectDataOutput out, Double obj) throws IOException {
            out.writeDouble(obj);
        }
    }

    public static final class FloatSerializer
    extends SingletonSerializer<Float> {
        @Override
        public int getTypeId() {
            return -9;
        }

        @Override
        public Float read(ObjectDataInput in) throws IOException {
            return Float.valueOf(in.readFloat());
        }

        @Override
        public void write(ObjectDataOutput out, Float obj) throws IOException {
            out.writeFloat(obj.floatValue());
        }
    }

    public static final class LongSerializer
    extends SingletonSerializer<Long> {
        @Override
        public int getTypeId() {
            return -8;
        }

        @Override
        public Long read(ObjectDataInput in) throws IOException {
            return in.readLong();
        }

        @Override
        public void write(ObjectDataOutput out, Long obj) throws IOException {
            out.writeLong(obj);
        }
    }

    public static final class IntegerSerializer
    extends SingletonSerializer<Integer> {
        @Override
        public int getTypeId() {
            return -7;
        }

        @Override
        public Integer read(ObjectDataInput in) throws IOException {
            return in.readInt();
        }

        @Override
        public void write(ObjectDataOutput out, Integer obj) throws IOException {
            out.writeInt(obj);
        }
    }

    public static final class ShortSerializer
    extends SingletonSerializer<Short> {
        @Override
        public int getTypeId() {
            return -6;
        }

        @Override
        public Short read(ObjectDataInput in) throws IOException {
            return in.readShort();
        }

        @Override
        public void write(ObjectDataOutput out, Short obj) throws IOException {
            out.writeShort(obj.shortValue());
        }
    }

    public static final class CharSerializer
    extends SingletonSerializer<Character> {
        @Override
        public int getTypeId() {
            return -5;
        }

        @Override
        public Character read(ObjectDataInput in) throws IOException {
            return Character.valueOf(in.readChar());
        }

        @Override
        public void write(ObjectDataOutput out, Character obj) throws IOException {
            out.writeChar(obj.charValue());
        }
    }

    public static final class BooleanSerializer
    extends SingletonSerializer<Boolean> {
        @Override
        public int getTypeId() {
            return -4;
        }

        @Override
        public void write(ObjectDataOutput out, Boolean obj) throws IOException {
            out.write(obj != false ? 1 : 0);
        }

        @Override
        public Boolean read(ObjectDataInput in) throws IOException {
            return in.readByte() != 0;
        }
    }

    public static final class ByteSerializer
    extends SingletonSerializer<Byte> {
        @Override
        public int getTypeId() {
            return -3;
        }

        @Override
        public Byte read(ObjectDataInput in) throws IOException {
            return in.readByte();
        }

        @Override
        public void write(ObjectDataOutput out, Byte obj) throws IOException {
            out.writeByte(obj.byteValue());
        }
    }

    public static final class NullSerializer
    extends SingletonSerializer<Object> {
        @Override
        public int getTypeId() {
            return 0;
        }

        @Override
        public Object read(ObjectDataInput in) throws IOException {
            return null;
        }

        @Override
        public void write(ObjectDataOutput out, Object obj) throws IOException {
        }
    }
}

