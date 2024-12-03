/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import org.codehaus.groovy.util.ComplexKeyHashMap;

public class SingleKeyHashMap
extends ComplexKeyHashMap {
    public SingleKeyHashMap() {
    }

    public SingleKeyHashMap(boolean b) {
        super(false);
    }

    public boolean containsKey(String name) {
        return this.get(name) != null;
    }

    public void put(Object key, Object value) {
        this.getOrPut((Object)key).value = value;
    }

    public final Object get(Object key) {
        int h = SingleKeyHashMap.hash(key.hashCode());
        ComplexKeyHashMap.Entry e = this.table[h & this.table.length - 1];
        while (e != null) {
            if (e.hash == h && ((Entry)e).key.equals(key)) {
                return ((Entry)e).value;
            }
            e = e.next;
        }
        return null;
    }

    public Entry getOrPut(Object key) {
        int h = SingleKeyHashMap.hash(key.hashCode());
        ComplexKeyHashMap.Entry[] t = this.table;
        int index = h & t.length - 1;
        ComplexKeyHashMap.Entry e = t[index];
        while (e != null) {
            if (e.hash == h && ((Entry)e).key.equals(key)) {
                return (Entry)e;
            }
            e = e.next;
        }
        Entry entry = new Entry();
        entry.next = t[index];
        entry.hash = h;
        entry.key = key;
        t[index] = entry;
        if (++this.size == this.threshold) {
            this.resize(2 * t.length);
        }
        return entry;
    }

    public Entry getOrPutEntry(Entry element) {
        Object key = element.key;
        int h = element.hash;
        ComplexKeyHashMap.Entry[] t = this.table;
        int index = h & t.length - 1;
        ComplexKeyHashMap.Entry e = t[index];
        while (e != null) {
            if (e.hash == h && ((Entry)e).key.equals(key)) {
                return (Entry)e;
            }
            e = e.next;
        }
        Entry entry = new Entry();
        entry.next = t[index];
        entry.hash = h;
        entry.key = key;
        t[index] = entry;
        if (++this.size == this.threshold) {
            this.resize(2 * t.length);
        }
        return entry;
    }

    public Entry putCopyOfUnexisting(Entry ee) {
        int h = ee.hash;
        ComplexKeyHashMap.Entry[] t = this.table;
        int index = h & t.length - 1;
        Entry entry = new Entry();
        entry.next = t[index];
        entry.hash = h;
        entry.key = ee.key;
        entry.value = ee.value;
        t[index] = entry;
        if (++this.size == this.threshold) {
            this.resize(2 * t.length);
        }
        return entry;
    }

    public final ComplexKeyHashMap.Entry remove(Object key) {
        int h = SingleKeyHashMap.hash(key.hashCode());
        int index = h & this.table.length - 1;
        ComplexKeyHashMap.Entry e = this.table[index];
        ComplexKeyHashMap.Entry prev = null;
        while (e != null) {
            if (e.hash == h && ((Entry)e).key.equals(key)) {
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

    public static SingleKeyHashMap copy(SingleKeyHashMap dst, SingleKeyHashMap src, Copier copier) {
        dst.threshold = src.threshold;
        dst.size = src.size;
        int len = src.table.length;
        ComplexKeyHashMap.Entry[] t = new ComplexKeyHashMap.Entry[len];
        ComplexKeyHashMap.Entry[] tt = src.table;
        for (int i = 0; i != len; ++i) {
            Entry e = (Entry)tt[i];
            while (e != null) {
                Entry ee = new Entry();
                ee.hash = e.hash;
                ee.key = e.key;
                ee.value = copier.copy(e.value);
                ee.next = t[i];
                t[i] = ee;
                e = (Entry)e.next;
            }
        }
        dst.table = t;
        return dst;
    }

    public static interface Copier {
        public Object copy(Object var1);
    }

    public static class Entry
    extends ComplexKeyHashMap.Entry {
        public Object key;

        public Object getKey() {
            return this.key;
        }
    }
}

