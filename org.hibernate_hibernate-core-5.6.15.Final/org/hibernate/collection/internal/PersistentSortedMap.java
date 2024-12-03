/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.collection.internal;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.collection.internal.PersistentMap;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.BasicCollectionPersister;

public class PersistentSortedMap
extends PersistentMap
implements SortedMap {
    protected Comparator comparator;

    public PersistentSortedMap() {
    }

    public PersistentSortedMap(SharedSessionContractImplementor session) {
        super(session);
    }

    @Deprecated
    public PersistentSortedMap(SessionImplementor session) {
        this((SharedSessionContractImplementor)session);
    }

    public PersistentSortedMap(SharedSessionContractImplementor session, SortedMap map) {
        super(session, (Map)map);
        this.comparator = map.comparator();
    }

    @Deprecated
    public PersistentSortedMap(SessionImplementor session, SortedMap map) {
        this((SharedSessionContractImplementor)session, map);
    }

    protected Serializable snapshot(BasicCollectionPersister persister, EntityMode entityMode) throws HibernateException {
        TreeMap clonedMap = new TreeMap(this.comparator);
        Iterator iterator = this.map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry e = o = iterator.next();
            clonedMap.put(e.getKey(), persister.getElementType().deepCopy(e.getValue(), persister.getFactory()));
        }
        return clonedMap;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public Comparator comparator() {
        return this.comparator;
    }

    public SortedMap subMap(Object fromKey, Object toKey) {
        this.read();
        SortedMap subMap = ((SortedMap)this.map).subMap(fromKey, toKey);
        return new SortedSubMap(subMap);
    }

    public SortedMap headMap(Object toKey) {
        this.read();
        SortedMap headMap = ((SortedMap)this.map).headMap(toKey);
        return new SortedSubMap(headMap);
    }

    public SortedMap tailMap(Object fromKey) {
        this.read();
        SortedMap tailMap = ((SortedMap)this.map).tailMap(fromKey);
        return new SortedSubMap(tailMap);
    }

    public Object firstKey() {
        this.read();
        return ((SortedMap)this.map).firstKey();
    }

    public Object lastKey() {
        this.read();
        return ((SortedMap)this.map).lastKey();
    }

    class SortedSubMap
    implements SortedMap {
        SortedMap subMap;

        SortedSubMap(SortedMap subMap) {
            this.subMap = subMap;
        }

        @Override
        public int size() {
            return this.subMap.size();
        }

        @Override
        public boolean isEmpty() {
            return this.subMap.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return this.subMap.containsKey(key);
        }

        @Override
        public boolean containsValue(Object key) {
            return this.subMap.containsValue(key);
        }

        @Override
        public Object get(Object key) {
            return this.subMap.get(key);
        }

        @Override
        public Object put(Object key, Object value) {
            PersistentSortedMap.this.write();
            return this.subMap.put(key, value);
        }

        @Override
        public Object remove(Object key) {
            PersistentSortedMap.this.write();
            return this.subMap.remove(key);
        }

        @Override
        public void putAll(Map other) {
            PersistentSortedMap.this.write();
            this.subMap.putAll(other);
        }

        @Override
        public void clear() {
            PersistentSortedMap.this.write();
            this.subMap.clear();
        }

        @Override
        public Set keySet() {
            return new AbstractPersistentCollection.SetProxy(this.subMap.keySet());
        }

        @Override
        public Collection values() {
            return new AbstractPersistentCollection.SetProxy(this.subMap.values());
        }

        @Override
        public Set entrySet() {
            return new PersistentMap.EntrySetProxy(this.subMap.entrySet());
        }

        public Comparator comparator() {
            return this.subMap.comparator();
        }

        public SortedMap subMap(Object fromKey, Object toKey) {
            SortedMap subMap = this.subMap.subMap(fromKey, toKey);
            return new SortedSubMap(subMap);
        }

        public SortedMap headMap(Object toKey) {
            SortedMap headMap = this.subMap.headMap(toKey);
            return new SortedSubMap(headMap);
        }

        public SortedMap tailMap(Object fromKey) {
            SortedMap tailMap = this.subMap.tailMap(fromKey);
            return new SortedSubMap(tailMap);
        }

        public Object firstKey() {
            return this.subMap.firstKey();
        }

        public Object lastKey() {
            return this.subMap.lastKey();
        }
    }
}

