/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.memory.impl;

import com.hazelcast.internal.memory.impl.UnsafeUtil;
import com.hazelcast.util.collection.ArrayUtils;
import java.nio.ByteOrder;

public final class AlignmentUtil {
    public static final int OBJECT_REFERENCE_ALIGN = UnsafeUtil.UNSAFE_AVAILABLE ? UnsafeUtil.UNSAFE.arrayIndexScale(Object[].class) : -1;
    public static final int OBJECT_REFERENCE_MASK = OBJECT_REFERENCE_ALIGN - 1;
    public static final boolean IS_PLATFORM_BIG_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
    private static final String[] ARCHITECTURES_KNOWN_TO_ALLOW_UNALIGNED_ACCESS = new String[]{"i386", "x86", "amd64", "x86_64"};

    private AlignmentUtil() {
    }

    public static boolean is2BytesAligned(long address) {
        return (address & 1L) == 0L;
    }

    public static boolean is4BytesAligned(long address) {
        return (address & 3L) == 0L;
    }

    public static boolean is8BytesAligned(long address) {
        return (address & 7L) == 0L;
    }

    public static boolean isReferenceAligned(long address) {
        return (address & (long)OBJECT_REFERENCE_MASK) == 0L;
    }

    public static void checkReferenceAligned(long address) {
        if (!AlignmentUtil.isReferenceAligned(address)) {
            throw new IllegalArgumentException("Memory access to object references must be " + OBJECT_REFERENCE_ALIGN + "-bytes aligned, but the address used was " + address);
        }
    }

    public static void check2BytesAligned(long address) {
        if (!AlignmentUtil.is2BytesAligned(address)) {
            throw new IllegalArgumentException("Atomic memory access must be aligned, but the address used was " + address);
        }
    }

    public static void check4BytesAligned(long address) {
        if (!AlignmentUtil.is4BytesAligned(address)) {
            throw new IllegalArgumentException("Atomic memory access must be aligned, but the address used was " + address);
        }
    }

    public static void check8BytesAligned(long address) {
        if (!AlignmentUtil.is8BytesAligned(address)) {
            throw new IllegalArgumentException("Atomic memory access must be aligned, but the address used was " + address);
        }
    }

    public static boolean isUnalignedAccessAllowed() {
        String currentArchitecture = System.getProperty("os.arch");
        return ArrayUtils.contains(ARCHITECTURES_KNOWN_TO_ALLOW_UNALIGNED_ACCESS, currentArchitecture);
    }
}

