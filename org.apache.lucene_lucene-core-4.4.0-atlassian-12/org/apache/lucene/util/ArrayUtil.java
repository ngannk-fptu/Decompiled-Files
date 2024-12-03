/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.Collection;
import java.util.Comparator;
import org.apache.lucene.util.ArrayIntroSorter;
import org.apache.lucene.util.ArrayTimSorter;
import org.apache.lucene.util.Constants;
import org.apache.lucene.util.RamUsageEstimator;

public final class ArrayUtil {
    private static final Comparator<?> NATURAL_COMPARATOR = new NaturalComparator();

    private ArrayUtil() {
    }

    public static int parseInt(char[] chars) throws NumberFormatException {
        return ArrayUtil.parseInt(chars, 0, chars.length, 10);
    }

    public static int parseInt(char[] chars, int offset, int len) throws NumberFormatException {
        return ArrayUtil.parseInt(chars, offset, len, 10);
    }

    public static int parseInt(char[] chars, int offset, int len, int radix) throws NumberFormatException {
        boolean negative;
        if (chars == null || radix < 2 || radix > 36) {
            throw new NumberFormatException();
        }
        int i = 0;
        if (len == 0) {
            throw new NumberFormatException("chars length is 0");
        }
        boolean bl = negative = chars[offset + i] == '-';
        if (negative && ++i == len) {
            throw new NumberFormatException("can't convert to an int");
        }
        if (negative) {
            ++offset;
            --len;
        }
        return ArrayUtil.parse(chars, offset, len, radix, negative);
    }

    private static int parse(char[] chars, int offset, int len, int radix, boolean negative) throws NumberFormatException {
        int max = Integer.MIN_VALUE / radix;
        int result = 0;
        for (int i = 0; i < len; ++i) {
            int digit = Character.digit(chars[i + offset], radix);
            if (digit == -1) {
                throw new NumberFormatException("Unable to parse");
            }
            if (max > result) {
                throw new NumberFormatException("Unable to parse");
            }
            int next = result * radix - digit;
            if (next > result) {
                throw new NumberFormatException("Unable to parse");
            }
            result = next;
        }
        if (!negative && (result = -result) < 0) {
            throw new NumberFormatException("Unable to parse");
        }
        return result;
    }

    public static int oversize(int minTargetSize, int bytesPerElement) {
        int newSize;
        if (minTargetSize < 0) {
            throw new IllegalArgumentException("invalid array size " + minTargetSize);
        }
        if (minTargetSize == 0) {
            return 0;
        }
        int extra = minTargetSize >> 3;
        if (extra < 3) {
            extra = 3;
        }
        if ((newSize = minTargetSize + extra) + 7 < 0) {
            return Integer.MAX_VALUE;
        }
        if (Constants.JRE_IS_64BIT) {
            switch (bytesPerElement) {
                case 4: {
                    return newSize + 1 & 0x7FFFFFFE;
                }
                case 2: {
                    return newSize + 3 & 0x7FFFFFFC;
                }
                case 1: {
                    return newSize + 7 & 0x7FFFFFF8;
                }
            }
            return newSize;
        }
        switch (bytesPerElement) {
            case 2: {
                return newSize + 1 & 0x7FFFFFFE;
            }
            case 1: {
                return newSize + 3 & 0x7FFFFFFC;
            }
        }
        return newSize;
    }

    public static int getShrinkSize(int currentSize, int targetSize, int bytesPerElement) {
        int newSize = ArrayUtil.oversize(targetSize, bytesPerElement);
        if (newSize < currentSize / 2) {
            return newSize;
        }
        return currentSize;
    }

    public static short[] grow(short[] array, int minSize) {
        assert (minSize >= 0) : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            short[] newArray = new short[ArrayUtil.oversize(minSize, 2)];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }

    public static short[] grow(short[] array) {
        return ArrayUtil.grow(array, 1 + array.length);
    }

    public static float[] grow(float[] array, int minSize) {
        assert (minSize >= 0) : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            float[] newArray = new float[ArrayUtil.oversize(minSize, 4)];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }

    public static float[] grow(float[] array) {
        return ArrayUtil.grow(array, 1 + array.length);
    }

    public static double[] grow(double[] array, int minSize) {
        assert (minSize >= 0) : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            double[] newArray = new double[ArrayUtil.oversize(minSize, 8)];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }

    public static double[] grow(double[] array) {
        return ArrayUtil.grow(array, 1 + array.length);
    }

