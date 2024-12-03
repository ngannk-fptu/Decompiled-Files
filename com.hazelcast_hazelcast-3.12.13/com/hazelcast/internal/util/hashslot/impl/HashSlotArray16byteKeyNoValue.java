/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.hashslot.impl;

import com.hazelcast.internal.memory.MemoryManager;
import com.hazelcast.internal.util.hashslot.SlotAssignmentResult;
import com.hazelcast.internal.util.hashslot.impl.HashSlotArray16byteKeyImpl;

public class HashSlotArray16byteKeyNoValue
extends HashSlotArray16byteKeyImpl {
    public HashSlotArray16byteKeyNoValue(long nullKey1, MemoryManager mm, int initialCapacity, float loadFactor) {
        super(nullKey1, 0L, mm, null, 0, initialCapacity, loadFactor);
    }

    public HashSlotArray16byteKeyNoValue(long nullKey1, MemoryManager mm) {
        this(nullKey1, mm, 16, 0.6f);
    }

    @Override
    protected boolean valueLengthValid(int valueLength) {
        return true;
    }

    @Override
    public SlotAssignmentResult ensure(long key1, long key2) {
        assert (key1 != this.unassignedSentinel) : "ensure() called with key1 == nullKey1 (" + this.unassignedSentinel + ')';
        return super.ensure0(key1, key2);
    }

    @Override
    public long get(long key1, long key2) {
        assert (key1 != this.unassignedSentinel) : "get() called with key1 == nullKey1 (" + this.unassignedSentinel + ')';
        return super.get0(key1, key2);
    }

    @Override
    public boolean remove(long key1, long key2) {
        assert (key1 != this.unassignedSentinel) : "remove() called with key1 == nullKey1 (" + this.unassignedSentinel + ')';
        return super.remove0(key1, key2);
    }
}

