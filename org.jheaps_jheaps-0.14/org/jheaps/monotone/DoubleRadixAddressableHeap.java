/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.monotone;

import java.lang.reflect.Array;
import java.math.BigInteger;
import org.jheaps.monotone.AbstractRadixAddressableHeap;
import org.jheaps.monotone.UnsignedUtils;

public class DoubleRadixAddressableHeap<V>
extends AbstractRadixAddressableHeap<Double, V> {
    private static final long serialVersionUID = 1L;

    public DoubleRadixAddressableHeap(double minKey, double maxKey) {
        if (!Double.isFinite(minKey) || minKey < 0.0) {
            throw new IllegalArgumentException("Minimum key must be finite and non-negative");
        }
        this.minKey = minKey;
        this.lastDeletedKey = minKey;
        if (!Double.isFinite(maxKey) || maxKey < minKey) {
            throw new IllegalArgumentException("Maximum key must be finite and not less than the minimum");
        }
        this.maxKey = maxKey;
        BigInteger minKeyAsBigInt = UnsignedUtils.unsignedLongToBigInt(Double.doubleToLongBits(minKey));
        BigInteger maxKeyAsBigInt = UnsignedUtils.unsignedLongToBigInt(Double.doubleToLongBits(maxKey));
        BigInteger diff = maxKeyAsBigInt.subtract(minKeyAsBigInt);
        int numBuckets = 3 + diff.bitLength();
        this.buckets = (AbstractRadixAddressableHeap.Node[])Array.newInstance(AbstractRadixAddressableHeap.Node.class, numBuckets);
        this.size = 0L;
        this.currentMin = null;
    }

    @Override
    protected int compare(Double o1, Double o2) {
        long x = Double.doubleToLongBits(o1) ^ Long.MIN_VALUE;
        long y = Double.doubleToLongBits(o2) ^ Long.MIN_VALUE;
        if (o1 < o2 ? !$assertionsDisabled && x >= y : (o1.doubleValue() == o2.doubleValue() ? !$assertionsDisabled && x != y : !$assertionsDisabled && x <= y)) {
            throw new AssertionError();
        }
        return x < y ? -1 : (x == y ? 0 : 1);
    }

    @Override
    protected int msd(Double a, Double b) {
        long uy;
        long ux = Double.doubleToLongBits(a);
        if (ux == (uy = Double.doubleToLongBits(b))) {
            return -1;
        }
        double d = UnsignedUtils.unsignedLongToDouble(ux ^ uy);
        return Math.getExponent(d);
    }
}

