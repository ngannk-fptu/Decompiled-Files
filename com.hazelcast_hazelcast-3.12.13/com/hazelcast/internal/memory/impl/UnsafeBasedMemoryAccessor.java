/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.memory.impl;

import com.hazelcast.internal.memory.GlobalMemoryAccessor;
import com.hazelcast.internal.memory.impl.AlignmentUtil;
import com.hazelcast.internal.memory.impl.UnsafeUtil;

abstract class UnsafeBasedMemoryAccessor
implements GlobalMemoryAccessor {
    UnsafeBasedMemoryAccessor() {
    }

    public static boolean isAvailable() {
        return UnsafeUtil.UNSAFE_AVAILABLE;
    }

    @Override
    public boolean isBigEndian() {
        return AlignmentUtil.IS_PLATFORM_BIG_ENDIAN;
    }
}

