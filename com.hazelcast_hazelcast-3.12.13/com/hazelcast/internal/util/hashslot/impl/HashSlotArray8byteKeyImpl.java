/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.hashslot.impl;

import com.hazelcast.internal.memory.MemoryManager;
import com.hazelcast.internal.util.hashslot.HashSlotArray8byteKey;
import com.hazelcast.internal.util.hashslot.HashSlotCursor8byteKey;
import com.hazelcast.internal.util.hashslot.SlotAssignmentResult;
import com.hazelcast.internal.util.hashslot.impl.HashSlotArrayBase;
import com.hazelcast.util.HashUtil;
import com.hazelcast.util.QuickMath;

public class HashSlotArray8byteKeyImpl
extends HashSlotArrayBase
implements HashSlotArray8byteKey {
    private static final int KEY_SIZE = 8;

    public HashSlotArray8byteKeyImpl(long unassignedSentinel, MemoryManager mm, int valueLength, int initialCapacity, float loadFactor) {
        this(unassignedSentinel, 8L, mm, valueLength, initialCapacity, loadFactor);
        assert (valueLength > 0) : "Attempted to instantiate HashSlotArrayImpl with zero value length";
    }

    public HashSlotArray8byteKeyImpl(long unassignedSentinel, MemoryManager mm, int valueLength) {
        this(unassignedSentinel, mm, valueLength, 16, 0.6f);
        assert (valueLength > 0) : "Attempted to instantiate HashSlotArrayImpl with zero value length";
    }

    protected HashSlotArray8byteKeyImpl(long unassignedSentinel, long offsetOfUnassignedSentinel, MemoryManager mm, int valueLength, int initialCapacity, float loadFactor) {
        super(unassignedSentinel, offsetOfUnassignedSentinel, mm, null, 8, valueLength, initialCapacity, loadFactor);
        assert (QuickMath.modPowerOfTwo(valueLength, 8) == 0) : "Value size must be a positive multiple of 8, but was " + valueLength;
    }

    @Override
    public SlotAssignmentResult ensure(long key) {
        return super.ensure0(key, 0L);
    }

    @Override
    public long get(long key) {
        return super.get0(key, 0L);
    }

    @Override
    public boolean remove(long key) {
        return super.remove0(key, 0L);
    }

    @Override
    public HashSlotCursor8byteKey cursor() {
        return new HashSlotArrayBase.Cursor();
    }

    @Override
    protected long key2OfSlot(long baseAddress, long slot) {
        return 0L;
    }

    @Override
    protected void putKey(long baseAddress, long slot, long key, long ignored) {
        this.mem().putLong(this.slotBase(baseAddress, slot) + 0L, key);
    }

    @Override
    protected long keyHash(long key, long ignored) {
        return HashUtil.fastLongMix(key);
    }
}

