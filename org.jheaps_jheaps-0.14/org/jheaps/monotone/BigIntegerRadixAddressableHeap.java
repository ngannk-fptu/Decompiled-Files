/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.monotone;

import java.lang.reflect.Array;
import java.math.BigInteger;
import org.jheaps.monotone.AbstractRadixAddressableHeap;

public class BigIntegerRadixAddressableHeap<V>
extends AbstractRadixAddressableHeap<BigInteger, V> {
    private static final long serialVersionUID = 1L;

    public BigIntegerRadixAddressableHeap(BigInteger minKey, BigInteger maxKey) {
        if (minKey == null) {
            throw new IllegalArgumentException("Minimum key cannot be null");
        }
        if (minKey.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum key must be non-negative");
        }
        this.minKey = minKey;
        this.lastDeletedKey = minKey;
        if (maxKey == null) {
            throw new IllegalArgumentException("Maximum key cannot be null");
        }
        if (maxKey.compareTo(minKey) < 0) {
            throw new IllegalArgumentException("Maximum key cannot be less than the minimum");
        }
        this.maxKey = maxKey;
        BigInteger diff = maxKey.subtract(minKey);
        int numBuckets = 3 + diff.bitLength();
        this.buckets = (AbstractRadixAddressableHeap.Node[])Array.newInstance(AbstractRadixAddressableHeap.Node.class, numBuckets);
        this.size = 0L;
        this.currentMin = null;
    }

    @Override
    protected int compare(BigInteger o1, BigInteger o2) {
        return o1.compareTo(o2);
    }

    @Override
    protected int msd(BigInteger a, BigInteger b) {
        if (a.equals(b)) {
            return -1;
        }
        return a.xor(b).bitLength() - 1;
    }
}

