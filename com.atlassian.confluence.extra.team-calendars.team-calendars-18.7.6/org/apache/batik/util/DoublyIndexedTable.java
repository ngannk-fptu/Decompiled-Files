/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoublyIndexedTable {
    protected int initialCapacity;
    protected Entry[] table;
    protected int count;

    public DoublyIndexedTable() {
        this(16);
    }

    public DoublyIndexedTable(int c) {
        this.initialCapacity = c;
        this.table = new Entry[c];
    }

    public DoublyIndexedTable(DoublyIndexedTable other) {
        this.initialCapacity = other.initialCapacity;
        this.table = new Entry[other.table.length];
        for (int i = 0; i < other.table.length; ++i) {
            Entry newE = null;
            Entry e = other.table[i];
            while (e != null) {
                newE = new Entry(e.hash, e.key1, e.key2, e.value, newE);
                e = e.next;
            }
            this.table[i] = newE;
        }
        this.count = other.count;
    }

    public int size() {
        return this.count;
    }

    public Object put(Object o1, Object o2, Object value) {
        Entry e;
        int hash = this.hashCode(o1, o2) & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry e2 = this.table[index];
        while (e2 != null) {
            if (e2.hash == hash && e2.match(o1, o2)) {
                Object old = e2.value;
                e2.value = value;
                return old;
            }
            e2 = e2.next;
        }
        int len = this.table.length;
        if (this.count++ >= len - (len >> 2)) {
            this.rehash();
            index = hash % this.table.length;
        }
        this.table[index] = e = new Entry(hash, o1, o2, value, this.table[index]);
        return null;
    }

    public Object get(Object o1, Object o2) {
        int hash = this.hashCode(o1, o2) & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry e = this.table[index];
        while (e != null) {
            if (e.hash == hash && e.match(o1, o2)) {
                return e.value;
            }
            e = e.next;
        }
        return null;
    }

    public Object remove(Object o1, Object o2) {
        int hash = this.hashCode(o1, o2) & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry e = this.table[index];
        if (e == null) {
            return null;
        }
        if (e.hash == hash && e.match(o1, o2)) {
            this.table[index] = e.next;
            --this.count;
            return e.value;
        }
        Entry prev = e;
        e = e.next;
        while (e != null) {
            if (e.hash == hash && e.match(o1, o2)) {
                prev.next = e.next;
                --this.count;
                return e.value;
            }
            prev = e;
            e = e.next;
        }
        return null;
    }

    public Object[] getValuesArray() {
        Object[] values = new Object[this.count];
        int i = 0;
        Entry[] entryArray = this.table;
        int n = entryArray.length;
        for (int j = 0; j < n; ++j) {
            Entry aTable;
            Entry e = aTable = entryArray[j];
            while (e != null) {
                values[i++] = e.value;
                e = e.next;
            }
        }
        return values;
    }

    public void clear() {
        this.table = new Entry[this.initialCapacity];
        this.count = 0;
    }

    public Iterator iterator() {
        return new TableIterator();
    }

    protected void rehash() {
        Entry[] oldTable = this.table;
        this.table = new Entry[oldTable.length * 2 + 1];
        for (int i = oldTable.length - 1; i >= 0; --i) {
            Entry old = oldTable[i];
            while (old != null) {
                Entry e = old;
                old = old.next;
                int index = e.hash % this.table.length;
                e.next = this.table[index];
                this.table[index] = e;
            }
        }
    }

    protected int hashCode(Object o1, Object o2) {
        int result = o1 == null ? 0 : o1.hashCode();
        return result ^ (o2 == null ? 0 : o2.hashCode());
    }

    protected class TableIterator
    implements Iterator {
        private int nextIndex;
        private Entry nextEntry;
        private boolean finished;

        public TableIterator() {
            while (this.nextIndex < DoublyIndexedTable.this.table.length) {
                this.nextEntry = DoublyIndexedTable.this.table[this.nextIndex];
                if (this.nextEntry != null) break;
                ++this.nextIndex;
            }
            this.finished = this.nextEntry == null;
        }

        @Override
        public boolean hasNext() {
            return !this.finished;
        }

        public Object next() {
            if (this.finished) {
                throw new NoSuchElementException();
            }
            Entry ret = this.nextEntry;
            this.findNext();
            return ret;
        }

        protected void findNext() {
            this.nextEntry = this.nextEntry.next;
            if (this.nextEntry == null) {
                ++this.nextIndex;
                while (this.nextIndex < DoublyIndexedTable.this.table.length) {
                    this.nextEntry = DoublyIndexedTable.this.table[this.nextIndex];
                    if (this.nextEntry != null) break;
                    ++this.nextIndex;
                }
            }
            this.finished = this.nextEntry == null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static class Entry {
        protected int hash;
        protected Object key1;
        protected Object key2;
        protected Object value;
        protected Entry next;

        public Entry(int hash, Object key1, Object key2, Object value, Entry next) {
            this.hash = hash;
            this.key1 = key1;
            this.key2 = key2;
            this.value = value;
            this.next = next;
        }

        public Object getKey1() {
            return this.key1;
        }

        public Object getKey2() {
            return this.key2;
        }

        public Object getValue() {
            return this.value;
        }

        protected boolean match(Object o1, Object o2) {
            if (this.key1 != null ? !this.key1.equals(o1) : o1 != null) {
                return false;
            }
            if (this.key2 != null) {
                return this.key2.equals(o2);
            }
            return o2 == null;
        }
    }
}

