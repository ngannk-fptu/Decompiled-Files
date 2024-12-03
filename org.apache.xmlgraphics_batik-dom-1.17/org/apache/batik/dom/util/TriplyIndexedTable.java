/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.util;

public class TriplyIndexedTable {
    protected static final int INITIAL_CAPACITY = 11;
    protected Entry[] table;
    protected int count;

    public TriplyIndexedTable() {
        this.table = new Entry[11];
    }

    public TriplyIndexedTable(int c) {
        this.table = new Entry[c];
    }

    public int size() {
        return this.count;
    }

    public Object put(Object o1, Object o2, Object o3, Object value) {
        Entry e;
        int hash = this.hashCode(o1, o2, o3) & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry e2 = this.table[index];
        while (e2 != null) {
            if (e2.hash == hash && e2.match(o1, o2, o3)) {
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
        this.table[index] = e = new Entry(hash, o1, o2, o3, value, this.table[index]);
        return null;
    }

    public Object get(Object o1, Object o2, Object o3) {
        int hash = this.hashCode(o1, o2, o3) & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry e = this.table[index];
        while (e != null) {
            if (e.hash == hash && e.match(o1, o2, o3)) {
                return e.value;
            }
            e = e.next;
        }
        return null;
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

    protected int hashCode(Object o1, Object o2, Object o3) {
        return (o1 == null ? 0 : o1.hashCode()) ^ (o2 == null ? 0 : o2.hashCode()) ^ (o3 == null ? 0 : o3.hashCode());
    }

    protected static class Entry {
        public int hash;
        public Object key1;
        public Object key2;
        public Object key3;
        public Object value;
        public Entry next;

        public Entry(int hash, Object key1, Object key2, Object key3, Object value, Entry next) {
            this.hash = hash;
            this.key1 = key1;
            this.key2 = key2;
            this.key3 = key3;
            this.value = value;
            this.next = next;
        }

        public boolean match(Object o1, Object o2, Object o3) {
            if (this.key1 != null ? !this.key1.equals(o1) : o1 != null) {
                return false;
            }
            if (this.key2 != null ? !this.key2.equals(o2) : o2 != null) {
                return false;
            }
            if (this.key3 != null) {
                return this.key3.equals(o3);
            }
            return o3 == null;
        }
    }
}

