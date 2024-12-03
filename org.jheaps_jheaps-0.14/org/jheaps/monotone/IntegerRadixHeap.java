/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.monotone;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import org.jheaps.monotone.AbstractRadixHeap;

public class IntegerRadixHeap
extends AbstractRadixHeap<Integer> {
    private static final long serialVersionUID = 1L;

    public IntegerRadixHeap(int minKey, int maxKey) {
        if (minKey < 0) {
            throw new IllegalArgumentException("Minimum key must be non-negative");
        }
        this.minKey = minKey;
        this.lastDeletedKey = minKey;
        if (maxKey < minKey) {
            throw new IllegalArgumentException("Maximum key cannot be less than the minimum");
        }
        this.maxKey = maxKey;
        int numBuckets = maxKey == minKey ? 2 : 3 + (int)Math.floor(Math.log((double)maxKey - (double)minKey) / Math.log(2.0));
        this.buckets = (List[])Array.newInstance(List.class, numBuckets);
        for (int i = 0; i < this.buckets.length; ++i) {
            this.buckets[i] = new ArrayList();
        }
        this.size = 0L;
        this.currentMin = null;
    }

    @Override
    protected int compare(Integer o1, Integer o2) {
        if (o1 < o2) {
            return -1;
        }
        if (o1 > o2) {
            return 1;
        }
        return 0;
    }

    @Override
    protected int msd(Integer a, Integer b) {
        if (a.intValue() == b.intValue()) {
            return -1;
        }
        float axorb = a ^ b;
        return Math.getExponent(axorb);
    }
}

