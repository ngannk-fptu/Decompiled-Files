/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.buffer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
BoundedCollection,
Serializable {
    private static final long serialVersionUID = 5603722811189451017L;
    private transient Object[] elements;
    private transient int start = 0;
    private transient int end = 0;
    private transient boolean full = false;
    private final int maxElements;

    public BoundedFifoBuffer() {
        this(32);
    }

    public BoundedFifoBuffer(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("The size must be greater than 0");
        }
        this.elements = new Object[size];
        this.maxElements = this.elements.length;
    }

    public BoundedFifoBuffer(Collection coll) {
        this(coll.size());
        this.addAll(coll);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(this.size());
        Iterator it = this.iterator();
        while (it.hasNext()) {
            out.writeObject(it.next());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.elements = new Object[this.maxElements];
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            this.elements[i] = in.readObject();
        }
        this.start = 0;
        this.full = size == this.maxElements;
        this.end = this.full ? 0 : size;
    }

    public int size() {
        int size = 0;
        size = this.end < this.start ? this.maxElements - this.start + this.end : (this.end == this.start ? (this.full ? this.maxElements : 0) : this.end - this.start);
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
        this.full = false;
        this.start = 0;
        this.end = 0;
        Arrays.fill(this.elements, null);
    }

    public boolean add(Object element) {
        if (null == element) {
            throw new NullPointerException("Attempted to add null object to buffer");
        }
        if (this.full) {
            throw new BufferOverflowException("The buffer cannot hold more than " + this.maxElements + " objects.");
        }
        this.elements[this.end++] = element;
        if (this.end >= this.maxElements) {
            this.end = 0;
        }
        if (this.end == this.start) {
            this.full = true;
        }
        return true;
    }

    public Object get() {
        if (this.isEmpty()) {
            throw new BufferUnderflowException("The buffer is already empty");
        }
        return this.elements[this.start];
    }

    public Object remove() {
        if (this.isEmpty()) {
            throw new BufferUnderflowException("The buffer is already empty");
        }
        Object element = this.elements[this.start];
        if (null != element) {
            this.elements[this.start++] = null;
            if (this.start >= this.maxElements) {
                this.start = 0;
            }
            this.full = false;
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
                this.index = BoundedFifoBuffer.this.start;
                this.lastReturnedIndex = -1;
                this.isFirst = BoundedFifoBuffer.this.full;
            }

            public boolean hasNext() {
                return this.isFirst || this.index != BoundedFifoBuffer.this.end;
            }

            public Object next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.isFirst = false;
                this.lastReturnedIndex = this.index;
                this.index = BoundedFifoBuffer.this.increment(this.index);
                return BoundedFifoBuffer.this.elements[this.lastReturnedIndex];
            }

            public void remove() {
                if (this.lastReturnedIndex == -1) {
                    throw new IllegalStateException();
                }
                if (this.lastReturnedIndex == BoundedFifoBuffer.this.start) {
                    BoundedFifoBuffer.this.remove();
                    this.lastReturnedIndex = -1;
                    return;
                }
                int pos = this.lastReturnedIndex + 1;
                if (BoundedFifoBuffer.this.start < this.lastReturnedIndex && pos < BoundedFifoBuffer.this.end) {
                    System.arraycopy(BoundedFifoBuffer.this.elements, pos, BoundedFifoBuffer.this.elements, this.lastReturnedIndex, BoundedFifoBuffer.this.end - pos);
                } else {
                    while (pos != BoundedFifoBuffer.this.end) {
                        if (pos >= BoundedFifoBuffer.this.maxElements) {
                            ((BoundedFifoBuffer)BoundedFifoBuffer.this).elements[pos - 1] = BoundedFifoBuffer.this.elements[0];
                            pos = 0;
                            continue;
                        }
                        ((BoundedFifoBuffer)BoundedFifoBuffer.this).elements[((BoundedFifoBuffer)BoundedFifoBuffer.this).decrement((int)pos)] = BoundedFifoBuffer.this.elements[pos];
                        pos = BoundedFifoBuffer.this.increment(pos);
                    }
                }
                this.lastReturnedIndex = -1;
                BoundedFifoBuffer.this.end = BoundedFifoBuffer.this.decrement(BoundedFifoBuffer.this.end);
                ((BoundedFifoBuffer)BoundedFifoBuffer.this).elements[((BoundedFifoBuffer)BoundedFifoBuffer.this).end] = null;
                BoundedFifoBuffer.this.full = false;
                this.index = BoundedFifoBuffer.this.decrement(this.index);
            }
        };
    }
}

