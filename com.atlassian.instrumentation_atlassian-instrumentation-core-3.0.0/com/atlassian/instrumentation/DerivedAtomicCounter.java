/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation;

import com.atlassian.instrumentation.AtomicGauge;
import com.atlassian.instrumentation.DerivedCounter;
import java.util.concurrent.atomic.AtomicLong;

public class DerivedAtomicCounter
extends AtomicGauge
implements DerivedCounter {
    public DerivedAtomicCounter(String name) {
        super(name);
    }

    public DerivedAtomicCounter(String name, long value) {
        super(name, value);
    }

    public DerivedAtomicCounter(String name, AtomicLong atomicLongRef) {
        super(name, atomicLongRef);
    }
}

