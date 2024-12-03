/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine;

public class StringIntMap {
    protected Entry[] table;
    protected int count;

    public StringIntMap(int c) {
        this.table = new Entry[c - (c >> 2) + 1];
    }

    public int get(String key) {
        int hash = key.hashCode() & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry e = this.table[index];
        while (e != null) {
            if (e.hash == hash && e.key.equals(key)) {
                return e.value;
            }
            e = e.next;
        }
        return -1;
    }

    public void put(String key, int value) {
        Entry e;
        int hash = key.hashCode() & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry e2 = this.table[index];
        while (e2 != null) {
            if (e2.hash == hash && e2.key.equals(key)) {
                e2.value = value;
                return;
            }
            e2 = e2.next;
        }
        int len = this.table.length;
        if (this.count++ >= len - (len >> 2)) {
            this.rehash();
            index = hash % this.table.length;
        }
        this.table[index] = e = new Entry(hash, key, value, this.table[index]);
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
        public final int hash;
        public String key;
        public int value;
        public Entry next;

        public Entry(int hash, String key, int value, Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}

