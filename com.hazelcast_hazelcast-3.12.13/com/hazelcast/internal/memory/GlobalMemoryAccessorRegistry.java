/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.memory;

import com.hazelcast.internal.memory.GlobalMemoryAccessor;
import com.hazelcast.internal.memory.GlobalMemoryAccessorType;
import com.hazelcast.internal.memory.impl.AlignmentAwareMemoryAccessor;
import com.hazelcast.internal.memory.impl.AlignmentUtil;
import com.hazelcast.internal.memory.impl.StandardMemoryAccessor;
import java.util.EnumMap;
import java.util.Map;

public final class GlobalMemoryAccessorRegistry {
    public static final GlobalMemoryAccessor MEM;
    public static final boolean MEM_AVAILABLE;
    public static final GlobalMemoryAccessor AMEM;
    public static final boolean AMEM_AVAILABLE;
    private static final Map<GlobalMemoryAccessorType, GlobalMemoryAccessor> STORAGE;

    private GlobalMemoryAccessorRegistry() {
    }

    public static GlobalMemoryAccessor getGlobalMemoryAccessor(GlobalMemoryAccessorType type) {
        return STORAGE.get((Object)type);
    }

    public static GlobalMemoryAccessor getDefaultGlobalMemoryAccessor() {
        return STORAGE.get((Object)GlobalMemoryAccessorType.PLATFORM_AWARE);
    }

    static {
        STORAGE = new EnumMap<GlobalMemoryAccessorType, GlobalMemoryAccessor>(GlobalMemoryAccessorType.class);
        boolean unalignedAccessAllowed = AlignmentUtil.isUnalignedAccessAllowed();
        if (StandardMemoryAccessor.isAvailable()) {
            STORAGE.put(GlobalMemoryAccessorType.STANDARD, StandardMemoryAccessor.INSTANCE);
            if (unalignedAccessAllowed) {
                STORAGE.put(GlobalMemoryAccessorType.PLATFORM_AWARE, StandardMemoryAccessor.INSTANCE);
            }
        }
        if (AlignmentAwareMemoryAccessor.isAvailable()) {
            STORAGE.put(GlobalMemoryAccessorType.ALIGNMENT_AWARE, AlignmentAwareMemoryAccessor.INSTANCE);
            if (!STORAGE.containsKey((Object)GlobalMemoryAccessorType.PLATFORM_AWARE)) {
                STORAGE.put(GlobalMemoryAccessorType.PLATFORM_AWARE, AlignmentAwareMemoryAccessor.INSTANCE);
            }
        }
        MEM_AVAILABLE = (MEM = GlobalMemoryAccessorRegistry.getDefaultGlobalMemoryAccessor()) != null;
        AMEM = GlobalMemoryAccessorRegistry.getGlobalMemoryAccessor(GlobalMemoryAccessorType.STANDARD);
        AMEM_AVAILABLE = AMEM != null;
    }
}

