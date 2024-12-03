/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.monotone;

import java.lang.reflect.Array;
import org.jheaps.monotone.AbstractRadixAddressableHeap;

public class LongRadixAddressableHeap<V>
extends AbstractRadixAddressableHeap<Long, V> {
    private static final long serialVersionUID = 1L;

    public LongRadixAddressableHeap(long minKey, long maxKey) {
        if (minKey < 0L) {
            throw new IllegalArgumentException("Minimum key must be non-negative");
        }
        this.minKey = minKey;
        this.lastDeletedKey = minKey;
        if (maxKey < minKey) {
            throw new IllegalArgumentException("Maximum key cannot be less than the minimum");
        }
        this.maxKey = maxKey;
        int numBuckets = maxKey == minKey ? 2 : 3 + (int)Math.floor(Math.log((double)maxKey - (double)minKey) / Math.log(2.0));
        this.buckets = (AbstractRadixAddressableHeap.Node[])Array.newInstance(AbstractRadixAddressableHeap.Node.class, numBuckets);
        this.size = 0L;
        this.currentMin = null;
    }

    @Override
    protected int compare(Long o1, Long o2) {
        if (o1 < o2) {
            return -1;
        }
        if (o1 > o2) {
            return 1;
        }
        return 0;
    }

    @Override
    protected int msd(Long a, Long b) {
        if (a.longValue() == b.longValue()) {
            return -1;
        }
        double axorb = a ^ b;
        return Math.getExponent(axorb);
    }
}

