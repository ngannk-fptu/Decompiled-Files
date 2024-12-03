/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util.counter;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;
import net.sf.ehcache.util.counter.Counter;

public class CounterImpl
implements Counter,
Serializable {
    private AtomicLong value;

    public CounterImpl() {
        this(0L);
    }

    public CounterImpl(long initialValue) {
        this.value = new AtomicLong(initialValue);
    }

    @Override
    public long increment() {
        return this.value.incrementAndGet();
    }

    @Override
    public long decrement() {
        return this.value.decrementAndGet();
    }

    @Override
    public long getAndSet(long newValue) {
        return this.value.getAndSet(newValue);
    }

    @Override
    public long getValue() {
        return this.value.get();
    }

    @Override
    public long increment(long amount) {
        return this.value.addAndGet(amount);
    }

    @Override
    public long decrement(long amount) {
        return this.value.addAndGet(amount * -1L);
    }

    @Override
    public void setValue(long newValue) {
        this.value.set(newValue);
    }
}

