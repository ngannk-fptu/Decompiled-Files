/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.util.collection;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Iterator;

@SuppressFBWarnings(value={"EI2"})
public class LongIterator
implements Iterator<Long> {
    private final long missingValue;
    private final long[] values;
    private int position;

    public LongIterator(long missingValue, long[] values) {
        this.missingValue = missingValue;
        this.values = values;
    }

    @Override
    public boolean hasNext() {
        long[] values = this.values;
        while (this.position < values.length) {
            if (values[this.position] != this.missingValue) {
                return true;
            }
            ++this.position;
        }
        return false;
    }

    @Override
    public Long next() {
        return this.nextValue();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    public long nextValue() {
        long value = this.values[this.position];
        ++this.position;
        return value;
    }

    void reset() {
        this.position = 0;
    }
}