    public static short[] shrink(short[] array, int targetSize) {
        assert (targetSize >= 0) : "size must be positive (got " + targetSize + "): likely integer overflow?";
        int newSize = ArrayUtil.getShrinkSize(array.length, targetSize, 2);
        if (newSize != array.length) {
            short[] newArray = new short[newSize];
            System.arraycopy(array, 0, newArray, 0, newSize);
            return newArray;
        }
        return array;
    }

    public static int[] grow(int[] array, int minSize) {
        assert (minSize >= 0) : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            int[] newArray = new int[ArrayUtil.oversize(minSize, 4)];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }

    public static int[] grow(int[] array) {
        return ArrayUtil.grow(array, 1 + array.length);
    }

    public static int[] shrink(int[] array, int targetSize) {
        assert (targetSize >= 0) : "size must be positive (got " + targetSize + "): likely integer overflow?";
        int newSize = ArrayUtil.getShrinkSize(array.length, targetSize, 4);
        if (newSize != array.length) {
            int[] newArray = new int[newSize];
            System.arraycopy(array, 0, newArray, 0, newSize);
            return newArray;
        }
        return array;
    }

    public static long[] grow(long[] array, int minSize) {
        assert (minSize >= 0) : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            long[] newArray = new long[ArrayUtil.oversize(minSize, 8)];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }

    public static long[] grow(long[] array) {
        return ArrayUtil.grow(array, 1 + array.length);
    }

    public static long[] shrink(long[] array, int targetSize) {
        assert (targetSize >= 0) : "size must be positive (got " + targetSize + "): likely integer overflow?";
        int newSize = ArrayUtil.getShrinkSize(array.length, targetSize, 8);
        if (newSize != array.length) {
            long[] newArray = new long[newSize];
            System.arraycopy(array, 0, newArray, 0, newSize);
            return newArray;
        }
        return array;
    }

    public static byte[] grow(byte[] array, int minSize) {
        assert (minSize >= 0) : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            byte[] newArray = new byte[ArrayUtil.oversize(minSize, 1)];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }

    public static byte[] grow(byte[] array) {
        return ArrayUtil.grow(array, 1 + array.length);
    }

    public static byte[] shrink(byte[] array, int targetSize) {
        assert (targetSize >= 0) : "size must be positive (got " + targetSize + "): likely integer overflow?";
        int newSize = ArrayUtil.getShrinkSize(array.length, targetSize, 1);
        if (newSize != array.length) {
            byte[] newArray = new byte[newSize];
            System.arraycopy(array, 0, newArray, 0, newSize);
            return newArray;
        }
        return array;
    }

    public static boolean[] grow(boolean[] array, int minSize) {
        assert (minSize >= 0) : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            boolean[] newArray = new boolean[ArrayUtil.oversize(minSize, 1)];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }

    public static boolean[] grow(boolean[] array) {
        return ArrayUtil.grow(array, 1 + array.length);
    }

    public static boolean[] shrink(boolean[] array, int targetSize) {
        assert (targetSize >= 0) : "size must be positive (got " + targetSize + "): likely integer overflow?";
        int newSize = ArrayUtil.getShrinkSize(array.length, targetSize, 1);
        if (newSize != array.length) {
            boolean[] newArray = new boolean[newSize];
            System.arraycopy(array, 0, newArray, 0, newSize);
            return newArray;
        }
        return array;
    }

    public static char[] grow(char[] array, int minSize) {
        assert (minSize >= 0) : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            char[] newArray = new char[ArrayUtil.oversize(minSize, 2)];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }

    public static char[] grow(char[] array) {
        return ArrayUtil.grow(array, 1 + array.length);
    }

    public static char[] shrink(char[] array, int targetSize) {
        assert (targetSize >= 0) : "size must be positive (got " + targetSize + "): likely integer overflow?";
        int newSize = ArrayUtil.getShrinkSize(array.length, targetSize, 2);
        if (newSize != array.length) {
            char[] newArray = new char[newSize];
            System.arraycopy(array, 0, newArray, 0, newSize);
            return newArray;
        }
        return array;
    }

    public static int[][] grow(int[][] array, int minSize) {
        assert (minSize >= 0) : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            int[][] newArray = new int[ArrayUtil.oversize(minSize, RamUsageEstimator.NUM_BYTES_OBJECT_REF)][];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }

    public static int[][] grow(int[][] array) {
        return ArrayUtil.grow(array, 1 + array.length);
    }

    public static int[][] shrink(int[][] array, int targetSize) {
        assert (targetSize >= 0) : "size must be positive (got " + targetSize + "): likely integer overflow?";
        int newSize = ArrayUtil.getShrinkSize(array.length, targetSize, RamUsageEstimator.NUM_BYTES_OBJECT_REF);
        if (newSize != array.length) {
            int[][] newArray = new int[newSize][];
            System.arraycopy(array, 0, newArray, 0, newSize);
            return newArray;
        }
        return array;
    }

