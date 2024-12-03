/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.caches;

import com.atlassian.instrumentation.Instrument;
import com.atlassian.instrumentation.caches.CacheInstrument;
import com.atlassian.instrumentation.compare.InstrumentComparator;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

public class CacheCounter
implements CacheInstrument {
    public static final Sizer NOOP_SIZER = new Sizer(){

        @Override
        public long getCacheSize() {
            return -1L;
        }
    };
    protected final String name;
    protected final AtomicLong hits;
    protected final AtomicLong misses;
    protected final AtomicLong missTime;
    protected final Sizer sizer;

    public CacheCounter(String name) {
        this(name, NOOP_SIZER);
    }

    public CacheCounter(String name, Sizer sizer) {
        this.name = Assertions.notNull("name", name);
        this.sizer = Assertions.notNull("sizer", sizer);
        this.hits = new AtomicLong(0L);
        this.misses = new AtomicLong(0L);
        this.missTime = new AtomicLong(0L);
    }

    public long hit() {
        return this.hits.incrementAndGet();
    }

    public long miss() {
        return this.misses.incrementAndGet();
    }

    public long miss(long nanosecondsTaken) {
        this.misses.incrementAndGet();
        return this.missTime.getAndAdd(nanosecondsTaken);
    }

    public <T> T miss(Callable<T> missObjectCreation) {
        long then = System.nanoTime();
        try {
            T t = missObjectCreation.call();
            return t;
        }
        catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
        finally {
            long delta = System.nanoTime() - then;
            this.misses.incrementAndGet();
            this.missTime.getAndAdd(delta);
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getValue() {
        return this.getMisses();
    }

    @Override
    public int compareTo(Instrument that) {
        return new InstrumentComparator().compare(this, that);
    }

    @Override
    public long getHits() {
        return this.hits.longValue();
    }

    @Override
    public long getMisses() {
        return this.misses.longValue();
    }

    @Override
    public long getMissTime() {
        return this.missTime.longValue();
    }

    @Override
    public double getHitMissRatio() {
        double misses;
        double hits = this.getHits();
        if (hits + (misses = (double)this.getMisses()) == 0.0) {
            return 0.0;
        }
        return hits / (hits + misses);
    }

    @Override
    public long getCacheSize() {
        return this.sizer.getCacheSize();
    }

    public static interface Sizer {
        public long getCacheSize();
    }
}

