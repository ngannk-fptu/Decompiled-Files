/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.util.collections;

import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class Range
implements Iterable<Integer> {
    private final int start;
    private final int end;

    public static Range range(int start, int end) {
        return new Range(start, end);
    }

    private Range(int start, int end) {
        Preconditions.checkArgument((start <= end ? 1 : 0) != 0, (Object)"start must be less than or equal to end");
        this.start = start;
        this.end = end;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new RangeIterator();
    }

    private class RangeIterator
    implements Iterator<Integer> {
        int index;

        private RangeIterator() {
            this.index = Range.this.start;
        }

        @Override
        public boolean hasNext() {
            return this.index < Range.this.end;
        }

        @Override
        public Integer next() {
            if (this.index >= Range.this.end) {
                throw new NoSuchElementException();
            }
            return this.index++;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

