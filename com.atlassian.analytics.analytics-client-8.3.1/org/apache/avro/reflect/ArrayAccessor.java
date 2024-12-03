/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.reflect;

import java.io.IOException;
import java.util.Arrays;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.ResolvingDecoder;

class ArrayAccessor {
    ArrayAccessor() {
    }

    static void writeArray(boolean[] data, Encoder out) throws IOException {
        int size = data.length;
        out.setItemCount(size);
        for (boolean datum : data) {
            out.startItem();
            out.writeBoolean(datum);
        }
    }

    static void writeArray(short[] data, Encoder out) throws IOException {
        int size = data.length;
        out.setItemCount(size);
        for (short datum : data) {
            out.startItem();
            out.writeInt(datum);
        }
    }

    static void writeArray(char[] data, Encoder out) throws IOException {
        int size = data.length;
        out.setItemCount(size);
        for (char datum : data) {
            out.startItem();
            out.writeInt(datum);
        }
    }

    static void writeArray(int[] data, Encoder out) throws IOException {
        int size = data.length;
        out.setItemCount(size);
        for (int datum : data) {
            out.startItem();
            out.writeInt(datum);
        }
    }

    static void writeArray(long[] data, Encoder out) throws IOException {
        int size = data.length;
        out.setItemCount(size);
        for (long datum : data) {
            out.startItem();
            out.writeLong(datum);
        }
    }

    static void writeArray(float[] data, Encoder out) throws IOException {
        int size = data.length;
        out.setItemCount(size);
        for (float datum : data) {
            out.startItem();
            out.writeFloat(datum);
        }
    }

    static void writeArray(double[] data, Encoder out) throws IOException {
        int size = data.length;
        out.setItemCount(size);
        for (double datum : data) {
            out.startItem();
            out.writeDouble(datum);
        }
    }

    static Object readArray(Object array, Class<?> elementType, long l, ResolvingDecoder in) throws IOException {
        if (elementType == Integer.TYPE) {
            return ArrayAccessor.readArray((int[])array, l, in);
        }
        if (elementType == Long.TYPE) {
            return ArrayAccessor.readArray((long[])array, l, in);
        }
        if (elementType == Float.TYPE) {
            return ArrayAccessor.readArray((float[])array, l, in);
        }
        if (elementType == Double.TYPE) {
            return ArrayAccessor.readArray((double[])array, l, in);
        }
        if (elementType == Boolean.TYPE) {
            return ArrayAccessor.readArray((boolean[])array, l, in);
        }
        if (elementType == Character.TYPE) {
            return ArrayAccessor.readArray((char[])array, l, in);
        }
        if (elementType == Short.TYPE) {
            return ArrayAccessor.readArray((short[])array, l, in);
        }
        return null;
    }

    static boolean[] readArray(boolean[] array, long l, ResolvingDecoder in) throws IOException {
        int index = 0;
        do {
            int limit;
            if (array.length < (limit = index + (int)l)) {
                array = Arrays.copyOf(array, limit);
            }
            while (index < limit) {
                array[index] = in.readBoolean();
                ++index;
            }
        } while ((l = in.arrayNext()) > 0L);
        return array;
    }

    static int[] readArray(int[] array, long l, ResolvingDecoder in) throws IOException {
        int index = 0;
        do {
            int limit;
            if (array.length < (limit = index + (int)l)) {
                array = Arrays.copyOf(array, limit);
            }
            while (index < limit) {
                array[index] = in.readInt();
                ++index;
            }
        } while ((l = in.arrayNext()) > 0L);
        return array;
    }

    static short[] readArray(short[] array, long l, ResolvingDecoder in) throws IOException {
        int index = 0;
        do {
            int limit;
            if (array.length < (limit = index + (int)l)) {
                array = Arrays.copyOf(array, limit);
            }
            while (index < limit) {
                array[index] = (short)in.readInt();
                ++index;
            }
        } while ((l = in.arrayNext()) > 0L);
        return array;
    }

    static char[] readArray(char[] array, long l, ResolvingDecoder in) throws IOException {
        int index = 0;
        do {
            int limit;
            if (array.length < (limit = index + (int)l)) {
                array = Arrays.copyOf(array, limit);
            }
            while (index < limit) {
                array[index] = (char)in.readInt();
                ++index;
            }
        } while ((l = in.arrayNext()) > 0L);
        return array;
    }

    static long[] readArray(long[] array, long l, ResolvingDecoder in) throws IOException {
        int index = 0;
        do {
            int limit;
            if (array.length < (limit = index + (int)l)) {
                array = Arrays.copyOf(array, limit);
            }
            while (index < limit) {
                array[index] = in.readLong();
                ++index;
            }
        } while ((l = in.arrayNext()) > 0L);
        return array;
    }

    static float[] readArray(float[] array, long l, ResolvingDecoder in) throws IOException {
        int index = 0;
        do {
            int limit;
            if (array.length < (limit = index + (int)l)) {
                array = Arrays.copyOf(array, limit);
            }
            while (index < limit) {
                array[index] = in.readFloat();
                ++index;
            }
        } while ((l = in.arrayNext()) > 0L);
        return array;
    }

    static double[] readArray(double[] array, long l, ResolvingDecoder in) throws IOException {
        int index = 0;
        do {
            int limit;
            if (array.length < (limit = index + (int)l)) {
                array = Arrays.copyOf(array, limit);
            }
            while (index < limit) {
                array[index] = in.readDouble();
                ++index;
            }
        } while ((l = in.arrayNext()) > 0L);
        return array;
    }
}

