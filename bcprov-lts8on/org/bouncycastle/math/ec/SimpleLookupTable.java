/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import org.bouncycastle.math.ec.AbstractECLookupTable;
import org.bouncycastle.math.ec.ECPoint;

public class SimpleLookupTable
extends AbstractECLookupTable {
    private final ECPoint[] points;

    private static ECPoint[] copy(ECPoint[] points, int off, int len) {
        ECPoint[] result = new ECPoint[len];
        for (int i = 0; i < len; ++i) {
            result[i] = points[off + i];
        }
        return result;
    }

    public SimpleLookupTable(ECPoint[] points, int off, int len) {
        this.points = SimpleLookupTable.copy(points, off, len);
    }

    @Override
    public int getSize() {
        return this.points.length;
    }

    @Override
    public ECPoint lookup(int index) {
        throw new UnsupportedOperationException("Constant-time lookup not supported");
    }

    @Override
    public ECPoint lookupVar(int index) {
        return this.points[index];
    }
}

