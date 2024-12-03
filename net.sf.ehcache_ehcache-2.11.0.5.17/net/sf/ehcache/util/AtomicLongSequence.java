/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util;

import java.util.concurrent.atomic.AtomicLong;
import net.sf.ehcache.util.LongSequence;

public class AtomicLongSequence
implements LongSequence {
    private final AtomicLong counter = new AtomicLong(0L);

    @Override
    public long next() {
        return this.counter.incrementAndGet();
    }
}

