/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.internal.memory.impl.AlignmentUtil;
import com.hazelcast.logging.Logger;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.util.QuickMath;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

@Deprecated
@PrivateApi
public final class UnsafeHelper {
    @Deprecated
    public static final Unsafe UNSAFE;
    @Deprecated
    public static final boolean UNSAFE_AVAILABLE;
    public static final long BYTE_ARRAY_BASE_OFFSET;
    public static final long BOOLEAN_ARRAY_BASE_OFFSET;
    public static final long SHORT_ARRAY_BASE_OFFSET;
    public static final long CHAR_ARRAY_BASE_OFFSET;
    public static final long INT_ARRAY_BASE_OFFSET;
    public static final long FLOAT_ARRAY_BASE_OFFSET;
    public static final long LONG_ARRAY_BASE_OFFSET;
    public static final long DOUBLE_ARRAY_BASE_OFFSET;
    public static final int BOOLEAN_ARRAY_INDEX_SCALE;
    public static final int BYTE_ARRAY_INDEX_SCALE;
    public static final int SHORT_ARRAY_INDEX_SCALE;
    public static final int CHAR_ARRAY_INDEX_SCALE;
    public static final int INT_ARRAY_INDEX_SCALE;
    public static final int FLOAT_ARRAY_INDEX_SCALE;
    public static final int LONG_ARRAY_INDEX_SCALE;
    public static final int DOUBLE_ARRAY_INDEX_SCALE;
    public static final int MEM_COPY_THRESHOLD = 0x100000;
    private static final String UNSAFE_MODE_PROPERTY_NAME = "hazelcast.unsafe.mode";
    private static final String UNSAFE_EXPLICITLY_DISABLED = "disabled";
    private static final String UNSAFE_EXPLICITLY_ENABLED = "enforced";
    private static final String UNSAFE_WARNING_WHEN_NOT_FOUND = "sun.misc.Unsafe isn't available, some features might be not available";
    private static final String UNSAFE_WARNING_WHEN_EXPLICTLY_DISABLED = "sun.misc.Unsafe has been disabled via System Property hazelcast.unsafe.mode, some features might be not available.";
    private static final String UNSAFE_WARNING_WHEN_UNALIGNED_ACCESS_NOT_ALLOWED = "sun.misc.Unsafe has been disabled because your platform does not support unaligned access to memory, some features might be not available.";
    private static final String UNSAFE_WARNING_WHEN_ENFORCED_ON_PLATFORM_WHERE_NOT_SUPPORTED = "You platform does not seem to support unaligned access to memory. Unsafe usage has been enforced via System Property hazelcast.unsafe.mode This is not a supported configuration and it can crash JVM or corrupt your data!";

    private UnsafeHelper() {
    }

    private static long arrayBaseOffset(Class<?> type, Unsafe unsafe) {
        return unsafe == null ? -1L : (long)unsafe.arrayBaseOffset(type);
    }

    private static int arrayIndexScale(Class<?> type, Unsafe unsafe) {
        return unsafe == null ? -1 : unsafe.arrayIndexScale(type);
    }

    static Unsafe findUnsafeIfAllowed() {
        Unsafe unsafe;
        if (UnsafeHelper.isUnsafeExplicitlyDisabled()) {
            Logger.getLogger(UnsafeHelper.class).warning(UNSAFE_WARNING_WHEN_EXPLICTLY_DISABLED);
            return null;
        }
        if (!UnsafeHelper.isUnalignedAccessAllowed()) {
            if (UnsafeHelper.isUnsafeExplicitlyEnforced()) {
                Logger.getLogger(UnsafeHelper.class).warning(UNSAFE_WARNING_WHEN_ENFORCED_ON_PLATFORM_WHERE_NOT_SUPPORTED);
            } else {
                Logger.getLogger(UnsafeHelper.class).warning(UNSAFE_WARNING_WHEN_UNALIGNED_ACCESS_NOT_ALLOWED);
                return null;
            }
        }
        if ((unsafe = UnsafeHelper.findUnsafe()) == null) {
            Logger.getLogger(UnsafeHelper.class).warning(UNSAFE_WARNING_WHEN_NOT_FOUND);
        }
        return unsafe;
    }

    private static boolean isUnsafeExplicitlyDisabled() {
        String mode = System.getProperty(UNSAFE_MODE_PROPERTY_NAME);
        return UNSAFE_EXPLICITLY_DISABLED.equals(mode);
    }

    private static boolean isUnsafeExplicitlyEnforced() {
        String mode = System.getProperty(UNSAFE_MODE_PROPERTY_NAME);
        return UNSAFE_EXPLICITLY_ENABLED.equals(mode);
    }

    static boolean isUnalignedAccessAllowed() {
        return AlignmentUtil.isUnalignedAccessAllowed();
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
                    catch (Exception e) {
                        throw new RuntimeException("Unsafe unavailable", e);
                    }
                    throw new RuntimeException("Unsafe unavailable");
                }
            });
        }
    }

    static {
        Unsafe unsafe;
        try {
            unsafe = UnsafeHelper.findUnsafeIfAllowed();
        }
        catch (RuntimeException e) {
            unsafe = null;
        }
        UNSAFE = unsafe;
        BYTE_ARRAY_BASE_OFFSET = UnsafeHelper.arrayBaseOffset(byte[].class, unsafe);
        BOOLEAN_ARRAY_BASE_OFFSET = UnsafeHelper.arrayBaseOffset(boolean[].class, unsafe);
        SHORT_ARRAY_BASE_OFFSET = UnsafeHelper.arrayBaseOffset(short[].class, unsafe);
        CHAR_ARRAY_BASE_OFFSET = UnsafeHelper.arrayBaseOffset(char[].class, unsafe);
        INT_ARRAY_BASE_OFFSET = UnsafeHelper.arrayBaseOffset(int[].class, unsafe);
        FLOAT_ARRAY_BASE_OFFSET = UnsafeHelper.arrayBaseOffset(float[].class, unsafe);
        LONG_ARRAY_BASE_OFFSET = UnsafeHelper.arrayBaseOffset(long[].class, unsafe);
        DOUBLE_ARRAY_BASE_OFFSET = UnsafeHelper.arrayBaseOffset(double[].class, unsafe);
        BYTE_ARRAY_INDEX_SCALE = UnsafeHelper.arrayIndexScale(byte[].class, unsafe);
        BOOLEAN_ARRAY_INDEX_SCALE = UnsafeHelper.arrayIndexScale(boolean[].class, unsafe);
        SHORT_ARRAY_INDEX_SCALE = UnsafeHelper.arrayIndexScale(short[].class, unsafe);
        CHAR_ARRAY_INDEX_SCALE = UnsafeHelper.arrayIndexScale(char[].class, unsafe);
        INT_ARRAY_INDEX_SCALE = UnsafeHelper.arrayIndexScale(int[].class, unsafe);
        FLOAT_ARRAY_INDEX_SCALE = UnsafeHelper.arrayIndexScale(float[].class, unsafe);
        LONG_ARRAY_INDEX_SCALE = UnsafeHelper.arrayIndexScale(long[].class, unsafe);
        DOUBLE_ARRAY_INDEX_SCALE = UnsafeHelper.arrayIndexScale(double[].class, unsafe);
        boolean unsafeAvailable = false;
        try {
            if (unsafe != null) {
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
                unsafeAvailable = true;
            }
        }
        catch (Throwable e) {
            Logger.getLogger(UnsafeHelper.class).warning(UNSAFE_WARNING_WHEN_NOT_FOUND);
        }
        UNSAFE_AVAILABLE = unsafeAvailable;
    }
}

