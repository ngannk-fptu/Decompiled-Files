/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

@Deprecated
public class IdentityHashMap
extends AbstractMap
implements Map,
Cloneable,
Serializable {
    public static final long serialVersionUID = 362498820763181265L;
    private transient Entry[] table;
    private transient int count;
    private int threshold;
    private float loadFactor;
    private transient int modCount = 0;
    private transient Set keySet = null;
    private transient Set entrySet = null;
    private transient Collection values = null;
    private static final int KEYS = 0;
    private static final int VALUES = 1;
    private static final int ENTRIES = 2;
    private static EmptyHashIterator emptyHashIterator = new EmptyHashIterator();

    public IdentityHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Initial Capacity: " + initialCapacity);
        }
        if (loadFactor <= 0.0f || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal Load factor: " + loadFactor);
        }
        if (initialCapacity == 0) {
            initialCapacity = 1;
        }
        this.loadFactor = loadFactor;
        this.table = new Entry[initialCapacity];
        this.threshold = (int)((float)initialCapacity * loadFactor);
    }

    public IdentityHashMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public IdentityHashMap() {
        this(11, 0.75f);
    }

    public IdentityHashMap(Map t) {
        this(Math.max(2 * t.size(), 11), 0.75f);
        this.putAll(t);
    }

    @Override
    public int size() {
        return this.count;
    }

    @Override
    public boolean isEmpty() {
        return this.count == 0;
    }

    @Override
    public boolean containsValue(Object value) {
        Entry[] tab = this.table;
        if (value == null) {
            int i = tab.length;
            while (i-- > 0) {
                Entry e = tab[i];
                while (e != null) {
                    if (e.value == null) {
                        return true;
                    }
                    e = e.next;
                }
            }
        } else {
            int i = tab.length;
            while (i-- > 0) {
                Entry e = tab[i];
                while (e != null) {
                    if (value.equals(e.value)) {
                        return true;
                    }
                    e = e.next;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        Entry[] tab = this.table;
        if (key != null) {
            int hash = System.identityHashCode(key);
            int index = (hash & Integer.MAX_VALUE) % tab.length;
            Entry e = tab[index];
            while (e != null) {
                if (e.hash == hash && key == e.key) {
                    return true;
                }
                e = e.next;
            }
        } else {
            Entry e = tab[0];
            while (e != null) {
                if (e.key == null) {
                    return true;
                }
                e = e.next;
            }
        }
        return false;
    }

    public Object get(Object key) {
        Entry[] tab = this.table;
        if (key != null) {
            int hash = System.identityHashCode(key);
            int index = (hash & Integer.MAX_VALUE) % tab.length;
            Entry e = tab[index];
            while (e != null) {
                if (e.hash == hash && key == e.key) {
                    return e.value;
                }
                e = e.next;
            }
        } else {
            Entry e = tab[0];
            while (e != null) {
                if (e.key == null) {
                    return e.value;
                }
                e = e.next;
            }
        }
        return null;
    }

    private void rehash() {
        int oldCapacity = this.table.length;
        Entry[] oldMap = this.table;
        int newCapacity = oldCapacity * 2 + 1;
        Entry[] newMap = new Entry[newCapacity];
        ++this.modCount;
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

    public Object put(Object key, Object value) {
        Entry e;
        Entry[] tab = this.table;
        int hash = 0;
        int index = 0;
        if (key != null) {
            hash = System.identityHashCode(key);
            index = (hash & Integer.MAX_VALUE) % tab.length;
            e = tab[index];
            while (e != null) {
                if (e.hash == hash && key == e.key) {
                    Object old = e.value;
                    e.value = value;
                    return old;
                }
                e = e.next;
            }
        } else {
            e = tab[0];
            while (e != null) {
                if (e.key == null) {
                    Object old = e.value;
                    e.value = value;
                    return old;
                }
                e = e.next;
            }
        }
        ++this.modCount;
        if (this.count >= this.threshold) {
            this.rehash();
            tab = this.table;
            index = (hash & Integer.MAX_VALUE) % tab.length;
        }
        tab[index] = e = new Entry(hash, key, value, tab[index]);
        ++this.count;
        return null;
    }

    public Object remove(Object key) {
        Entry[] tab = this.table;
        if (key != null) {
            int hash = System.identityHashCode(key);
            int index = (hash & Integer.MAX_VALUE) % tab.length;
            Entry e = tab[index];
            Entry prev = null;
            while (e != null) {
                if (e.hash == hash && key == e.key) {
                    ++this.modCount;
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
        } else {
            Entry e = tab[0];
            Entry prev = null;
            while (e != null) {
                if (e.key == null) {
                    ++this.modCount;
                    if (prev != null) {
                        prev.next = e.next;
                    } else {
                        tab[0] = e.next;
                    }
                    --this.count;
                    Object oldValue = e.value;
                    e.value = null;
                    return oldValue;
                }
                prev = e;
                e = e.next;
            }
        }
        return null;
    }

    public void putAll(Map t) {
        for (Map.Entry e : t.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        Entry[] tab = this.table;
        ++this.modCount;
        int index = tab.length;
        while (--index >= 0) {
            tab[index] = null;
        }
        this.count = 0;
    }

    @Override
    public Object clone() {
        try {
            IdentityHashMap t = (IdentityHashMap)super.clone();
            t.table = new Entry[this.table.length];
            int i = this.table.length;
            while (i-- > 0) {
                t.table[i] = this.table[i] != null ? (Entry)this.table[i].clone() : null;
            }
            t.keySet = null;
            t.entrySet = null;
            t.values = null;
            t.modCount = 0;
            return t;
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public Set keySet() {
        if (this.keySet == null) {
            this.keySet = new AbstractSet(){

                @Override
                public Iterator iterator() {
                    return IdentityHashMap.this.getHashIterator(0);
                }

                @Override
                public int size() {
                    return IdentityHashMap.this.count;
                }

                @Override
                public boolean contains(Object o) {
                    return IdentityHashMap.this.containsKey(o);
                }

                @Override
                public boolean remove(Object o) {
                    int oldSize = IdentityHashMap.this.count;
                    IdentityHashMap.this.remove(o);
                    return IdentityHashMap.this.count != oldSize;
                }

                @Override
                public void clear() {
                    IdentityHashMap.this.clear();
                }
            };
        }
        return this.keySet;
    }

    public Collection values() {
        if (this.values == null) {
            this.values = new AbstractCollection(){

                @Override
                public Iterator iterator() {
                    return IdentityHashMap.this.getHashIterator(1);
                }

                @Override
                public int size() {
                    return IdentityHashMap.this.count;
                }

                @Override
                public boolean contains(Object o) {
                    return IdentityHashMap.this.containsValue(o);
                }

                @Override
                public void clear() {
                    IdentityHashMap.this.clear();
                }
            };
        }
        return this.values;
    }

    public Set entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new AbstractSet(){

                @Override
                public Iterator iterator() {
                    return IdentityHashMap.this.getHashIterator(2);
                }

                @Override
                public boolean contains(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry entry = (Map.Entry)o;
                    Object key = entry.getKey();
                    Entry[] tab = IdentityHashMap.this.table;
                    int hash = key == null ? 0 : System.identityHashCode(key);
                    int index = (hash & Integer.MAX_VALUE) % tab.length;
                    Entry e = tab[index];
                    while (e != null) {
                        if (e.hash == hash && e.equals(entry)) {
                            return true;
                        }
                        e = e.next;
                    }
                    return false;
                }

                @Override
                public boolean remove(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry entry = (Map.Entry)o;
                    Object key = entry.getKey();
                    Entry[] tab = IdentityHashMap.this.table;
                    int hash = key == null ? 0 : System.identityHashCode(key);
                    int index = (hash & Integer.MAX_VALUE) % tab.length;
                    Entry e = tab[index];
                    Entry prev = null;
                    while (e != null) {
                        if (e.hash == hash && e.equals(entry)) {
                            IdentityHashMap.this.modCount++;
                            if (prev != null) {
                                prev.next = e.next;
                            } else {
                                tab[index] = e.next;
                            }
                            IdentityHashMap.this.count--;
                            e.value = null;
                            return true;
                        }
                        prev = e;
                        e = e.next;
                    }
                    return false;
                }

                @Override
                public int size() {
                    return IdentityHashMap.this.count;
                }

                @Override
                public void clear() {
                    IdentityHashMap.this.clear();
                }
            };
        }
        return this.entrySet;
    }

    private Iterator getHashIterator(int type) {
        if (this.count == 0) {
            return emptyHashIterator;
        }
        return new HashIterator(type);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.table.length);
        s.writeInt(this.count);
        for (int index = this.table.length - 1; index >= 0; --index) {
            Entry entry = this.table[index];
            while (entry != null) {
                s.writeObject(entry.key);
                s.writeObject(entry.value);
                entry = entry.next;
            }
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        int numBuckets = s.readInt();
        this.table = new Entry[numBuckets];
        int size = s.readInt();
        for (int i = 0; i < size; ++i) {
            Object key = s.readObject();
            Object value = s.readObject();
            this.put(key, value);
        }
    }

    int capacity() {
        return this.table.length;
    }

    float loadFactor() {
        return this.loadFactor;
    }

    private class HashIterator
    implements Iterator {
        Entry[] table;
        int index;
        Entry entry;
        Entry lastReturned;
        int type;
        private int expectedModCount;

        HashIterator(int type) {
            this.table = IdentityHashMap.this.table;
            this.index = this.table.length;
            this.entry = null;
            this.lastReturned = null;
            this.expectedModCount = IdentityHashMap.this.modCount;
            this.type = type;
        }

        @Override
        public boolean hasNext() {
            Entry e = this.entry;
            int i = this.index;
            Entry[] t = this.table;
            while (e == null && i > 0) {
                e = t[--i];
            }
            this.entry = e;
            this.index = i;
            return e != null;
        }

        public Object next() {
            if (IdentityHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            Entry et = this.entry;
            int i = this.index;
            Entry[] t = this.table;
            while (et == null && i > 0) {
                et = t[--i];
            }
            this.entry = et;
            this.index = i;
            if (et != null) {
                Entry e = this.lastReturned = this.entry;
                this.entry = e.next;
                return this.type == 0 ? e.key : (this.type == 1 ? e.value : e);
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            if (IdentityHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            Entry[] tab = IdentityHashMap.this.table;
            int index = (this.lastReturned.hash & Integer.MAX_VALUE) % tab.length;
            Entry e = tab[index];
            Entry prev = null;
            while (e != null) {
                if (e == this.lastReturned) {
                    IdentityHashMap.this.modCount++;
                    ++this.expectedModCount;
                    if (prev == null) {
                        tab[index] = e.next;
                    } else {
                        prev.next = e.next;
                    }
                    IdentityHashMap.this.count--;
                    this.lastReturned = null;
                    return;
                }
                prev = e;
                e = e.next;
            }
            throw new ConcurrentModificationException();
        }
    }

    private static class EmptyHashIterator
    implements Iterator {
        EmptyHashIterator() {
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        public Object next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new IllegalStateException();
        }
    }

    private static class Entry
    implements Map.Entry {
        int hash;
        Object key;
        Object value;
        Entry next;

        Entry(int hash, Object key, Object value, Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        protected Object clone() {
            return new Entry(this.hash, this.key, this.value, this.next == null ? null : (Entry)this.next.clone());
        }

        public Object getKey() {
            return this.key;
        }

        public Object getValue() {
            return this.value;
        }

        public Object setValue(Object value) {
            Object oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return this.key == e.getKey() && (this.value == null ? e.getValue() == null : this.value.equals(e.getValue()));
        }

        @Override
        public int hashCode() {
            return this.hash ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}

