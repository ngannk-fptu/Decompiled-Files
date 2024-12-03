/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

class StrongInternPool<E>
implements Cloneable,
Serializable {
    private static final Object NULL = new Object();
    private static final long serialVersionUID = 10929568968762L;
    private static final int DEFAULT_CAPACITY = 8;
    private static final int MAXIMUM_CAPACITY = 0x40000000;
    private static final float DEFAULT_LOAD_FACTOR = 0.67f;
    private transient Object[] table;
    private transient int size;
    private transient int threshold;
    private final float loadFactor;
    private transient int modCount;
    private transient Index index;

    public StrongInternPool(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Can not have a negative size table!");
        }
        if (initialCapacity > 0x40000000) {
            initialCapacity = 0x40000000;
        }
        if (!(loadFactor > 0.0f) || !(loadFactor <= 1.0f)) {
            throw new IllegalArgumentException("Load factor must be greater than 0 and less than or equal to 1");
        }
        this.loadFactor = loadFactor;
        this.init(initialCapacity, loadFactor);
    }

    private void init(int initialCapacity, float loadFactor) {
        int c;
        for (c = 1; c < initialCapacity; c <<= 1) {
        }
        this.threshold = (int)((float)c * loadFactor);
        if (initialCapacity > this.threshold && c < 0x40000000) {
            this.threshold = (int)((float)(c <<= 1) * loadFactor);
        }
        this.table = new Object[c];
    }

    private static boolean eq(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals((Object[])o1, (Object[])o2);
        }
        if (o1 instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.equals((byte[])o1, (byte[])o2);
        }
        return o1 != null && o1.equals(o2);
    }

    public StrongInternPool(int initialCapacity) {
        this(initialCapacity, 0.67f);
    }

    public StrongInternPool() {
        this(8);
    }

    private static int hash(Object o) {
        int h = o instanceof Object[] ? Arrays.hashCode((Object[])o) : (o instanceof byte[] ? Arrays.hashCode((byte[])o) : o.hashCode());
        return (h << 1) - (h << 8);
    }

    private static final <K> K maskNull(K key) {
        return (K)(key == null ? NULL : key);
    }

    private static final <K> K unmaskNull(K key) {
        return key == NULL ? null : (K)key;
    }

    private int nextIndex(int index, int length) {
        index = index >= length - 1 ? 0 : index + 1;
        return index;
    }

    private static final int index(int hashCode, int length) {
        return hashCode & length - 1;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public boolean contains(Object entry) {
        int index;
        entry = StrongInternPool.maskNull(entry);
        int hash = StrongInternPool.hash(entry);
        int length = this.table.length;
        int start = index = StrongInternPool.index(hash, length);
        do {
            Object e;
            if ((e = this.table[index]) == null) {
                return false;
            }
            if (!StrongInternPool.eq(entry, e)) continue;
            return true;
        } while ((index = this.nextIndex(index, length)) != start);
        return false;
    }

    private int offset(Object entry) {
        int index;
        entry = StrongInternPool.maskNull(entry);
        int hash = StrongInternPool.hash(entry);
        int length = this.table.length;
        int start = index = StrongInternPool.index(hash, length);
        do {
            Object e;
            if ((e = this.table[index]) == null) {
                return -1;
            }
            if (!StrongInternPool.eq(entry, e)) continue;
            return index;
        } while ((index = this.nextIndex(index, length)) != start);
        return -1;
    }

    public E intern(E entry) {
        Object e;
        int index;
        entry = StrongInternPool.maskNull(entry);
        Object[] table = this.table;
        int hash = StrongInternPool.hash(entry);
        int length = table.length;
        int start = index = StrongInternPool.index(hash, length);
        while ((e = table[index]) != null) {
            if (StrongInternPool.eq(entry, e)) {
                return (E)StrongInternPool.unmaskNull(e);
            }
            if ((index = this.nextIndex(index, length)) != start) continue;
            throw new IllegalStateException("Table is full!");
        }
        ++this.modCount;
        table[index] = entry;
        if (++this.size >= this.threshold) {
            this.resize(length);
        }
        return StrongInternPool.unmaskNull(entry);
    }

    private void resize(int from) {
        Object[] old;
        int newLength = from << 1;
        if (newLength > 0x40000000 || newLength <= from) {
            return;
        }
        Object[] newTable = new Object[newLength];
        for (Object e : old = this.table) {
            if (e == null) continue;
            int index = StrongInternPool.index(StrongInternPool.hash(e), newLength);
            while (newTable[index] != null) {
                index = this.nextIndex(index, newLength);
            }
            newTable[index] = e;
        }
        this.threshold = (int)(this.loadFactor * (float)newLength);
        this.table = newTable;
    }

    public boolean remove(Object o) {
        int start;
        o = StrongInternPool.maskNull(o);
        Object[] table = this.table;
        int length = table.length;
        int hash = StrongInternPool.hash(o);
        int index = start = StrongInternPool.index(hash, length);
        do {
            Object e;
            if ((e = table[index]) == null) {
                return false;
            }
            if (!StrongInternPool.eq(e, o)) continue;
            table[index] = null;
            this.relocate(index);
            ++this.modCount;
            --this.size;
            return true;
        } while ((index = this.nextIndex(index, length)) != start);
        return false;
    }

    private void relocate(int start) {
        Object[] table = this.table;
        int length = table.length;
        int current = this.nextIndex(start, length);
        Object e;
        while ((e = table[current]) != null) {
            int prefer = StrongInternPool.index(StrongInternPool.hash(e), length);
            if (current < prefer && (prefer <= start || start <= current) || prefer <= start && start <= current) {
                table[start] = e;
                table[current] = null;
                start = current;
            }
            current = this.nextIndex(current, length);
        }
        return;
    }

    public void clear() {
        ++this.modCount;
        Object[] table = this.table;
        for (int i = 0; i < table.length; ++i) {
            table[i] = null;
        }
        this.size = 0;
    }

    public StrongInternPool<E> clone() {
        try {
            StrongInternPool clone = (StrongInternPool)super.clone();
            clone.table = (Object[])this.table.clone();
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    public Object[] toInternalArray() {
        return this.table;
    }

    public void printDebugStats() {
        int optimal = 0;
        int total = 0;
        int totalSkew = 0;
        int maxSkew = 0;
        for (int i = 0; i < this.table.length; ++i) {
            Object e = this.table[i];
            if (e == null) continue;
            ++total;
            int target = StrongInternPool.index(StrongInternPool.hash(e), this.table.length);
            if (i == target) {
                ++optimal;
                continue;
            }
            int skew = Math.abs(i - target);
            if (skew > maxSkew) {
                maxSkew = skew;
            }
            totalSkew += skew;
        }
        System.out.println(" Size:            " + this.size);
        System.out.println(" Real Size:       " + total);
        System.out.println(" Optimal:         " + optimal + " (" + (float)optimal * 100.0f / (float)total + "%)");
        System.out.println(" Average Distnce: " + (float)totalSkew / (float)(total - optimal));
        System.out.println(" Max Distance:    " + maxSkew);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        int size = s.readInt();
        this.init(size, this.loadFactor);
        for (int i = 0; i < size; ++i) {
            this.putForCreate(s.readObject());
        }
        this.size = size;
    }

    private void putForCreate(E entry) {
        entry = StrongInternPool.maskNull(entry);
        Object[] table = this.table;
        int hash = StrongInternPool.hash(entry);
        int length = table.length;
        int index = StrongInternPool.index(hash, length);
        Object e = table[index];
        while (e != null) {
            index = this.nextIndex(index, length);
            e = table[index];
        }
        table[index] = entry;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.size);
        for (Object e : this.table) {
            if (e == null) continue;
            s.writeObject(StrongInternPool.unmaskNull(e));
        }
    }

    public Iterator<E> iterator() {
        return new IdentityHashSetIterator();
    }

    public Index index() {
        if (this.index == null || this.index.modCount != this.modCount) {
            this.index = new Index();
        }
        return this.index;
    }

    public String toString() {
        Iterator<E> i = this.iterator();
        if (!i.hasNext()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        while (true) {
            E e = i.next();
            sb.append(e);
            if (!i.hasNext()) {
                return sb.append(']').toString();
            }
            sb.append(", ");
        }
    }

    private class IdentityHashSetIterator
    implements Iterator<E> {
        private int next = 0;
        private int expectedCount = StrongInternPool.access$300(StrongInternPool.this);
        private int current = -1;
        private boolean hasNext;
        Object[] table = StrongInternPool.access$200(StrongInternPool.this);

        private IdentityHashSetIterator() {
        }

        @Override
        public boolean hasNext() {
            if (this.hasNext) {
                return true;
            }
            Object[] table = this.table;
            for (int i = this.next; i < table.length; ++i) {
                if (table[i] == null) continue;
                this.next = i;
                this.hasNext = true;
                return true;
            }
            this.next = table.length;
            return false;
        }

        @Override
        public E next() {
            if (StrongInternPool.this.modCount != this.expectedCount) {
                throw new ConcurrentModificationException();
            }
            if (!this.hasNext && !this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.current = this.next++;
            this.hasNext = false;
            return StrongInternPool.unmaskNull(this.table[this.current]);
        }

        @Override
        public void remove() {
            Object e;
            int current;
            if (StrongInternPool.this.modCount != this.expectedCount) {
                throw new ConcurrentModificationException();
            }
            int delete = current = this.current;
            if (current == -1) {
                throw new IllegalStateException();
            }
            this.current = -1;
            this.next = delete;
            Object[] table = this.table;
            if (table != StrongInternPool.this.table) {
                StrongInternPool.this.remove(table[delete]);
                table[delete] = null;
                this.expectedCount = StrongInternPool.this.modCount;
                return;
            }
            int length = table.length;
            int i = delete;
            table[delete] = null;
            StrongInternPool.this.size--;
            while ((e = table[i = StrongInternPool.this.nextIndex(i, length)]) != null) {
                int prefer = StrongInternPool.index(StrongInternPool.hash(e), length);
                if ((i >= prefer || prefer > delete && delete > i) && (prefer > delete || delete > i)) continue;
                if (i < current && current <= delete && table == StrongInternPool.this.table) {
                    int remaining = length - current;
                    Object[] newTable = new Object[remaining];
                    System.arraycopy(table, current, newTable, 0, remaining);
                    this.table = newTable;
                    this.next = 0;
                }
                table[delete] = e;
                table[i] = null;
                delete = i;
            }
        }
    }

    public class Index {
        private int[] offsets;
        private int modCount;

        Index() {
            this.offsets = new int[StrongInternPool.this.table.length];
            int c = 1;
            for (int i = 0; i < this.offsets.length; ++i) {
                if (StrongInternPool.this.table[i] == null) continue;
                this.offsets[i] = c++;
            }
            this.modCount = StrongInternPool.this.modCount;
        }

        public int positionOf(E e) {
            int offset = StrongInternPool.this.offset(e);
            return offset < 0 ? -1 : this.offsets[offset];
        }
    }
}

