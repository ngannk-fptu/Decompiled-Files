/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.memory.impl;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.QuickMath;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

public final class UnsafeUtil {
    public static final boolean UNSAFE_AVAILABLE;
    public static final Unsafe UNSAFE;
    private static final ILogger LOGGER;

    private UnsafeUtil() {
    }

    private static Unsafe findUnsafe() {
        try {
            return Unsafe.getUnsafe();
        }
        catch (SecurityException se) {
            return AccessController.doPrivileged(new PrivilegedAction<Unsafe>(){

                @Override
                public Unsafe run() {
                    try {
                        Class<Unsafe> type = Unsafe.class;
                        try {
                            Field field = type.getDeclaredField("theUnsafe");
                            field.setAccessible(true);
                            return (Unsafe)type.cast(field.get(type));
                        }
                        catch (Exception e) {
                            for (Field field : type.getDeclaredFields()) {
                                if (!type.isAssignableFrom(field.getType())) continue;
                                field.setAccessible(true);
                                return (Unsafe)type.cast(field.get(type));
                            }
                        }
                    }
                    catch (Throwable t) {
                        throw ExceptionUtil.rethrow(t);
                    }
                    throw new RuntimeException("Unsafe unavailable");
                }
            });
        }
    }

    private static void checkUnsafeInstance(Unsafe unsafe) {
        long arrayBaseOffset = unsafe.arrayBaseOffset(byte[].class);
        byte[] buffer = new byte[(int)arrayBaseOffset + 16];
        unsafe.putByte(buffer, arrayBaseOffset, (byte)0);
        unsafe.putBoolean(buffer, arrayBaseOffset, false);
        unsafe.putChar(buffer, QuickMath.normalize(arrayBaseOffset, 2), '0');
        unsafe.putShort(buffer, QuickMath.normalize(arrayBaseOffset, 2), (short)1);
        unsafe.putInt(buffer, QuickMath.normalize(arrayBaseOffset, 4), 2);
        unsafe.putFloat(buffer, QuickMath.normalize(arrayBaseOffset, 4), 3.0f);
        unsafe.putLong(buffer, QuickMath.normalize(arrayBaseOffset, 8), 4L);
        unsafe.putDouble(buffer, QuickMath.normalize(arrayBaseOffset, 8), 5.0);
        unsafe.copyMemory(new byte[buffer.length], arrayBaseOffset, buffer, arrayBaseOffset, buffer.length);
    }

    private static void logFailureToFindUnsafeDueTo(Throwable reason) {
        if (LOGGER.isFinestEnabled()) {
            LOGGER.finest("Unable to get an instance of Unsafe. Unsafe-based operations will be unavailable", reason);
        } else {
            LOGGER.warning("Unable to get an instance of Unsafe. Unsafe-based operations will be unavailable");
        }
    }

    static {
        Unsafe unsafe;
        LOGGER = Logger.getLogger(UnsafeUtil.class);
        try {
            unsafe = UnsafeUtil.findUnsafe();
            if (unsafe != null) {
                UnsafeUtil.checkUnsafeInstance(unsafe);
            }
        }
        catch (Throwable t) {
            unsafe = null;
            UnsafeUtil.logFailureToFindUnsafeDueTo(t);
        }
        UNSAFE = unsafe;
        UNSAFE_AVAILABLE = UNSAFE != null;
    }
}

