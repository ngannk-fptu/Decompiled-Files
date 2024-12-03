/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.iterators.EmptyIterator;
import org.apache.commons.collections.iterators.EmptyMapIterator;

public class AbstractHashedMap
extends AbstractMap
implements IterableMap {
    protected static final String NO_NEXT_ENTRY = "No next() entry in the iteration";
    protected static final String NO_PREVIOUS_ENTRY = "No previous() entry in the iteration";
    protected static final String REMOVE_INVALID = "remove() can only be called once after next()";
    protected static final String GETKEY_INVALID = "getKey() can only be called after next() and before remove()";
    protected static final String GETVALUE_INVALID = "getValue() can only be called after next() and before remove()";
    protected static final String SETVALUE_INVALID = "setValue() can only be called after next() and before remove()";
    protected static final int DEFAULT_CAPACITY = 16;
    protected static final int DEFAULT_THRESHOLD = 12;
    protected static final float DEFAULT_LOAD_FACTOR = 0.75f;
    protected static final int MAXIMUM_CAPACITY = 0x40000000;
    protected static final Object NULL = new Object();
    protected transient float loadFactor;
    protected transient int size;
    protected transient HashEntry[] data;
    protected transient int threshold;
    protected transient int modCount;
    protected transient EntrySet entrySet;
    protected transient KeySet keySet;
    protected transient Values values;

    protected AbstractHashedMap() {
    }

    protected AbstractHashedMap(int initialCapacity, float loadFactor, int threshold) {
        this.loadFactor = loadFactor;
        this.data = new HashEntry[initialCapacity];
        this.threshold = threshold;
        this.init();
    }

    protected AbstractHashedMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    protected AbstractHashedMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Initial capacity must be greater than 0");
        }
        if (loadFactor <= 0.0f || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Load factor must be greater than 0");
        }
        this.loadFactor = loadFactor;
        initialCapacity = this.calculateNewCapacity(initialCapacity);
        this.threshold = this.calculateThreshold(initialCapacity, loadFactor);
        this.data = new HashEntry[initialCapacity];
        this.init();
    }

    protected AbstractHashedMap(Map map) {
        this(Math.max(2 * map.size(), 16), 0.75f);
        this.putAll(map);
    }

    protected void init() {
    }

    public Object get(Object key) {
        key = this.convertKey(key);
        int hashCode = this.hash(key);
        HashEntry entry = this.data[this.hashIndex(hashCode, this.data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
                return entry.getValue();
            }
            entry = entry.next;
        }
        return null;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public boolean containsKey(Object key) {
        key = this.convertKey(key);
        int hashCode = this.hash(key);
        HashEntry entry = this.data[this.hashIndex(hashCode, this.data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
                return true;
            }
            entry = entry.next;
        }
        return false;
    }

    public boolean containsValue(Object value) {
        if (value == null) {
            int isize = this.data.length;
            for (int i = 0; i < isize; ++i) {
                HashEntry entry = this.data[i];
                while (entry != null) {
                    if (entry.getValue() == null) {
                        return true;
                    }
                    entry = entry.next;
                }
            }
        } else {
            int isize = this.data.length;
            for (int i = 0; i < isize; ++i) {
                HashEntry entry = this.data[i];
                while (entry != null) {
                    if (this.isEqualValue(value, entry.getValue())) {
                        return true;
                    }
                    entry = entry.next;
                }
            }
        }
        return false;
    }

    public Object put(Object key, Object value) {
        key = this.convertKey(key);
        int hashCode = this.hash(key);
        int index = this.hashIndex(hashCode, this.data.length);
        HashEntry entry = this.data[index];
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
                Object oldValue = entry.getValue();
                this.updateEntry(entry, value);
                return oldValue;
            }
            entry = entry.next;
        }
        this.addMapping(index, hashCode, key, value);
        return null;
    }

    public void putAll(Map map) {
        int mapSize = map.size();
        if (mapSize == 0) {
            return;
        }
        int newSize = (int)((float)(this.size + mapSize) / this.loadFactor + 1.0f);
        this.ensureCapacity(this.calculateNewCapacity(newSize));
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public Object remove(Object key) {
        key = this.convertKey(key);
        int hashCode = this.hash(key);
        int index = this.hashIndex(hashCode, this.data.length);
        HashEntry entry = this.data[index];
        HashEntry previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
                Object oldValue = entry.getValue();
                this.removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }

    public void clear() {
        ++this.modCount;
        HashEntry[] data = this.data;
        for (int i = data.length - 1; i >= 0; --i) {
            data[i] = null;
        }
        this.size = 0;
    }

    protected Object convertKey(Object key) {
        return key == null ? NULL : key;
    }

    protected int hash(Object key) {
        int h = key.hashCode();
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }

    protected boolean isEqualKey(Object key1, Object key2) {
        return key1 == key2 || key1.equals(key2);
    }

    protected boolean isEqualValue(Object value1, Object value2) {
        return value1 == value2 || value1.equals(value2);
    }

    protected int hashIndex(int hashCode, int dataSize) {
        return hashCode & dataSize - 1;
    }

    protected HashEntry getEntry(Object key) {
        key = this.convertKey(key);
        int hashCode = this.hash(key);
        HashEntry entry = this.data[this.hashIndex(hashCode, this.data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
                return entry;
            }
            entry = entry.next;
        }
        return null;
    }

    protected void updateEntry(HashEntry entry, Object newValue) {
        entry.setValue(newValue);
    }

    protected void reuseEntry(HashEntry entry, int hashIndex, int hashCode, Object key, Object value) {
        entry.next = this.data[hashIndex];
        entry.hashCode = hashCode;
        entry.key = key;
        entry.value = value;
    }

    protected void addMapping(int hashIndex, int hashCode, Object key, Object value) {
        ++this.modCount;
        HashEntry entry = this.createEntry(this.data[hashIndex], hashCode, key, value);
        this.addEntry(entry, hashIndex);
        ++this.size;
        this.checkCapacity();
    }

    protected HashEntry createEntry(HashEntry next, int hashCode, Object key, Object value) {
        return new HashEntry(next, hashCode, key, value);
    }

    protected void addEntry(HashEntry entry, int hashIndex) {
        this.data[hashIndex] = entry;
    }

    protected void removeMapping(HashEntry entry, int hashIndex, HashEntry previous) {
        ++this.modCount;
        this.removeEntry(entry, hashIndex, previous);
        --this.size;
        this.destroyEntry(entry);
    }

    protected void removeEntry(HashEntry entry, int hashIndex, HashEntry previous) {
        if (previous == null) {
            this.data[hashIndex] = entry.next;
        } else {
            previous.next = entry.next;
        }
    }

    protected void destroyEntry(HashEntry entry) {
        entry.next = null;
        entry.key = null;
        entry.value = null;
    }

    protected void checkCapacity() {
        int newCapacity;
        if (this.size >= this.threshold && (newCapacity = this.data.length * 2) <= 0x40000000) {
            this.ensureCapacity(newCapacity);
        }
    }

    protected void ensureCapacity(int newCapacity) {
        int oldCapacity = this.data.length;
        if (newCapacity <= oldCapacity) {
            return;
        }
        if (this.size == 0) {
            this.threshold = this.calculateThreshold(newCapacity, this.loadFactor);
            this.data = new HashEntry[newCapacity];
        } else {
            HashEntry[] oldEntries = this.data;
            HashEntry[] newEntries = new HashEntry[newCapacity];
            ++this.modCount;
            for (int i = oldCapacity - 1; i >= 0; --i) {
                HashEntry next;
                HashEntry entry = oldEntries[i];
                if (entry == null) continue;
                oldEntries[i] = null;
                do {
                    next = entry.next;
                    int index = this.hashIndex(entry.hashCode, newCapacity);
                    entry.next = newEntries[index];
                    newEntries[index] = entry;
                } while ((entry = next) != null);
            }
            this.threshold = this.calculateThreshold(newCapacity, this.loadFactor);
            this.data = newEntries;
        }
    }

    protected int calculateNewCapacity(int proposedCapacity) {
        int newCapacity;
        if (proposedCapacity > 0x40000000) {
            newCapacity = 0x40000000;
        } else {
            for (newCapacity = 1; newCapacity < proposedCapacity; newCapacity <<= 1) {
            }
            if (newCapacity > 0x40000000) {
                newCapacity = 0x40000000;
            }
        }
        return newCapacity;
    }

    protected int calculateThreshold(int newCapacity, float factor) {
        return (int)((float)newCapacity * factor);
    }

    protected HashEntry entryNext(HashEntry entry) {
        return entry.next;
    }

    protected int entryHashCode(HashEntry entry) {
        return entry.hashCode;
    }

    protected Object entryKey(HashEntry entry) {
        return entry.key;
    }

    protected Object entryValue(HashEntry entry) {
        return entry.value;
    }

    public MapIterator mapIterator() {
        if (this.size == 0) {
            return EmptyMapIterator.INSTANCE;
        }
        return new HashMapIterator(this);
    }

    public Set entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntrySet(this);
        }
        return this.entrySet;
    }

    protected Iterator createEntrySetIterator() {
        if (this.size() == 0) {
            return EmptyIterator.INSTANCE;
        }
        return new EntrySetIterator(this);
    }

    public Set keySet() {
        if (this.keySet == null) {
            this.keySet = new KeySet(this);
        }
        return this.keySet;
    }

    protected Iterator createKeySetIterator() {
        if (this.size() == 0) {
            return EmptyIterator.INSTANCE;
        }
        return new KeySetIterator(this);
    }

    public Collection values() {
        if (this.values == null) {
            this.values = new Values(this);
        }
        return this.values;
    }

    protected Iterator createValuesIterator() {
        if (this.size() == 0) {
            return EmptyIterator.INSTANCE;
        }
        return new ValuesIterator(this);
    }

    protected void doWriteObject(ObjectOutputStream out) throws IOException {
        out.writeFloat(this.loadFactor);
        out.writeInt(this.data.length);
        out.writeInt(this.size);
        MapIterator it = this.mapIterator();
        while (it.hasNext()) {
            out.writeObject(it.next());
            out.writeObject(it.getValue());
        }
    }

    protected void doReadObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.loadFactor = in.readFloat();
        int capacity = in.readInt();
        int size = in.readInt();
        this.init();
        this.threshold = this.calculateThreshold(capacity, this.loadFactor);
        this.data = new HashEntry[capacity];
        for (int i = 0; i < size; ++i) {
            Object key = in.readObject();
            Object value = in.readObject();
            this.put(key, value);
        }
    }

    protected Object clone() {
        try {
            AbstractHashedMap cloned = (AbstractHashedMap)super.clone();
            cloned.data = new HashEntry[this.data.length];
            cloned.entrySet = null;
            cloned.keySet = null;
            cloned.values = null;
            cloned.modCount = 0;
            cloned.size = 0;
            cloned.init();
            cloned.putAll((Map)this);
            return cloned;
        }
        catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        Map map = (Map)obj;
        if (map.size() != this.size()) {
            return false;
        }
        MapIterator it = this.mapIterator();
        try {
            while (it.hasNext()) {
                Object key = it.next();
                Object value = it.getValue();
                if (!(value == null ? map.get(key) != null || !map.containsKey(key) : !value.equals(map.get(key)))) continue;
                return false;
            }
        }
        catch (ClassCastException ignored) {
            return false;
        }
        catch (NullPointerException ignored) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int total = 0;
        Iterator it = this.createEntrySetIterator();
        while (it.hasNext()) {
            total += it.next().hashCode();
        }
        return total;
    }

    public String toString() {
        if (this.size() == 0) {
            return "{}";
        }
        StringBuffer buf = new StringBuffer(32 * this.size());
        buf.append('{');
        MapIterator it = this.mapIterator();
        boolean hasNext = it.hasNext();
        while (hasNext) {
            Object key = it.next();
            Object value = it.getValue();
            buf.append(key == this ? "(this Map)" : key).append('=').append(value == this ? "(this Map)" : value);
            hasNext = it.hasNext();
            if (!hasNext) continue;
            buf.append(',').append(' ');
        }
        buf.append('}');
        return buf.toString();
    }

    protected static abstract class HashIterator
    implements Iterator {
        protected final AbstractHashedMap parent;
        protected int hashIndex;
        protected HashEntry last;
        protected HashEntry next;
        protected int expectedModCount;

        protected HashIterator(AbstractHashedMap parent) {
            this.parent = parent;
            HashEntry[] data = parent.data;
            int i = data.length;
            HashEntry next = null;
            while (i > 0 && next == null) {
                next = data[--i];
            }
            this.next = next;
            this.hashIndex = i;
            this.expectedModCount = parent.modCount;
        }

        public boolean hasNext() {
            return this.next != null;
        }

        protected HashEntry nextEntry() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            HashEntry newCurrent = this.next;
            if (newCurrent == null) {
                throw new NoSuchElementException(AbstractHashedMap.NO_NEXT_ENTRY);
            }
            HashEntry[] data = this.parent.data;
            int i = this.hashIndex;
            HashEntry n = newCurrent.next;
            while (n == null && i > 0) {
                n = data[--i];
            }
            this.next = n;
            this.hashIndex = i;
            this.last = newCurrent;
            return newCurrent;
        }

        protected HashEntry currentEntry() {
            return this.last;
        }

        public void remove() {
            if (this.last == null) {
                throw new IllegalStateException(AbstractHashedMap.REMOVE_INVALID);
            }
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            this.parent.remove(this.last.getKey());
            this.last = null;
            this.expectedModCount = this.parent.modCount;
        }

        public String toString() {
            if (this.last != null) {
                return "Iterator[" + this.last.getKey() + "=" + this.last.getValue() + "]";
            }
            return "Iterator[]";
        }
    }

    protected static class HashEntry
    implements Map.Entry,
    KeyValue {
        protected HashEntry next;
        protected int hashCode;
        protected Object key;
        protected Object value;

        protected HashEntry(HashEntry next, int hashCode, Object key, Object value) {
            this.next = next;
            this.hashCode = hashCode;
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return this.key == NULL ? null : this.key;
        }

        public Object getValue() {
            return this.value;
        }

        public Object setValue(Object value) {
            Object old = this.value;
            this.value = value;
            return old;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry other = (Map.Entry)obj;
            return (this.getKey() == null ? other.getKey() == null : this.getKey().equals(other.getKey())) && (this.getValue() == null ? other.getValue() == null : this.getValue().equals(other.getValue()));
        }

        public int hashCode() {
            return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
        }

        public String toString() {
            return "" + this.getKey() + '=' + this.getValue();
        }
    }

    protected static class ValuesIterator
    extends HashIterator {
        protected ValuesIterator(AbstractHashedMap parent) {
            super(parent);
        }

        public Object next() {
            return super.nextEntry().getValue();
        }
    }

    protected static class Values
    extends AbstractCollection {
        protected final AbstractHashedMap parent;

        protected Values(AbstractHashedMap parent) {
            this.parent = parent;
        }

        public int size() {
            return this.parent.size();
        }

        public void clear() {
            this.parent.clear();
        }

        public boolean contains(Object value) {
            return this.parent.containsValue(value);
        }

        public Iterator iterator() {
            return this.parent.createValuesIterator();
        }
    }

    protected static class KeySetIterator
    extends EntrySetIterator {
        protected KeySetIterator(AbstractHashedMap parent) {
            super(parent);
        }

        public Object next() {
            return super.nextEntry().getKey();
        }
    }

    protected static class KeySet
    extends AbstractSet {
        protected final AbstractHashedMap parent;

        protected KeySet(AbstractHashedMap parent) {
            this.parent = parent;
        }

        public int size() {
            return this.parent.size();
        }

        public void clear() {
            this.parent.clear();
        }

        public boolean contains(Object key) {
            return this.parent.containsKey(key);
        }

        public boolean remove(Object key) {
            boolean result = this.parent.containsKey(key);
            this.parent.remove(key);
            return result;
        }

        public Iterator iterator() {
            return this.parent.createKeySetIterator();
        }
    }

    protected static class EntrySetIterator
    extends HashIterator {
        protected EntrySetIterator(AbstractHashedMap parent) {
            super(parent);
        }

        public Object next() {
            return super.nextEntry();
        }
    }

    protected static class EntrySet
    extends AbstractSet {
        protected final AbstractHashedMap parent;

        protected EntrySet(AbstractHashedMap parent) {
            this.parent = parent;
        }

        public int size() {
            return this.parent.size();
        }

        public void clear() {
            this.parent.clear();
        }

        public boolean contains(Object entry) {
            if (entry instanceof Map.Entry) {
                Map.Entry e = (Map.Entry)entry;
                HashEntry match = this.parent.getEntry(e.getKey());
                return match != null && match.equals(e);
            }
            return false;
        }

        public boolean remove(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            if (!this.contains(obj)) {
                return false;
            }
            Map.Entry entry = (Map.Entry)obj;
            Object key = entry.getKey();
            this.parent.remove(key);
            return true;
        }

        public Iterator iterator() {
            return this.parent.createEntrySetIterator();
        }
    }

    protected static class HashMapIterator
    extends HashIterator
    implements MapIterator {
        protected HashMapIterator(AbstractHashedMap parent) {
            super(parent);
        }

        public Object next() {
            return super.nextEntry().getKey();
        }

        public Object getKey() {
            HashEntry current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException(AbstractHashedMap.GETKEY_INVALID);
            }
            return current.getKey();
        }

        public Object getValue() {
            HashEntry current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException(AbstractHashedMap.GETVALUE_INVALID);
            }
            return current.getValue();
        }

        public Object setValue(Object value) {
            HashEntry current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException(AbstractHashedMap.SETVALUE_INVALID);
            }
            return current.setValue(value);
        }
    }
}

