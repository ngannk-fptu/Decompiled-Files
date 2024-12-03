/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import java.util.Arrays;

final class DoubleList {
    static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    private double[] _array = new double[8];
    private int _count = 0;

    public double[] toArray() {
        return this._count < 1 ? EMPTY_DOUBLE_ARRAY : Arrays.copyOf(this._array, this._count);
    }

    private void ensureCapacity(int reqSize) {
        if (reqSize > this._array.length) {
            int newSize = reqSize * 3 / 2;
            this._array = Arrays.copyOf(this._array, newSize);
        }
    }

    public void add(double value) {
        this.ensureCapacity(this._count + 1);
        this._array[this._count] = value;
        ++this._count;
    }

    public int getLength() {
        return this._count;
    }
}

