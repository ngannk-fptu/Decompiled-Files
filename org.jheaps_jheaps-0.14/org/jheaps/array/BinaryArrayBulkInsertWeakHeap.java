/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.array;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.jheaps.annotations.ConstantTime;
import org.jheaps.annotations.LinearTime;
import org.jheaps.annotations.LogarithmicTime;
import org.jheaps.array.BinaryArrayWeakHeap;

public class BinaryArrayBulkInsertWeakHeap<K>
extends BinaryArrayWeakHeap<K>
implements Serializable {
    private static final long serialVersionUID = 1L;
    protected static final int INSERTION_BUFFER_CAPACITY = 34;
    protected K[] insertionBuffer = new Object[34];
    protected int insertionBufferSize = 0;
    protected int insertionBufferMinPos = 0;

    public BinaryArrayBulkInsertWeakHeap() {
        this(null, 16);
    }

    public BinaryArrayBulkInsertWeakHeap(int capacity) {
        this(null, capacity);
    }

    public BinaryArrayBulkInsertWeakHeap(Comparator<? super K> comparator) {
        this(comparator, 16);
    }

    public BinaryArrayBulkInsertWeakHeap(Comparator<? super K> comparator, int capacity) {
        super(comparator, capacity);
    }

    @Override
    @ConstantTime
    public boolean isEmpty() {
        return this.size + this.insertionBufferSize == 0;
    }

    @Override
    @ConstantTime
    public long size() {
        return (long)this.size + (long)this.insertionBufferSize;
    }

    @Override
    @ConstantTime
    public void clear() {
        this.size = 0;
        this.insertionBufferSize = 0;
        this.insertionBufferMinPos = 0;
    }

    @Override
    @ConstantTime
    public K findMin() {
        if (this.size + this.insertionBufferSize == 0) {
            throw new NoSuchElementException();
        }
        if (this.insertionBufferSize == 0) {
            return (K)this.array[0];
        }
        if (this.size == 0) {
            return this.insertionBuffer[this.insertionBufferMinPos];
        }
        K insertionBufferMin = this.insertionBuffer[this.insertionBufferMinPos];
        if (this.comparator == null) {
            if (((Comparable)this.array[0]).compareTo(insertionBufferMin) <= 0) {
                return (K)this.array[0];
            }
            return insertionBufferMin;
        }
        if (this.comparator.compare(this.array[0], insertionBufferMin) <= 0) {
            return (K)this.array[0];
        }
        return insertionBufferMin;
    }

    @Override
    @ConstantTime(amortized=true)
    public void insert(K key) {
        if (key == null) {
            throw new NullPointerException("Null keys not permitted");
        }
        this.insertionBuffer[this.insertionBufferSize++] = key;
        if (this.isBulkInsertionBufferFull()) {
            if (this.size + this.insertionBufferSize > this.array.length) {
                if (this.array.length == 0) {
                    this.ensureCapacity(1);
                } else {
                    this.ensureCapacity(2 * this.array.length);
                }
                this.ensureCapacity(this.size + this.insertionBufferSize);
            }
            if (this.comparator == null) {
                this.bulkInsert();
            } else {
                this.bulkInsertWithComparator();
            }
        } else if (this.insertionBufferSize > 1) {
            K insertionBufferMin = this.insertionBuffer[this.insertionBufferMinPos];
            if (this.comparator == null) {
                if (((Comparable)key).compareTo(insertionBufferMin) < 0) {
                    this.insertionBufferMinPos = this.insertionBufferSize - 1;
                }
            } else if (this.comparator.compare(key, insertionBufferMin) < 0) {
                this.insertionBufferMinPos = this.insertionBufferSize - 1;
            }
        }
    }

    @Override
    @LogarithmicTime(amortized=true)
    public K deleteMin() {
        Object result;
        if (this.size + this.insertionBufferSize == 0) {
            throw new NoSuchElementException();
        }
        boolean deleteFromInsertionBuffer = false;
        if (this.size == 0) {
            deleteFromInsertionBuffer = true;
        } else if (this.insertionBufferSize > 0) {
            Object arrayMin = this.array[0];
            K insertionBufferMin = this.insertionBuffer[this.insertionBufferMinPos];
            if (this.comparator == null) {
                if (((Comparable)insertionBufferMin).compareTo(arrayMin) < 0) {
                    deleteFromInsertionBuffer = true;
                }
            } else if (this.comparator.compare(insertionBufferMin, arrayMin) < 0) {
                deleteFromInsertionBuffer = true;
            }
        }
        if (deleteFromInsertionBuffer) {
            result = this.insertionBuffer[this.insertionBufferMinPos];
            this.insertionBuffer[this.insertionBufferMinPos] = this.insertionBuffer[this.insertionBufferSize - 1];
            this.insertionBuffer[this.insertionBufferSize - 1] = null;
            --this.insertionBufferSize;
            this.insertionBufferMinPos = 0;
            if (this.comparator == null) {
                for (int i = 1; i < this.insertionBufferSize; ++i) {
                    if (((Comparable)this.insertionBuffer[i]).compareTo(this.insertionBuffer[this.insertionBufferMinPos]) >= 0) continue;
                    this.insertionBufferMinPos = i;
                }
            } else {
                for (int i = 1; i < this.insertionBufferSize; ++i) {
                    if (this.comparator.compare(this.insertionBuffer[i], this.insertionBuffer[this.insertionBufferMinPos]) >= 0) continue;
                    this.insertionBufferMinPos = i;
                }
            }
        } else {
            result = this.array[0];
            --this.size;
            this.array[0] = this.array[this.size];
            this.array[this.size] = null;
            if (this.size > 1) {
                if (this.comparator == null) {
                    this.fixdown(0);
                } else {
                    this.fixdownWithComparator(0);
                }
            }
            if (this.minCapacity <= this.array.length && 4 * this.size < this.array.length) {
                this.ensureCapacity(this.array.length / 2);
            }
        }
        return (K)result;
    }

    @LinearTime
    public static <K> BinaryArrayBulkInsertWeakHeap<K> heapify(K[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length == 0) {
            return new BinaryArrayBulkInsertWeakHeap<K>();
        }
        BinaryArrayBulkInsertWeakHeap<K> h = new BinaryArrayBulkInsertWeakHeap<K>(array.length);
        System.arraycopy(array, 0, h.array, 0, array.length);
        h.size = array.length;
        for (int j = h.size - 1; j > 0; --j) {
            h.join(h.dancestor(j), j);
        }
        return h;
    }

    @LinearTime
    public static <K> BinaryArrayBulkInsertWeakHeap<K> heapify(K[] array, Comparator<? super K> comparator) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length == 0) {
            return new BinaryArrayBulkInsertWeakHeap<K>(comparator);
        }
        BinaryArrayBulkInsertWeakHeap<K> h = new BinaryArrayBulkInsertWeakHeap<K>(comparator, array.length);
        System.arraycopy(array, 0, h.array, 0, array.length);
        h.size = array.length;
        for (int j = h.size - 1; j > 0; --j) {
            h.joinWithComparator(h.dancestor(j), j);
        }
        return h;
    }

    protected boolean isBulkInsertionBufferFull() {
        if (this.insertionBufferSize >= this.insertionBuffer.length) {
            return true;
        }
        double sizeAsDouble = (double)this.size + (double)this.insertionBufferSize;
        return Math.getExponent(sizeAsDouble) + 3 >= this.insertionBuffer.length;
    }

    protected void bulkInsert() {
        int i;
        if (this.insertionBufferSize == 0) {
            return;
        }
        int right = this.size + this.insertionBufferSize - 2;
        int left = Math.max(this.size, right / 2);
        while (this.insertionBufferSize > 0) {
            --this.insertionBufferSize;
            this.array[this.size] = this.insertionBuffer[this.insertionBufferSize];
            this.insertionBuffer[this.insertionBufferSize] = null;
            this.reverse.clear(this.size);
            ++this.size;
        }
        while (right > left + 1) {
            right /= 2;
            for (int j = left /= 2; j <= right; ++j) {
                this.fixdown(j);
            }
        }
        if (left != 0) {
            i = this.dancestor(left);
            this.fixdown(i);
            this.fixup(i);
        }
        if (right != 0) {
            i = this.dancestor(right);
            this.fixdown(i);
            this.fixup(i);
        }
        this.insertionBufferMinPos = 0;
    }

    protected void bulkInsertWithComparator() {
        int i;
        if (this.insertionBufferSize == 0) {
            return;
        }
        int right = this.size + this.insertionBufferSize - 2;
        int left = Math.max(this.size, right / 2);
        while (this.insertionBufferSize > 0) {
            --this.insertionBufferSize;
            this.array[this.size] = this.insertionBuffer[this.insertionBufferSize];
            this.insertionBuffer[this.insertionBufferSize] = null;
            this.reverse.clear(this.size);
            ++this.size;
        }
        while (right > left + 1) {
            right /= 2;
            for (int j = left /= 2; j <= right; ++j) {
                this.fixdownWithComparator(j);
            }
        }
        if (left != 0) {
            i = this.dancestor(left);
            this.fixdownWithComparator(i);
            this.fixupWithComparator(i);
        }
        if (right != 0) {
            i = this.dancestor(right);
            this.fixdownWithComparator(i);
            this.fixupWithComparator(i);
        }
        this.insertionBufferMinPos = 0;
    }
}

