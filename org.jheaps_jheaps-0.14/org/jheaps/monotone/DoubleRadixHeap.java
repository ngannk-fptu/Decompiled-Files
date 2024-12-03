/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.monotone;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.jheaps.monotone.AbstractRadixHeap;
import org.jheaps.monotone.UnsignedUtils;

public class DoubleRadixHeap
extends AbstractRadixHeap<Double> {
    private static final long serialVersionUID = 1L;

    public DoubleRadixHeap(double minKey, double maxKey) {
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
        this.buckets = (List[])Array.newInstance(List.class, numBuckets);
        for (int i = 0; i < this.buckets.length; ++i) {
            this.buckets[i] = new ArrayList();
        }
        this.size = 0L;
        this.currentMin = null;
    }

    @Override
    protected int compare(Double o1, Double o2) {
        long y;
        long x = Double.doubleToLongBits(o1) ^ Long.MIN_VALUE;
        return x < (y = Double.doubleToLongBits(o2) ^ Long.MIN_VALUE) ? -1 : (x == y ? 0 : 1);
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

