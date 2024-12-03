/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.map;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.AbstractHashedMap;
import org.apache.commons.collections.map.HashedMap;

public class MultiKeyMap
implements IterableMap,
Serializable {
    private static final long serialVersionUID = -1788199231038721040L;
    protected final AbstractHashedMap map;

    public static MultiKeyMap decorate(AbstractHashedMap map) {
        if (map == null) {
            throw new IllegalArgumentException("Map must not be null");
        }
        if (map.size() > 0) {
            throw new IllegalArgumentException("Map must be empty");
        }
        return new MultiKeyMap(map);
    }

    public MultiKeyMap() {
        this.map = new HashedMap();
    }

    protected MultiKeyMap(AbstractHashedMap map) {
        this.map = map;
    }

    public Object get(Object key1, Object key2) {
        int hashCode = this.hash(key1, key2);
        AbstractHashedMap.HashEntry entry = this.map.data[this.map.hashIndex(hashCode, this.map.data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2)) {
                return entry.getValue();
            }
            entry = entry.next;
        }
        return null;
    }

    public boolean containsKey(Object key1, Object key2) {
        int hashCode = this.hash(key1, key2);
        AbstractHashedMap.HashEntry entry = this.map.data[this.map.hashIndex(hashCode, this.map.data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2)) {
                return true;
            }
            entry = entry.next;
        }
        return false;
    }

    public Object put(Object key1, Object key2, Object value) {
        int hashCode = this.hash(key1, key2);
        int index = this.map.hashIndex(hashCode, this.map.data.length);
        AbstractHashedMap.HashEntry entry = this.map.data[index];
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2)) {
                Object oldValue = entry.getValue();
                this.map.updateEntry(entry, value);
                return oldValue;
            }
            entry = entry.next;
        }
        this.map.addMapping(index, hashCode, new MultiKey(key1, key2), value);
        return null;
    }

    public Object remove(Object key1, Object key2) {
        int hashCode = this.hash(key1, key2);
        int index = this.map.hashIndex(hashCode, this.map.data.length);
        AbstractHashedMap.HashEntry entry = this.map.data[index];
        AbstractHashedMap.HashEntry previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2)) {
                Object oldValue = entry.getValue();
                this.map.removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }

    protected int hash(Object key1, Object key2) {
        int h = 0;
        if (key1 != null) {
            h ^= key1.hashCode();
        }
        if (key2 != null) {
            h ^= key2.hashCode();
        }
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }

    protected boolean isEqualKey(AbstractHashedMap.HashEntry entry, Object key1, Object key2) {
        MultiKey multi = (MultiKey)entry.getKey();
        return multi.size() == 2 && (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) && (key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1)));
    }

    public Object get(Object key1, Object key2, Object key3) {
        int hashCode = this.hash(key1, key2, key3);
        AbstractHashedMap.HashEntry entry = this.map.data[this.map.hashIndex(hashCode, this.map.data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3)) {
                return entry.getValue();
            }
            entry = entry.next;
        }
        return null;
    }

    public boolean containsKey(Object key1, Object key2, Object key3) {
        int hashCode = this.hash(key1, key2, key3);
        AbstractHashedMap.HashEntry entry = this.map.data[this.map.hashIndex(hashCode, this.map.data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3)) {
                return true;
            }
            entry = entry.next;
        }
        return false;
    }

    public Object put(Object key1, Object key2, Object key3, Object value) {
        int hashCode = this.hash(key1, key2, key3);
        int index = this.map.hashIndex(hashCode, this.map.data.length);
        AbstractHashedMap.HashEntry entry = this.map.data[index];
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3)) {
                Object oldValue = entry.getValue();
                this.map.updateEntry(entry, value);
                return oldValue;
            }
            entry = entry.next;
        }
        this.map.addMapping(index, hashCode, new MultiKey(key1, key2, key3), value);
        return null;
    }

    public Object remove(Object key1, Object key2, Object key3) {
        int hashCode = this.hash(key1, key2, key3);
        int index = this.map.hashIndex(hashCode, this.map.data.length);
        AbstractHashedMap.HashEntry entry = this.map.data[index];
        AbstractHashedMap.HashEntry previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3)) {
                Object oldValue = entry.getValue();
                this.map.removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }

    protected int hash(Object key1, Object key2, Object key3) {
        int h = 0;
        if (key1 != null) {
            h ^= key1.hashCode();
        }
        if (key2 != null) {
            h ^= key2.hashCode();
        }
        if (key3 != null) {
            h ^= key3.hashCode();
        }
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }

    protected boolean isEqualKey(AbstractHashedMap.HashEntry entry, Object key1, Object key2, Object key3) {
        MultiKey multi = (MultiKey)entry.getKey();
        return multi.size() == 3 && (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) && (key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))) && (key3 == null ? multi.getKey(2) == null : key3.equals(multi.getKey(2)));
    }

    public Object get(Object key1, Object key2, Object key3, Object key4) {
        int hashCode = this.hash(key1, key2, key3, key4);
        AbstractHashedMap.HashEntry entry = this.map.data[this.map.hashIndex(hashCode, this.map.data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4)) {
                return entry.getValue();
            }
            entry = entry.next;
        }
        return null;
    }

    public boolean containsKey(Object key1, Object key2, Object key3, Object key4) {
        int hashCode = this.hash(key1, key2, key3, key4);
        AbstractHashedMap.HashEntry entry = this.map.data[this.map.hashIndex(hashCode, this.map.data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4)) {
                return true;
            }
            entry = entry.next;
        }
        return false;
    }

    public Object put(Object key1, Object key2, Object key3, Object key4, Object value) {
        int hashCode = this.hash(key1, key2, key3, key4);
        int index = this.map.hashIndex(hashCode, this.map.data.length);
        AbstractHashedMap.HashEntry entry = this.map.data[index];
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4)) {
                Object oldValue = entry.getValue();
                this.map.updateEntry(entry, value);
                return oldValue;
            }
            entry = entry.next;
        }
        this.map.addMapping(index, hashCode, new MultiKey(key1, key2, key3, key4), value);
        return null;
    }

    public Object remove(Object key1, Object key2, Object key3, Object key4) {
        int hashCode = this.hash(key1, key2, key3, key4);
        int index = this.map.hashIndex(hashCode, this.map.data.length);
        AbstractHashedMap.HashEntry entry = this.map.data[index];
        AbstractHashedMap.HashEntry previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4)) {
                Object oldValue = entry.getValue();
                this.map.removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }

    protected int hash(Object key1, Object key2, Object key3, Object key4) {
        int h = 0;
        if (key1 != null) {
            h ^= key1.hashCode();
        }
        if (key2 != null) {
            h ^= key2.hashCode();
        }
        if (key3 != null) {
            h ^= key3.hashCode();
        }
        if (key4 != null) {
            h ^= key4.hashCode();
        }
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }

    protected boolean isEqualKey(AbstractHashedMap.HashEntry entry, Object key1, Object key2, Object key3, Object key4) {
        MultiKey multi = (MultiKey)entry.getKey();
        return multi.size() == 4 && (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) && (key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))) && (key3 == null ? multi.getKey(2) == null : key3.equals(multi.getKey(2))) && (key4 == null ? multi.getKey(3) == null : key4.equals(multi.getKey(3)));
    }

    public Object get(Object key1, Object key2, Object key3, Object key4, Object key5) {
        int hashCode = this.hash(key1, key2, key3, key4, key5);
        AbstractHashedMap.HashEntry entry = this.map.data[this.map.hashIndex(hashCode, this.map.data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4, key5)) {
                return entry.getValue();
            }
            entry = entry.next;
        }
        return null;
    }

    public boolean containsKey(Object key1, Object key2, Object key3, Object key4, Object key5) {
        int hashCode = this.hash(key1, key2, key3, key4, key5);
        AbstractHashedMap.HashEntry entry = this.map.data[this.map.hashIndex(hashCode, this.map.data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4, key5)) {
                return true;
            }
            entry = entry.next;
        }
        return false;
    }

    public Object put(Object key1, Object key2, Object key3, Object key4, Object key5, Object value) {
        int hashCode = this.hash(key1, key2, key3, key4, key5);
        int index = this.map.hashIndex(hashCode, this.map.data.length);
        AbstractHashedMap.HashEntry entry = this.map.data[index];
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4, key5)) {
                Object oldValue = entry.getValue();
                this.map.updateEntry(entry, value);
                return oldValue;
            }
            entry = entry.next;
        }
        this.map.addMapping(index, hashCode, new MultiKey(key1, key2, key3, key4, key5), value);
        return null;
    }

    public Object remove(Object key1, Object key2, Object key3, Object key4, Object key5) {
        int hashCode = this.hash(key1, key2, key3, key4, key5);
        int index = this.map.hashIndex(hashCode, this.map.data.length);
        AbstractHashedMap.HashEntry entry = this.map.data[index];
        AbstractHashedMap.HashEntry previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4, key5)) {
                Object oldValue = entry.getValue();
                this.map.removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }

    protected int hash(Object key1, Object key2, Object key3, Object key4, Object key5) {
        int h = 0;
        if (key1 != null) {
            h ^= key1.hashCode();
        }
        if (key2 != null) {
            h ^= key2.hashCode();
        }
        if (key3 != null) {
            h ^= key3.hashCode();
        }
        if (key4 != null) {
            h ^= key4.hashCode();
        }
        if (key5 != null) {
            h ^= key5.hashCode();
        }
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }

    protected boolean isEqualKey(AbstractHashedMap.HashEntry entry, Object key1, Object key2, Object key3, Object key4, Object key5) {
        MultiKey multi = (MultiKey)entry.getKey();
        return multi.size() == 5 && (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) && (key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))) && (key3 == null ? multi.getKey(2) == null : key3.equals(multi.getKey(2))) && (key4 == null ? multi.getKey(3) == null : key4.equals(multi.getKey(3))) && (key5 == null ? multi.getKey(4) == null : key5.equals(multi.getKey(4)));
    }

    public boolean removeAll(Object key1) {
        boolean modified = false;
        MapIterator it = this.mapIterator();
        while (it.hasNext()) {
            MultiKey multi = (MultiKey)it.next();
            if (multi.size() < 1 || !(key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0)))) continue;
            it.remove();
            modified = true;
        }
        return modified;
    }

    public boolean removeAll(Object key1, Object key2) {
        boolean modified = false;
        MapIterator it = this.mapIterator();
        while (it.hasNext()) {
            MultiKey multi = (MultiKey)it.next();
            if (multi.size() < 2 || !(key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) || !(key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1)))) continue;
            it.remove();
            modified = true;
        }
        return modified;
    }

    public boolean removeAll(Object key1, Object key2, Object key3) {
        boolean modified = false;
        MapIterator it = this.mapIterator();
        while (it.hasNext()) {
            MultiKey multi = (MultiKey)it.next();
            if (multi.size() < 3 || !(key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) || !(key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))) || !(key3 == null ? multi.getKey(2) == null : key3.equals(multi.getKey(2)))) continue;
            it.remove();
            modified = true;
        }
        return modified;
    }

    public boolean removeAll(Object key1, Object key2, Object key3, Object key4) {
        boolean modified = false;
        MapIterator it = this.mapIterator();
        while (it.hasNext()) {
            MultiKey multi = (MultiKey)it.next();
            if (multi.size() < 4 || !(key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) || !(key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))) || !(key3 == null ? multi.getKey(2) == null : key3.equals(multi.getKey(2))) || !(key4 == null ? multi.getKey(3) == null : key4.equals(multi.getKey(3)))) continue;
            it.remove();
            modified = true;
        }
        return modified;
    }

    protected void checkKey(Object key) {
        if (key == null) {
            throw new NullPointerException("Key must not be null");
        }
        if (!(key instanceof MultiKey)) {
            throw new ClassCastException("Key must be a MultiKey");
        }
    }

    public Object clone() {
        return new MultiKeyMap((AbstractHashedMap)this.map.clone());
    }

    public Object put(Object key, Object value) {
        this.checkKey(key);
        return this.map.put(key, value);
    }

    public void putAll(Map mapToCopy) {
        Iterator it = mapToCopy.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            this.checkKey(key);
        }
        this.map.putAll(mapToCopy);
    }

    public MapIterator mapIterator() {
        return this.map.mapIterator();
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    public Object get(Object key) {
        return this.map.get(key);
    }

    public Object remove(Object key) {
        return this.map.remove(key);
    }

    public void clear() {
        this.map.clear();
    }

    public Set keySet() {
        return this.map.keySet();
    }

    public Collection values() {
        return this.map.values();
    }

    public Set entrySet() {
        return this.map.entrySet();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        return this.map.equals(obj);
    }

    public int hashCode() {
        return this.map.hashCode();
    }

    public String toString() {
        return this.map.toString();
    }
}

