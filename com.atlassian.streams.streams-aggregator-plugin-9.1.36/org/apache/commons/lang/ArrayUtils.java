/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ArrayUtils {
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    public static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final long[] EMPTY_LONG_ARRAY = new long[0];
    public static final Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];
    public static final int[] EMPTY_INT_ARRAY = new int[0];
    public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];
    public static final short[] EMPTY_SHORT_ARRAY = new short[0];
    public static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final Byte[] EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];
    public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    public static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];
    public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
    public static final Float[] EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];
    public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
    public static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];
    public static final char[] EMPTY_CHAR_ARRAY = new char[0];
    public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];
    public static final int INDEX_NOT_FOUND = -1;
    static /* synthetic */ Class class$java$lang$Object;

    public static String toString(Object array) {
        return ArrayUtils.toString(array, "{}");
    }

    public static String toString(Object array, String stringIfNull) {
        if (array == null) {
            return stringIfNull;
        }
        return new ToStringBuilder(array, ToStringStyle.SIMPLE_STYLE).append(array).toString();
    }

    public static int hashCode(Object array) {
        return new HashCodeBuilder().append(array).toHashCode();
    }

    public static boolean isEquals(Object array1, Object array2) {
        return new EqualsBuilder().append(array1, array2).isEquals();
    }

    public static Map toMap(Object[] array) {
        if (array == null) {
            return null;
        }
        HashMap<Object, Object> map = new HashMap<Object, Object>((int)((double)array.length * 1.5));
        for (int i = 0; i < array.length; ++i) {
            Object[] entry;
            Object object = array[i];
            if (object instanceof Map.Entry) {
                entry = (Object[])object;
                map.put(entry.getKey(), entry.getValue());
                continue;
            }
            if (object instanceof Object[]) {
                entry = (Object[])object;
                if (entry.length < 2) {
                    throw new IllegalArgumentException("Array element " + i + ", '" + object + "', has a length less than 2");
                }
                map.put(entry[0], entry[1]);
                continue;
            }
            throw new IllegalArgumentException("Array element " + i + ", '" + object + "', is neither of type Map.Entry nor an Array");
        }
        return map;
    }

    public static Object[] clone(Object[] array) {
        if (array == null) {
            return null;
        }
        return (Object[])array.clone();
    }

    public static long[] clone(long[] array) {
        if (array == null) {
            return null;
        }
        return (long[])array.clone();
    }

    public static int[] clone(int[] array) {
        if (array == null) {
            return null;
        }
        return (int[])array.clone();
    }

    public static short[] clone(short[] array) {
        if (array == null) {
            return null;
        }
        return (short[])array.clone();
    }

    public static char[] clone(char[] array) {
        if (array == null) {
            return null;
        }
        return (char[])array.clone();
    }

    public static byte[] clone(byte[] array) {
        if (array == null) {
            return null;
        }
        return (byte[])array.clone();
    }

    public static double[] clone(double[] array) {
        if (array == null) {
            return null;
        }
        return (double[])array.clone();
    }

    public static float[] clone(float[] array) {
        if (array == null) {
            return null;
        }
        return (float[])array.clone();
    }

    public static boolean[] clone(boolean[] array) {
        if (array == null) {
            return null;
        }
        return (boolean[])array.clone();
    }

    public static Object[] subarray(Object[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        Class<?> type = array.getClass().getComponentType();
        if (newSize <= 0) {
            return (Object[])Array.newInstance(type, 0);
        }
        Object[] subarray = (Object[])Array.newInstance(type, newSize);
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static long[] subarray(long[] array, int startIndexInclusive, int endIndexExclusive) {
        int newSize;
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        if ((newSize = endIndexExclusive - startIndexInclusive) <= 0) {
            return EMPTY_LONG_ARRAY;
        }
        long[] subarray = new long[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static int[] subarray(int[] array, int startIndexInclusive, int endIndexExclusive) {
        int newSize;
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        if ((newSize = endIndexExclusive - startIndexInclusive) <= 0) {
            return EMPTY_INT_ARRAY;
        }
        int[] subarray = new int[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static short[] subarray(short[] array, int startIndexInclusive, int endIndexExclusive) {
        int newSize;
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        if ((newSize = endIndexExclusive - startIndexInclusive) <= 0) {
            return EMPTY_SHORT_ARRAY;
        }
        short[] subarray = new short[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static char[] subarray(char[] array, int startIndexInclusive, int endIndexExclusive) {
        int newSize;
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        if ((newSize = endIndexExclusive - startIndexInclusive) <= 0) {
            return EMPTY_CHAR_ARRAY;
        }
        char[] subarray = new char[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static byte[] subarray(byte[] array, int startIndexInclusive, int endIndexExclusive) {
        int newSize;
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        if ((newSize = endIndexExclusive - startIndexInclusive) <= 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] subarray = new byte[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static double[] subarray(double[] array, int startIndexInclusive, int endIndexExclusive) {
        int newSize;
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        if ((newSize = endIndexExclusive - startIndexInclusive) <= 0) {
            return EMPTY_DOUBLE_ARRAY;
        }
        double[] subarray = new double[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static float[] subarray(float[] array, int startIndexInclusive, int endIndexExclusive) {
        int newSize;
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        if ((newSize = endIndexExclusive - startIndexInclusive) <= 0) {
            return EMPTY_FLOAT_ARRAY;
        }
        float[] subarray = new float[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static boolean[] subarray(boolean[] array, int startIndexInclusive, int endIndexExclusive) {
        int newSize;
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        if ((newSize = endIndexExclusive - startIndexInclusive) <= 0) {
            return EMPTY_BOOLEAN_ARRAY;
        }
        boolean[] subarray = new boolean[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static boolean isSameLength(Object[] array1, Object[] array2) {
        return !(array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0) && (array1 == null || array2 == null || array1.length == array2.length);
    }

    public static boolean isSameLength(long[] array1, long[] array2) {
        return !(array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0) && (array1 == null || array2 == null || array1.length == array2.length);
    }

    public static boolean isSameLength(int[] array1, int[] array2) {
        return !(array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0) && (array1 == null || array2 == null || array1.length == array2.length);
    }

    public static boolean isSameLength(short[] array1, short[] array2) {
        return !(array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0) && (array1 == null || array2 == null || array1.length == array2.length);
    }

    public static boolean isSameLength(char[] array1, char[] array2) {
        return !(array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0) && (array1 == null || array2 == null || array1.length == array2.length);
    }

    public static boolean isSameLength(byte[] array1, byte[] array2) {
        return !(array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0) && (array1 == null || array2 == null || array1.length == array2.length);
    }

    public static boolean isSameLength(double[] array1, double[] array2) {
        return !(array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0) && (array1 == null || array2 == null || array1.length == array2.length);
    }

    public static boolean isSameLength(float[] array1, float[] array2) {
        return !(array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0) && (array1 == null || array2 == null || array1.length == array2.length);
    }

    public static boolean isSameLength(boolean[] array1, boolean[] array2) {
        return !(array1 == null && array2 != null && array2.length > 0 || array2 == null && array1 != null && array1.length > 0) && (array1 == null || array2 == null || array1.length == array2.length);
    }

    public static int getLength(Object array) {
        if (array == null) {
            return 0;
        }
        return Array.getLength(array);
    }

    public static boolean isSameType(Object array1, Object array2) {
        if (array1 == null || array2 == null) {
            throw new IllegalArgumentException("The Array must not be null");
        }
        return array1.getClass().getName().equals(array2.getClass().getName());
    }

    public static void reverse(Object[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        for (int j = array.length - 1; j > i; --j, ++i) {
            Object tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    public static void reverse(long[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        for (int j = array.length - 1; j > i; --j, ++i) {
            long tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    public static void reverse(int[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        for (int j = array.length - 1; j > i; --j, ++i) {
            int tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    public static void reverse(short[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        for (int j = array.length - 1; j > i; --j, ++i) {
            short tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    public static void reverse(char[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        for (int j = array.length - 1; j > i; --j, ++i) {
            char tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    public static void reverse(byte[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        for (int j = array.length - 1; j > i; --j, ++i) {
            byte tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    public static void reverse(double[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        for (int j = array.length - 1; j > i; --j, ++i) {
            double tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    public static void reverse(float[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        for (int j = array.length - 1; j > i; --j, ++i) {
            float tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    public static void reverse(boolean[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        for (int j = array.length - 1; j > i; --j, ++i) {
            boolean tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    public static int indexOf(Object[] array, Object objectToFind) {
        return ArrayUtils.indexOf(array, objectToFind, 0);
    }

    public static int indexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; ++i) {
                if (array[i] != null) continue;
                return i;
            }
        } else {
            for (int i = startIndex; i < array.length; ++i) {
                if (!objectToFind.equals(array[i])) continue;
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(Object[] array, Object objectToFind) {
        return ArrayUtils.lastIndexOf(array, objectToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i >= 0; --i) {
                if (array[i] != null) continue;
                return i;
            }
        } else {
            for (int i = startIndex; i >= 0; --i) {
                if (!objectToFind.equals(array[i])) continue;
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(Object[] array, Object objectToFind) {
        return ArrayUtils.indexOf(array, objectToFind) != -1;
    }

    public static int indexOf(long[] array, long valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind, 0);
    }

    public static int indexOf(long[] array, long valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; ++i) {
            if (valueToFind != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static int lastIndexOf(long[] array, long valueToFind) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(long[] array, long valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; --i) {
            if (valueToFind != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static boolean contains(long[] array, long valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(int[] array, int valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind, 0);
    }

    public static int indexOf(int[] array, int valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; ++i) {
            if (valueToFind != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static int lastIndexOf(int[] array, int valueToFind) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(int[] array, int valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; --i) {
            if (valueToFind != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static boolean contains(int[] array, int valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(short[] array, short valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind, 0);
    }

    public static int indexOf(short[] array, short valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; ++i) {
            if (valueToFind != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static int lastIndexOf(short[] array, short valueToFind) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(short[] array, short valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; --i) {
            if (valueToFind != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static boolean contains(short[] array, short valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(char[] array, char valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind, 0);
    }

    public static int indexOf(char[] array, char valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; ++i) {
            if (valueToFind != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static int lastIndexOf(char[] array, char valueToFind) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(char[] array, char valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; --i) {
            if (valueToFind != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static boolean contains(char[] array, char valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(byte[] array, byte valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind, 0);
    }

    public static int indexOf(byte[] array, byte valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; ++i) {
            if (valueToFind != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static int lastIndexOf(byte[] array, byte valueToFind) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(byte[] array, byte valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; --i) {
            if (valueToFind != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static boolean contains(byte[] array, byte valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(double[] array, double valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind, 0);
    }

    public static int indexOf(double[] array, double valueToFind, double tolerance) {
        return ArrayUtils.indexOf(array, valueToFind, 0, tolerance);
    }

    public static int indexOf(double[] array, double valueToFind, int startIndex) {
        if (ArrayUtils.isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; ++i) {
            if (valueToFind != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static int indexOf(double[] array, double valueToFind, int startIndex, double tolerance) {
        if (ArrayUtils.isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        double min = valueToFind - tolerance;
        double max = valueToFind + tolerance;
        for (int i = startIndex; i < array.length; ++i) {
            if (!(array[i] >= min) || !(array[i] <= max)) continue;
            return i;
        }
        return -1;
    }

    public static int lastIndexOf(double[] array, double valueToFind) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(double[] array, double valueToFind, double tolerance) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE, tolerance);
    }

    public static int lastIndexOf(double[] array, double valueToFind, int startIndex) {
        if (ArrayUtils.isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; --i) {
            if (valueToFind != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static int lastIndexOf(double[] array, double valueToFind, int startIndex, double tolerance) {
        if (ArrayUtils.isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        double min = valueToFind - tolerance;
        double max = valueToFind + tolerance;
        for (int i = startIndex; i >= 0; --i) {
            if (!(array[i] >= min) || !(array[i] <= max)) continue;
            return i;
        }
        return -1;
    }

    public static boolean contains(double[] array, double valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind) != -1;
    }

    public static boolean contains(double[] array, double valueToFind, double tolerance) {
        return ArrayUtils.indexOf(array, valueToFind, 0, tolerance) != -1;
    }

    public static int indexOf(float[] array, float valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind, 0);
    }

    public static int indexOf(float[] array, float valueToFind, int startIndex) {
        if (ArrayUtils.isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; ++i) {
            if (valueToFind != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static int lastIndexOf(float[] array, float valueToFind) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(float[] array, float valueToFind, int startIndex) {
        if (ArrayUtils.isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; --i) {
            if (valueToFind != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static boolean contains(float[] array, float valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(boolean[] array, boolean valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind, 0);
    }

    public static int indexOf(boolean[] array, boolean valueToFind, int startIndex) {
        if (ArrayUtils.isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; ++i) {
            if (valueToFind != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static int lastIndexOf(boolean[] array, boolean valueToFind) {
        return ArrayUtils.lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(boolean[] array, boolean valueToFind, int startIndex) {
        if (ArrayUtils.isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; --i) {
            if (valueToFind != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static boolean contains(boolean[] array, boolean valueToFind) {
        return ArrayUtils.indexOf(array, valueToFind) != -1;
    }

    public static char[] toPrimitive(Character[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_CHAR_ARRAY;
        }
        char[] result = new char[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i].charValue();
        }
        return result;
    }

    public static char[] toPrimitive(Character[] array, char valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_CHAR_ARRAY;
        }
        char[] result = new char[array.length];
        for (int i = 0; i < array.length; ++i) {
            Character b = array[i];
            result[i] = b == null ? valueForNull : b.charValue();
        }
        return result;
    }

    public static Character[] toObject(char[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_CHARACTER_OBJECT_ARRAY;
        }
        Character[] result = new Character[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = new Character(array[i]);
        }
        return result;
    }

    public static long[] toPrimitive(Long[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_LONG_ARRAY;
        }
        long[] result = new long[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }

    public static long[] toPrimitive(Long[] array, long valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_LONG_ARRAY;
        }
        long[] result = new long[array.length];
        for (int i = 0; i < array.length; ++i) {
            Long b = array[i];
            result[i] = b == null ? valueForNull : b;
        }
        return result;
    }

    public static Long[] toObject(long[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_LONG_OBJECT_ARRAY;
        }
        Long[] result = new Long[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = new Long(array[i]);
        }
        return result;
    }

    public static int[] toPrimitive(Integer[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_INT_ARRAY;
        }
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }

    public static int[] toPrimitive(Integer[] array, int valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_INT_ARRAY;
        }
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            Integer b = array[i];
            result[i] = b == null ? valueForNull : b;
        }
        return result;
    }

    public static Integer[] toObject(int[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_INTEGER_OBJECT_ARRAY;
        }
        Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = new Integer(array[i]);
        }
        return result;
    }

    public static short[] toPrimitive(Short[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_SHORT_ARRAY;
        }
        short[] result = new short[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }

    public static short[] toPrimitive(Short[] array, short valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_SHORT_ARRAY;
        }
        short[] result = new short[array.length];
        for (int i = 0; i < array.length; ++i) {
            Short b = array[i];
            result[i] = b == null ? valueForNull : b;
        }
        return result;
    }

    public static Short[] toObject(short[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_SHORT_OBJECT_ARRAY;
        }
        Short[] result = new Short[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = new Short(array[i]);
        }
        return result;
    }

    public static byte[] toPrimitive(Byte[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }

    public static byte[] toPrimitive(Byte[] array, byte valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            Byte b = array[i];
            result[i] = b == null ? valueForNull : b;
        }
        return result;
    }

    public static Byte[] toObject(byte[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_BYTE_OBJECT_ARRAY;
        }
        Byte[] result = new Byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = new Byte(array[i]);
        }
        return result;
    }

    public static double[] toPrimitive(Double[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_DOUBLE_ARRAY;
        }
        double[] result = new double[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }

    public static double[] toPrimitive(Double[] array, double valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_DOUBLE_ARRAY;
        }
        double[] result = new double[array.length];
        for (int i = 0; i < array.length; ++i) {
            Double b = array[i];
            result[i] = b == null ? valueForNull : b;
        }
        return result;
    }

    public static Double[] toObject(double[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_DOUBLE_OBJECT_ARRAY;
        }
        Double[] result = new Double[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = new Double(array[i]);
        }
        return result;
    }

    public static float[] toPrimitive(Float[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_FLOAT_ARRAY;
        }
        float[] result = new float[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i].floatValue();
        }
        return result;
    }

    public static float[] toPrimitive(Float[] array, float valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_FLOAT_ARRAY;
        }
        float[] result = new float[array.length];
        for (int i = 0; i < array.length; ++i) {
            Float b = array[i];
            result[i] = b == null ? valueForNull : b.floatValue();
        }
        return result;
    }

    public static Float[] toObject(float[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_FLOAT_OBJECT_ARRAY;
        }
        Float[] result = new Float[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = new Float(array[i]);
        }
        return result;
    }

    public static boolean[] toPrimitive(Boolean[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_BOOLEAN_ARRAY;
        }
        boolean[] result = new boolean[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }

    public static boolean[] toPrimitive(Boolean[] array, boolean valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_BOOLEAN_ARRAY;
        }
        boolean[] result = new boolean[array.length];
        for (int i = 0; i < array.length; ++i) {
            Boolean b = array[i];
            result[i] = b == null ? valueForNull : b;
        }
        return result;
    }

    public static Boolean[] toObject(boolean[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_BOOLEAN_OBJECT_ARRAY;
        }
        Boolean[] result = new Boolean[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i] ? Boolean.TRUE : Boolean.FALSE;
        }
        return result;
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(short[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    public static Object[] addAll(Object[] array1, Object[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        }
        if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        Object[] joinedArray = (Object[])Array.newInstance(array1.getClass().getComponentType(), array1.length + array2.length);
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static boolean[] addAll(boolean[] array1, boolean[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        }
        if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        boolean[] joinedArray = new boolean[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static char[] addAll(char[] array1, char[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        }
        if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        char[] joinedArray = new char[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static byte[] addAll(byte[] array1, byte[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        }
        if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        byte[] joinedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static short[] addAll(short[] array1, short[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        }
        if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        short[] joinedArray = new short[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static int[] addAll(int[] array1, int[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        }
        if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        int[] joinedArray = new int[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static long[] addAll(long[] array1, long[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        }
        if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        long[] joinedArray = new long[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static float[] addAll(float[] array1, float[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        }
        if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        float[] joinedArray = new float[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static double[] addAll(double[] array1, double[] array2) {
        if (array1 == null) {
            return ArrayUtils.clone(array2);
        }
        if (array2 == null) {
            return ArrayUtils.clone(array1);
        }
        double[] joinedArray = new double[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static Object[] add(Object[] array, Object element) {
        Class<Object> type = array != null ? array.getClass() : (element != null ? element.getClass() : (class$java$lang$Object == null ? (class$java$lang$Object = ArrayUtils.class$("java.lang.Object")) : class$java$lang$Object));
        Object[] newArray = (Object[])ArrayUtils.copyArrayGrow1(array, type);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static boolean[] add(boolean[] array, boolean element) {
        boolean[] newArray = (boolean[])ArrayUtils.copyArrayGrow1(array, Boolean.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static byte[] add(byte[] array, byte element) {
        byte[] newArray = (byte[])ArrayUtils.copyArrayGrow1(array, Byte.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static char[] add(char[] array, char element) {
        char[] newArray = (char[])ArrayUtils.copyArrayGrow1(array, Character.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static double[] add(double[] array, double element) {
        double[] newArray = (double[])ArrayUtils.copyArrayGrow1(array, Double.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static float[] add(float[] array, float element) {
        float[] newArray = (float[])ArrayUtils.copyArrayGrow1(array, Float.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static int[] add(int[] array, int element) {
        int[] newArray = (int[])ArrayUtils.copyArrayGrow1(array, Integer.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static long[] add(long[] array, long element) {
        long[] newArray = (long[])ArrayUtils.copyArrayGrow1(array, Long.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    public static short[] add(short[] array, short element) {
        short[] newArray = (short[])ArrayUtils.copyArrayGrow1(array, Short.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    private static Object copyArrayGrow1(Object array, Class newArrayComponentType) {
        if (array != null) {
            int arrayLength = Array.getLength(array);
            Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
            System.arraycopy(array, 0, newArray, 0, arrayLength);
            return newArray;
        }
        return Array.newInstance(newArrayComponentType, 1);
    }

    public static Object[] add(Object[] array, int index, Object element) {
        Class<?> clss = null;
        if (array != null) {
            clss = array.getClass().getComponentType();
        } else if (element != null) {
            clss = element.getClass();
        } else {
            return new Object[]{null};
        }
        return (Object[])ArrayUtils.add(array, index, element, clss);
    }

    public static boolean[] add(boolean[] array, int index, boolean element) {
        return (boolean[])ArrayUtils.add(array, index, BooleanUtils.toBooleanObject(element), Boolean.TYPE);
    }

    public static char[] add(char[] array, int index, char element) {
        return (char[])ArrayUtils.add(array, index, new Character(element), Character.TYPE);
    }

    public static byte[] add(byte[] array, int index, byte element) {
        return (byte[])ArrayUtils.add(array, index, new Byte(element), Byte.TYPE);
    }

    public static short[] add(short[] array, int index, short element) {
        return (short[])ArrayUtils.add(array, index, new Short(element), Short.TYPE);
    }

    public static int[] add(int[] array, int index, int element) {
        return (int[])ArrayUtils.add(array, index, new Integer(element), Integer.TYPE);
    }

    public static long[] add(long[] array, int index, long element) {
        return (long[])ArrayUtils.add(array, index, new Long(element), Long.TYPE);
    }

    public static float[] add(float[] array, int index, float element) {
        return (float[])ArrayUtils.add(array, index, new Float(element), Float.TYPE);
    }

    public static double[] add(double[] array, int index, double element) {
        return (double[])ArrayUtils.add(array, index, new Double(element), Double.TYPE);
    }

    private static Object add(Object array, int index, Object element, Class clss) {
        if (array == null) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Length: 0");
            }
            Object joinedArray = Array.newInstance(clss, 1);
            Array.set(joinedArray, 0, element);
            return joinedArray;
        }
        int length = Array.getLength(array);
        if (index > length || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        Object result = Array.newInstance(clss, length + 1);
        System.arraycopy(array, 0, result, 0, index);
        Array.set(result, index, element);
        if (index < length) {
            System.arraycopy(array, index, result, index + 1, length - index);
        }
        return result;
    }

    public static Object[] remove(Object[] array, int index) {
        return (Object[])ArrayUtils.remove((Object)array, index);
    }

    public static Object[] removeElement(Object[] array, Object element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == -1) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    public static boolean[] remove(boolean[] array, int index) {
        return (boolean[])ArrayUtils.remove((Object)array, index);
    }

    public static boolean[] removeElement(boolean[] array, boolean element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == -1) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    public static byte[] remove(byte[] array, int index) {
        return (byte[])ArrayUtils.remove((Object)array, index);
    }

    public static byte[] removeElement(byte[] array, byte element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == -1) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    public static char[] remove(char[] array, int index) {
        return (char[])ArrayUtils.remove((Object)array, index);
    }

    public static char[] removeElement(char[] array, char element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == -1) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    public static double[] remove(double[] array, int index) {
        return (double[])ArrayUtils.remove((Object)array, index);
    }

    public static double[] removeElement(double[] array, double element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == -1) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    public static float[] remove(float[] array, int index) {
        return (float[])ArrayUtils.remove((Object)array, index);
    }

    public static float[] removeElement(float[] array, float element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == -1) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    public static int[] remove(int[] array, int index) {
        return (int[])ArrayUtils.remove((Object)array, index);
    }

    public static int[] removeElement(int[] array, int element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == -1) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    public static long[] remove(long[] array, int index) {
        return (long[])ArrayUtils.remove((Object)array, index);
    }

    public static long[] removeElement(long[] array, long element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == -1) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    public static short[] remove(short[] array, int index) {
        return (short[])ArrayUtils.remove((Object)array, index);
    }

    public static short[] removeElement(short[] array, short element) {
        int index = ArrayUtils.indexOf(array, element);
        if (index == -1) {
            return ArrayUtils.clone(array);
        }
        return ArrayUtils.remove(array, index);
    }

    private static Object remove(Object array, int index) {
        int length = ArrayUtils.getLength(array);
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, length - index - 1);
        }
        return result;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

