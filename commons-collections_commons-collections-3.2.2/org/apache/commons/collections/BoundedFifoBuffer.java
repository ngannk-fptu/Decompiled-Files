/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections.BoundedCollection;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferOverflowException;
import org.apache.commons.collections.BufferUnderflowException;

public class BoundedFifoBuffer
extends AbstractCollection
implements Buffer,
BoundedCollection {
    private final Object[] m_elements;
    private int m_start = 0;
    private int m_end = 0;
    private boolean m_full = false;
    private final int maxElements;

    public BoundedFifoBuffer() {
        this(32);
    }

    public BoundedFifoBuffer(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("The size must be greater than 0");
        }
        this.m_elements = new Object[size];
        this.maxElements = this.m_elements.length;
    }

    public BoundedFifoBuffer(Collection coll) {
        this(coll.size());
        this.addAll(coll);
    }

    public int size() {
        int size = 0;
        size = this.m_end < this.m_start ? this.maxElements - this.m_start + this.m_end : (this.m_end == this.m_start ? (this.m_full ? this.maxElements : 0) : this.m_end - this.m_start);
        return size;
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public boolean isFull() {
        return this.size() == this.maxElements;
    }

    public int maxSize() {
        return this.maxElements;
    }

    public void clear() {
        this.m_full = false;
        this.m_start = 0;
        this.m_end = 0;
        Arrays.fill(this.m_elements, null);
    }

    public boolean add(Object element) {
        if (null == element) {
            throw new NullPointerException("Attempted to add null object to buffer");
        }
        if (this.m_full) {
            throw new BufferOverflowException("The buffer cannot hold more than " + this.maxElements + " objects.");
        }
        this.m_elements[this.m_end++] = element;
        if (this.m_end >= this.maxElements) {
            this.m_end = 0;
        }
        if (this.m_end == this.m_start) {
            this.m_full = true;
        }
        return true;
    }

    public Object get() {
        if (this.isEmpty()) {
            throw new BufferUnderflowException("The buffer is already empty");
        }
        return this.m_elements[this.m_start];
    }

    public Object remove() {
        if (this.isEmpty()) {
            throw new BufferUnderflowException("The buffer is already empty");
        }
        Object element = this.m_elements[this.m_start];
        if (null != element) {
            this.m_elements[this.m_start++] = null;
            if (this.m_start >= this.maxElements) {
                this.m_start = 0;
            }
            this.m_full = false;
        }
        return element;
    }

    private int increment(int index) {
        if (++index >= this.maxElements) {
            index = 0;
        }
        return index;
    }

    private int decrement(int index) {
        if (--index < 0) {
            index = this.maxElements - 1;
        }
        return index;
    }

    public Iterator iterator() {
        return new Iterator(){
            private int index;
            private int lastReturnedIndex;
            private boolean isFirst;
            {
                this.index = BoundedFifoBuffer.this.m_start;
                this.lastReturnedIndex = -1;
                this.isFirst = BoundedFifoBuffer.this.m_full;
            }

            public boolean hasNext() {
                return this.isFirst || this.index != BoundedFifoBuffer.this.m_end;
            }

            public Object next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.isFirst = false;
                this.lastReturnedIndex = this.index;
                this.index = BoundedFifoBuffer.this.increment(this.index);
                return BoundedFifoBuffer.this.m_elements[this.lastReturnedIndex];
            }

            public void remove() {
                if (this.lastReturnedIndex == -1) {
                    throw new IllegalStateException();
                }
                if (this.lastReturnedIndex == BoundedFifoBuffer.this.m_start) {
                    BoundedFifoBuffer.this.remove();
                    this.lastReturnedIndex = -1;
                    return;
                }
                int i = this.lastReturnedIndex + 1;
                while (i != BoundedFifoBuffer.this.m_end) {
                    if (i >= BoundedFifoBuffer.this.maxElements) {
                        ((BoundedFifoBuffer)BoundedFifoBuffer.this).m_elements[i - 1] = BoundedFifoBuffer.this.m_elements[0];
                        i = 0;
                        continue;
                    }
                    ((BoundedFifoBuffer)BoundedFifoBuffer.this).m_elements[i - 1] = BoundedFifoBuffer.this.m_elements[i];
                    ++i;
                }
                this.lastReturnedIndex = -1;
                BoundedFifoBuffer.this.m_end = BoundedFifoBuffer.this.decrement(BoundedFifoBuffer.this.m_end);
                ((BoundedFifoBuffer)BoundedFifoBuffer.this).m_elements[((BoundedFifoBuffer)BoundedFifoBuffer.this).m_end] = null;
                BoundedFifoBuffer.this.m_full = false;
                this.index = BoundedFifoBuffer.this.decrement(this.index);
            }
        };
    }
}