    public static float[][] grow(float[][] array, int minSize) {
        assert (minSize >= 0) : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            float[][] newArray = new float[ArrayUtil.oversize(minSize, RamUsageEstimator.NUM_BYTES_OBJECT_REF)][];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }

    public static float[][] grow(float[][] array) {
        return ArrayUtil.grow(array, 1 + array.length);
    }

    public static float[][] shrink(float[][] array, int targetSize) {
        assert (targetSize >= 0) : "size must be positive (got " + targetSize + "): likely integer overflow?";
        int newSize = ArrayUtil.getShrinkSize(array.length, targetSize, RamUsageEstimator.NUM_BYTES_OBJECT_REF);
        if (newSize != array.length) {
            float[][] newArray = new float[newSize][];
            System.arraycopy(array, 0, newArray, 0, newSize);
            return newArray;
        }
        return array;
    }

    public static int hashCode(char[] array, int start, int end) {
        int code = 0;
        for (int i = end - 1; i >= start; --i) {
            code = code * 31 + array[i];
        }
        return code;
    }

    public static int hashCode(byte[] array, int start, int end) {
        int code = 0;
        for (int i = end - 1; i >= start; --i) {
            code = code * 31 + array[i];
        }
        return code;
    }

    public static boolean equals(char[] left, int offsetLeft, char[] right, int offsetRight, int length) {
        if (offsetLeft + length <= left.length && offsetRight + length <= right.length) {
            for (int i = 0; i < length; ++i) {
                if (left[offsetLeft + i] == right[offsetRight + i]) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean equals(byte[] left, int offsetLeft, byte[] right, int offsetRight, int length) {
        if (offsetLeft + length <= left.length && offsetRight + length <= right.length) {
            for (int i = 0; i < length; ++i) {
                if (left[offsetLeft + i] == right[offsetRight + i]) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean equals(int[] left, int offsetLeft, int[] right, int offsetRight, int length) {
        if (offsetLeft + length <= left.length && offsetRight + length <= right.length) {
            for (int i = 0; i < length; ++i) {
                if (left[offsetLeft + i] == right[offsetRight + i]) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public static int[] toIntArray(Collection<Integer> ints) {
        int[] result = new int[ints.size()];
        int upto = 0;
        for (int v : ints) {
            result[upto++] = v;
        }
        assert (upto == result.length);
        return result;
    }

    public static <T extends Comparable<? super T>> Comparator<T> naturalComparator() {
        return NATURAL_COMPARATOR;
    }

    public static <T> void swap(T[] arr, int i, int j) {
        T tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static <T> void introSort(T[] a, int fromIndex, int toIndex, Comparator<? super T> comp) {
        if (toIndex - fromIndex <= 1) {
            return;
        }
        new ArrayIntroSorter<T>(a, comp).sort(fromIndex, toIndex);
    }

    public static <T> void introSort(T[] a, Comparator<? super T> comp) {
        ArrayUtil.introSort(a, 0, a.length, comp);
    }

    public static <T extends Comparable<? super T>> void introSort(T[] a, int fromIndex, int toIndex) {
        if (toIndex - fromIndex <= 1) {
            return;
        }
        ArrayUtil.introSort(a, fromIndex, toIndex, ArrayUtil.naturalComparator());
    }

    public static <T extends Comparable<? super T>> void introSort(T[] a) {
        ArrayUtil.introSort(a, (int)0, (int)a.length);
    }

    public static <T> void timSort(T[] a, int fromIndex, int toIndex, Comparator<? super T> comp) {
        if (toIndex - fromIndex <= 1) {
            return;
        }
        new ArrayTimSorter<T>(a, comp, a.length / 64).sort(fromIndex, toIndex);
    }

    public static <T> void timSort(T[] a, Comparator<? super T> comp) {
        ArrayUtil.timSort(a, 0, a.length, comp);
    }

    public static <T extends Comparable<? super T>> void timSort(T[] a, int fromIndex, int toIndex) {
        if (toIndex - fromIndex <= 1) {
            return;
        }
        ArrayUtil.timSort(a, fromIndex, toIndex, ArrayUtil.naturalComparator());
    }

    public static <T extends Comparable<? super T>> void timSort(T[] a) {
        ArrayUtil.timSort(a, (int)0, (int)a.length);
    }

    private static class NaturalComparator<T extends Comparable<? super T>>
    implements Comparator<T> {
        NaturalComparator() {
        }

        @Override
        public int compare(T o1, T o2) {
            return o1.compareTo(o2);
        }
    }
}

