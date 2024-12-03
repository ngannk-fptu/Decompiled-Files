/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

public class SoftDoublyIndexedTable {
    protected static final int INITIAL_CAPACITY = 11;
    protected Entry[] table;
    protected int count;
    protected ReferenceQueue referenceQueue = new ReferenceQueue();

    public SoftDoublyIndexedTable() {
        this.table = new Entry[11];
    }

    public SoftDoublyIndexedTable(int c) {
        this.table = new Entry[c];
    }

    public int size() {
        return this.count;
    }

    public Object get(Object o1, Object o2) {
        int hash = this.hashCode(o1, o2) & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry e = this.table[index];
        while (e != null) {
            if (e.hash == hash && e.match(o1, o2)) {
                return e.get();
            }
            e = e.next;
        }
        return null;
    }

    public Object put(Object o1, Object o2, Object value) {
        int len;
        this.removeClearedEntries();
        int hash = this.hashCode(o1, o2) & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry e = this.table[index];
        if (e != null) {
            if (e.hash == hash && e.match(o1, o2)) {
                Object old = e.get();
                this.table[index] = new Entry(hash, o1, o2, value, e.next);
                return old;
            }
            Entry o = e;
            e = e.next;
            while (e != null) {
                if (e.hash == hash && e.match(o1, o2)) {
                    Object old = e.get();
                    o.next = e = new Entry(hash, o1, o2, value, e.next);
                    return old;
                }
                o = e;
                e = e.next;
            }
        }
        if (this.count++ >= (len = this.table.length) - (len >> 2)) {
            this.rehash();
            index = hash % this.table.length;
        }
        this.table[index] = new Entry(hash, o1, o2, value, this.table[index]);
        return null;
    }

    public void clear() {
        this.table = new Entry[11];
        this.count = 0;
        this.referenceQueue = new ReferenceQueue();
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

    protected void removeClearedEntries() {
        Entry e;
        while ((e = (Entry)this.referenceQueue.poll()) != null) {
            int index = e.hash % this.table.length;
            Entry t = this.table[index];
            if (t == e) {
                this.table[index] = e.next;
            } else {
                while (t != null) {
                    Entry c = t.next;
                    if (c == e) {
                        t.next = e.next;
                        break;
                    }
                    t = c;
                }
            }
            --this.count;
        }
    }

    protected class Entry
    extends SoftReference {
        public int hash;
        public Object key1;
        public Object key2;
        public Entry next;

        public Entry(int hash, Object key1, Object key2, Object value, Entry next) {
            super(value, SoftDoublyIndexedTable.this.referenceQueue);
            this.hash = hash;
            this.key1 = key1;
            this.key2 = key2;
            this.next = next;
        }

        public boolean match(Object o1, Object o2) {
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

