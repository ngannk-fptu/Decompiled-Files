/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.scanner.dtd;

import java.util.Enumeration;

final class SimpleHashtable
implements Enumeration {
    private Entry[] table;
    private Entry current = null;
    private int currentBucket = 0;
    private int count;
    private int threshold;
    private static final float loadFactor = 0.75f;

    public SimpleHashtable(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        if (initialCapacity == 0) {
            initialCapacity = 1;
        }
        this.table = new Entry[initialCapacity];
        this.threshold = (int)((float)initialCapacity * 0.75f);
    }

    public SimpleHashtable() {
        this(11);
    }

    public void clear() {
        this.count = 0;
        this.currentBucket = 0;
        this.current = null;
        for (int i = 0; i < this.table.length; ++i) {
            this.table[i] = null;
        }
    }

    public int size() {
        return this.count;
    }

    public Enumeration keys() {
        this.currentBucket = 0;
        this.current = null;
        return this;
    }

    public boolean hasMoreElements() {
        if (this.current != null) {
            return true;
        }
        while (this.currentBucket < this.table.length) {
            this.current = this.table[this.currentBucket++];
            if (this.current == null) continue;
            return true;
        }
        return false;
    }

    public Object nextElement() {
        if (this.current == null) {
            throw new IllegalStateException();
        }
        Object retval = this.current.key;
        this.current = this.current.next;
        return retval;
    }

    public Object get(String key) {
        Entry[] tab = this.table;
        int hash = key.hashCode();
        int index = (hash & Integer.MAX_VALUE) % tab.length;
        Entry e = tab[index];
        while (e != null) {
            if (e.hash == hash && e.key == key) {
                return e.value;
            }
            e = e.next;
        }
        return null;
    }

    public Object getNonInterned(String key) {
        Entry[] tab = this.table;
        int hash = key.hashCode();
        int index = (hash & Integer.MAX_VALUE) % tab.length;
        Entry e = tab[index];
        while (e != null) {
            if (e.hash == hash && e.key.equals(key)) {
                return e.value;
            }
            e = e.next;
        }
        return null;
    }

    private void rehash() {
        int oldCapacity = this.table.length;
        Entry[] oldMap = this.table;
        int newCapacity = oldCapacity * 2 + 1;
        Entry[] newMap = new Entry[newCapacity];
        this.threshold = (int)((float)newCapacity * 0.75f);
        this.table = newMap;
        int i = oldCapacity;
        while (i-- > 0) {
            Entry old = oldMap[i];
            while (old != null) {
                Entry e = old;
                old = old.next;
                int index = (e.hash & Integer.MAX_VALUE) % newCapacity;
                e.next = newMap[index];
                newMap[index] = e;
            }
        }
    }

    public Object put(Object key, Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        Entry[] tab = this.table;
        int hash = key.hashCode();
        int index = (hash & Integer.MAX_VALUE) % tab.length;
        Entry e = tab[index];
        while (e != null) {
            if (e.hash == hash && e.key == key) {
                Object old = e.value;
                e.value = value;
                return old;
            }
            e = e.next;
        }
        if (this.count >= this.threshold) {
            this.rehash();
            tab = this.table;
            index = (hash & Integer.MAX_VALUE) % tab.length;
        }
        tab[index] = e = new Entry(hash, key, value, tab[index]);
        ++this.count;
        return null;
    }

    private static class Entry {
        int hash;
        Object key;
        Object value;
        Entry next;

        protected Entry(int hash, Object key, Object value, Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}

