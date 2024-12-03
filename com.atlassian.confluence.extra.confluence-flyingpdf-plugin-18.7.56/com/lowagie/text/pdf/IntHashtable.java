/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntHashtable
implements Cloneable {
    private transient Entry[] table;
    private transient int count;
    private int threshold;
    private float loadFactor;

    public IntHashtable() {
        this(150, 0.75f);
    }

    public IntHashtable(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public IntHashtable(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("illegal.capacity.1", initialCapacity));
        }
        if (loadFactor <= 0.0f) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("illegal.load.1", String.valueOf(loadFactor)));
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

    public boolean contains(int value) {
        Entry[] tab = this.table;
        int i = tab.length;
        while (i-- > 0) {
            Entry e = tab[i];
            while (e != null) {
                if (e.value == value) {
                    return true;
                }
                e = e.next;
            }
        }
        return false;
    }

    public boolean containsValue(int value) {
        return this.contains(value);
    }

    public boolean containsKey(int key) {
        Entry[] tab = this.table;
        int hash = key;
        int index = (hash & Integer.MAX_VALUE) % tab.length;
        Entry e = tab[index];
        while (e != null) {
            if (e.hash == hash && e.key == key) {
                return true;
            }
            e = e.next;
        }
        return false;
    }

    public int get(int key) {
        Entry[] tab = this.table;
        int hash = key;
        int index = (hash & Integer.MAX_VALUE) % tab.length;
        Entry e = tab[index];
        while (e != null) {
            if (e.hash == hash && e.key == key) {
                return e.value;
            }
            e = e.next;
        }
        return 0;
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

    public int put(int key, int value) {
        Entry[] tab = this.table;
        int hash = key;
        int index = (hash & Integer.MAX_VALUE) % tab.length;
        Entry e = tab[index];
        while (e != null) {
            if (e.hash == hash && e.key == key) {
                int old = e.value;
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
        return 0;
    }

    public int remove(int key) {
        Entry[] tab = this.table;
        int hash = key;
        int index = (hash & Integer.MAX_VALUE) % tab.length;
        Entry e = tab[index];
        Entry prev = null;
        while (e != null) {
            if (e.hash == hash && e.key == key) {
                if (prev != null) {
                    prev.next = e.next;
                } else {
                    tab[index] = e.next;
                }
                --this.count;
                int oldValue = e.value;
                e.value = 0;
                return oldValue;
            }
            prev = e;
            e = e.next;
        }
        return 0;
    }

    public void clear() {
        Entry[] tab = this.table;
        int index = tab.length;
        while (--index >= 0) {
            tab[index] = null;
        }
        this.count = 0;
    }

    public Iterator getEntryIterator() {
        return new IntHashtableIterator(this.table);
    }

    public int[] toOrderedKeys() {
        int[] res = this.getKeys();
        Arrays.sort(res);
        return res;
    }

    public int[] getKeys() {
        int[] res = new int[this.count];
        int ptr = 0;
        int index = this.table.length;
        Entry entry = null;
        while (true) {
            if (entry == null) {
                while (index-- > 0 && (entry = this.table[index]) == null) {
                }
            }
            if (entry == null) break;
            Entry e = entry;
            entry = e.next;
            res[ptr++] = e.key;
        }
        return res;
    }

    public int getOneKey() {
        if (this.count == 0) {
            return 0;
        }
        int index = this.table.length;
        Entry entry = null;
        while (index-- > 0 && (entry = this.table[index]) == null) {
        }
        if (entry == null) {
            return 0;
        }
        return entry.key;
    }

    public Object clone() {
        try {
            IntHashtable t = (IntHashtable)super.clone();
            t.table = new Entry[this.table.length];
            int i = this.table.length;
            while (i-- > 0) {
                t.table[i] = this.table[i] != null ? (Entry)this.table[i].clone() : null;
            }
            return t;
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    static class IntHashtableIterator
    implements Iterator {
        int index;
        Entry[] table;
        Entry entry;

        IntHashtableIterator(Entry[] table) {
            this.table = table;
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
                return e;
            }
            throw new NoSuchElementException(MessageLocalization.getComposedMessage("inthashtableiterator"));
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("remove.not.supported"));
        }
    }

    static class Entry {
        int hash;
        int key;
        int value;
        Entry next;

        protected Entry(int hash, int key, int value, Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public int getKey() {
            return this.key;
        }

        public int getValue() {
            return this.value;
        }

        protected Object clone() {
            Entry entry = new Entry(this.hash, this.key, this.value, this.next != null ? (Entry)this.next.clone() : null);
            return entry;
        }
    }
}

