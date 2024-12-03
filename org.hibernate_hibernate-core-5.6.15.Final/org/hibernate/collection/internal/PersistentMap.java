/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.collection.internal;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.Type;

public class PersistentMap
extends AbstractPersistentCollection
implements Map {
    protected Map map;
    private transient List<Object[]> loadingEntries;

    public PersistentMap() {
    }

    public PersistentMap(SharedSessionContractImplementor session) {
        super(session);
    }

    @Deprecated
    public PersistentMap(SessionImplementor session) {
        this((SharedSessionContractImplementor)session);
    }

    public PersistentMap(SharedSessionContractImplementor session, Map map) {
        super(session);
        this.map = map;
        this.setInitialized();
        this.setDirectlyAccessible(true);
    }

    @Deprecated
    public PersistentMap(SessionImplementor session, Map map) {
        this((SharedSessionContractImplementor)session, map);
    }

    @Override
    public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
        HashMap clonedMap = new HashMap(this.map.size());
        Iterator iterator = this.map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry e = o = iterator.next();
            Object copy = persister.getElementType().deepCopy(e.getValue(), persister.getFactory());
            clonedMap.put(e.getKey(), copy);
        }
        return clonedMap;
    }

    @Override
    public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
        Map sn = (Map)((Object)snapshot);
        return PersistentMap.getOrphans(sn.values(), this.map.values(), entityName, this.getSession());
    }

    @Override
    public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
        Type elementType = persister.getElementType();
        Map snapshotMap = (Map)((Object)this.getSnapshot());
        if (snapshotMap.size() != this.map.size()) {
            return false;
        }
        for (Map.Entry o : this.map.entrySet()) {
            Map.Entry entry = o;
            if (!elementType.isDirty(entry.getValue(), snapshotMap.get(entry.getKey()), this.getSession())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isSnapshotEmpty(Serializable snapshot) {
        return ((Map)((Object)snapshot)).isEmpty();
    }

    @Override
    public boolean isWrapper(Object collection) {
        return this.map == collection;
    }

    @Override
    public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
        this.map = (Map)persister.getCollectionType().instantiate(anticipatedSize);
    }

    @Override
    public int size() {
        return this.readSize() ? this.getCachedSize() : this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.readSize() ? this.getCachedSize() == 0 : this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        Boolean exists = this.readIndexExistence(key);
        return exists == null ? this.map.containsKey(key) : exists.booleanValue();
    }

    @Override
    public boolean containsValue(Object value) {
        Boolean exists = this.readElementExistence(value);
        return exists == null ? this.map.containsValue(value) : exists.booleanValue();
    }

    public Object get(Object key) {
        Object result = this.readElementByIndex(key);
        return result == UNKNOWN ? this.map.get(key) : result;
    }

    public Object put(Object key, Object value) {
        Object old;
        if (this.isPutQueueEnabled() && (old = this.readElementByIndex(key)) != UNKNOWN) {
            this.queueOperation(new Put(key, value, old));
            return old;
        }
        this.initialize(true);
        old = this.map.put(key, value);
        if (value != old) {
            this.dirty();
        }
        return old;
    }

    public Object remove(Object key) {
        Object old;
        if (this.isPutQueueEnabled() && (old = this.readElementByIndex(key)) != UNKNOWN) {
            this.elementRemoved = true;
            this.queueOperation(new Remove(key, old));
            return old;
        }
        this.initialize(true);
        if (this.map.containsKey(key)) {
            this.elementRemoved = true;
            this.dirty();
        }
        return this.map.remove(key);
    }

    public void putAll(Map puts) {
        if (puts.size() > 0) {
            this.initialize(true);
            Iterator iterator = puts.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry o;
                Map.Entry entry = o = iterator.next();
                this.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void clear() {
        if (this.isClearQueueEnabled()) {
            this.queueOperation(new Clear());
        } else {
            this.initialize(true);
            if (!this.map.isEmpty()) {
                this.dirty();
                this.map.clear();
            }
        }
    }

    public Set keySet() {
        this.read();
        return new AbstractPersistentCollection.SetProxy(this.map.keySet());
    }

    public Collection values() {
        this.read();
        return new AbstractPersistentCollection.SetProxy(this.map.values());
    }

    public Set entrySet() {
        this.read();
        return new EntrySetProxy(this.map.entrySet());
    }

    @Override
    public boolean empty() {
        return this.map.isEmpty();
    }

    public String toString() {
        this.read();
        return this.map.toString();
    }

    @Override
    public Object readFrom(ResultSet rs, CollectionPersister persister, CollectionAliases descriptor, Object owner) throws HibernateException, SQLException {
        Object element = persister.readElement(rs, owner, descriptor.getSuffixedElementAliases(), this.getSession());
        if (element != null) {
            Object index = persister.readIndex(rs, descriptor.getSuffixedIndexAliases(), this.getSession());
            if (this.loadingEntries == null) {
                this.loadingEntries = new ArrayList<Object[]>();
            }
            this.loadingEntries.add(new Object[]{index, element});
        }
        return element;
    }

    @Override
    public boolean endRead() {
        if (this.loadingEntries != null) {
            for (Object[] entry : this.loadingEntries) {
                this.map.put(entry[0], entry[1]);
            }
            this.loadingEntries = null;
        }
        return super.endRead();
    }

    @Override
    public Iterator entries(CollectionPersister persister) {
        return this.map.entrySet().iterator();
    }

    @Override
    public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner) throws HibernateException {
        Serializable[] array = (Serializable[])disassembled;
        int size = array.length;
        this.beforeInitialize(persister, size);
        for (int i = 0; i < size; i += 2) {
            this.map.put(persister.getIndexType().assemble(array[i], this.getSession(), owner), persister.getElementType().assemble(array[i + 1], this.getSession(), owner));
        }
    }

    @Override
    public Serializable disassemble(CollectionPersister persister) throws HibernateException {
        Serializable[] result = new Serializable[this.map.size() * 2];
        Iterator itr = this.map.entrySet().iterator();
        int i = 0;
        while (itr.hasNext()) {
            Map.Entry e = itr.next();
            result[i++] = persister.getIndexType().disassemble(e.getKey(), this.getSession(), null);
            result[i++] = persister.getElementType().disassemble(e.getValue(), this.getSession(), null);
        }
        return result;
    }

    @Override
    public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
        ArrayList deletes = new ArrayList();
        Iterator iterator = ((Map)((Object)this.getSnapshot())).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry e = o = iterator.next();
            Object key = e.getKey();
            if (e.getValue() == null || this.map.get(key) != null) continue;
            deletes.add(indexIsFormula ? e.getValue() : key);
        }
        return deletes.iterator();
    }

    @Override
    public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
        Map sn = (Map)((Object)this.getSnapshot());
        Map.Entry e = (Map.Entry)entry;
        return e.getValue() != null && sn.get(e.getKey()) == null;
    }

    @Override
    public boolean needsUpdating(Object entry, int i, Type elemType) throws HibernateException {
        Map sn = (Map)((Object)this.getSnapshot());
        Map.Entry e = (Map.Entry)entry;
        Object snValue = sn.get(e.getKey());
        return e.getValue() != null && snValue != null && elemType.isDirty(snValue, e.getValue(), this.getSession());
    }

    @Override
    public Object getIndex(Object entry, int i, CollectionPersister persister) {
        return ((Map.Entry)entry).getKey();
    }

    @Override
    public Object getElement(Object entry) {
        return ((Map.Entry)entry).getValue();
    }

    @Override
    public Object getSnapshotElement(Object entry, int i) {
        Map sn = (Map)((Object)this.getSnapshot());
        return sn.get(((Map.Entry)entry).getKey());
    }

    @Override
    public boolean equals(Object other) {
        this.read();
        return this.map.equals(other);
    }

    @Override
    public int hashCode() {
        this.read();
        return this.map.hashCode();
    }

    @Override
    public boolean entryExists(Object entry, int i) {
        return ((Map.Entry)entry).getValue() != null;
    }

    final class Remove
    extends AbstractMapValueDelayedOperation {
        public Remove(Object index, Object orphan) {
            super(index, null, orphan);
        }

        @Override
        public void operate() {
            PersistentMap.this.map.remove(this.getIndex());
        }
    }

    final class Put
    extends AbstractMapValueDelayedOperation {
        public Put(Object index, Object addedValue, Object orphan) {
            super(index, addedValue, orphan);
        }

        @Override
        public void operate() {
            PersistentMap.this.map.put(this.getIndex(), this.getAddedInstance());
        }
    }

    abstract class AbstractMapValueDelayedOperation
    extends AbstractPersistentCollection.AbstractValueDelayedOperation {
        private Object index;

        protected AbstractMapValueDelayedOperation(Object index, Object addedValue, Object orphan) {
            super(addedValue, orphan);
            this.index = index;
        }

        protected final Object getIndex() {
            return this.index;
        }
    }

    final class Clear
    implements AbstractPersistentCollection.DelayedOperation {
        Clear() {
        }

        @Override
        public void operate() {
            PersistentMap.this.map.clear();
        }

        @Override
        public Object getAddedInstance() {
            return null;
        }

        @Override
        public Object getOrphan() {
            throw new UnsupportedOperationException("queued clear cannot be used with orphan delete");
        }
    }

    final class MapEntryProxy
    implements Map.Entry {
        private final Map.Entry me;

        MapEntryProxy(Map.Entry me) {
            this.me = me;
        }

        public Object getKey() {
            return this.me.getKey();
        }

        public Object getValue() {
            return this.me.getValue();
        }

        @Override
        public boolean equals(Object o) {
            return this.me.equals(o);
        }

        @Override
        public int hashCode() {
            return this.me.hashCode();
        }

        public Object setValue(Object value) {
            PersistentMap.this.write();
            return this.me.setValue(value);
        }
    }

    final class EntryIteratorProxy
    implements Iterator {
        private final Iterator iter;

        EntryIteratorProxy(Iterator iter) {
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        public Object next() {
            return new MapEntryProxy((Map.Entry)this.iter.next());
        }

        @Override
        public void remove() {
            PersistentMap.this.write();
            this.iter.remove();
        }
    }

    class EntrySetProxy
    implements Set {
        private final Set set;

        EntrySetProxy(Set set) {
            this.set = set;
        }

        @Override
        public boolean add(Object entry) {
            return this.set.add(entry);
        }

        @Override
        public boolean addAll(Collection entries) {
            return this.set.addAll(entries);
        }

        @Override
        public void clear() {
            PersistentMap.this.write();
            this.set.clear();
        }

        @Override
        public boolean contains(Object entry) {
            return this.set.contains(entry);
        }

        @Override
        public boolean containsAll(Collection entries) {
            return this.set.containsAll(entries);
        }

        @Override
        public boolean isEmpty() {
            return this.set.isEmpty();
        }

        @Override
        public Iterator iterator() {
            return new EntryIteratorProxy(this.set.iterator());
        }

        @Override
        public boolean remove(Object entry) {
            PersistentMap.this.write();
            return this.set.remove(entry);
        }

        @Override
        public boolean removeAll(Collection entries) {
            PersistentMap.this.write();
            return this.set.removeAll(entries);
        }

        @Override
        public boolean retainAll(Collection entries) {
            PersistentMap.this.write();
            return this.set.retainAll(entries);
        }

        @Override
        public int size() {
            return this.set.size();
        }

        @Override
        public Object[] toArray() {
            return this.set.toArray();
        }

        @Override
        public Object[] toArray(Object[] array) {
            return this.set.toArray(array);
        }
    }
}

