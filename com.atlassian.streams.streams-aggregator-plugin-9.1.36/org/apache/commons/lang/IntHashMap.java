/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang;

class IntHashMap {
    private transient Entry[] table;
    private transient int count;
    private int threshold;
    private float loadFactor;

    public IntHashMap() {
        this(20, 0.75f);
    }

    public IntHashMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public IntHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        if (loadFactor <= 0.0f) {
            throw new IllegalArgumentException("Illegal Load: " + loadFactor);
        }
        if (initialCapacity == 0) {
            initialCapacity = 1;
        }
        this.loadFactor = loadFactor;
        this.table = new Entry[initialCapacity];
        this.threshold = (int)((float)initialCapacity * loadFactor);
    }

    public int size() {
        return this.count;
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    public boolean contains(Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        Entry[] tab = this.table;
        int i = tab.length;
        while (i-- > 0) {
            Entry e = tab[i];
            while (e != null) {
                if (e.value.equals(value)) {
                    return true;
                }
                e = e.next;
            }
        }
        return false;
    }

    public boolean containsValue(Object value) {
        return this.contains(value);
    }

    public boolean containsKey(int key) {
        Entry[] tab = this.table;
        int hash = key;
        int index = (hash & Integer.MAX_VALUE) % tab.length;
        Entry e = tab[index];
        while (e != null) {
            if (e.hash == hash) {
                return true;
            }
            e = e.next;
        }
        return false;
    }

    public Object get(int key) {
        Entry[] tab = this.table;
        int hash = key;
        int index = (hash & Integer.MAX_VALUE) % tab.length;
        Entry e = tab[index];
        while (e != null) {
            if (e.hash == hash) {
                return e.value;
            }
            e = e.next;
        }
        return null;
    }

    protected void rehash() {
        int oldCapacity = this.table.length;
        Entry[] oldMap = this.table;
        int newCapacity = oldCapacity * 2 + 1;
        Entry[] newMap = new Entry[newCapacity];
        this.threshold = (int)((float)newCapacity * this.loadFactor);
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

    public Object put(int key, Object value) {
        Entry[] tab = this.table;
        int hash = key;
        int index = (hash & Integer.MAX_VALUE) % tab.length;
        Entry e = tab[index];
        while (e != null) {
            if (e.hash == hash) {
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

    public Object remove(int key) {
        Entry[] tab = this.table;
        int hash = key;
        int index = (hash & Integer.MAX_VALUE) % tab.length;
        Entry e = tab[index];
        Entry prev = null;
        while (e != null) {
            if (e.hash == hash) {
                if (prev != null) {
                    prev.next = e.next;
                } else {
                    tab[index] = e.next;
                }
                --this.count;
                Object oldValue = e.value;
                e.value = null;
                return oldValue;
            }
            prev = e;
            e = e.next;
        }
        return null;
    }

    public synchronized void clear() {
        Entry[] tab = this.table;
        int index = tab.length;
        while (--index >= 0) {
            tab[index] = null;
        }
        this.count = 0;
    }

    private static class Entry {
        int hash;
        int key;
        Object value;
        Entry next;

        protected Entry(int hash, int key, Object value, Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}

