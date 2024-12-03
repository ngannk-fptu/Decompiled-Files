/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.util;

import java.io.Serializable;

public class IntTable
implements Serializable {
    protected static final int INITIAL_CAPACITY = 11;
    protected Entry[] table;
    protected int count;

    public IntTable() {
        this.table = new Entry[11];
    }

    public IntTable(int c) {
        this.table = new Entry[c];
    }

    public IntTable(IntTable t) {
        this.count = t.count;
        this.table = new Entry[t.table.length];
        for (int i = 0; i < this.table.length; ++i) {
            Entry e = t.table[i];
            Entry n = null;
            if (e == null) continue;
            this.table[i] = n = new Entry(e.hash, e.key, e.value, null);
            e = e.next;
            while (e != null) {
                n = n.next = new Entry(e.hash, e.key, e.value, null);
                e = e.next;
            }
        }
    }

    public int size() {
        return this.count;
    }

    protected Entry find(Object key) {
        return null;
    }

    public int get(Object key) {
        int hash = key == null ? 0 : key.hashCode() & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry e = this.table[index];
        while (e != null) {
            if (e.hash == hash && (e.key == null && key == null || e.key != null && e.key.equals(key))) {
                return e.value;
            }
            e = e.next;
        }
        return 0;
    }

    public int put(Object key, int value) {
        int hash = key == null ? 0 : key.hashCode() & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry e = this.table[index];
        while (e != null) {
            if (e.hash == hash && (e.key == null && key == null || e.key != null && e.key.equals(key))) {
                int old = e.value;
                e.value = value;
                return old;
            }
            e = e.next;
        }
        int len = this.table.length;
        if (this.count++ >= len - (len >> 2)) {
            this.rehash();
            index = hash % this.table.length;
        }
        this.table[index] = new Entry(hash, key, value, this.table[index]);
        return 0;
    }

    public int inc(Object key) {
        int hash = key == null ? 0 : key.hashCode() & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry e = this.table[index];
        while (e != null) {
            if (e.hash == hash && (e.key == null && key == null || e.key != null && e.key.equals(key))) {
                return e.value++;
            }
            e = e.next;
        }
        int len = this.table.length;
        if (this.count++ >= len - (len >> 2)) {
            this.rehash();
            index = hash % this.table.length;
        }
        this.table[index] = new Entry(hash, key, 1, this.table[index]);
        return 0;
    }

    public int dec(Object key) {
        int hash = key == null ? 0 : key.hashCode() & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry e = this.table[index];
        while (e != null) {
            if (e.hash == hash && (e.key == null && key == null || e.key != null && e.key.equals(key))) {
                return e.value--;
            }
            e = e.next;
        }
        int len = this.table.length;
        if (this.count++ >= len - (len >> 2)) {
            this.rehash();
            index = hash % this.table.length;
        }
        this.table[index] = new Entry(hash, key, -1, this.table[index]);
        return 0;
    }

    public int remove(Object key) {
        int hash = key == null ? 0 : key.hashCode() & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry p = null;
        Entry e = this.table[index];
        while (e != null) {
            if (e.hash == hash && (e.key == null && key == null || e.key != null && e.key.equals(key))) {
                int result = e.value;
                if (p == null) {
                    this.table[index] = e.next;
                } else {
                    p.next = e.next;
                }
                --this.count;
                return result;
            }
            p = e;
            e = e.next;
        }
        return 0;
    }

    public void clear() {
        for (int i = 0; i < this.table.length; ++i) {
            this.table[i] = null;
        }
        this.count = 0;
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

    protected static class Entry
    implements Serializable {
        public int hash;
        public Object key;
        public int value;
        public Entry next;

        public Entry(int hash, Object key, int value, Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}

