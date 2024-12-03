/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import com.mchange.v2.lang.ObjectUtils;

public final class ArrayUtils {
    public static int indexOf(Object[] objectArray, Object object) {
        int n = objectArray.length;
        for (int i = 0; i < n; ++i) {
            if (!object.equals(objectArray[i])) continue;
            return i;
        }
        return -1;
    }

    public static int identityIndexOf(Object[] objectArray, Object object) {
        int n = objectArray.length;
        for (int i = 0; i < n; ++i) {
            if (object != objectArray[i]) continue;
            return i;
        }
        return -1;
    }

    public static boolean startsWith(byte[] byArray, byte[] byArray2) {
        int n = byArray.length;
        int n2 = byArray2.length;
        if (n < n2) {
            return false;
        }
        for (int i = 0; i < n2; ++i) {
            if (byArray[i] == byArray2[i]) continue;
            return false;
        }
        return true;
    }

    public static int hashArray(Object[] objectArray) {
        int n;
        int n2 = n = objectArray.length;
        for (int i = 0; i < n; ++i) {
            int n3 = ObjectUtils.hashOrZero(objectArray[i]);
            int n4 = i % 32;
            int n5 = n3 >>> n4;
            n2 ^= (n5 |= n3 << 32 - n4);
        }
        return n2;
    }

    public static int hashArray(int[] nArray) {
        int n;
        int n2 = n = nArray.length;
        for (int i = 0; i < n; ++i) {
            int n3 = nArray[i];
            int n4 = i % 32;
            int n5 = n3 >>> n4;
            n2 ^= (n5 |= n3 << 32 - n4);
        }
        return n2;
    }

    public static int hashOrZeroArray(Object[] objectArray) {
        return objectArray == null ? 0 : ArrayUtils.hashArray(objectArray);
    }

    public static int hashOrZeroArray(int[] nArray) {
        return nArray == null ? 0 : ArrayUtils.hashArray(nArray);
    }

    public static String stringifyContents(Object[] objectArray) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[ ");
        int n = objectArray.length;
        for (int i = 0; i < n; ++i) {
            if (i != 0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(objectArray[i].toString());
        }
        stringBuffer.append(" ]");
        return stringBuffer.toString();
    }

    private static String toString(String[] stringArray, int n) {
        StringBuffer stringBuffer = new StringBuffer(n);
        boolean bl = true;
        stringBuffer.append('[');
        int n2 = stringArray.length;
        for (int i = 0; i < n2; ++i) {
            if (bl) {
                bl = false;
            } else {
                stringBuffer.append(',');
            }
            stringBuffer.append(stringArray[i]);
        }
        stringBuffer.append(']');
        return stringBuffer.toString();
    }

    public static String toString(boolean[] blArray) {
        String[] stringArray = new String[blArray.length];
        int n = 0;
        int n2 = blArray.length;
        for (int i = 0; i < n2; ++i) {
            String string = String.valueOf(blArray[i]);
            n += string.length();
            stringArray[i] = string;
        }
        return ArrayUtils.toString(stringArray, n + blArray.length + 1);
    }

    public static String toString(byte[] byArray) {
        String[] stringArray = new String[byArray.length];
        int n = 0;
        int n2 = byArray.length;
        for (int i = 0; i < n2; ++i) {
            String string = String.valueOf(byArray[i]);
            n += string.length();
            stringArray[i] = string;
        }
        return ArrayUtils.toString(stringArray, n + byArray.length + 1);
    }

    public static String toString(char[] cArray) {
        String[] stringArray = new String[cArray.length];
        int n = 0;
        int n2 = cArray.length;
        for (int i = 0; i < n2; ++i) {
            String string = String.valueOf(cArray[i]);
            n += string.length();
            stringArray[i] = string;
        }
        return ArrayUtils.toString(stringArray, n + cArray.length + 1);
    }

    public static String toString(short[] sArray) {
        String[] stringArray = new String[sArray.length];
        int n = 0;
        int n2 = sArray.length;
        for (int i = 0; i < n2; ++i) {
            String string = String.valueOf(sArray[i]);
            n += string.length();
            stringArray[i] = string;
        }
        return ArrayUtils.toString(stringArray, n + sArray.length + 1);
    }

    public static String toString(int[] nArray) {
        String[] stringArray = new String[nArray.length];
        int n = 0;
        int n2 = nArray.length;
        for (int i = 0; i < n2; ++i) {
            String string = String.valueOf(nArray[i]);
            n += string.length();
            stringArray[i] = string;
        }
        return ArrayUtils.toString(stringArray, n + nArray.length + 1);
    }

    public static String toString(long[] lArray) {
        String[] stringArray = new String[lArray.length];
        int n = 0;
        int n2 = lArray.length;
        for (int i = 0; i < n2; ++i) {
            String string = String.valueOf(lArray[i]);
            n += string.length();
            stringArray[i] = string;
        }
        return ArrayUtils.toString(stringArray, n + lArray.length + 1);
    }

    public static String toString(float[] fArray) {
        String[] stringArray = new String[fArray.length];
        int n = 0;
        int n2 = fArray.length;
        for (int i = 0; i < n2; ++i) {
            String string = String.valueOf(fArray[i]);
            n += string.length();
            stringArray[i] = string;
        }
        return ArrayUtils.toString(stringArray, n + fArray.length + 1);
    }

    public static String toString(double[] dArray) {
        String[] stringArray = new String[dArray.length];
        int n = 0;
        int n2 = dArray.length;
        for (int i = 0; i < n2; ++i) {
            String string = String.valueOf(dArray[i]);
            n += string.length();
            stringArray[i] = string;
        }
        return ArrayUtils.toString(stringArray, n + dArray.length + 1);
    }

    public static String toString(Object[] objectArray) {
        String[] stringArray = new String[objectArray.length];
        int n = 0;
        int n2 = objectArray.length;
        for (int i = 0; i < n2; ++i) {
            Object object = objectArray[i];
            String string = object instanceof Object[] ? ArrayUtils.toString((Object[])object) : (object instanceof double[] ? ArrayUtils.toString((double[])object) : (object instanceof float[] ? ArrayUtils.toString((float[])object) : (object instanceof long[] ? ArrayUtils.toString((long[])object) : (object instanceof int[] ? ArrayUtils.toString((int[])object) : (object instanceof short[] ? ArrayUtils.toString((short[])object) : (object instanceof char[] ? ArrayUtils.toString((char[])object) : (object instanceof byte[] ? ArrayUtils.toString((byte[])object) : (object instanceof boolean[] ? ArrayUtils.toString((boolean[])object) : String.valueOf(objectArray[i])))))))));
            n += string.length();
            stringArray[i] = string;
        }
        return ArrayUtils.toString(stringArray, n + objectArray.length + 1);
    }

    private ArrayUtils() {
    }
}

