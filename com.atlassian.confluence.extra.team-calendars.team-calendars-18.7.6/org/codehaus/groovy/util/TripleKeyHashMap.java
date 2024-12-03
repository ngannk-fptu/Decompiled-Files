/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import org.codehaus.groovy.util.ComplexKeyHashMap;

public class TripleKeyHashMap
extends ComplexKeyHashMap {
    public final Object get(Object key1, Object key2, Object key3) {
        int h = TripleKeyHashMap.hash(31 * (31 * key1.hashCode() + key2.hashCode()) + key3.hashCode());
        ComplexKeyHashMap.Entry e = this.table[h & this.table.length - 1];
        while (e != null) {
            if (e.hash == h && this.checkEquals((Entry)e, key1, key2, key3)) {
                return e;
            }
            e = e.next;
        }
        return null;
    }

    public boolean checkEquals(Entry e, Object key1, Object key2, Object key3) {
        return e.key1.equals(key1) && e.key2.equals(key2) && e.key3.equals(key3);
    }

    public Entry getOrPut(Object key1, Object key2, Object key3) {
        int h = TripleKeyHashMap.hash(31 * (31 * key1.hashCode() + key2.hashCode()) + key3.hashCode());
        int index = h & this.table.length - 1;
        ComplexKeyHashMap.Entry e = this.table[index];
        while (e != null) {
            if (e.hash == h && this.checkEquals((Entry)e, key1, key2, key3)) {
                return (Entry)e;
            }
            e = e.next;
        }
        Entry entry = this.createEntry();
        entry.next = this.table[index];
        entry.hash = h;
        entry.key1 = key1;
        entry.key2 = key2;
        entry.key3 = key3;
        this.table[index] = entry;
        if (++this.size == this.threshold) {
            this.resize(2 * this.table.length);
        }
        return entry;
    }

    public Entry createEntry() {
        return new Entry();
    }

    public final ComplexKeyHashMap.Entry remove(Object key1, Object key2, Object key3) {
        int h = TripleKeyHashMap.hash(31 * (31 * key1.hashCode() + key2.hashCode()) + key3.hashCode());
        int index = h & this.table.length - 1;
        ComplexKeyHashMap.Entry e = this.table[index];
        ComplexKeyHashMap.Entry prev = null;
        while (e != null) {
            if (e.hash == h && this.checkEquals((Entry)e, key1, key2, key3)) {
                if (prev == null) {
                    this.table[index] = e.next;
                } else {
                    prev.next = e.next;
                }
                --this.size;
                e.next = null;
                return e;
            }
            prev = e;
            e = e.next;
        }
        return null;
    }

    public static class Entry
    extends ComplexKeyHashMap.Entry {
        public Object key1;
        public Object key2;
        public Object key3;
    }
}

