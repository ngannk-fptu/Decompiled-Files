/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation;

import com.atlassian.instrumentation.AbsoluteCounter;
import com.atlassian.instrumentation.AtomicCounter;
import java.util.concurrent.atomic.AtomicLong;

public class AbsoluteAtomicCounter
extends AtomicCounter
implements AbsoluteCounter {
    public AbsoluteAtomicCounter(String name) {
        super(name);
    }

    public AbsoluteAtomicCounter(String name, long value) {
        super(name, value);
    }

    public AbsoluteAtomicCounter(String name, AtomicLong atomicLongRef) {
        super(name, atomicLongRef);
    }

    @Override
    public long getValue() {
        return this.value.getAndSet(0L);
    }
}

