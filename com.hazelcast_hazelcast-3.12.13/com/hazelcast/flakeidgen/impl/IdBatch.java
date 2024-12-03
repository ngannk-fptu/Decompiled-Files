/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.hazelcast.flakeidgen.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;

public class IdBatch
implements Iterable<Long> {
    private final long base;
    private final long increment;
    private final int batchSize;

    public IdBatch(long base, long increment, int batchSize) {
        this.base = base;
        this.increment = increment;
        this.batchSize = batchSize;
    }

    public long base() {
        return this.base;
    }

    public long increment() {
        return this.increment;
    }

    public int batchSize() {
        return this.batchSize;
    }

    @Override
    @Nonnull
    public Iterator<Long> iterator() {
        return new Iterator<Long>(){
            private long base2;
            private int remaining;
            {
                this.base2 = IdBatch.this.base;
                this.remaining = IdBatch.this.batchSize;
            }

            @Override
            public boolean hasNext() {
                return this.remaining > 0;
            }

            @Override
            public Long next() {
                if (this.remaining == 0) {
                    throw new NoSuchElementException();
                }
                --this.remaining;
                try {
                    Long l = this.base2;
                    return l;
                }
                finally {
                    this.base2 += IdBatch.this.increment;
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}

