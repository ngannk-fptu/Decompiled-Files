/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.hashslot.impl;

import com.hazelcast.internal.memory.MemoryManager;
import com.hazelcast.internal.util.hashslot.impl.HashSlotArray8byteKeyImpl;

public class HashSlotArray8byteKeyNoValue
extends HashSlotArray8byteKeyImpl {
    public HashSlotArray8byteKeyNoValue(long unassignedSentinel, MemoryManager mm, int initialCapacity, float loadFactor) {
        super(unassignedSentinel, 0L, mm, 0, initialCapacity, loadFactor);
    }

    public HashSlotArray8byteKeyNoValue(long unassignedSentinel, MemoryManager mm) {
        super(unassignedSentinel, 0L, mm, 0, 16, 0.6f);
    }
}

