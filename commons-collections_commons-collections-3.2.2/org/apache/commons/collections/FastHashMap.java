/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FastHashMap
extends HashMap {
    protected HashMap map = null;
    protected boolean fast = false;

    public FastHashMap() {
        this.map = new HashMap();
    }

    public FastHashMap(int capacity) {
        this.map = new HashMap(capacity);
    }

    public FastHashMap(int capacity, float factor) {
        this.map = new HashMap(capacity, factor);
    }

    public FastHashMap(Map map) {
        this.map = new HashMap(map);
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
        HashMap hashMap = this.map;
        synchronized (hashMap) {
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
        HashMap hashMap = this.map;
        synchronized (hashMap) {
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
        HashMap hashMap = this.map;
        synchronized (hashMap) {
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
        HashMap hashMap = this.map;
        synchronized (hashMap) {
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
        HashMap hashMap = this.map;
        synchronized (hashMap) {
            return this.map.containsValue(value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object put(Object key, Object value) {
        if (this.fast) {
            FastHashMap fastHashMap = this;
            synchronized (fastHashMap) {
                HashMap temp = (HashMap)this.map.clone();
                Object result = temp.put(key, value);
                this.map = temp;
                return result;
            }
        }
        HashMap hashMap = this.map;
        synchronized (hashMap) {
            return this.map.put(key, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void putAll(Map in) {
        if (this.fast) {
            FastHashMap fastHashMap = this;
            synchronized (fastHashMap) {
                HashMap temp = (HashMap)this.map.clone();
                temp.putAll(in);
                this.map = temp;
            }
        }
        HashMap hashMap = this.map;
        synchronized (hashMap) {
            this.map.putAll(in);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object remove(Object key) {
        if (this.fast) {
            FastHashMap fastHashMap = this;
            synchronized (fastHashMap) {
                HashMap temp = (HashMap)this.map.clone();
                Object result = temp.remove(key);
                this.map = temp;
                return result;
            }
        }
        HashMap hashMap = this.map;
        synchronized (hashMap) {
            return this.map.remove(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear() {
        if (this.fast) {
            FastHashMap fastHashMap = this;
            synchronized (fastHashMap) {
                this.map = new HashMap();
            }
        }
        HashMap hashMap = this.map;
        synchronized (hashMap) {
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
        HashMap hashMap = this.map;
        synchronized (hashMap) {
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
        HashMap hashMap = this.map;
        synchronized (hashMap) {
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
        FastHashMap results = null;
        if (this.fast) {
            results = new FastHashMap((Map)this.map);
        } else {
            HashMap hashMap = this.map;
            synchronized (hashMap) {
                results = new FastHashMap((Map)this.map);
            }
        }
        results.setFast(this.getFast());
        return results;
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
            if (FastHashMap.this.fast) {
                FastHashMap fastHashMap = FastHashMap.this;
                synchronized (fastHashMap) {
                    FastHashMap.this.map = new HashMap();
                }
            }
            HashMap hashMap = FastHashMap.this.map;
            synchronized (hashMap) {
                this.get(FastHashMap.this.map).clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean remove(Object o) {
            if (FastHashMap.this.fast) {
                FastHashMap fastHashMap = FastHashMap.this;
                synchronized (fastHashMap) {
                    HashMap temp = (HashMap)FastHashMap.this.map.clone();
                    boolean r = this.get(temp).remove(o);
                    FastHashMap.this.map = temp;
                    return r;
                }
            }
            HashMap hashMap = FastHashMap.this.map;
            synchronized (hashMap) {
                return this.get(FastHashMap.this.map).remove(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean removeAll(Collection o) {
            if (FastHashMap.this.fast) {
                FastHashMap fastHashMap = FastHashMap.this;
                synchronized (fastHashMap) {
                    HashMap temp = (HashMap)FastHashMap.this.map.clone();
                    boolean r = this.get(temp).removeAll(o);
                    FastHashMap.this.map = temp;
                    return r;
                }
            }
            HashMap hashMap = FastHashMap.this.map;
            synchronized (hashMap) {
                return this.get(FastHashMap.this.map).removeAll(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean retainAll(Collection o) {
            if (FastHashMap.this.fast) {
                FastHashMap fastHashMap = FastHashMap.this;
                synchronized (fastHashMap) {
                    HashMap temp = (HashMap)FastHashMap.this.map.clone();
                    boolean r = this.get(temp).retainAll(o);
                    FastHashMap.this.map = temp;
                    return r;
                }
            }
            HashMap hashMap = FastHashMap.this.map;
            synchronized (hashMap) {
                return this.get(FastHashMap.this.map).retainAll(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int size() {
            if (FastHashMap.this.fast) {
                return this.get(FastHashMap.this.map).size();
            }
            HashMap hashMap = FastHashMap.this.map;
            synchronized (hashMap) {
                return this.get(FastHashMap.this.map).size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean isEmpty() {
            if (FastHashMap.this.fast) {
                return this.get(FastHashMap.this.map).isEmpty();
            }
            HashMap hashMap = FastHashMap.this.map;
            synchronized (hashMap) {
                return this.get(FastHashMap.this.map).isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean contains(Object o) {
            if (FastHashMap.this.fast) {
                return this.get(FastHashMap.this.map).contains(o);
            }
            HashMap hashMap = FastHashMap.this.map;
            synchronized (hashMap) {
                return this.get(FastHashMap.this.map).contains(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean containsAll(Collection o) {
            if (FastHashMap.this.fast) {
                return this.get(FastHashMap.this.map).containsAll(o);
            }
            HashMap hashMap = FastHashMap.this.map;
            synchronized (hashMap) {
                return this.get(FastHashMap.this.map).containsAll(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Object[] toArray(Object[] o) {
            if (FastHashMap.this.fast) {
                return this.get(FastHashMap.this.map).toArray(o);
            }
            HashMap hashMap = FastHashMap.this.map;
            synchronized (hashMap) {
                return this.get(FastHashMap.this.map).toArray(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Object[] toArray() {
            if (FastHashMap.this.fast) {
                return this.get(FastHashMap.this.map).toArray();
            }
            HashMap hashMap = FastHashMap.this.map;
            synchronized (hashMap) {
                return this.get(FastHashMap.this.map).toArray();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (FastHashMap.this.fast) {
                return this.get(FastHashMap.this.map).equals(o);
            }
            HashMap hashMap = FastHashMap.this.map;
            synchronized (hashMap) {
                return this.get(FastHashMap.this.map).equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int hashCode() {
            if (FastHashMap.this.fast) {
                return this.get(FastHashMap.this.map).hashCode();
            }
            HashMap hashMap = FastHashMap.this.map;
            synchronized (hashMap) {
                return this.get(FastHashMap.this.map).hashCode();
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
                this.expected = ((CollectionView)CollectionView.this).FastHashMap.this.map;
                this.iterator = this.expected.entrySet().iterator();
            }

            public boolean hasNext() {
                if (this.expected != ((CollectionView)CollectionView.this).FastHashMap.this.map) {
                    throw new ConcurrentModificationException();
                }
                return this.iterator.hasNext();
            }

            public Object next() {
                if (this.expected != ((CollectionView)CollectionView.this).FastHashMap.this.map) {
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
                if (((CollectionView)CollectionView.this).FastHashMap.this.fast) {
                    FastHashMap fastHashMap = FastHashMap.this;
                    synchronized (fastHashMap) {
                        if (this.expected != ((CollectionView)CollectionView.this).FastHashMap.this.map) {
                            throw new ConcurrentModificationException();
                        }
                        FastHashMap.this.remove(this.lastReturned.getKey());
                        this.lastReturned = null;
                        this.expected = ((CollectionView)CollectionView.this).FastHashMap.this.map;
                    }
                } else {
                    this.iterator.remove();
                    this.lastReturned = null;
                }
            }
        }
    }
}

