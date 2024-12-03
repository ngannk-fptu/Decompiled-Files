/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.hashslot.impl;

import com.hazelcast.internal.memory.MemoryManager;
import com.hazelcast.internal.util.hashslot.HashSlotArray12byteKey;
import com.hazelcast.internal.util.hashslot.HashSlotCursor12byteKey;
import com.hazelcast.internal.util.hashslot.SlotAssignmentResult;
import com.hazelcast.internal.util.hashslot.impl.HashSlotArrayBase;
import com.hazelcast.util.HashUtil;
import com.hazelcast.util.QuickMath;

public final class HashSlotArray12byteKeyImpl
extends HashSlotArrayBase
implements HashSlotArray12byteKey {
    private static final int KEY_SIZE = 12;

    public HashSlotArray12byteKeyImpl(int unassignedSentinel, MemoryManager mm, int valueLength, int initialCapacity, float loadFactor) {
        this(unassignedSentinel, 12L, mm, valueLength, initialCapacity, loadFactor);
        assert (valueLength > 0) : "Attempted to instantiate HashSlotArrayImpl with zero value length";
    }

    public HashSlotArray12byteKeyImpl(int unassignedSentinel, MemoryManager mm, int valueLength) {
        this(unassignedSentinel, mm, valueLength, 16, 0.6f);
        assert (valueLength > 0) : "Attempted to instantiate HashSlotArrayImpl with zero value length";
    }

    private HashSlotArray12byteKeyImpl(int unassignedSentinel, long offsetOfUnassignedSentinel, MemoryManager mm, int valueLength, int initialCapacity, float loadFactor) {
        super(unassignedSentinel, offsetOfUnassignedSentinel, mm, null, 12, valueLength, initialCapacity, loadFactor);
        assert (valueLength >= 4 && QuickMath.modPowerOfTwo(valueLength - 4, 8) == 0) : "Value length must be 4 plus a positive multiple of 8, but was " + valueLength;
    }

    @Override
    public SlotAssignmentResult ensure(long key1, int key2) {
        return super.ensure0(key1, key2);
    }

    @Override
    public long get(long key1, int key2) {
        return super.get0(key1, key2);
    }

    @Override
    public boolean remove(long key1, int key2) {
        return super.remove0(key1, key2);
    }

    @Override
    public HashSlotCursor12byteKey cursor() {
        return new HashSlotArrayBase.CursorIntKey2();
    }

    @Override
    protected long key2OfSlot(long baseAddress, long slot) {
        return this.mem().getInt(this.slotBase(baseAddress, slot) + 8L);
    }

    @Override
    protected void putKey(long baseAddress, long slot, long key1, long key2) {
        this.mem().putLong(this.slotBase(baseAddress, slot) + 0L, key1);
        this.mem().putInt(this.slotBase(baseAddress, slot) + 8L, (int)key2);
    }

    @Override
    protected void markUnassigned(long baseAddress, long slot) {
        this.mem().putInt(this.slotBase(baseAddress, slot) + this.offsetOfUnassignedSentinel, (int)this.unassignedSentinel);
    }

    @Override
    protected boolean isAssigned(long baseAddress, long slot) {
        return (long)this.mem().getInt(this.slotBase(baseAddress, slot) + this.offsetOfUnassignedSentinel) != this.unassignedSentinel;
    }

    @Override
    protected long keyHash(long key1, long key2) {
        return HashUtil.fastLongMix(key1 + (long)HashUtil.fastIntMix((int)key2));
    }
}

