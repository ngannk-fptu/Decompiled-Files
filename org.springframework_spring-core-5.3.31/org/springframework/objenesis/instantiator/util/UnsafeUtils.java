/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.objenesis.instantiator.util;

import java.lang.reflect.Field;
import org.springframework.objenesis.ObjenesisException;
import sun.misc.Unsafe;

public final class UnsafeUtils {
    private static final Unsafe unsafe;

    private UnsafeUtils() {
    }

    public static Unsafe getUnsafe() {
        return unsafe;
    }

    static {
        Field f;
        try {
            f = Unsafe.class.getDeclaredField("theUnsafe");
        }
        catch (NoSuchFieldException e) {
            throw new ObjenesisException(e);
        }
        f.setAccessible(true);
        try {
            unsafe = (Unsafe)f.get(null);
        }
        catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        }
    }
}

