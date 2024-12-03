/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

public final class DataUtil {
    static final char[] EMPTY_CHAR_ARRAY = new char[0];
    static final Integer[] INTS = new Integer[100];
    static final Long MAX_LONG;
    static final String NO_TYPE = "Illegal to pass null; can not determine component type";

    private DataUtil() {
    }

    public static char[] getEmptyCharArray() {
        return EMPTY_CHAR_ARRAY;
    }

    public static Integer Integer(int i) {
        if (i < 0 || i >= INTS.length) {
            return new Integer(i);
        }
        return INTS[i];
    }

    public static Long Long(long l) {
        if (l == Long.MAX_VALUE) {
            return MAX_LONG;
        }
        return new Long(l);
    }

    public static boolean anyValuesInCommon(Collection c1, Collection c2) {
        if (c1.size() > c2.size()) {
            Collection tmp = c1;
            c1 = c2;
            c2 = tmp;
        }
        Iterator it = c1.iterator();
        while (it.hasNext()) {
            if (!c2.contains(it.next())) continue;
            return true;
        }
        return false;
    }

    public static Object growArrayBy50Pct(Object arr) {
        if (arr == null) {
            throw new IllegalArgumentException(NO_TYPE);
        }
        Object old = arr;
        int len = Array.getLength(arr);
        arr = Array.newInstance(arr.getClass().getComponentType(), len + (len >> 1));
        System.arraycopy(old, 0, arr, 0, len);
        return arr;
    }

    public static Object growArrayToAtLeast(Object arr, int minLen) {
        if (arr == null) {
            throw new IllegalArgumentException(NO_TYPE);
        }
        Object old = arr;
        int oldLen = Array.getLength(arr);
        int newLen = oldLen + (oldLen + 1 >> 1);
        if (newLen < minLen) {
            newLen = minLen;
        }
        arr = Array.newInstance(arr.getClass().getComponentType(), newLen);
        System.arraycopy(old, 0, arr, 0, oldLen);
        return arr;
    }

    public static String[] growArrayBy(String[] arr, int more) {
        if (arr == null) {
            return new String[more];
        }
        String[] old = arr;
        int len = arr.length;
        arr = new String[len + more];
        System.arraycopy(old, 0, arr, 0, len);
        return arr;
    }

    public static int[] growArrayBy(int[] arr, int more) {
        if (arr == null) {
            return new int[more];
        }
        int[] old = arr;
        int len = arr.length;
        arr = new int[len + more];
        System.arraycopy(old, 0, arr, 0, len);
        return arr;
    }

    static {
        for (int i = 0; i < INTS.length; ++i) {
            DataUtil.INTS[i] = new Integer(i);
        }
        MAX_LONG = new Long(Long.MAX_VALUE);
    }
}

