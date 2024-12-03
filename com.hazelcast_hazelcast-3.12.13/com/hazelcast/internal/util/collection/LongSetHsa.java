/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.collection;

import com.hazelcast.internal.memory.MemoryManager;
import com.hazelcast.internal.util.collection.LongCursor;
import com.hazelcast.internal.util.collection.LongSet;
import com.hazelcast.internal.util.hashslot.HashSlotArray8byteKey;
import com.hazelcast.internal.util.hashslot.HashSlotCursor8byteKey;
import com.hazelcast.internal.util.hashslot.impl.HashSlotArray8byteKeyNoValue;

public class LongSetHsa
implements LongSet {
    private final long nullValue;
    private final HashSlotArray8byteKey hsa;

    public LongSetHsa(long nullValue, MemoryManager memMgr) {
        this(nullValue, memMgr, 16, 0.6f);
    }

    public LongSetHsa(long nullValue, MemoryManager memMgr, int initialCapacity, float loadFactor) {
        this.nullValue = nullValue;
        this.hsa = new HashSlotArray8byteKeyNoValue(nullValue, memMgr, initialCapacity, loadFactor);
        this.hsa.gotoNew();
    }

    @Override
    public boolean add(long value) {
        assert (value != this.nullValue) : "add() called with null-sentinel value " + this.nullValue;
        return this.hsa.ensure(value).isNew();
    }

    @Override
    public boolean remove(long value) {
        assert (value != this.nullValue) : "remove() called with null-sentinel value " + this.nullValue;
        return this.hsa.remove(value);
    }

    @Override
    public boolean contains(long value) {
        assert (value != this.nullValue) : "contains() called with null-sentinel value " + this.nullValue;
        return this.hsa.get(value) != 0L;
    }

    @Override
    public long size() {
        return this.hsa.size();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0L;
    }

    @Override
    public void clear() {
        this.hsa.clear();
    }

    @Override
    public LongCursor cursor() {
        assert (this.hsa.address() >= 0L) : "cursor() called on a disposed map";
        return new Cursor();
    }

    @Override
    public void dispose() {
        this.hsa.dispose();
    }

    private final class Cursor
    implements LongCursor {
        private final HashSlotCursor8byteKey hsaCursor;

        private Cursor() {
            this.hsaCursor = LongSetHsa.this.hsa.cursor();
        }

        @Override
        public boolean advance() {
            return this.hsaCursor.advance();
        }

        @Override
        public long value() {
            return this.hsaCursor.key();
        }

        @Override
        public void reset() {
            this.hsaCursor.reset();
        }
    }
}

