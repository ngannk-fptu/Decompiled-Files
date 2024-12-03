/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.collection;

import com.hazelcast.internal.memory.MemoryAccessor;
import com.hazelcast.internal.memory.MemoryManager;
import com.hazelcast.internal.util.collection.Long2LongMap;
import com.hazelcast.internal.util.collection.LongLongCursor;
import com.hazelcast.internal.util.hashslot.HashSlotArray8byteKey;
import com.hazelcast.internal.util.hashslot.HashSlotCursor8byteKey;
import com.hazelcast.internal.util.hashslot.SlotAssignmentResult;
import com.hazelcast.internal.util.hashslot.impl.HashSlotArray8byteKeyImpl;

public class Long2LongMapHsa
implements Long2LongMap {
    private final HashSlotArray8byteKey hsa;
    private final long nullValue;
    private MemoryAccessor mem;

    public Long2LongMapHsa(long nullValue, MemoryManager memMgr) {
        this.hsa = new HashSlotArray8byteKeyImpl(nullValue, memMgr, 8);
        this.hsa.gotoNew();
        this.mem = memMgr.getAccessor();
        this.nullValue = nullValue;
    }

    @Override
    public long get(long key) {
        long valueAddr = this.hsa.get(key);
        return valueAddr != 0L ? this.mem.getLong(valueAddr) : this.nullValue;
    }

    @Override
    public long put(long key, long value) {
        assert (value != this.nullValue) : "put() called with null-sentinel value " + this.nullValue;
        SlotAssignmentResult slot = this.hsa.ensure(key);
        long result = !slot.isNew() ? this.mem.getLong(slot.address()) : this.nullValue;
        this.mem.putLong(slot.address(), value);
        return result;
    }

    @Override
    public long putIfAbsent(long key, long value) {
        assert (value != this.nullValue) : "putIfAbsent() called with null-sentinel value " + this.nullValue;
        SlotAssignmentResult slot = this.hsa.ensure(key);
        if (slot.isNew()) {
            this.mem.putLong(slot.address(), value);
            return this.nullValue;
        }
        return this.mem.getLong(slot.address());
    }

    @Override
    public void putAll(Long2LongMap from) {
        LongLongCursor cursor = from.cursor();
        while (cursor.advance()) {
            this.put(cursor.key(), cursor.value());
        }
    }

    @Override
    public boolean replace(long key, long oldValue, long newValue) {
        assert (oldValue != this.nullValue) : "replace() called with null-sentinel oldValue " + this.nullValue;
        assert (newValue != this.nullValue) : "replace() called with null-sentinel newValue " + this.nullValue;
        long valueAddr = this.hsa.get(key);
        if (valueAddr == 0L) {
            return false;
        }
        long actualValue = this.mem.getLong(valueAddr);
        if (actualValue != oldValue) {
            return false;
        }
        this.mem.putLong(valueAddr, newValue);
        return true;
    }

    @Override
    public long replace(long key, long value) {
        assert (value != this.nullValue) : "replace() called with null-sentinel value " + this.nullValue;
        long valueAddr = this.hsa.get(key);
        if (valueAddr == 0L) {
            return this.nullValue;
        }
        long oldValue = this.mem.getLong(valueAddr);
        this.mem.putLong(valueAddr, value);
        return oldValue;
    }

    @Override
    public long remove(long key) {
        long valueAddr = this.hsa.get(key);
        if (valueAddr == 0L) {
            return this.nullValue;
        }
        long oldValue = this.mem.getLong(valueAddr);
        this.hsa.remove(key);
        return oldValue;
    }

    @Override
    public boolean remove(long key, long value) {
        assert (value != this.nullValue) : "remove() called with null-sentinel value " + this.nullValue;
        long valueAddr = this.hsa.get(key);
        if (valueAddr == 0L) {
            return false;
        }
        long actualValue = this.mem.getLong(valueAddr);
        if (actualValue == value) {
            this.hsa.remove(key);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsKey(long key) {
        return this.hsa.get(key) != 0L;
    }

    @Override
    public long size() {
        return this.hsa.size();
    }

    @Override
    public boolean isEmpty() {
        return this.hsa.size() == 0L;
    }

    @Override
    public void clear() {
        this.hsa.clear();
    }

    @Override
    public void dispose() {
        this.hsa.dispose();
    }

    @Override
    public LongLongCursor cursor() {
        return new Cursor(this.hsa);
    }

    private final class Cursor
    implements LongLongCursor {
        private final HashSlotCursor8byteKey cursor;

        Cursor(HashSlotArray8byteKey hsa) {
            this.cursor = hsa.cursor();
        }

        @Override
        public boolean advance() {
            return this.cursor.advance();
        }

        @Override
        public long key() {
            return this.cursor.key();
        }

        @Override
        public long value() {
            return Long2LongMapHsa.this.mem.getLong(this.cursor.valueAddress());
        }
    }
}

