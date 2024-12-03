/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public class PresortedMap
implements SortedMap {
    private final ArraySet set;
    private final Comparator comparator;

    public PresortedMap() {
        this(null, new ArraySet());
    }

    public PresortedMap(Comparator comparator) {
        this(comparator, new ArraySet());
    }

    private PresortedMap(Comparator comparator, ArraySet set) {
        this.comparator = comparator != null ? comparator : new ArraySetComparator(set);
        this.set = set;
    }

    public Comparator comparator() {
        return this.comparator;
    }

    public Set entrySet() {
        return this.set;
    }

    public Object firstKey() {
        throw new UnsupportedOperationException();
    }

    public SortedMap headMap(Object toKey) {
        throw new UnsupportedOperationException();
    }

    public Set keySet() {
        ArraySet keySet = new ArraySet();
        Iterator iterator = this.set.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            keySet.add(entry.getKey());
        }
        return keySet;
    }

    public Object lastKey() {
        throw new UnsupportedOperationException();
    }

    public SortedMap subMap(Object fromKey, Object toKey) {
        throw new UnsupportedOperationException();
    }

    public SortedMap tailMap(Object fromKey) {
        throw new UnsupportedOperationException();
    }

    public Collection values() {
        ArraySet values = new ArraySet();
        Iterator iterator = this.set.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            values.add(entry.getValue());
        }
        return values;
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object key) {
        return false;
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    public Object get(Object key) {
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    public Object put(final Object key, final Object value) {
        this.set.add(new Map.Entry(){

            public Object getKey() {
                return key;
            }

            public Object getValue() {
                return value;
            }

            public Object setValue(Object value2) {
                throw new UnsupportedOperationException();
            }
        });
        return null;
    }

    public void putAll(Map m) {
        Iterator iter = m.entrySet().iterator();
        while (iter.hasNext()) {
            this.set.add(iter.next());
        }
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return this.set.size();
    }

    private static class ArraySetComparator
    implements Comparator {
        private final ArrayList list;
        private Map.Entry[] array;

        ArraySetComparator(ArrayList list) {
            this.list = list;
        }

        public int compare(Object object1, Object object2) {
            if (this.array == null || this.list.size() != this.array.length) {
                int i;
                Map.Entry[] a = new Map.Entry[this.list.size()];
                if (this.array != null) {
                    System.arraycopy(this.array, 0, a, 0, this.array.length);
                }
                int n = i = this.array == null ? 0 : this.array.length;
                while (i < this.list.size()) {
                    a[i] = (Map.Entry)this.list.get(i);
                    ++i;
                }
                this.array = a;
            }
            int idx1 = Integer.MAX_VALUE;
            int idx2 = Integer.MAX_VALUE;
            for (int i = 0; i < this.array.length && (idx1 >= Integer.MAX_VALUE || idx2 >= Integer.MAX_VALUE); ++i) {
                if (idx1 == Integer.MAX_VALUE && object1 == this.array[i].getKey()) {
                    idx1 = i;
                }
                if (idx2 != Integer.MAX_VALUE || object2 != this.array[i].getKey()) continue;
                idx2 = i;
            }
            return idx1 - idx2;
        }
    }

    private static class ArraySet
    extends ArrayList
    implements Set {
        private ArraySet() {
        }
    }
}

