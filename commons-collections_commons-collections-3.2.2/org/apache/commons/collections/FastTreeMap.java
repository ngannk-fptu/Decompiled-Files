/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class FastTreeMap
extends TreeMap {
    protected TreeMap map = null;
    protected boolean fast = false;

    public FastTreeMap() {
        this.map = new TreeMap();
    }

    public FastTreeMap(Comparator comparator) {
        this.map = new TreeMap(comparator);
    }

    public FastTreeMap(Map map) {
        this.map = new TreeMap(map);
    }

    public FastTreeMap(SortedMap map) {
        this.map = new TreeMap(map);
    }

    public boolean getFast() {
        return this.fast;
    }

    public void setFast(boolean fast) {
        this.fast = fast;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object get(Object key) {
        if (this.fast) {
            return this.map.get(key);
        }
        TreeMap treeMap = this.map;
        synchronized (treeMap) {
            return this.map.get(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int size() {
        if (this.fast) {
            return this.map.size();
        }
        TreeMap treeMap = this.map;
        synchronized (treeMap) {
            return this.map.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isEmpty() {
        if (this.fast) {
            return this.map.isEmpty();
        }
        TreeMap treeMap = this.map;
        synchronized (treeMap) {
            return this.map.isEmpty();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsKey(Object key) {
        if (this.fast) {
            return this.map.containsKey(key);
        }
        TreeMap treeMap = this.map;
        synchronized (treeMap) {
            return this.map.containsKey(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsValue(Object value) {
        if (this.fast) {
            return this.map.containsValue(value);
        }
        TreeMap treeMap = this.map;
        synchronized (treeMap) {
            return this.map.containsValue(value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Comparator comparator() {
        if (this.fast) {
            return this.map.comparator();
        }
        TreeMap treeMap = this.map;
        synchronized (treeMap) {
            return this.map.comparator();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object firstKey() {
        if (this.fast) {
            return this.map.firstKey();
        }
        TreeMap treeMap = this.map;
        synchronized (treeMap) {
            return this.map.firstKey();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object lastKey() {
        if (this.fast) {
            return this.map.lastKey();
        }
        TreeMap treeMap = this.map;
        synchronized (treeMap) {
            return this.map.lastKey();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object put(Object key, Object value) {
        if (this.fast) {
            FastTreeMap fastTreeMap = this;
            synchronized (fastTreeMap) {
                TreeMap temp = (TreeMap)this.map.clone();
                Object result = temp.put(key, value);
                this.map = temp;
                return result;
            }
        }
        TreeMap treeMap = this.map;
        synchronized (treeMap) {
            return this.map.put(key, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void putAll(Map in) {
        if (this.fast) {
            FastTreeMap fastTreeMap = this;
            synchronized (fastTreeMap) {
                TreeMap temp = (TreeMap)this.map.clone();
                temp.putAll(in);
                this.map = temp;
            }
        }
        TreeMap treeMap = this.map;
        synchronized (treeMap) {
            this.map.putAll(in);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object remove(Object key) {
        if (this.fast) {
            FastTreeMap fastTreeMap = this;
            synchronized (fastTreeMap) {
                TreeMap temp = (TreeMap)this.map.clone();
                Object result = temp.remove(key);
                this.map = temp;
                return result;
            }
        }
        TreeMap treeMap = this.map;
        synchronized (treeMap) {
            return this.map.remove(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear() {
        if (this.fast) {
            FastTreeMap fastTreeMap = this;
            synchronized (fastTreeMap) {
                this.map = new TreeMap();
            }
        }
        TreeMap treeMap = this.map;
        synchronized (treeMap) {
            this.map.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Map)) {
            return false;
        }
        Map mo = (Map)o;
        if (this.fast) {
            if (mo.size() != this.map.size()) {
                return false;
            }
            Iterator i = this.map.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry e = i.next();
                Object key = e.getKey();
                Object value = e.getValue();
                if (!(value == null ? mo.get(key) != null || !mo.containsKey(key) : !value.equals(mo.get(key)))) continue;
                return false;
            }
            return true;
        }
        TreeMap treeMap = this.map;
        synchronized (treeMap) {
            if (mo.size() != this.map.size()) {
                return false;
            }
            Iterator i = this.map.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry e = i.next();
                Object key = e.getKey();
                Object value = e.getValue();
                if (value == null) {
                    if (mo.get(key) == null && mo.containsKey(key)) continue;
                    return false;
                }
                if (value.equals(mo.get(key))) continue;
                return false;
            }
            return true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int hashCode() {
        if (this.fast) {
            int h = 0;
            Iterator i = this.map.entrySet().iterator();
            while (i.hasNext()) {
                h += ((Object)i.next()).hashCode();
            }
            return h;
        }
        TreeMap treeMap = this.map;
        synchronized (treeMap) {
            int h = 0;
            Iterator i = this.map.entrySet().iterator();
            while (i.hasNext()) {
                h += ((Object)i.next()).hashCode();
            }
            return h;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object clone() {
        FastTreeMap results = null;
        if (this.fast) {
            results = new FastTreeMap((SortedMap)this.map);
        } else {
            TreeMap treeMap = this.map;
            synchronized (treeMap) {
                results = new FastTreeMap((SortedMap)this.map);
            }
        }
        results.setFast(this.getFast());
        return results;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SortedMap headMap(Object key) {
        if (this.fast) {
            return this.map.headMap(key);
        }
        TreeMap treeMap = this.map;
        synchronized (treeMap) {
            return this.map.headMap(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SortedMap subMap(Object fromKey, Object toKey) {
        if (this.fast) {
            return this.map.subMap(fromKey, toKey);
        }
        TreeMap treeMap = this.map;
        synchronized (treeMap) {
            return this.map.subMap(fromKey, toKey);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SortedMap tailMap(Object key) {
        if (this.fast) {
            return this.map.tailMap(key);
        }
        TreeMap treeMap = this.map;
        synchronized (treeMap) {
            return this.map.tailMap(key);
        }
    }

    public Set entrySet() {
        return new EntrySet();
    }

    public Set keySet() {
        return new KeySet();
    }

    public Collection values() {
        return new Values();
    }

    private class EntrySet
    extends CollectionView
    implements Set {
        private EntrySet() {
        }

        protected Collection get(Map map) {
            return map.entrySet();
        }

        protected Object iteratorNext(Map.Entry entry) {
            return entry;
        }
    }

    private class Values
    extends CollectionView {
        private Values() {
        }

        protected Collection get(Map map) {
            return map.values();
        }

        protected Object iteratorNext(Map.Entry entry) {
            return entry.getValue();
        }
    }

    private class KeySet
    extends CollectionView
    implements Set {
        private KeySet() {
        }

        protected Collection get(Map map) {
            return map.keySet();
        }

        protected Object iteratorNext(Map.Entry entry) {
            return entry.getKey();
        }
    }

    private abstract class CollectionView
    implements Collection {
        protected abstract Collection get(Map var1);

        protected abstract Object iteratorNext(Map.Entry var1);

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void clear() {
            if (FastTreeMap.this.fast) {
                FastTreeMap fastTreeMap = FastTreeMap.this;
                synchronized (fastTreeMap) {
                    FastTreeMap.this.map = new TreeMap();
                }
            }
            TreeMap treeMap = FastTreeMap.this.map;
            synchronized (treeMap) {
                this.get(FastTreeMap.this.map).clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean remove(Object o) {
            if (FastTreeMap.this.fast) {
                FastTreeMap fastTreeMap = FastTreeMap.this;
                synchronized (fastTreeMap) {
                    TreeMap temp = (TreeMap)FastTreeMap.this.map.clone();
                    boolean r = this.get(temp).remove(o);
                    FastTreeMap.this.map = temp;
                    return r;
                }
            }
            TreeMap treeMap = FastTreeMap.this.map;
            synchronized (treeMap) {
                return this.get(FastTreeMap.this.map).remove(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean removeAll(Collection o) {
            if (FastTreeMap.this.fast) {
                FastTreeMap fastTreeMap = FastTreeMap.this;
                synchronized (fastTreeMap) {
                    TreeMap temp = (TreeMap)FastTreeMap.this.map.clone();
                    boolean r = this.get(temp).removeAll(o);
                    FastTreeMap.this.map = temp;
                    return r;
                }
            }
            TreeMap treeMap = FastTreeMap.this.map;
            synchronized (treeMap) {
                return this.get(FastTreeMap.this.map).removeAll(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean retainAll(Collection o) {
            if (FastTreeMap.this.fast) {
                FastTreeMap fastTreeMap = FastTreeMap.this;
                synchronized (fastTreeMap) {
                    TreeMap temp = (TreeMap)FastTreeMap.this.map.clone();
                    boolean r = this.get(temp).retainAll(o);
                    FastTreeMap.this.map = temp;
                    return r;
                }
            }
            TreeMap treeMap = FastTreeMap.this.map;
            synchronized (treeMap) {
                return this.get(FastTreeMap.this.map).retainAll(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int size() {
            if (FastTreeMap.this.fast) {
                return this.get(FastTreeMap.this.map).size();
            }
            TreeMap treeMap = FastTreeMap.this.map;
            synchronized (treeMap) {
                return this.get(FastTreeMap.this.map).size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean isEmpty() {
            if (FastTreeMap.this.fast) {
                return this.get(FastTreeMap.this.map).isEmpty();
            }
            TreeMap treeMap = FastTreeMap.this.map;
            synchronized (treeMap) {
                return this.get(FastTreeMap.this.map).isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean contains(Object o) {
            if (FastTreeMap.this.fast) {
                return this.get(FastTreeMap.this.map).contains(o);
            }
            TreeMap treeMap = FastTreeMap.this.map;
            synchronized (treeMap) {
                return this.get(FastTreeMap.this.map).contains(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean containsAll(Collection o) {
            if (FastTreeMap.this.fast) {
                return this.get(FastTreeMap.this.map).containsAll(o);
            }
            TreeMap treeMap = FastTreeMap.this.map;
            synchronized (treeMap) {
                return this.get(FastTreeMap.this.map).containsAll(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Object[] toArray(Object[] o) {
            if (FastTreeMap.this.fast) {
                return this.get(FastTreeMap.this.map).toArray(o);
            }
            TreeMap treeMap = FastTreeMap.this.map;
            synchronized (treeMap) {
                return this.get(FastTreeMap.this.map).toArray(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Object[] toArray() {
            if (FastTreeMap.this.fast) {
                return this.get(FastTreeMap.this.map).toArray();
            }
            TreeMap treeMap = FastTreeMap.this.map;
            synchronized (treeMap) {
                return this.get(FastTreeMap.this.map).toArray();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (FastTreeMap.this.fast) {
                return this.get(FastTreeMap.this.map).equals(o);
            }
            TreeMap treeMap = FastTreeMap.this.map;
            synchronized (treeMap) {
                return this.get(FastTreeMap.this.map).equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int hashCode() {
            if (FastTreeMap.this.fast) {
                return this.get(FastTreeMap.this.map).hashCode();
            }
            TreeMap treeMap = FastTreeMap.this.map;
            synchronized (treeMap) {
                return this.get(FastTreeMap.this.map).hashCode();
            }
        }

        public boolean add(Object o) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        public Iterator iterator() {
            return new CollectionViewIterator();
        }

        private class CollectionViewIterator
        implements Iterator {
            private Map expected;
            private Map.Entry lastReturned = null;
            private Iterator iterator;

            public CollectionViewIterator() {
                this.expected = ((CollectionView)CollectionView.this).FastTreeMap.this.map;
                this.iterator = this.expected.entrySet().iterator();
            }

            public boolean hasNext() {
                if (this.expected != ((CollectionView)CollectionView.this).FastTreeMap.this.map) {
                    throw new ConcurrentModificationException();
                }
                return this.iterator.hasNext();
            }

            public Object next() {
                if (this.expected != ((CollectionView)CollectionView.this).FastTreeMap.this.map) {
                    throw new ConcurrentModificationException();
                }
                this.lastReturned = (Map.Entry)this.iterator.next();
                return CollectionView.this.iteratorNext(this.lastReturned);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void remove() {
                if (this.lastReturned == null) {
                    throw new IllegalStateException();
                }
                if (((CollectionView)CollectionView.this).FastTreeMap.this.fast) {
                    FastTreeMap fastTreeMap = FastTreeMap.this;
                    synchronized (fastTreeMap) {
                        if (this.expected != ((CollectionView)CollectionView.this).FastTreeMap.this.map) {
                            throw new ConcurrentModificationException();
                        }
                        FastTreeMap.this.remove(this.lastReturned.getKey());
                        this.lastReturned = null;
                        this.expected = ((CollectionView)CollectionView.this).FastTreeMap.this.map;
                    }
                } else {
                    this.iterator.remove();
                    this.lastReturned = null;
                }
            }
        }
    }
}

