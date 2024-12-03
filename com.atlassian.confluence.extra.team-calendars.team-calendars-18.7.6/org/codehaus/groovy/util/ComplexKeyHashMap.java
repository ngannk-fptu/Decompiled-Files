/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import java.util.NoSuchElementException;

public class ComplexKeyHashMap {
    protected Entry[] table;
    protected static final int DEFAULT_CAPACITY = 32;
    protected static final int MINIMUM_CAPACITY = 4;
    protected static final int MAXIMUM_CAPACITY = 0x10000000;
    protected int size;
    protected transient int threshold;

    public ComplexKeyHashMap() {
        this.init(32);
    }

    public ComplexKeyHashMap(boolean b) {
    }

    public ComplexKeyHashMap(int expectedMaxSize) {
        this.init(ComplexKeyHashMap.capacity(expectedMaxSize));
    }

    public static int hash(int h) {
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void clear() {
        Entry[] tab = this.table;
        for (int i = 0; i < tab.length; ++i) {
            tab[i] = null;
        }
        this.size = 0;
    }

    public void init(int initCapacity) {
        this.threshold = initCapacity * 6 / 8;
        this.table = new Entry[initCapacity];
    }

    public void resize(int newLength) {
        Entry[] oldTable = this.table;
        int oldLength = this.table.length;
        Entry[] newTable = new Entry[newLength];
        for (int j = 0; j < oldLength; ++j) {
            Entry e = oldTable[j];
            while (e != null) {
                Entry next = e.next;
                int index = e.hash & newLength - 1;
                e.next = newTable[index];
                newTable[index] = e;
                e = next;
            }
        }
        this.table = newTable;
        this.threshold = 6 * newLength / 8;
    }

    private static int capacity(int expectedMaxSize) {
        int result;
        int minCapacity = 8 * expectedMaxSize / 6;
        if (minCapacity > 0x10000000 || minCapacity < 0) {
            result = 0x10000000;
        } else {
            for (result = 4; result < minCapacity; result <<= 1) {
            }
        }
        return result;
    }

    public Entry[] getTable() {
        return this.table;
    }

    public EntryIterator getEntrySetIterator() {
        return new EntryIterator(){
            Entry next;
            int index;
            {
                Entry[] t = ComplexKeyHashMap.this.table;
                int i = t.length;
                Entry n = null;
                if (ComplexKeyHashMap.this.size != 0) {
                    while (i > 0 && (n = t[--i]) == null) {
                    }
                }
                this.next = n;
                this.index = i;
            }

            @Override
            public boolean hasNext() {
                return this.next != null;
            }

            @Override
            public Entry next() {
                return this.nextEntry();
            }

            Entry nextEntry() {
                Entry e = this.next;
                if (e == null) {
                    throw new NoSuchElementException();
                }
                Entry n = e.next;
                Entry[] t = ComplexKeyHashMap.this.table;
                int i = this.index;
                while (n == null && i > 0) {
                    n = t[--i];
                }
                this.index = i;
                this.next = n;
                return e;
            }
        };
    }

    public static interface EntryIterator {
        public boolean hasNext();

        public Entry next();
    }

    public static class Entry {
        public int hash;
        public Entry next;
        public Object value;

        public Object getValue() {
            return this.value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}

