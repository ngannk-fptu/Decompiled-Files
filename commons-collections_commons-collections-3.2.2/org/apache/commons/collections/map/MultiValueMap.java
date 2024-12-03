/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.map;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.FunctorException;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.iterators.EmptyIterator;
import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.collections.map.AbstractMapDecorator;

public class MultiValueMap
extends AbstractMapDecorator
implements MultiMap {
    private final Factory collectionFactory;
    private transient Collection values;
    static /* synthetic */ Class class$java$util$ArrayList;

    public static MultiValueMap decorate(Map map) {
        return new MultiValueMap(map, new ReflectionFactory(class$java$util$ArrayList == null ? (class$java$util$ArrayList = MultiValueMap.class$("java.util.ArrayList")) : class$java$util$ArrayList));
    }

    public static MultiValueMap decorate(Map map, Class collectionClass) {
        return new MultiValueMap(map, new ReflectionFactory(collectionClass));
    }

    public static MultiValueMap decorate(Map map, Factory collectionFactory) {
        return new MultiValueMap(map, collectionFactory);
    }

    public MultiValueMap() {
        this(new HashMap(), new ReflectionFactory(class$java$util$ArrayList == null ? (class$java$util$ArrayList = MultiValueMap.class$("java.util.ArrayList")) : class$java$util$ArrayList));
    }

    protected MultiValueMap(Map map, Factory collectionFactory) {
        super(map);
        if (collectionFactory == null) {
            throw new IllegalArgumentException("The factory must not be null");
        }
        this.collectionFactory = collectionFactory;
    }

    public void clear() {
        this.getMap().clear();
    }

    public Object remove(Object key, Object value) {
        Collection valuesForKey = this.getCollection(key);
        if (valuesForKey == null) {
            return null;
        }
        boolean removed = valuesForKey.remove(value);
        if (!removed) {
            return null;
        }
        if (valuesForKey.isEmpty()) {
            this.remove(key);
        }
        return value;
    }

    public boolean containsValue(Object value) {
        Set pairs = this.getMap().entrySet();
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

    public Object put(Object key, Object value) {
        boolean result = false;
        Collection coll = this.getCollection(key);
        if (coll == null) {
            coll = this.createCollection(1);
            result = coll.add(value);
            if (coll.size() > 0) {
                this.getMap().put(key, coll);
                result = true;
            }
        } else {
            result = coll.add(value);
        }
        return result ? value : null;
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

    public Collection values() {
        Collection vs = this.values;
        return vs != null ? vs : (this.values = new Values());
    }

    public boolean containsValue(Object key, Object value) {
        Collection coll = this.getCollection(key);
        if (coll == null) {
            return false;
        }
        return coll.contains(value);
    }

    public Collection getCollection(Object key) {
        return (Collection)this.getMap().get(key);
    }

    public int size(Object key) {
        Collection coll = this.getCollection(key);
        if (coll == null) {
            return 0;
        }
        return coll.size();
    }

    public boolean putAll(Object key, Collection values) {
        if (values == null || values.size() == 0) {
            return false;
        }
        boolean result = false;
        Collection coll = this.getCollection(key);
        if (coll == null) {
            coll = this.createCollection(values.size());
            coll.addAll(values);
            if (coll.size() > 0) {
                this.getMap().put(key, coll);
                result = true;
            }
        } else {
            result = coll.addAll(values);
        }
        return result;
    }

    public Iterator iterator(Object key) {
        if (!this.containsKey(key)) {
            return EmptyIterator.INSTANCE;
        }
        return new ValuesIterator(key);
    }

    public int totalSize() {
        int total = 0;
        Collection values = this.getMap().values();
        Iterator it = values.iterator();
        while (it.hasNext()) {
            Collection coll = (Collection)it.next();
            total += coll.size();
        }
        return total;
    }

    protected Collection createCollection(int size) {
        return (Collection)this.collectionFactory.create();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private static class ReflectionFactory
    implements Factory {
        private final Class clazz;

        public ReflectionFactory(Class clazz) {
            this.clazz = clazz;
        }

        public Object create() {
            try {
                return this.clazz.newInstance();
            }
            catch (Exception ex) {
                throw new FunctorException("Cannot instantiate class: " + this.clazz, ex);
            }
        }
    }

    private class ValuesIterator
    implements Iterator {
        private final Object key;
        private final Collection values;
        private final Iterator iterator;

        public ValuesIterator(Object key) {
            this.key = key;
            this.values = MultiValueMap.this.getCollection(key);
            this.iterator = this.values.iterator();
        }

        public void remove() {
            this.iterator.remove();
            if (this.values.isEmpty()) {
                MultiValueMap.this.remove(this.key);
            }
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public Object next() {
            return this.iterator.next();
        }
    }

    private class Values
    extends AbstractCollection {
        private Values() {
        }

        public Iterator iterator() {
            IteratorChain chain = new IteratorChain();
            Iterator it = MultiValueMap.this.keySet().iterator();
            while (it.hasNext()) {
                chain.addIterator(new ValuesIterator(it.next()));
            }
            return chain;
        }

        public int size() {
            return MultiValueMap.this.totalSize();
        }

        public void clear() {
            MultiValueMap.this.clear();
        }
    }
}

