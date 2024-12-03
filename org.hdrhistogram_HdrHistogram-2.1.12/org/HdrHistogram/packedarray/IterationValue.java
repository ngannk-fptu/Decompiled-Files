/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram.packedarray;

public class IterationValue {
    private int index;
    private long value;

    IterationValue() {
    }

    void set(int index, long value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return this.index;
    }

    public long getValue() {
        return this.value;
    }
}

