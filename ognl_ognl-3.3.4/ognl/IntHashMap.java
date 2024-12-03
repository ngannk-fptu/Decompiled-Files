/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class IntHashMap
implements Map {
    private Entry[] table;
    private int count;
    private int threshold;
    private float loadFactor;

    public IntHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity <= 0 || (double)loadFactor <= 0.0) {
            throw new IllegalArgumentException();
        }
        this.loadFactor = loadFactor;
        this.table = new Entry[initialCapacity];
        this.threshold = (int)((float)initialCapacity * loadFactor);
    }

    public IntHashMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public IntHashMap() {
        this(101, 0.75f);
    }

    protected void rehash() {
        int oldCapacity = this.table.length;
        Entry[] oldTable = this.table;
        int newCapacity = oldCapacity * 2 + 1;
        Entry[] newTable = new Entry[newCapacity];
        this.threshold = (int)((float)newCapacity * this.loadFactor);
        this.table = newTable;
        int i = oldCapacity;
        while (i-- > 0) {
            Entry old = oldTable[i];
            while (old != null) {
                Entry e = old;
                int index = (e.hash & Integer.MAX_VALUE) % newCapacity;
                old = old.next;
                e.next = newTable[index];
                newTable[index] = e;
            }
        }
    }

    public final boolean containsKey(int key) {
        int index = (key & Integer.MAX_VALUE) % this.table.length;
        Entry e = this.table[index];
        while (e != null) {
            if (e.hash == key && e.key == key) {
                return true;
            }
            e = e.next;
        }
        return false;
    }

    public final Object get(int key) {
        int index = (key & Integer.MAX_VALUE) % this.table.length;
        Entry e = this.table[index];
        while (e != null) {
            if (e.hash == key && e.key == key) {
                return e.value;
            }
            e = e.next;
        }
        return null;
    }

    public final Object put(int key, Object value) {
        int index = (key & Integer.MAX_VALUE) % this.table.length;
        if (value == null) {
            throw new IllegalArgumentException();
        }
        Entry e = this.table[index];
        while (e != null) {
            if (e.hash == key && e.key == key) {
                Object old = e.value;
                e.value = value;
                return old;
            }
            e = e.next;
        }
        if (this.count >= this.threshold) {
            this.rehash();
            return this.put(key, value);
        }
        e = new Entry();
        e.hash = key;
        e.key = key;
        e.value = value;
        e.next = this.table[index];
        this.table[index] = e;
        ++this.count;
        return null;
    }

    public final Object remove(int key) {
        int index = (key & Integer.MAX_VALUE) % this.table.length;
        Entry e = this.table[index];
        Entry prev = null;
        while (e != null) {
            if (e.hash == key && e.key == key) {
                if (prev != null) {
                    prev.next = e.next;
                } else {
                    this.table[index] = e.next;
                }
                --this.count;
                return e.value;
            }
            prev = e;
            e = e.next;
        }
        return null;
    }

    @Override
    public int size() {
        return this.count;
    }

    @Override
    public boolean isEmpty() {
        return this.count == 0;
    }

    public Object get(Object key) {
        if (!(key instanceof Number)) {
            throw new IllegalArgumentException("key is not an Number subclass");
        }
        return this.get(((Number)key).intValue());
    }

    public Object put(Object key, Object value) {
        if (!(key instanceof Number)) {
            throw new IllegalArgumentException("key cannot be null");
        }
        return this.put(((Number)key).intValue(), value);
    }

    public void putAll(Map otherMap) {
        for (Object k : otherMap.keySet()) {
            this.put(k, otherMap.get(k));
        }
    }

    public Object remove(Object key) {
        if (!(key instanceof Number)) {
            throw new IllegalArgumentException("key cannot be null");
        }
        return this.remove(((Number)key).intValue());
    }

    @Override
    public void clear() {
        Entry[] tab = this.table;
        int index = tab.length;
        while (--index >= 0) {
            tab[index] = null;
        }
        this.count = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof Number)) {
            throw new InternalError("key is not an Number subclass");
        }
        return this.containsKey(((Number)key).intValue());
    }

    @Override
    public boolean containsValue(Object value) {
        Entry[] tab = this.table;
        if (value == null) {
            throw new IllegalArgumentException();
        }
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

    public Set keySet() {
        HashSet result = new HashSet();
        IntHashMapIterator it = new IntHashMapIterator(this.table, true);
        while (it.hasNext()) {
            result.add(it.next());
        }
        return result;
    }

    public Collection values() {
        ArrayList result = new ArrayList();
        IntHashMapIterator it = new IntHashMapIterator(this.table, false);
        while (it.hasNext()) {
            result.add(it.next());
        }
        return result;
    }

    public Set entrySet() {
        throw new UnsupportedOperationException("entrySet");
    }

    public static class Entry {
        int hash;
        int key;
        Object value;
        Entry next;
    }

    private static class IntHashMapIterator
    implements Iterator {
        boolean keys;
        int index;
        Entry[] table;
        Entry entry;

        IntHashMapIterator(Entry[] table, boolean keys) {
            this.table = table;
            this.keys = keys;
            this.index = table.length;
        }

        @Override
        public boolean hasNext() {
            if (this.entry != null) {
                return true;
            }
            while (this.index-- > 0) {
                this.entry = this.table[this.index];
                if (this.entry == null) continue;
                return true;
            }
            return false;
        }

        public Object next() {
            if (this.entry == null) {
                while (this.index-- > 0 && (this.entry = this.table[this.index]) == null) {
                }
            }
            if (this.entry != null) {
                Entry e = this.entry;
                this.entry = e.next;
                return this.keys ? new Integer(e.key) : e.value;
            }
            throw new NoSuchElementException("IntHashMapIterator");
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}

