/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

public abstract class PriorityQueue<T> {
    private int size = 0;
    private final int maxSize;
    private final T[] heap;

    public PriorityQueue(int maxSize) {
        this(maxSize, true);
    }

    public PriorityQueue(int maxSize, boolean prepopulate) {
        T sentinel;
        int heapSize = 0 == maxSize ? 2 : (maxSize == Integer.MAX_VALUE ? Integer.MAX_VALUE : maxSize + 1);
        this.heap = new Object[heapSize];
        this.maxSize = maxSize;
        if (prepopulate && (sentinel = this.getSentinelObject()) != null) {
            this.heap[1] = sentinel;
            for (int i = 2; i < this.heap.length; ++i) {
                this.heap[i] = this.getSentinelObject();
            }
            this.size = maxSize;
        }
    }

    protected abstract boolean lessThan(T var1, T var2);

    protected T getSentinelObject() {
        return null;
    }

    public final T add(T element) {
        ++this.size;
        this.heap[this.size] = element;
        this.upHeap();
        return this.heap[1];
    }

    public T insertWithOverflow(T element) {
        if (this.size < this.maxSize) {
            this.add(element);
            return null;
        }
        if (this.size > 0 && !this.lessThan(element, this.heap[1])) {
            T ret = this.heap[1];
            this.heap[1] = element;
            this.updateTop();
            return ret;
        }
        return element;
    }

    public final T top() {
        return this.heap[1];
    }

    public final T pop() {
        if (this.size > 0) {
            T result = this.heap[1];
            this.heap[1] = this.heap[this.size];
            this.heap[this.size] = null;
            --this.size;
            this.downHeap();
            return result;
        }
        return null;
    }

    public final T updateTop() {
        this.downHeap();
        return this.heap[1];
    }

    public final int size() {
        return this.size;
    }

    public final void clear() {
        for (int i = 0; i <= this.size; ++i) {
            this.heap[i] = null;
        }
        this.size = 0;
    }

    private final void upHeap() {
        int i = this.size;
        T node = this.heap[i];
        for (int j = i >>> 1; j > 0 && this.lessThan(node, this.heap[j]); j >>>= 1) {
            this.heap[i] = this.heap[j];
            i = j;
        }
        this.heap[i] = node;
    }

    private final void downHeap() {
        int i = 1;
        T node = this.heap[i];
        int j = i << 1;
        int k = j + 1;
        if (k <= this.size && this.lessThan(this.heap[k], this.heap[j])) {
            j = k;
        }
        while (j <= this.size && this.lessThan(this.heap[j], node)) {
            this.heap[i] = this.heap[j];
            i = j;
            k = (j = i << 1) + 1;
            if (k > this.size || !this.lessThan(this.heap[k], this.heap[j])) continue;
            j = k;
        }
        this.heap[i] = node;
    }

    protected final Object[] getHeapArray() {
        return this.heap;
    }
}

