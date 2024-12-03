/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.iterators.EmptyIterator;

public class MultiHashMap
extends HashMap
implements MultiMap {
    private transient Collection values = null;
    private static final long serialVersionUID = 1943563828307035349L;

    public MultiHashMap() {
    }

    public MultiHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public MultiHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public MultiHashMap(Map mapToCopy) {
        super((int)((float)mapToCopy.size() * 1.4f));
        this.putAll(mapToCopy);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        String version = "1.2";
        try {
            version = System.getProperty("java.version");
        }
        catch (SecurityException ex) {
            // empty catch block
        }
        if (version.startsWith("1.2") || version.startsWith("1.3")) {
            Iterator iterator = this.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                super.put(entry.getKey(), ((Collection)entry.getValue()).iterator().next());
            }
        }
    }

    public int totalSize() {
        int total = 0;
        Collection values = super.values();
        Iterator it = values.iterator();
        while (it.hasNext()) {
            Collection coll = (Collection)it.next();
            total += coll.size();
        }
        return total;
    }

    public Collection getCollection(Object key) {
        return (Collection)this.get(key);
    }

    public int size(Object key) {
        Collection coll = this.getCollection(key);
        if (coll == null) {
            return 0;
        }
        return coll.size();
    }

    public Iterator iterator(Object key) {
        Collection coll = this.getCollection(key);
        if (coll == null) {
            return EmptyIterator.INSTANCE;
        }
        return coll.iterator();
    }

    public Object put(Object key, Object value) {
        boolean results;
        Collection coll = this.getCollection(key);
        if (coll == null) {
            coll = this.createCollection(null);
            super.put(key, coll);
        }
        return (results = coll.add(value)) ? value : null;
    }

    public void putAll(Map map) {
        if (map instanceof MultiMap) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = it.next();
                Collection coll = (Collection)entry.getValue();
                this.putAll(entry.getKey(), coll);
            }
        } else {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = it.next();
                this.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public boolean putAll(Object key, Collection values) {
        if (values == null || values.size() == 0) {
            return false;
        }
        Collection coll = this.getCollection(key);
        if (coll == null) {
            coll = this.createCollection(values);
            if (coll.size() == 0) {
                return false;
            }
            super.put(key, coll);
            return true;
        }
        return coll.addAll(values);
    }

    public boolean containsValue(Object value) {
        Set pairs = super.entrySet();
        if (pairs == null) {
            return false;
        }
        Iterator pairsIterator = pairs.iterator();
        while (pairsIterator.hasNext()) {
            Map.Entry keyValuePair = pairsIterator.next();
            Collection coll = (Collection)keyValuePair.getValue();
            if (!coll.contains(value)) continue;
            return true;
        }
        return false;
    }

    public boolean containsValue(Object key, Object value) {
        Collection coll = this.getCollection(key);
        if (coll == null) {
            return false;
        }
        return coll.contains(value);
    }

    public Object remove(Object key, Object item) {
        Collection valuesForKey = this.getCollection(key);
        if (valuesForKey == null) {
            return null;
        }
        boolean removed = valuesForKey.remove(item);
        if (!removed) {
            return null;
        }
        if (valuesForKey.isEmpty()) {
            this.remove(key);
        }
        return item;
    }

    public void clear() {
        Set pairs = super.entrySet();
        Iterator pairsIterator = pairs.iterator();
        while (pairsIterator.hasNext()) {
            Map.Entry keyValuePair = pairsIterator.next();
            Collection coll = (Collection)keyValuePair.getValue();
            coll.clear();
        }
        super.clear();
    }

    public Collection values() {
        Collection vs = this.values;
        return vs != null ? vs : (this.values = new Values());
    }

    Iterator superValuesIterator() {
        return super.values().iterator();
    }

    public Object clone() {
        MultiHashMap cloned = (MultiHashMap)super.clone();
        Iterator it = cloned.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            Collection coll = (Collection)entry.getValue();
            Collection newColl = this.createCollection(coll);
            entry.setValue(newColl);
        }
        return cloned;
    }

    protected Collection createCollection(Collection coll) {
        if (coll == null) {
            return new ArrayList();
        }
        return new ArrayList(coll);
    }

    private class ValueIterator
    implements Iterator {
        private Iterator backedIterator;
        private Iterator tempIterator;

        private ValueIterator() {
            this.backedIterator = MultiHashMap.this.superValuesIterator();
        }

        private boolean searchNextIterator() {
            while (this.tempIterator == null || !this.tempIterator.hasNext()) {
                if (!this.backedIterator.hasNext()) {
                    return false;
                }
                this.tempIterator = ((Collection)this.backedIterator.next()).iterator();
            }
            return true;
        }

        public boolean hasNext() {
            return this.searchNextIterator();
        }

        public Object next() {
            if (!this.searchNextIterator()) {
                throw new NoSuchElementException();
            }
            return this.tempIterator.next();
        }

        public void remove() {
            if (this.tempIterator == null) {
                throw new IllegalStateException();
            }
            this.tempIterator.remove();
        }
    }

    private class Values
    extends AbstractCollection {
        private Values() {
        }

        public Iterator iterator() {
            return new ValueIterator();
        }

        public int size() {
            int compt = 0;
            Iterator it = this.iterator();
            while (it.hasNext()) {
                it.next();
                ++compt;
            }
            return compt;
        }

        public void clear() {
            MultiHashMap.this.clear();
        }
    }
}

