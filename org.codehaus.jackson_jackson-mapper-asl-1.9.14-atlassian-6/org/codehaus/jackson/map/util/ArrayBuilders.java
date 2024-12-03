/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.codehaus.jackson.map.util.PrimitiveArrayBuilder;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ArrayBuilders {
    BooleanBuilder _booleanBuilder = null;
    ByteBuilder _byteBuilder = null;
    ShortBuilder _shortBuilder = null;
    IntBuilder _intBuilder = null;
    LongBuilder _longBuilder = null;
    FloatBuilder _floatBuilder = null;
    DoubleBuilder _doubleBuilder = null;

    public BooleanBuilder getBooleanBuilder() {
        if (this._booleanBuilder == null) {
            this._booleanBuilder = new BooleanBuilder();
        }
        return this._booleanBuilder;
    }

    public ByteBuilder getByteBuilder() {
        if (this._byteBuilder == null) {
            this._byteBuilder = new ByteBuilder();
        }
        return this._byteBuilder;
    }

    public ShortBuilder getShortBuilder() {
        if (this._shortBuilder == null) {
            this._shortBuilder = new ShortBuilder();
        }
        return this._shortBuilder;
    }

    public IntBuilder getIntBuilder() {
        if (this._intBuilder == null) {
            this._intBuilder = new IntBuilder();
        }
        return this._intBuilder;
    }

    public LongBuilder getLongBuilder() {
        if (this._longBuilder == null) {
            this._longBuilder = new LongBuilder();
        }
        return this._longBuilder;
    }

    public FloatBuilder getFloatBuilder() {
        if (this._floatBuilder == null) {
            this._floatBuilder = new FloatBuilder();
        }
        return this._floatBuilder;
    }

    public DoubleBuilder getDoubleBuilder() {
        if (this._doubleBuilder == null) {
            this._doubleBuilder = new DoubleBuilder();
        }
        return this._doubleBuilder;
    }

    public static <T> HashSet<T> arrayToSet(T[] elements) {
        HashSet<T> result = new HashSet<T>();
        if (elements != null) {
            for (T elem : elements) {
                result.add(elem);
            }
        }
        return result;
    }

    public static <T> List<T> addToList(List<T> list, T element) {
        if (list == null) {
            list = new ArrayList<T>();
        }
        list.add(element);
        return list;
    }

    public static <T> T[] insertInList(T[] array, T element) {
        int len = array.length;
        Object[] result = (Object[])Array.newInstance(array.getClass().getComponentType(), len + 1);
        if (len > 0) {
            System.arraycopy(array, 0, result, 1, len);
        }
        result[0] = element;
        return result;
    }

    public static <T> T[] insertInListNoDup(T[] array, T element) {
        int len = array.length;
        for (int ix = 0; ix < len; ++ix) {
            if (array[ix] != element) continue;
            if (ix == 0) {
                return array;
            }
            Object[] result = (Object[])Array.newInstance(array.getClass().getComponentType(), len);
            System.arraycopy(array, 0, result, 1, ix);
            array[0] = element;
            return result;
        }
        Object[] result = (Object[])Array.newInstance(array.getClass().getComponentType(), len + 1);
        if (len > 0) {
            System.arraycopy(array, 0, result, 1, len);
        }
        result[0] = element;
        return result;
    }

    public static <T> Iterator<T> arrayAsIterator(T[] array) {
        return new ArrayIterator<T>(array);
    }

    public static <T> Iterable<T> arrayAsIterable(T[] array) {
        return new ArrayIterator<T>(array);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class ArrayIterator<T>
    implements Iterator<T>,
    Iterable<T> {
        private final T[] _array;
        private int _index;

        public ArrayIterator(T[] array) {
            this._array = array;
            this._index = 0;
        }

        @Override
        public boolean hasNext() {
            return this._index < this._array.length;
        }

        @Override
        public T next() {
            if (this._index >= this._array.length) {
                throw new NoSuchElementException();
            }
            return this._array[this._index++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<T> iterator() {
            return this;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class DoubleBuilder
    extends PrimitiveArrayBuilder<double[]> {
        @Override
        public final double[] _constructArray(int len) {
            return new double[len];
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class FloatBuilder
    extends PrimitiveArrayBuilder<float[]> {
        @Override
        public final float[] _constructArray(int len) {
            return new float[len];
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class LongBuilder
    extends PrimitiveArrayBuilder<long[]> {
        @Override
        public final long[] _constructArray(int len) {
            return new long[len];
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class IntBuilder
    extends PrimitiveArrayBuilder<int[]> {
        @Override
        public final int[] _constructArray(int len) {
            return new int[len];
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class ShortBuilder
    extends PrimitiveArrayBuilder<short[]> {
        @Override
        public final short[] _constructArray(int len) {
            return new short[len];
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class ByteBuilder
    extends PrimitiveArrayBuilder<byte[]> {
        @Override
        public final byte[] _constructArray(int len) {
            return new byte[len];
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class BooleanBuilder
    extends PrimitiveArrayBuilder<boolean[]> {
        @Override
        public final boolean[] _constructArray(int len) {
            return new boolean[len];
        }
    }
}

