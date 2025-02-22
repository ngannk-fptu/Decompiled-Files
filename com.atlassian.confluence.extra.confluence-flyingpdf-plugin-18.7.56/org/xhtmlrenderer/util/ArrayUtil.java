/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import org.xhtmlrenderer.util.Constants;

public class ArrayUtil {
    public static String[] cloneOrEmpty(String[] source) {
        return source == null ? Constants.EMPTY_STR_ARR : (String[])source.clone();
    }

    public static byte[] cloneOrEmpty(byte[] source) {
        return source == null ? Constants.EMPTY_BYTE_ARR : (byte[])source.clone();
    }

    public static int[] cloneOrEmpty(int[] source) {
        return source == null ? Constants.EMPTY_INT_ARR : (int[])source.clone();
    }
}

