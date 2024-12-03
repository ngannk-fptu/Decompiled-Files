/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.Arrays;
import org.apache.lucene.util.Sorter;

public abstract class TimSorter
extends Sorter {
    static final int MINRUN = 32;
    static final int THRESHOLD = 64;
    static final int STACKSIZE = 40;
    static final int MIN_GALLOP = 7;
    final int maxTempSlots;
    int minRun;
    int to;
    int stackSize;
    int[] runEnds = new int[41];

    protected TimSorter(int maxTempSlots) {
        this.maxTempSlots = maxTempSlots;
    }

    static int minRun(int length) {
        int n;
        assert (length >= 32);
        int r = 0;
        for (n = length; n >= 64; n >>>= 1) {
            r |= n & 1;
        }
        int minRun = n + r;
        assert (minRun >= 32 && minRun <= 64);
        return minRun;
    }

    int runLen(int i) {
        int off = this.stackSize - i;
        return this.runEnds[off] - this.runEnds[off - 1];
    }

    int runBase(int i) {
        return this.runEnds[this.stackSize - i - 1];
    }

    int runEnd(int i) {
        return this.runEnds[this.stackSize - i];
    }

    void setRunEnd(int i, int runEnd) {
        this.runEnds[this.stackSize - i] = runEnd;
    }

    void pushRunLen(int len) {
        this.runEnds[this.stackSize + 1] = this.runEnds[this.stackSize] + len;
        ++this.stackSize;
    }

    int nextRun() {
        int o;
        int runBase = this.runEnd(0);
        assert (runBase < this.to);
        if (runBase == this.to - 1) {
            return 1;
        }
        if (this.compare(runBase, runBase + 1) > 0) {
            for (o = runBase + 2; o < this.to && this.compare(o - 1, o) > 0; ++o) {
            }
            this.reverse(runBase, o);
        } else {
            while (o < this.to && this.compare(o - 1, o) <= 0) {
                ++o;
            }
        }
        int runHi = Math.max(o, Math.min(this.to, runBase + this.minRun));
        this.binarySort(runBase, runHi, o);
        return runHi - runBase;
    }

    void ensureInvariants() {
        while (this.stackSize > 1) {
            int runLen2;
            int runLen0 = this.runLen(0);
            int runLen1 = this.runLen(1);
            if (this.stackSize > 2 && (runLen2 = this.runLen(2)) <= runLen1 + runLen0) {
                if (runLen2 < runLen0) {
                    this.mergeAt(1);
                    continue;
                }
                this.mergeAt(0);
                continue;
            }
            if (runLen1 > runLen0) break;
            this.mergeAt(0);
        }
    }

    void exhaustStack() {
        while (this.stackSize > 1) {
            this.mergeAt(0);
        }
    }

    void reset(int from, int to) {
        this.stackSize = 0;
        Arrays.fill(this.runEnds, 0);
        this.runEnds[0] = from;
        this.to = to;
        int length = to - from;
        this.minRun = length <= 64 ? length : TimSorter.minRun(length);
    }

    void mergeAt(int n) {
        assert (this.stackSize >= 2);
        this.merge(this.runBase(n + 1), this.runBase(n), this.runEnd(n));
        for (int j = n + 1; j > 0; --j) {
            this.setRunEnd(j, this.runEnd(j - 1));
        }
        --this.stackSize;
    }

    void merge(int lo, int mid, int hi) {
        if (this.compare(mid - 1, mid) <= 0) {
            return;
        }
        lo = this.upper2(lo, mid, mid);
        if ((hi = this.lower2(mid, hi, mid - 1)) - mid <= mid - lo && hi - mid <= this.maxTempSlots) {
            this.mergeHi(lo, mid, hi);
        } else if (mid - lo <= this.maxTempSlots) {
            this.mergeLo(lo, mid, hi);
        } else {
            this.mergeInPlace(lo, mid, hi);
        }
    }

    @Override
    public void sort(int from, int to) {
        this.checkRange(from, to);
        if (to - from <= 1) {
            return;
        }
        this.reset(from, to);
        do {
            this.ensureInvariants();
            this.pushRunLen(this.nextRun());
        } while (this.runEnd(0) < to);
        this.exhaustStack();
        assert (this.runEnd(0) == to);
    }

    @Override
    void doRotate(int lo, int mid, int hi) {
        int len1 = mid - lo;
        int len2 = hi - mid;
        if (len1 == len2) {
            while (mid < hi) {
                this.swap(lo++, mid++);
            }
        } else if (len2 < len1 && len2 <= this.maxTempSlots) {
            this.save(mid, len2);
            int i = lo + len1 - 1;
            int j = hi - 1;
            while (i >= lo) {
                this.copy(i, j);
                --i;
                --j;
            }
            i = 0;
            j = lo;
            while (i < len2) {
                this.restore(i, j);
                ++i;
                ++j;
            }
        } else if (len1 <= this.maxTempSlots) {
            this.save(lo, len1);
            int i = mid;
            int j = lo;
            while (i < hi) {
                this.copy(i, j);
                ++i;
                ++j;
            }
            i = 0;
            for (j = lo + len2; j < hi; ++j) {
                this.restore(i, j);
                ++i;
            }
        } else {
            this.reverse(lo, mid);
            this.reverse(mid, hi);
            this.reverse(lo, hi);
        }
    }

    void mergeLo(int lo, int mid, int hi) {
        assert (this.compare(lo, mid) > 0);
        int len1 = mid - lo;
        this.save(lo, len1);
        this.copy(mid, lo);
        int i = 0;
        int j = mid + 1;
        int dest = lo + 1;
        block0: while (true) {
            int count = 0;
            while (count < 7) {
                if (i >= len1 || j >= hi) break block0;
                if (this.compareSaved(i, j) <= 0) {
                    this.restore(i++, dest++);
                    count = 0;
                    continue;
                }
                this.copy(j++, dest++);
                ++count;
            }
            int next = this.lowerSaved3(j, hi, i);
            while (j < next) {
                this.copy(j++, dest);
                ++dest;
            }
            this.restore(i++, dest++);
        }
        while (i < len1) {
            this.restore(i++, dest);
            ++dest;
        }
        assert (j == dest);
    }

    void mergeHi(int lo, int mid, int hi) {
        assert (this.compare(mid - 1, hi - 1) > 0);
        int len2 = hi - mid;
        this.save(mid, len2);
        this.copy(mid - 1, hi - 1);
        int i = mid - 2;
        int j = len2 - 1;
        int dest = hi - 2;
        block0: while (true) {
            int count = 0;
            while (count < 7) {
                if (i < lo || j < 0) break block0;
                if (this.compareSaved(j, i) >= 0) {
                    this.restore(j--, dest--);
                    count = 0;
                    continue;
                }
                this.copy(i--, dest--);
                ++count;
            }
            int next = this.upperSaved3(lo, i + 1, j);
            while (i >= next) {
                this.copy(i--, dest--);
            }
            this.restore(j--, dest--);
        }
        while (j >= 0) {
            this.restore(j--, dest);
            --dest;
        }
        assert (i == dest);
    }

    int lowerSaved(int from, int to, int val) {
        int len = to - from;
        while (len > 0) {
            int half = len >>> 1;
            int mid = from + half;
            if (this.compareSaved(val, mid) > 0) {
                from = mid + 1;
                len = len - half - 1;
                continue;
            }
            len = half;
        }
        return from;
    }

    int upperSaved(int from, int to, int val) {
        int len = to - from;
        while (len > 0) {
            int half = len >>> 1;
            int mid = from + half;
            if (this.compareSaved(val, mid) < 0) {
                len = half;
                continue;
            }
            from = mid + 1;
            len = len - half - 1;
        }
        return from;
    }

    int lowerSaved3(int from, int to, int val) {
        int delta;
        int f = from;
        for (int t = f + 1; t < to; t += delta << 1) {
            if (this.compareSaved(val, t) <= 0) {
                return this.lowerSaved(f, t, val);
            }
            delta = t - f;
            f = t;
        }
        return this.lowerSaved(f, to, val);
    }

    int upperSaved3(int from, int to, int val) {
        int delta;
        int t = to;
        for (int f = to - 1; f > from; f -= delta << 1) {
            if (this.compareSaved(val, f) >= 0) {
                return this.upperSaved(f, t, val);
            }
            delta = t - f;
            t = f;
        }
        return this.upperSaved(from, t, val);
    }

    protected abstract void copy(int var1, int var2);

    protected abstract void save(int var1, int var2);

    protected abstract void restore(int var1, int var2);

    protected abstract int compareSaved(int var1, int var2);
}

