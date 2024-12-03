/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

public abstract class Sorter {
    static final int THRESHOLD = 20;

    protected Sorter() {
    }

    protected abstract int compare(int var1, int var2);

    protected abstract void swap(int var1, int var2);

    public abstract void sort(int var1, int var2);

    void checkRange(int from, int to) {
        if (to < from) {
            throw new IllegalArgumentException("'to' must be >= 'from', got from=" + from + " and to=" + to);
        }
    }

    void mergeInPlace(int from, int mid, int to) {
        int len22;
        int second_cut;
        int first_cut;
        if (from == mid || mid == to || this.compare(mid - 1, mid) <= 0) {
            return;
        }
        if (to - from == 2) {
            this.swap(mid - 1, mid);
            return;
        }
        while (this.compare(from, mid) <= 0) {
            ++from;
        }
        while (this.compare(mid - 1, to - 1) <= 0) {
            --to;
        }
        if (mid - from > to - mid) {
            int len11 = mid - from >>> 1;
            first_cut = from + len11;
            second_cut = this.lower(mid, to, first_cut);
            len22 = second_cut - mid;
        } else {
            len22 = to - mid >>> 1;
            second_cut = mid + len22;
            first_cut = this.upper(from, mid, second_cut);
            int len11 = first_cut - from;
        }
        this.rotate(first_cut, mid, second_cut);
        int new_mid = first_cut + len22;
        this.mergeInPlace(from, first_cut, new_mid);
        this.mergeInPlace(new_mid, second_cut, to);
    }

    int lower(int from, int to, int val) {
        int len = to - from;
        while (len > 0) {
            int half = len >>> 1;
            int mid = from + half;
            if (this.compare(mid, val) < 0) {
                from = mid + 1;
                len = len - half - 1;
                continue;
            }
            len = half;
        }
        return from;
    }

    int upper(int from, int to, int val) {
        int len = to - from;
        while (len > 0) {
            int half = len >>> 1;
            int mid = from + half;
            if (this.compare(val, mid) < 0) {
                len = half;
                continue;
            }
            from = mid + 1;
            len = len - half - 1;
        }
        return from;
    }

    int lower2(int from, int to, int val) {
        int delta;
        int t = to;
        for (int f = to - 1; f > from; f -= delta << 1) {
            if (this.compare(f, val) < 0) {
                return this.lower(f, t, val);
            }
            delta = t - f;
            t = f;
        }
        return this.lower(from, t, val);
    }

    int upper2(int from, int to, int val) {
        int delta;
        int f = from;
        for (int t = f + 1; t < to; t += delta << 1) {
            if (this.compare(t, val) > 0) {
                return this.upper(f, t, val);
            }
            delta = t - f;
            f = t;
        }
        return this.upper(f, to, val);
    }

    final void reverse(int from, int to) {
        --to;
        while (from < to) {
            this.swap(from, to);
            ++from;
            --to;
        }
    }

    final void rotate(int lo, int mid, int hi) {
        assert (lo <= mid && mid <= hi);
        if (lo == mid || mid == hi) {
            return;
        }
        this.doRotate(lo, mid, hi);
    }

    void doRotate(int lo, int mid, int hi) {
        if (mid - lo == hi - mid) {
            while (mid < hi) {
                this.swap(lo++, mid++);
            }
        } else {
            this.reverse(lo, mid);
            this.reverse(mid, hi);
            this.reverse(lo, hi);
        }
    }

    void insertionSort(int from, int to) {
        for (int i = from + 1; i < to; ++i) {
            for (int j = i; j > from && this.compare(j - 1, j) > 0; --j) {
                this.swap(j - 1, j);
            }
        }
    }

    void binarySort(int from, int to) {
        this.binarySort(from, to, from + 1);
    }

    void binarySort(int from, int to, int i) {
        while (i < to) {
            int l = from;
            int h = i - 1;
            while (l <= h) {
                int mid = l + h >>> 1;
                int cmp = this.compare(i, mid);
                if (cmp < 0) {
                    h = mid - 1;
                    continue;
                }
                l = mid + 1;
            }
            switch (i - l) {
                case 2: {
                    this.swap(l + 1, l + 2);
                    this.swap(l, l + 1);
                    break;
                }
                case 1: {
                    this.swap(l, l + 1);
                    break;
                }
                case 0: {
                    break;
                }
                default: {
                    for (int j = i; j > l; --j) {
                        this.swap(j - 1, j);
                    }
                }
            }
            ++i;
        }
    }

    void heapSort(int from, int to) {
        if (to - from <= 1) {
            return;
        }
        this.heapify(from, to);
        for (int end = to - 1; end > from; --end) {
            this.swap(from, end);
            this.siftDown(from, from, end);
        }
    }

    void heapify(int from, int to) {
        for (int i = Sorter.heapParent(from, to - 1); i >= from; --i) {
            this.siftDown(i, from, to);
        }
    }

    void siftDown(int i, int from, int to) {
        int leftChild = Sorter.heapChild(from, i);
        while (leftChild < to) {
            int rightChild = leftChild + 1;
            if (this.compare(i, leftChild) < 0) {
                if (rightChild < to && this.compare(leftChild, rightChild) < 0) {
                    this.swap(i, rightChild);
                    i = rightChild;
                } else {
                    this.swap(i, leftChild);
                    i = leftChild;
                }
            } else {
                if (rightChild >= to || this.compare(i, rightChild) >= 0) break;
                this.swap(i, rightChild);
                i = rightChild;
            }
            leftChild = Sorter.heapChild(from, i);
        }
    }

    static int heapParent(int from, int i) {
        return (i - 1 - from >>> 1) + from;
    }

    static int heapChild(int from, int i) {
        return (i - from << 1) + 1 + from;
    }
}

