/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.counters;

import com.hazelcast.internal.memory.GlobalMemoryAccessorRegistry;
import com.hazelcast.internal.util.counters.Counter;
import com.hazelcast.util.EmptyStatement;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public abstract class SwCounter
implements Counter {
    private SwCounter() {
    }

    public static SwCounter newSwCounter() {
        return SwCounter.newSwCounter(0L);
    }

    public static SwCounter newSwCounter(long initialValue) {
        return GlobalMemoryAccessorRegistry.MEM_AVAILABLE ? new UnsafeSwCounter(initialValue) : new SafeSwCounter(initialValue);
    }

    static final class SafeSwCounter
    extends SwCounter {
        private static final AtomicLongFieldUpdater<SafeSwCounter> COUNTER = AtomicLongFieldUpdater.newUpdater(SafeSwCounter.class, "value");
        private volatile long value;

        protected SafeSwCounter(long initialValue) {
            this.value = initialValue;
        }

        @Override
        public long inc() {
            long newValue = this.value + 1L;
            COUNTER.lazySet(this, newValue);
            return newValue;
        }

        @Override
        public long inc(long amount) {
            long newValue = this.value + amount;
            COUNTER.lazySet(this, newValue);
            return newValue;
        }

        @Override
        public long get() {
            return this.value;
        }

        public String toString() {
            return "Counter{value=" + this.value + '}';
        }
    }

    static final class UnsafeSwCounter
    extends SwCounter {
        private static final long OFFSET;
        private long localValue;
        private volatile long value;

        protected UnsafeSwCounter(long initialValue) {
            this.value = initialValue;
        }

        @Override
        public long inc() {
            long newLocalValue = ++this.localValue;
            GlobalMemoryAccessorRegistry.MEM.putOrderedLong(this, OFFSET, newLocalValue);
            return newLocalValue;
        }

        @Override
        public long inc(long amount) {
            long newLocalValue = this.localValue += amount;
            GlobalMemoryAccessorRegistry.MEM.putOrderedLong(this, OFFSET, newLocalValue);
            return newLocalValue;
        }

        @Override
        public long get() {
            return this.value;
        }

        public String toString() {
            return "Counter{value=" + this.value + '}';
        }

        static {
            Field field = null;
            try {
                field = UnsafeSwCounter.class.getDeclaredField("value");
            }
            catch (NoSuchFieldException ignore) {
                EmptyStatement.ignore(ignore);
            }
            OFFSET = GlobalMemoryAccessorRegistry.MEM.objectFieldOffset(field);
        }
    }
}

