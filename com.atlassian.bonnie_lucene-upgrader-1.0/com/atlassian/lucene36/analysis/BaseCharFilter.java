/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.CharFilter;
import com.atlassian.lucene36.analysis.CharStream;
import com.atlassian.lucene36.util.ArrayUtil;
import java.util.Arrays;

public abstract class BaseCharFilter
extends CharFilter {
    private int[] offsets;
    private int[] diffs;
    private int size = 0;

    public BaseCharFilter(CharStream in) {
        super(in);
    }

    protected int correct(int currentOff) {
        if (this.offsets == null || currentOff < this.offsets[0]) {
            return currentOff;
        }
        int hi = this.size - 1;
        if (currentOff >= this.offsets[hi]) {
            return currentOff + this.diffs[hi];
        }
        int lo = 0;
        int mid = -1;
        while (hi >= lo) {
            mid = lo + hi >>> 1;
            if (currentOff < this.offsets[mid]) {
                hi = mid - 1;
                continue;
            }
            if (currentOff > this.offsets[mid]) {
                lo = mid + 1;
                continue;
            }
            return currentOff + this.diffs[mid];
        }
        if (currentOff < this.offsets[mid]) {
            return mid == 0 ? currentOff : currentOff + this.diffs[mid - 1];
        }
        return currentOff + this.diffs[mid];
    }

    protected int getLastCumulativeDiff() {
        return this.offsets == null ? 0 : this.diffs[this.size - 1];
    }

    protected void addOffCorrectMap(int off, int cumulativeDiff) {
        if (this.offsets == null) {
            this.offsets = new int[64];
            this.diffs = new int[64];
        } else if (this.size == this.offsets.length) {
            this.offsets = ArrayUtil.grow(this.offsets);
            this.diffs = ArrayUtil.grow(this.diffs);
        }
        assert (this.size == 0 || off >= this.offsets[this.size]) : "Offset #" + this.size + "(" + off + ") is less than the last recorded offset " + this.offsets[this.size] + "\n" + Arrays.toString(this.offsets) + "\n" + Arrays.toString(this.diffs);
        if (this.size == 0 || off != this.offsets[this.size - 1]) {
            this.offsets[this.size] = off;
            this.diffs[this.size++] = cumulativeDiff;
        } else {
            this.diffs[this.size - 1] = cumulativeDiff;
        }
    }
}

