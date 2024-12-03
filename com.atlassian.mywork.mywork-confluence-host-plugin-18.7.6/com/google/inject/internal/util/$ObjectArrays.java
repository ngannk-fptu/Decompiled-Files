/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import java.lang.reflect.Array;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class $ObjectArrays {
    private $ObjectArrays() {
    }

    public static <T> T[] newArray(T[] reference, int length) {
        Class<?> type = reference.getClass().getComponentType();
        Object[] result = (Object[])Array.newInstance(type, length);
        return result;
    }
}

