/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.util.collection;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntIterator
implements Iterator<Integer> {
    private final int missingValue;
    private final int[] values;
    private int position;

    @SuppressFBWarnings(value={"EI2"}, justification="This is flyweight over caller's array, so no copying")
    public IntIterator(int missingValue, int[] values) {
        this.missingValue = missingValue;
        this.values = values;
        this.position = -1;
    }

    @Override
    public boolean hasNext() {
        int[] values = this.values;
        while (this.position < values.length) {
            if (this.position >= 0 && values[this.position] != this.missingValue) {
                return true;
            }
            ++this.position;
        }
        return false;
    }

    @Override
    public Integer next() {
        return this.nextValue();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    public int nextValue() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        int value = this.values[this.position];
        ++this.position;
        return value;
    }

    void reset() {
        this.position = 0;
    }
}

