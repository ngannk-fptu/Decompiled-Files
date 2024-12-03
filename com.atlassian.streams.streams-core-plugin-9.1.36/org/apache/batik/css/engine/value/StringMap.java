/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value;

public class StringMap {
    protected static final int INITIAL_CAPACITY = 11;
    protected Entry[] table;
    protected int count;

    public StringMap() {
        this.table = new Entry[11];
    }

    public StringMap(StringMap t) {
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

    public Object get(String key) {
        int hash = key.hashCode() & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry e = this.table[index];
        while (e != null) {
            if (e.hash == hash && e.key == key) {
                return e.value;
            }
            e = e.next;
        }
        return null;
    }

    public Object put(String key, Object value) {
        Entry e;
        int hash = key.hashCode() & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry e2 = this.table[index];
        while (e2 != null) {
            if (e2.hash == hash && e2.key == key) {
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
        this.table[index] = e = new Entry(hash, key, value, this.table[index]);
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

    protected static class Entry {
        public int hash;
        public String key;
        public Object value;
        public Entry next;

        public Entry(int hash, String key, Object value, Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}

