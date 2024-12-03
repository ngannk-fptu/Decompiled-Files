/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.internal;

import java.lang.reflect.Array;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ArrayCopy {
    private ArrayCopy() {
    }

    public static final <E> E[] copyOf(E[] source, int len) {
        Object[] dest = (Object[])Array.newInstance(source.getClass().getComponentType(), len);
        System.arraycopy(source, 0, dest, 0, len < source.length ? len : source.length);
        return dest;
    }

    public static final <E> E[] copyOfRange(E[] source, int from, int to) {
        int len = to - from;
        if (len < 0) {
            throw new IllegalArgumentException("From(" + from + ") > To (" + to + ")");
        }
        Object[] dest = (Object[])Array.newInstance(source.getClass().getComponentType(), len);
        int tocopy = from + len > source.length ? source.length - from : len;
        System.arraycopy(source, from, dest, 0, tocopy);
        return dest;
    }

    public static final char[] copyOf(char[] source, int len) {
        char[] dest = new char[len];
        System.arraycopy(source, 0, dest, 0, len < source.length ? len : source.length);
        return dest;
    }

    public static final int[] copyOf(int[] source, int len) {
        int[] dest = new int[len];
        System.arraycopy(source, 0, dest, 0, len < source.length ? len : source.length);
        return dest;
    }

    public static final boolean[] copyOf(boolean[] source, int len) {
        boolean[] dest = new boolean[len];
        System.arraycopy(source, 0, dest, 0, len < source.length ? len : source.length);
        return dest;
    }
}

