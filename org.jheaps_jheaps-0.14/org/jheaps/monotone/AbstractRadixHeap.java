/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.monotone;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import org.jheaps.Heap;
import org.jheaps.annotations.ConstantTime;
import org.jheaps.annotations.LogarithmicTime;

abstract class AbstractRadixHeap<K>
implements Heap<K>,
Serializable {
    private static final long serialVersionUID = 1L;
    protected static final int EMPTY = -1;
    protected List<K>[] buckets;
    protected long size;
    protected K lastDeletedKey;
    protected K currentMin;
    protected int currentMinBucket;
    protected int currentMinPos;
    protected K minKey;
    protected K maxKey;

    AbstractRadixHeap() {
    }

    @Override
    @ConstantTime
    public K findMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        return this.currentMin;
    }

    @Override
    @ConstantTime(amortized=true)
    public void insert(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Null keys not permitted");
        }
        if (this.compare(key, this.maxKey) > 0) {
            throw new IllegalArgumentException("Key is more than the maximum allowed key");
        }
        if (this.compare(key, this.lastDeletedKey) < 0) {
            throw new IllegalArgumentException("Invalid key. Monotone heap.");
        }
        int b = this.computeBucket(key, this.lastDeletedKey);
        this.buckets[b].add(key);
        if (this.currentMin == null || this.compare(key, this.currentMin) < 0) {
            this.currentMin = key;
            this.currentMinBucket = b;
            this.currentMinPos = this.buckets[b].size() - 1;
        }
        ++this.size;
    }

    @Override
    @LogarithmicTime(amortized=true)
    public K deleteMin() {
        if (this.size == 0L) {
            throw new NoSuchElementException();
        }
        this.lastDeletedKey = this.currentMin;
        if (this.currentMinBucket == 0) {
            this.buckets[this.currentMinBucket].remove(this.currentMinPos);
            this.currentMin = null;
            this.currentMinBucket = -1;
            this.currentMinPos = -1;
            if (--this.size > 0L) {
                this.findAndCacheMinimum(0);
            }
        } else {
            K newMin = null;
            int newMinBucket = -1;
            int newMinPos = -1;
            int pos = 0;
            for (K val : this.buckets[this.currentMinBucket]) {
                if (pos != this.currentMinPos) {
                    int b = this.computeBucket(val, this.lastDeletedKey);
                    assert (b < this.currentMinBucket);
                    this.buckets[b].add(val);
                    if (newMin == null || this.compare(val, newMin) < 0) {
                        newMin = val;
                        newMinBucket = b;
                        newMinPos = this.buckets[b].size() - 1;
                    }
                }
                ++pos;
            }
            this.buckets[this.currentMinBucket].clear();
            this.currentMin = newMin;
            this.currentMinBucket = newMinBucket;
            this.currentMinPos = newMinPos;
            if (--this.size > 0L) {
                this.findAndCacheMinimum(this.currentMinBucket + 1);
            }
        }
        return this.lastDeletedKey;
    }

    @Override
    @ConstantTime
    public boolean isEmpty() {
        return this.size == 0L;
    }

    @Override
    @ConstantTime
    public long size() {
        return this.size;
    }

    @Override
    public void clear() {
        for (List<K> bucket : this.buckets) {
            bucket.clear();
        }
        this.size = 0L;
        this.lastDeletedKey = this.minKey;
        this.currentMin = null;
        this.currentMinBucket = -1;
        this.currentMinPos = -1;
    }

    @Override
    public Comparator<? super K> comparator() {
        return null;
    }

    protected abstract int compare(K var1, K var2);

    protected int computeBucket(K key, K minKey) {
        return 1 + Math.min(this.msd(key, minKey), this.buckets.length - 2);
    }

    protected abstract int msd(K var1, K var2);

    private void findAndCacheMinimum(int firstBucket) {
        if (this.currentMin == null) {
            this.currentMinBucket = -1;
            for (int i = firstBucket; i < this.buckets.length; ++i) {
                if (this.buckets[i].isEmpty()) continue;
                this.currentMinBucket = i;
                break;
            }
            this.currentMinPos = -1;
            if (this.currentMinBucket >= 0) {
                int pos = 0;
                for (K val : this.buckets[this.currentMinBucket]) {
                    if (this.currentMin == null || this.compare(val, this.currentMin) < 0) {
                        this.currentMin = val;
                        this.currentMinPos = pos;
                    }
                    ++pos;
                }
            }
        }
    }
}

