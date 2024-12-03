/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.DefaultMapEntry;

public class ReferenceMap
extends AbstractMap {
    private static final long serialVersionUID = -3370601314380922368L;
    public static final int HARD = 0;
    public static final int SOFT = 1;
    public static final int WEAK = 2;
    private int keyType;
    private int valueType;
    private float loadFactor;
    private boolean purgeValues = false;
    private transient ReferenceQueue queue = new ReferenceQueue();
    private transient Entry[] table;
    private transient int size;
    private transient int threshold;
    private volatile transient int modCount;
    private transient Set keySet;
    private transient Set entrySet;
    private transient Collection values;

    public ReferenceMap() {
        this(0, 1);
    }

    public ReferenceMap(int keyType, int valueType, boolean purgeValues) {
        this(keyType, valueType);
        this.purgeValues = purgeValues;
    }

    public ReferenceMap(int keyType, int valueType) {
        this(keyType, valueType, 16, 0.75f);
    }

    public ReferenceMap(int keyType, int valueType, int capacity, float loadFactor, boolean purgeValues) {
        this(keyType, valueType, capacity, loadFactor);
        this.purgeValues = purgeValues;
    }

    public ReferenceMap(int keyType, int valueType, int capacity, float loadFactor) {
        int v;
        ReferenceMap.verify("keyType", keyType);
        ReferenceMap.verify("valueType", valueType);
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        if (loadFactor <= 0.0f || loadFactor >= 1.0f) {
            throw new IllegalArgumentException("Load factor must be greater than 0 and less than 1.");
        }
        this.keyType = keyType;
        this.valueType = valueType;
        for (v = 1; v < capacity; v *= 2) {
        }
        this.table = new Entry[v];
        this.loadFactor = loadFactor;
        this.threshold = (int)((float)v * loadFactor);
    }

    private static void verify(String name, int type) {
        if (type < 0 || type > 2) {
            throw new IllegalArgumentException(name + " must be HARD, SOFT, WEAK.");
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(this.table.length);
        Iterator iter = this.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            out.writeObject(entry.getKey());
            out.writeObject(entry.getValue());
        }
        out.writeObject(null);
    }

    private void readObject(ObjectInputStream inp) throws IOException, ClassNotFoundException {
        inp.defaultReadObject();
        this.table = new Entry[inp.readInt()];
        this.threshold = (int)((float)this.table.length * this.loadFactor);
        this.queue = new ReferenceQueue();
        Object key = inp.readObject();
        while (key != null) {
            Object value = inp.readObject();
            this.put(key, value);
            key = inp.readObject();
        }
    }

    private Object toReference(int type, Object referent, int hash) {
        switch (type) {
            case 0: {
                return referent;
            }
            case 1: {
                return new SoftRef(hash, referent, this.queue);
            }
            case 2: {
                return new WeakRef(hash, referent, this.queue);
            }
        }
        throw new Error();
    }

    private Entry getEntry(Object key) {
        if (key == null) {
            return null;
        }
        int hash = key.hashCode();
        int index = this.indexFor(hash);
        Entry entry = this.table[index];
        while (entry != null) {
            if (entry.hash == hash && key.equals(entry.getKey())) {
                return entry;
            }
            entry = entry.next;
        }
        return null;
    }

    private int indexFor(int hash) {
        hash += ~(hash << 15);
        hash ^= hash >>> 10;
        hash += hash << 3;
        hash ^= hash >>> 6;
        hash += ~(hash << 11);
        hash ^= hash >>> 16;
        return hash & this.table.length - 1;
    }

    private void resize() {
        Entry[] old = this.table;
        this.table = new Entry[old.length * 2];
        for (int i = 0; i < old.length; ++i) {
            Entry next = old[i];
            while (next != null) {
                Entry entry = next;
                next = next.next;
                int index = this.indexFor(entry.hash);
                entry.next = this.table[index];
                this.table[index] = entry;
            }
            old[i] = null;
        }
        this.threshold = (int)((float)this.table.length * this.loadFactor);
    }

    private void purge() {
        Reference ref = this.queue.poll();
        while (ref != null) {
            this.purge(ref);
            ref = this.queue.poll();
        }
    }

    private void purge(Reference ref) {
        int hash = ref.hashCode();
        int index = this.indexFor(hash);
        Entry previous = null;
        Entry entry = this.table[index];
        while (entry != null) {
            if (entry.purge(ref)) {
                if (previous == null) {
                    this.table[index] = entry.next;
                } else {
                    previous.next = entry.next;
                }
                --this.size;
                return;
            }
            previous = entry;
            entry = entry.next;
        }
    }

    public int size() {
        this.purge();
        return this.size;
    }

    public boolean isEmpty() {
        this.purge();
        return this.size == 0;
    }

    public boolean containsKey(Object key) {
        this.purge();
        Entry entry = this.getEntry(key);
        if (entry == null) {
            return false;
        }
        return entry.getValue() != null;
    }

    public Object get(Object key) {
        this.purge();
        Entry entry = this.getEntry(key);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }

    public Object put(Object key, Object value) {
        if (key == null) {
            throw new NullPointerException("null keys not allowed");
        }
        if (value == null) {
            throw new NullPointerException("null values not allowed");
        }
        this.purge();
        if (this.size + 1 > this.threshold) {
            this.resize();
        }
        int hash = key.hashCode();
        int index = this.indexFor(hash);
        Entry entry = this.table[index];
        while (entry != null) {
            if (hash == entry.hash && key.equals(entry.getKey())) {
                Object result = entry.getValue();
                entry.setValue(value);
                return result;
            }
            entry = entry.next;
        }
        ++this.size;
        ++this.modCount;
        key = this.toReference(this.keyType, key, hash);
        value = this.toReference(this.valueType, value, hash);
        this.table[index] = new Entry(key, hash, value, this.table[index]);
        return null;
    }

    public Object remove(Object key) {
        if (key == null) {
            return null;
        }
        this.purge();
        int hash = key.hashCode();
        int index = this.indexFor(hash);
        Entry previous = null;
        Entry entry = this.table[index];
        while (entry != null) {
            if (hash == entry.hash && key.equals(entry.getKey())) {
                if (previous == null) {
                    this.table[index] = entry.next;
                } else {
                    previous.next = entry.next;
                }
                --this.size;
                ++this.modCount;
                return entry.getValue();
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }

    public void clear() {
        Arrays.fill(this.table, null);
        this.size = 0;
        while (this.queue.poll() != null) {
        }
    }

    public Set entrySet() {
        if (this.entrySet != null) {
            return this.entrySet;
        }
        this.entrySet = new AbstractSet(){

            public int size() {
                return ReferenceMap.this.size();
            }

            public void clear() {
                ReferenceMap.this.clear();
            }

            public boolean contains(Object o) {
                if (o == null) {
                    return false;
                }
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                Map.Entry e = (Map.Entry)o;
                Entry e2 = ReferenceMap.this.getEntry(e.getKey());
                return e2 != null && e.equals(e2);
            }

            public boolean remove(Object o) {
                boolean r = this.contains(o);
                if (r) {
                    Map.Entry e = (Map.Entry)o;
                    ReferenceMap.this.remove(e.getKey());
                }
                return r;
            }

            public Iterator iterator() {
                return new EntryIterator();
            }

            public Object[] toArray() {
                return this.toArray(new Object[0]);
            }

            public Object[] toArray(Object[] arr) {
                ArrayList<DefaultMapEntry> list = new ArrayList<DefaultMapEntry>();
                Iterator iterator = this.iterator();
                while (iterator.hasNext()) {
                    Entry e = (Entry)iterator.next();
                    list.add(new DefaultMapEntry(e.getKey(), e.getValue()));
                }
                return list.toArray(arr);
            }
        };
        return this.entrySet;
    }

    public Set keySet() {
        if (this.keySet != null) {
            return this.keySet;
        }
        this.keySet = new AbstractSet(){

            public int size() {
                return ReferenceMap.this.size();
            }

            public Iterator iterator() {
                return new KeyIterator();
            }

            public boolean contains(Object o) {
                return ReferenceMap.this.containsKey(o);
            }

            public boolean remove(Object o) {
                Object r = ReferenceMap.this.remove(o);
                return r != null;
            }

            public void clear() {
                ReferenceMap.this.clear();
            }

            public Object[] toArray() {
                return this.toArray(new Object[0]);
            }

            public Object[] toArray(Object[] array) {
                ArrayList c = new ArrayList(this.size());
                Iterator it = this.iterator();
                while (it.hasNext()) {
                    c.add(it.next());
                }
                return c.toArray(array);
            }
        };
        return this.keySet;
    }

    public Collection values() {
        if (this.values != null) {
            return this.values;
        }
        this.values = new AbstractCollection(){

            public int size() {
                return ReferenceMap.this.size();
            }

            public void clear() {
                ReferenceMap.this.clear();
            }

            public Iterator iterator() {
                return new ValueIterator();
            }

            public Object[] toArray() {
                return this.toArray(new Object[0]);
            }

            public Object[] toArray(Object[] array) {
                ArrayList c = new ArrayList(this.size());
                Iterator it = this.iterator();
                while (it.hasNext()) {
                    c.add(it.next());
                }
                return c.toArray(array);
            }
        };
        return this.values;
    }

    private static class WeakRef
    extends WeakReference {
        private int hash;

        public WeakRef(int hash, Object r, ReferenceQueue q) {
            super(r, q);
            this.hash = hash;
        }

        public int hashCode() {
            return this.hash;
        }
    }

    private static class SoftRef
    extends SoftReference {
        private int hash;

        public SoftRef(int hash, Object r, ReferenceQueue q) {
            super(r, q);
            this.hash = hash;
        }

        public int hashCode() {
            return this.hash;
        }
    }

    private class KeyIterator
    extends EntryIterator {
        private KeyIterator() {
        }

        public Object next() {
            return this.nextEntry().getKey();
        }
    }

    private class ValueIterator
    extends EntryIterator {
        private ValueIterator() {
        }

        public Object next() {
            return this.nextEntry().getValue();
        }
    }

    private class EntryIterator
    implements Iterator {
        int index;
        Entry entry;
        Entry previous;
        Object nextKey;
        Object nextValue;
        Object currentKey;
        Object currentValue;
        int expectedModCount;

        public EntryIterator() {
            this.index = ReferenceMap.this.size() != 0 ? ReferenceMap.this.table.length : 0;
            this.expectedModCount = ReferenceMap.this.modCount;
        }

        public boolean hasNext() {
            this.checkMod();
            while (this.nextNull()) {
                Entry e = this.entry;
                int i = this.index;
                while (e == null && i > 0) {
                    e = ReferenceMap.this.table[--i];
                }
                this.entry = e;
                this.index = i;
                if (e == null) {
                    this.currentKey = null;
                    this.currentValue = null;
                    return false;
                }
                this.nextKey = e.getKey();
                this.nextValue = e.getValue();
                if (!this.nextNull()) continue;
                this.entry = this.entry.next;
            }
            return true;
        }

        private void checkMod() {
            if (ReferenceMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        private boolean nextNull() {
            return this.nextKey == null || this.nextValue == null;
        }

        protected Entry nextEntry() {
            this.checkMod();
            if (this.nextNull() && !this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.previous = this.entry;
            this.entry = this.entry.next;
            this.currentKey = this.nextKey;
            this.currentValue = this.nextValue;
            this.nextKey = null;
            this.nextValue = null;
            return this.previous;
        }

        public Object next() {
            return this.nextEntry();
        }

        public void remove() {
            this.checkMod();
            if (this.previous == null) {
                throw new IllegalStateException();
            }
            ReferenceMap.this.remove(this.currentKey);
            this.previous = null;
            this.currentKey = null;
            this.currentValue = null;
            this.expectedModCount = ReferenceMap.this.modCount;
        }
    }

    private class Entry
    implements Map.Entry,
    KeyValue {
        Object key;
        Object value;
        int hash;
        Entry next;

        public Entry(Object key, int hash, Object value, Entry next) {
            this.key = key;
            this.hash = hash;
            this.value = value;
            this.next = next;
        }

        public Object getKey() {
            return ReferenceMap.this.keyType > 0 ? ((Reference)this.key).get() : this.key;
        }

        public Object getValue() {
            return ReferenceMap.this.valueType > 0 ? ((Reference)this.value).get() : this.value;
        }

        public Object setValue(Object object) {
            Object old = this.getValue();
            if (ReferenceMap.this.valueType > 0) {
                ((Reference)this.value).clear();
            }
            this.value = ReferenceMap.this.toReference(ReferenceMap.this.valueType, object, this.hash);
            return old;
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o == this) {
                return true;
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry)o;
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key == null || value == null) {
                return false;
            }
            return key.equals(this.getKey()) && value.equals(this.getValue());
        }

        public int hashCode() {
            Object v = this.getValue();
            return this.hash ^ (v == null ? 0 : v.hashCode());
        }

        public String toString() {
            return this.getKey() + "=" + this.getValue();
        }

        boolean purge(Reference ref) {
            boolean r = ReferenceMap.this.keyType > 0 && this.key == ref;
            boolean bl = r = r || ReferenceMap.this.valueType > 0 && this.value == ref;
            if (r) {
                if (ReferenceMap.this.keyType > 0) {
                    ((Reference)this.key).clear();
                }
                if (ReferenceMap.this.valueType > 0) {
                    ((Reference)this.value).clear();
                } else if (ReferenceMap.this.purgeValues) {
                    this.value = null;
                }
            }
            return r;
        }
    }
}

