/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.hashslot.impl;

import com.hazelcast.internal.memory.MemoryAccessor;
import com.hazelcast.internal.memory.MemoryAllocator;
import com.hazelcast.internal.memory.MemoryManager;
import com.hazelcast.internal.util.hashslot.HashSlotArray;
import com.hazelcast.internal.util.hashslot.HashSlotCursor12byteKey;
import com.hazelcast.internal.util.hashslot.HashSlotCursor16byteKey;
import com.hazelcast.internal.util.hashslot.HashSlotCursor8byteKey;
import com.hazelcast.internal.util.hashslot.SlotAssignmentResult;
import com.hazelcast.internal.util.hashslot.impl.CapacityUtil;
import com.hazelcast.internal.util.hashslot.impl.SlotAssignmentResultImpl;
import com.hazelcast.util.HashUtil;

public abstract class HashSlotArrayBase
implements HashSlotArray {
    public static final int HEADER_SIZE = 24;
    public static final int CAPACITY_OFFSET = -8;
    public static final int SIZE_OFFSET = -16;
    public static final int EXPAND_THRESHOLD_OFFSET = -24;
    protected static final int VALUE_SIZE_GRANULARITY = 8;
    protected static final int KEY_1_OFFSET = 0;
    protected static final int KEY_2_OFFSET = 8;
    protected final long unassignedSentinel;
    protected final long offsetOfUnassignedSentinel;
    protected final int slotLength;
    private MemoryAccessor mem;
    private MemoryAllocator malloc;
    private MemoryAllocator auxMalloc;
    private long baseAddress = 24L;
    private final int initialCapacity;
    private final int valueOffset;
    private final int valueLength;
    private final float loadFactor;
    private final SlotAssignmentResultImpl slotAssignmentResult = new SlotAssignmentResultImpl();

    protected HashSlotArrayBase(long unassignedSentinel, long offsetOfUnassignedSentinel, MemoryManager mm, MemoryAllocator auxMalloc, int keyLength, int valueLength, int initialCapacity, float loadFactor) {
        this.unassignedSentinel = unassignedSentinel;
        this.offsetOfUnassignedSentinel = offsetOfUnassignedSentinel;
        if (mm != null) {
            this.malloc = mm.getAllocator();
            this.mem = mm.getAccessor();
        }
        this.auxMalloc = auxMalloc;
        this.valueOffset = keyLength;
        this.valueLength = valueLength;
        this.slotLength = keyLength + valueLength;
        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
    }

    @Override
    public final long address() {
        return this.baseAddress;
    }

    @Override
    public final void gotoAddress(long address) {
        this.baseAddress = address;
    }

    @Override
    public final long gotoNew() {
        this.allocateInitial();
        return this.address();
    }

    @Override
    public final long size() {
        this.assertValid();
        return this.mem.getLong(this.baseAddress + -16L);
    }

    @Override
    public final long capacity() {
        this.assertValid();
        return this.mem.getLong(this.baseAddress + -8L);
    }

    @Override
    public final long expansionThreshold() {
        this.assertValid();
        return this.mem.getLong(this.baseAddress + -24L);
    }

    @Override
    public final void clear() {
        this.assertValid();
        this.markAllUnassigned();
        this.setSize(0L);
    }

    @Override
    public final boolean trimToSize() {
        long minCapacity = HashSlotArrayBase.minCapacityForSize(this.size(), this.loadFactor);
        if (this.capacity() <= minCapacity) {
            return false;
        }
        this.resizeTo(minCapacity);
        assert (this.expansionThreshold() >= this.size()) : String.format("trimToSize() shrunk the capacity to %,d and expandAt to %,d, which is less than the current size %,d", this.capacity(), this.expansionThreshold(), this.size());
        return true;
    }

    @Override
    public final void dispose() {
        if (this.baseAddress <= 24L) {
            return;
        }
        this.malloc.free(this.baseAddress - 24L, 24L + this.capacity() * (long)this.slotLength);
        this.baseAddress = -1L;
    }

    public final void migrateTo(MemoryAllocator newMalloc) {
        this.baseAddress = this.move(this.baseAddress, this.capacity(), this.malloc, newMalloc);
        this.malloc = newMalloc;
        this.auxMalloc = null;
    }

    protected final SlotAssignmentResult ensure0(long key1, long key2) {
        this.assertValid();
        long size = this.size();
        if (size == this.expansionThreshold()) {
            this.resizeTo(CapacityUtil.nextCapacity(this.capacity()));
        }
        long slot = this.keyHash(key1, key2) & this.mask();
        while (this.isSlotAssigned(slot)) {
            if (this.equal(this.key1OfSlot(slot), this.key2OfSlot(slot), key1, key2)) {
                this.slotAssignmentResult.setAddress(this.valueAddrOfSlot(slot));
                this.slotAssignmentResult.setNew(false);
                return this.slotAssignmentResult;
            }
            slot = slot + 1L & this.mask();
        }
        this.setSize(size + 1L);
        this.putKey(this.baseAddress, slot, key1, key2);
        this.slotAssignmentResult.setAddress(this.valueAddrOfSlot(slot));
        this.slotAssignmentResult.setNew(true);
        return this.slotAssignmentResult;
    }

    protected final long get0(long key1, long key2) {
        long slot;
        this.assertValid();
        long wrappedAround = slot = this.keyHash(key1, key2) & this.mask();
        while (this.isSlotAssigned(slot)) {
            if (this.equal(this.key1OfSlot(slot), this.key2OfSlot(slot), key1, key2)) {
                return this.valueAddrOfSlot(slot);
            }
            if ((slot = slot + 1L & this.mask()) != wrappedAround) continue;
            break;
        }
        return 0L;
    }

    protected final boolean remove0(long key1, long key2) {
        long slot;
        this.assertValid();
        long wrappedAround = slot = this.keyHash(key1, key2) & this.mask();
        while (this.isSlotAssigned(slot)) {
            if (this.equal(this.key1OfSlot(slot), this.key2OfSlot(slot), key1, key2)) {
                this.setSize(this.size() - 1L);
                this.shiftConflictingKeys(slot);
                return true;
            }
            if ((slot = slot + 1L & this.mask()) != wrappedAround) continue;
            break;
        }
        return false;
    }

    protected final void shiftConflictingKeys(long slotCurr) {
        long slotPrev;
        long mask = this.mask();
        while (true) {
            slotPrev = slotCurr;
            slotCurr = slotPrev + 1L & mask;
            while (this.isSlotAssigned(slotCurr)) {
                long slotOther = this.slotHash(this.baseAddress, slotCurr) & mask;
                if (slotPrev <= slotCurr ? slotPrev >= slotOther || slotOther > slotCurr : slotPrev >= slotOther && slotOther > slotCurr) break;
                slotCurr = slotCurr + 1L & mask;
            }
            if (!this.isSlotAssigned(slotCurr)) break;
            this.putKey(this.baseAddress, slotPrev, this.key1OfSlot(slotCurr), this.key2OfSlot(slotCurr));
            this.mem.copyMemory(this.valueAddrOfSlot(slotCurr), this.valueAddrOfSlot(slotPrev), this.valueLength);
        }
        this.markUnassigned(this.baseAddress, slotPrev);
    }

    protected final void allocateArrayAndAdjustFields(long size, long newCapacity) {
        this.baseAddress = this.malloc.allocate(24L + newCapacity * (long)this.slotLength) + 24L;
        this.setSize(size);
        this.setCapacity(newCapacity);
        this.setExpansionThreshold(HashSlotArrayBase.maxSizeForCapacity(newCapacity, this.loadFactor));
        this.markAllUnassigned();
    }

    protected final void rehash(long oldCapacity, long oldAddress) {
        long mask = this.mask();
        long slot = oldCapacity;
        while (--slot >= 0L) {
            if (!this.isAssigned(oldAddress, slot)) continue;
            long newSlot = this.slotHash(oldAddress, slot) & mask;
            while (this.isSlotAssigned(newSlot)) {
                newSlot = newSlot + 1L & mask;
            }
            this.putKey(this.baseAddress, newSlot, this.key1OfSlot(oldAddress, slot), this.key2OfSlot(oldAddress, slot));
            long valueAddrOfOldSlot = this.slotBase(oldAddress, slot) + (long)this.valueOffset;
            this.mem.copyMemory(valueAddrOfOldSlot, this.valueAddrOfSlot(newSlot), this.valueLength);
        }
    }

    protected final void setMemMgr(MemoryManager memoryManager) {
        this.mem = memoryManager.getAccessor();
        this.malloc = memoryManager.getAllocator();
    }

    protected final void assertValid() {
        assert (this.baseAddress - 24L != 0L) : "This instance doesn't point to a valid hashtable. Base address = " + this.baseAddress;
    }

    protected final MemoryAllocator malloc() {
        return this.malloc;
    }

    protected final MemoryAccessor mem() {
        return this.mem;
    }

    protected final long slotBase(long baseAddr, long slot) {
        return baseAddr + (long)this.slotLength * slot;
    }

    protected void resizeTo(long newCapacity) {
        MemoryAllocator oldMalloc;
        long oldAddress;
        long oldCapacity = this.capacity();
        long oldAllocatedSize = 24L + oldCapacity * (long)this.slotLength;
        if (this.auxMalloc != null) {
            long size = this.size();
            oldAddress = this.move(this.baseAddress, oldCapacity, this.malloc, this.auxMalloc);
            oldMalloc = this.auxMalloc;
            this.auxAllocateAndAdjustFields(oldAddress, size, oldCapacity, newCapacity);
        } else {
            oldMalloc = this.malloc;
            oldAddress = this.baseAddress;
            this.allocateArrayAndAdjustFields(this.size(), newCapacity);
        }
        this.rehash(oldCapacity, oldAddress);
        oldMalloc.free(oldAddress - 24L, oldAllocatedSize);
    }

    protected long key1OfSlot(long baseAddress, long slot) {
        return this.mem.getLong(this.slotBase(baseAddress, slot) + 0L);
    }

    protected long key2OfSlot(long baseAddress, long slot) {
        return this.mem.getLong(this.slotBase(baseAddress, slot) + 8L);
    }

    protected boolean isAssigned(long baseAddress, long slot) {
        return this.mem.getLong(this.slotBase(baseAddress, slot) + this.offsetOfUnassignedSentinel) != this.unassignedSentinel;
    }

    protected void markUnassigned(long baseAddress, long slot) {
        this.mem.putLong(this.slotBase(baseAddress, slot) + this.offsetOfUnassignedSentinel, this.unassignedSentinel);
    }

    protected void putKey(long baseAddress, long slot, long key1, long key2) {
        long slotBase = this.slotBase(baseAddress, slot);
        this.mem.putLong(slotBase + 0L, key1);
        this.mem.putLong(slotBase + 8L, key2);
    }

    protected long keyHash(long key1, long key2) {
        return HashUtil.fastLongMix(HashUtil.fastLongMix(key1) + key2);
    }

    protected long slotHash(long baseAddress, long slot) {
        return this.keyHash(this.key1OfSlot(baseAddress, slot), this.key2OfSlot(baseAddress, slot));
    }

    protected boolean equal(long key1a, long key2a, long key1b, long key2b) {
        return key1a == key1b && key2a == key2b;
    }

    private void setCapacity(long capacity) {
        this.assertValid();
        this.mem.putLong(this.baseAddress + -8L, capacity);
    }

    private void setExpansionThreshold(long thresh) {
        this.assertValid();
        this.mem.putLong(this.baseAddress + -24L, thresh);
    }

    private long mask() {
        return this.capacity() - 1L;
    }

    private void setSize(long newSize) {
        this.mem.putLong(this.baseAddress + -16L, newSize);
    }

    private void allocateInitial() {
        this.allocateArrayAndAdjustFields(0L, CapacityUtil.roundCapacity((int)((float)this.initialCapacity / this.loadFactor)));
    }

    private long key1OfSlot(long slot) {
        return this.key1OfSlot(this.baseAddress, slot);
    }

    private long key2OfSlot(long slot) {
        return this.key2OfSlot(this.baseAddress, slot);
    }

    private long valueAddrOfSlot(long slot) {
        return this.slotBase(this.baseAddress, slot) + (long)this.valueOffset;
    }

    private boolean isSlotAssigned(long slot) {
        return this.isAssigned(this.baseAddress, slot);
    }

    private void auxAllocateAndAdjustFields(long auxAddress, long size, long oldCapacity, long newCapacity) {
        try {
            this.allocateArrayAndAdjustFields(size, newCapacity);
        }
        catch (Error e) {
            try {
                this.baseAddress = this.move(auxAddress, oldCapacity, this.auxMalloc, this.malloc);
            }
            catch (Error e1) {
                this.baseAddress = 0L;
            }
            throw e;
        }
    }

    private long move(long fromBaseAddress, long capacity, MemoryAllocator fromMalloc, MemoryAllocator toMalloc) {
        long allocatedSize = 24L + capacity * (long)this.slotLength;
        long toBaseAddress = toMalloc.allocate(allocatedSize) + 24L;
        this.mem.copyMemory(fromBaseAddress - 24L, toBaseAddress - 24L, allocatedSize);
        fromMalloc.free(fromBaseAddress - 24L, allocatedSize);
        return toBaseAddress;
    }

    private void markAllUnassigned() {
        long capacity = this.capacity();
        for (long i = 0L; i < capacity; ++i) {
            this.markUnassigned(this.baseAddress, i);
        }
    }

    private static long maxSizeForCapacity(long capacity, float loadFactor) {
        return Math.max(2L, (long)Math.ceil((float)capacity * loadFactor)) - 1L;
    }

    private static long minCapacityForSize(long size, float loadFactor) {
        return CapacityUtil.roundCapacity((long)Math.ceil((float)size / loadFactor));
    }

    protected final class CursorLongKey2
    extends Cursor
    implements HashSlotCursor16byteKey {
        protected CursorLongKey2() {
        }

        @Override
        public long key2() {
            this.assertCursorValid();
            return HashSlotArrayBase.this.key2OfSlot(this.currentSlot);
        }
    }

    protected final class CursorIntKey2
    extends Cursor
    implements HashSlotCursor12byteKey {
        protected CursorIntKey2() {
        }

        @Override
        public int key2() {
            this.assertCursorValid();
            return (int)HashSlotArrayBase.this.key2OfSlot(this.currentSlot);
        }
    }

    protected class Cursor
    implements HashSlotCursor8byteKey {
        long currentSlot;

        public Cursor() {
            this.reset();
        }

        @Override
        public final void reset() {
            this.currentSlot = -1L;
        }

        @Override
        public final boolean advance() {
            HashSlotArrayBase.this.assertValid();
            assert (this.currentSlot != Long.MIN_VALUE) : "Cursor has advanced past the last slot";
            if (this.tryAdvance()) {
                return true;
            }
            this.currentSlot = Long.MIN_VALUE;
            return false;
        }

        @Override
        public final long key() {
            return this.key1();
        }

        public final long key1() {
            this.assertCursorValid();
            return HashSlotArrayBase.this.key1OfSlot(this.currentSlot);
        }

        @Override
        public final long valueAddress() {
            this.assertCursorValid();
            return HashSlotArrayBase.this.valueAddrOfSlot(this.currentSlot);
        }

        final void assertCursorValid() {
            HashSlotArrayBase.this.assertValid();
            assert (this.currentSlot >= 0L) : "Cursor is invalid";
        }

        private boolean tryAdvance() {
            long capacity = HashSlotArrayBase.this.capacity();
            for (long slot = this.currentSlot + 1L; slot < capacity; ++slot) {
                if (!HashSlotArrayBase.this.isSlotAssigned(slot)) continue;
                this.currentSlot = slot;
                return true;
            }
            return false;
        }
    }
}

