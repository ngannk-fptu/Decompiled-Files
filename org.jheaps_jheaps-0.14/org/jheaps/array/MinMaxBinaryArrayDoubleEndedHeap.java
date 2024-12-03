/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.array;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.jheaps.DoubleEndedHeap;
import org.jheaps.annotations.LinearTime;
import org.jheaps.array.AbstractArrayHeap;

public class MinMaxBinaryArrayDoubleEndedHeap<K>
extends AbstractArrayHeap<K>
implements DoubleEndedHeap<K>,
Serializable {
    private static final long serialVersionUID = -8985374211686556917L;
    public static final int DEFAULT_HEAP_CAPACITY = 16;

    public MinMaxBinaryArrayDoubleEndedHeap() {
        super(null, 16);
    }

    public MinMaxBinaryArrayDoubleEndedHeap(int capacity) {
        super(null, capacity);
    }

    public MinMaxBinaryArrayDoubleEndedHeap(Comparator<? super K> comparator) {
        super(comparator, 16);
    }

    public MinMaxBinaryArrayDoubleEndedHeap(Comparator<? super K> comparator, int capacity) {
        super(comparator, capacity);
    }

    @LinearTime
    public static <K> MinMaxBinaryArrayDoubleEndedHeap<K> heapify(K[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length == 0) {
            return new MinMaxBinaryArrayDoubleEndedHeap<K>();
        }
        MinMaxBinaryArrayDoubleEndedHeap<K> h = new MinMaxBinaryArrayDoubleEndedHeap<K>(array.length);
        System.arraycopy(array, 0, h.array, 1, array.length);
        h.size = array.length;
        for (int i = array.length / 2; i > 0; --i) {
            h.fixdown(i);
        }
        return h;
    }

    @LinearTime
    public static <K> MinMaxBinaryArrayDoubleEndedHeap<K> heapify(K[] array, Comparator<? super K> comparator) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length == 0) {
            return new MinMaxBinaryArrayDoubleEndedHeap<K>(comparator);
        }
        MinMaxBinaryArrayDoubleEndedHeap<K> h = new MinMaxBinaryArrayDoubleEndedHeap<K>(comparator, array.length);
        System.arraycopy(array, 0, h.array, 1, array.length);
        h.size = array.length;
        for (int i = array.length / 2; i > 0; --i) {
            h.fixdownWithComparator(i);
        }
        return h;
    }

    @Override
    protected void ensureCapacity(int capacity) {
        this.checkCapacity(capacity);
        Object[] newArray = new Object[capacity + 1];
        System.arraycopy(this.array, 1, newArray, 1, this.size);
        this.array = newArray;
    }

    @Override
    public K findMax() {
        switch (this.size) {
            case 0: {
                throw new NoSuchElementException();
            }
            case 1: {
                return (K)this.array[1];
            }
            case 2: {
                return (K)this.array[2];
            }
        }
        if (this.comparator == null) {
            if (((Comparable)this.array[3]).compareTo(this.array[2]) > 0) {
                return (K)this.array[3];
            }
            return (K)this.array[2];
        }
        if (this.comparator.compare(this.array[3], this.array[2]) > 0) {
            return (K)this.array[3];
        }
        return (K)this.array[2];
    }

    @Override
    public K deleteMax() {
        Object result;
        switch (this.size) {
            case 0: {
                throw new NoSuchElementException();
            }
            case 1: {
                result = this.array[1];
                this.array[1] = null;
                --this.size;
                break;
            }
            case 2: {
                result = this.array[2];
                this.array[2] = null;
                --this.size;
                break;
            }
            default: {
                if (this.comparator == null) {
                    if (((Comparable)this.array[3]).compareTo(this.array[2]) > 0) {
                        result = this.array[3];
                        this.array[3] = this.array[this.size];
                        this.array[this.size] = null;
                        --this.size;
                        if (this.size < 3) break;
                        this.fixdownMax(3);
                        break;
                    }
                    result = this.array[2];
                    this.array[2] = this.array[this.size];
                    this.array[this.size] = null;
                    --this.size;
                    this.fixdownMax(2);
                    break;
                }
                if (this.comparator.compare(this.array[3], this.array[2]) > 0) {
                    result = this.array[3];
                    this.array[3] = this.array[this.size];
                    this.array[this.size] = null;
                    --this.size;
                    if (this.size < 3) break;
                    this.fixdownMaxWithComparator(3);
                    break;
                }
                result = this.array[2];
                this.array[2] = this.array[this.size];
                this.array[this.size] = null;
                --this.size;
                this.fixdownMaxWithComparator(2);
            }
        }
        if (2 * this.minCapacity < this.array.length - 1 && 4 * this.size < this.array.length - 1) {
            this.ensureCapacity((this.array.length - 1) / 2);
        }
        return (K)result;
    }

    @Override
    protected void fixup(int k) {
        if (this.onMinLevel(k)) {
            int p = k / 2;
            Object kValue = this.array[k];
            if (p > 0 && ((Comparable)this.array[p]).compareTo(kValue) < 0) {
                this.array[k] = this.array[p];
                this.array[p] = kValue;
                this.fixupMax(p);
            } else {
                this.fixupMin(k);
            }
        } else {
            int p = k / 2;
            Object kValue = this.array[k];
            if (p > 0 && ((Comparable)kValue).compareTo(this.array[p]) < 0) {
                this.array[k] = this.array[p];
                this.array[p] = kValue;
                this.fixupMin(p);
            } else {
                this.fixupMax(k);
            }
        }
    }

    @Override
    protected void fixupWithComparator(int k) {
        if (this.onMinLevel(k)) {
            int p = k / 2;
            Object kValue = this.array[k];
            if (p > 0 && this.comparator.compare(this.array[p], kValue) < 0) {
                this.array[k] = this.array[p];
                this.array[p] = kValue;
                this.fixupMaxWithComparator(p);
            } else {
                this.fixupMinWithComparator(k);
            }
        } else {
            int p = k / 2;
            Object kValue = this.array[k];
            if (p > 0 && this.comparator.compare(kValue, this.array[p]) < 0) {
                this.array[k] = this.array[p];
                this.array[p] = kValue;
                this.fixupMinWithComparator(p);
            } else {
                this.fixupMaxWithComparator(k);
            }
        }
    }

    private void fixupMin(int k) {
        Object key = this.array[k];
        int gp = k / 4;
        while (gp > 0 && ((Comparable)this.array[gp]).compareTo(key) > 0) {
            this.array[k] = this.array[gp];
            k = gp;
            gp = k / 4;
        }
        this.array[k] = key;
    }

    private void fixupMinWithComparator(int k) {
        Object key = this.array[k];
        int gp = k / 4;
        while (gp > 0 && this.comparator.compare(this.array[gp], key) > 0) {
            this.array[k] = this.array[gp];
            k = gp;
            gp = k / 4;
        }
        this.array[k] = key;
    }

    private void fixupMax(int k) {
        Object key = this.array[k];
        int gp = k / 4;
        while (gp > 0 && ((Comparable)this.array[gp]).compareTo(key) < 0) {
            this.array[k] = this.array[gp];
            k = gp;
            gp = k / 4;
        }
        this.array[k] = key;
    }

    private void fixupMaxWithComparator(int k) {
        Object key = this.array[k];
        int gp = k / 4;
        while (gp > 0 && this.comparator.compare(this.array[gp], key) < 0) {
            this.array[k] = this.array[gp];
            k = gp;
            gp = k / 4;
        }
        this.array[k] = key;
    }

    @Override
    protected void fixdown(int k) {
        if (this.onMinLevel(k)) {
            this.fixdownMin(k);
        } else {
            this.fixdownMax(k);
        }
    }

    @Override
    protected void fixdownWithComparator(int k) {
        if (this.onMinLevel(k)) {
            this.fixdownMinWithComparator(k);
        } else {
            this.fixdownMaxWithComparator(k);
        }
    }

    private void fixdownMin(int k) {
        int c = 2 * k;
        while (c <= this.size) {
            Object tmp;
            int m = this.minChildOrGrandchild(k);
            if (m > c + 1) {
                if (((Comparable)this.array[m]).compareTo(this.array[k]) >= 0) break;
                tmp = this.array[k];
                this.array[k] = this.array[m];
                this.array[m] = tmp;
                if (((Comparable)this.array[m]).compareTo(this.array[m / 2]) > 0) {
                    tmp = this.array[m];
                    this.array[m] = this.array[m / 2];
                    this.array[m / 2] = tmp;
                }
                k = m;
                c = 2 * k;
                continue;
            }
            if (((Comparable)this.array[m]).compareTo(this.array[k]) >= 0) break;
            tmp = this.array[k];
            this.array[k] = this.array[m];
            this.array[m] = tmp;
            break;
        }
    }

    private void fixdownMinWithComparator(int k) {
        int c = 2 * k;
        while (c <= this.size) {
            Object tmp;
            int m = this.minChildOrGrandchildWithComparator(k);
            if (m > c + 1) {
                if (this.comparator.compare(this.array[m], this.array[k]) >= 0) break;
                tmp = this.array[k];
                this.array[k] = this.array[m];
                this.array[m] = tmp;
                if (this.comparator.compare(this.array[m], this.array[m / 2]) > 0) {
                    tmp = this.array[m];
                    this.array[m] = this.array[m / 2];
                    this.array[m / 2] = tmp;
                }
                k = m;
                c = 2 * k;
                continue;
            }
            if (this.comparator.compare(this.array[m], this.array[k]) >= 0) break;
            tmp = this.array[k];
            this.array[k] = this.array[m];
            this.array[m] = tmp;
            break;
        }
    }

    private void fixdownMax(int k) {
        int c = 2 * k;
        while (c <= this.size) {
            Object tmp;
            int m = this.maxChildOrGrandchild(k);
            if (m > c + 1) {
                if (((Comparable)this.array[m]).compareTo(this.array[k]) <= 0) break;
                tmp = this.array[k];
                this.array[k] = this.array[m];
                this.array[m] = tmp;
                if (((Comparable)this.array[m]).compareTo(this.array[m / 2]) < 0) {
                    tmp = this.array[m];
                    this.array[m] = this.array[m / 2];
                    this.array[m / 2] = tmp;
                }
                k = m;
                c = 2 * k;
                continue;
            }
            if (((Comparable)this.array[m]).compareTo(this.array[k]) <= 0) break;
            tmp = this.array[k];
            this.array[k] = this.array[m];
            this.array[m] = tmp;
            break;
        }
    }

    private void fixdownMaxWithComparator(int k) {
        int c = 2 * k;
        while (c <= this.size) {
            Object tmp;
            int m = this.maxChildOrGrandchildWithComparator(k);
            if (m > c + 1) {
                if (this.comparator.compare(this.array[m], this.array[k]) <= 0) break;
                tmp = this.array[k];
                this.array[k] = this.array[m];
                this.array[m] = tmp;
                if (this.comparator.compare(this.array[m], this.array[m / 2]) < 0) {
                    tmp = this.array[m];
                    this.array[m] = this.array[m / 2];
                    this.array[m / 2] = tmp;
                }
                k = m;
                c = 2 * k;
                continue;
            }
            if (this.comparator.compare(this.array[m], this.array[k]) <= 0) break;
            tmp = this.array[k];
            this.array[k] = this.array[m];
            this.array[m] = tmp;
            break;
        }
    }

    boolean onMinLevel(int k) {
        float kAsFloat = k;
        int exponent = Math.getExponent(kAsFloat);
        return exponent % 2 == 0;
    }

    private int maxChildOrGrandchild(int k) {
        int gc = 4 * k;
        if (gc + 3 <= this.size) {
            Object gcValue = this.array[gc];
            int maxgc = gc++;
            if (((Comparable)this.array[gc]).compareTo(gcValue) > 0) {
                gcValue = this.array[gc];
                maxgc = gc;
            }
            if (((Comparable)this.array[++gc]).compareTo(gcValue) > 0) {
                gcValue = this.array[gc];
                maxgc = gc;
            }
            if (((Comparable)this.array[++gc]).compareTo(gcValue) > 0) {
                maxgc = gc;
            }
            return maxgc;
        }
        switch (this.size - gc) {
            case 2: {
                Object gcValue = this.array[gc];
                int maxgc = gc++;
                if (((Comparable)this.array[gc]).compareTo(gcValue) > 0) {
                    gcValue = this.array[gc];
                    maxgc = gc;
                }
                if (((Comparable)this.array[++gc]).compareTo(gcValue) > 0) {
                    maxgc = gc;
                }
                return maxgc;
            }
            case 1: {
                Object gcValue = this.array[gc];
                int maxgc = gc++;
                if (((Comparable)this.array[gc]).compareTo(gcValue) > 0) {
                    gcValue = this.array[gc];
                    maxgc = gc;
                }
                if (2 * k + 1 <= this.size && ((Comparable)this.array[2 * k + 1]).compareTo(gcValue) > 0) {
                    maxgc = 2 * k + 1;
                }
                return maxgc;
            }
            case 0: {
                Object gcValue = this.array[gc];
                int maxgc = gc;
                if (2 * k + 1 <= this.size && ((Comparable)this.array[2 * k + 1]).compareTo(gcValue) > 0) {
                    maxgc = 2 * k + 1;
                }
                return maxgc;
            }
        }
        int maxgc = 2 * k;
        Object gcValue = this.array[maxgc];
        if (2 * k + 1 <= this.size && ((Comparable)this.array[2 * k + 1]).compareTo(gcValue) > 0) {
            maxgc = 2 * k + 1;
        }
        return maxgc;
    }

    private int maxChildOrGrandchildWithComparator(int k) {
        int gc = 4 * k;
        if (gc + 3 <= this.size) {
            Object gcValue = this.array[gc];
            int maxgc = gc++;
            if (this.comparator.compare(this.array[gc], gcValue) > 0) {
                gcValue = this.array[gc];
                maxgc = gc;
            }
            if (this.comparator.compare(this.array[++gc], gcValue) > 0) {
                gcValue = this.array[gc];
                maxgc = gc;
            }
            if (this.comparator.compare(this.array[++gc], gcValue) > 0) {
                maxgc = gc;
            }
            return maxgc;
        }
        switch (this.size - gc) {
            case 2: {
                Object gcValue = this.array[gc];
                int maxgc = gc++;
                if (this.comparator.compare(this.array[gc], gcValue) > 0) {
                    gcValue = this.array[gc];
                    maxgc = gc;
                }
                if (this.comparator.compare(this.array[++gc], gcValue) > 0) {
                    maxgc = gc;
                }
                return maxgc;
            }
            case 1: {
                Object gcValue = this.array[gc];
                int maxgc = gc++;
                if (this.comparator.compare(this.array[gc], gcValue) > 0) {
                    gcValue = this.array[gc];
                    maxgc = gc;
                }
                if (2 * k + 1 <= this.size && this.comparator.compare(this.array[2 * k + 1], gcValue) > 0) {
                    maxgc = 2 * k + 1;
                }
                return maxgc;
            }
            case 0: {
                Object gcValue = this.array[gc];
                int maxgc = gc;
                if (2 * k + 1 <= this.size && this.comparator.compare(this.array[2 * k + 1], gcValue) > 0) {
                    maxgc = 2 * k + 1;
                }
                return maxgc;
            }
        }
        int maxgc = 2 * k;
        Object gcValue = this.array[maxgc];
        if (2 * k + 1 <= this.size && this.comparator.compare(this.array[2 * k + 1], gcValue) > 0) {
            maxgc = 2 * k + 1;
        }
        return maxgc;
    }

    private int minChildOrGrandchild(int k) {
        int gc = 4 * k;
        if (gc + 3 <= this.size) {
            Object gcValue = this.array[gc];
            int mingc = gc++;
            if (((Comparable)this.array[gc]).compareTo(gcValue) < 0) {
                gcValue = this.array[gc];
                mingc = gc;
            }
            if (((Comparable)this.array[++gc]).compareTo(gcValue) < 0) {
                gcValue = this.array[gc];
                mingc = gc;
            }
            if (((Comparable)this.array[++gc]).compareTo(gcValue) < 0) {
                mingc = gc;
            }
            return mingc;
        }
        switch (this.size - gc) {
            case 2: {
                Object gcValue = this.array[gc];
                int mingc = gc++;
                if (((Comparable)this.array[gc]).compareTo(gcValue) < 0) {
                    gcValue = this.array[gc];
                    mingc = gc;
                }
                if (((Comparable)this.array[++gc]).compareTo(gcValue) < 0) {
                    mingc = gc;
                }
                return mingc;
            }
            case 1: {
                Object gcValue = this.array[gc];
                int mingc = gc++;
                if (((Comparable)this.array[gc]).compareTo(gcValue) < 0) {
                    gcValue = this.array[gc];
                    mingc = gc;
                }
                if (2 * k + 1 <= this.size && ((Comparable)this.array[2 * k + 1]).compareTo(gcValue) < 0) {
                    mingc = 2 * k + 1;
                }
                return mingc;
            }
            case 0: {
                Object gcValue = this.array[gc];
                int mingc = gc;
                if (2 * k + 1 <= this.size && ((Comparable)this.array[2 * k + 1]).compareTo(gcValue) < 0) {
                    mingc = 2 * k + 1;
                }
                return mingc;
            }
        }
        int mingc = 2 * k;
        Object gcValue = this.array[mingc];
        if (2 * k + 1 <= this.size && ((Comparable)this.array[2 * k + 1]).compareTo(gcValue) < 0) {
            mingc = 2 * k + 1;
        }
        return mingc;
    }

    private int minChildOrGrandchildWithComparator(int k) {
        int gc = 4 * k;
        if (gc + 3 <= this.size) {
            Object gcValue = this.array[gc];
            int mingc = gc++;
            if (this.comparator.compare(this.array[gc], gcValue) < 0) {
                gcValue = this.array[gc];
                mingc = gc;
            }
            if (this.comparator.compare(this.array[++gc], gcValue) < 0) {
                gcValue = this.array[gc];
                mingc = gc;
            }
            if (this.comparator.compare(this.array[++gc], gcValue) < 0) {
                mingc = gc;
            }
            return mingc;
        }
        switch (this.size - gc) {
            case 2: {
                Object gcValue = this.array[gc];
                int mingc = gc++;
                if (this.comparator.compare(this.array[gc], gcValue) < 0) {
                    gcValue = this.array[gc];
                    mingc = gc;
                }
                if (this.comparator.compare(this.array[++gc], gcValue) < 0) {
                    mingc = gc;
                }
                return mingc;
            }
            case 1: {
                Object gcValue = this.array[gc];
                int mingc = gc++;
                if (this.comparator.compare(this.array[gc], gcValue) < 0) {
                    gcValue = this.array[gc];
                    mingc = gc;
                }
                if (2 * k + 1 <= this.size && this.comparator.compare(this.array[2 * k + 1], gcValue) < 0) {
                    mingc = 2 * k + 1;
                }
                return mingc;
            }
            case 0: {
                Object gcValue = this.array[gc];
                int mingc = gc;
                if (2 * k + 1 <= this.size && this.comparator.compare(this.array[2 * k + 1], gcValue) < 0) {
                    mingc = 2 * k + 1;
                }
                return mingc;
            }
        }
        int mingc = 2 * k;
        Object gcValue = this.array[mingc];
        if (2 * k + 1 <= this.size && this.comparator.compare(this.array[2 * k + 1], gcValue) < 0) {
            mingc = 2 * k + 1;
        }
        return mingc;
    }
}

