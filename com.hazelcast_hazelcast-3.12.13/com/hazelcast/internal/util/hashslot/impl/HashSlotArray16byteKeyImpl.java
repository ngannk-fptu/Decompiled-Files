/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.hashslot.impl;

import com.hazelcast.internal.memory.MemoryAllocator;
import com.hazelcast.internal.memory.MemoryManager;
import com.hazelcast.internal.util.hashslot.HashSlotArray16byteKey;
import com.hazelcast.internal.util.hashslot.HashSlotCursor16byteKey;
import com.hazelcast.internal.util.hashslot.SlotAssignmentResult;
import com.hazelcast.internal.util.hashslot.impl.HashSlotArrayBase;
import com.hazelcast.util.QuickMath;

public class HashSlotArray16byteKeyImpl
extends HashSlotArrayBase
implements HashSlotArray16byteKey {
    private static final int KEY_SIZE = 16;

    public HashSlotArray16byteKeyImpl(long nullSentinel, MemoryManager memMgr, MemoryAllocator auxMalloc, int valueLength, int initialCapacity, float loadFactor) {
        this(nullSentinel, 16L, memMgr, auxMalloc, valueLength, initialCapacity, loadFactor);
        assert (this.valueLengthValid(valueLength)) : "Invalid value length: " + valueLength;
    }

    public HashSlotArray16byteKeyImpl(long nullSentinel, MemoryManager memMgr, int valueLength, int initialCapacity, float loadFactor) {
        this(nullSentinel, memMgr, null, valueLength, initialCapacity, loadFactor);
    }

    public HashSlotArray16byteKeyImpl(long nullSentinel, MemoryManager mm, int valueLength) {
        this(nullSentinel, mm, null, valueLength, 16, 0.6f);
    }

    protected HashSlotArray16byteKeyImpl(long nullSentinel, long offsetOfNullSentinel, MemoryManager mm, MemoryAllocator auxMalloc, int valueLength, int initialCapacity, float loadFactor) {
        super(nullSentinel, offsetOfNullSentinel, mm, auxMalloc, 16, valueLength, initialCapacity, loadFactor);
        assert (QuickMath.modPowerOfTwo(valueLength, 8) == 0) : "Value length must be a positive multiple of 8, but was " + valueLength;
    }

    @Override
    public SlotAssignmentResult ensure(long key1, long key2) {
        return super.ensure0(key1, key2);
    }

    @Override
    public long get(long key1, long key2) {
        return super.get0(key1, key2);
    }

    @Override
    public boolean remove(long key1, long key2) {
        return super.remove0(key1, key2);
    }

    @Override
    public HashSlotCursor16byteKey cursor() {
        return new HashSlotArrayBase.CursorLongKey2();
    }

    protected boolean valueLengthValid(int valueLength) {
        return valueLength > 0;
    }

    public static long addrOfKey1At(long slotBase) {
        return slotBase + 0L;
    }

    public static long addrOfKey2At(long slotBase) {
        return slotBase + 8L;
    }

    public static long addrOfValueAt(long slotBase) {
        return slotBase + 16L;
    }

    public static long valueAddr2slotBase(long valueAddr) {
        return valueAddr - 16L;
    }
}

