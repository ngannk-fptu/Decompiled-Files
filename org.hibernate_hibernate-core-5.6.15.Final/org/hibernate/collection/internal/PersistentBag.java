/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.collection.internal;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.Type;

public class PersistentBag
extends AbstractPersistentCollection
implements List {
    protected List bag;
    private Collection providedCollection;

    public PersistentBag() {
    }

    public PersistentBag(SharedSessionContractImplementor session) {
        super(session);
    }

    @Deprecated
    public PersistentBag(SessionImplementor session) {
        this((SharedSessionContractImplementor)session);
    }

    public PersistentBag(SharedSessionContractImplementor session, Collection coll) {
        super(session);
        this.providedCollection = coll;
        this.bag = coll instanceof List ? (List)coll : new ArrayList(coll);
        this.setInitialized();
        this.setDirectlyAccessible(true);
    }

    @Deprecated
    public PersistentBag(SessionImplementor session, Collection coll) {
        this((SharedSessionContractImplementor)session, coll);
    }

    @Override
    public boolean isWrapper(Object collection) {
        return this.bag == collection;
    }

    @Override
    public boolean isDirectlyProvidedCollection(Object collection) {
        return this.isDirectlyAccessible() && this.providedCollection == collection;
    }

    @Override
    public boolean empty() {
        return this.bag.isEmpty();
    }

    @Override
    public Iterator entries(CollectionPersister persister) {
        return this.bag.iterator();
    }

    @Override
    public Object readFrom(ResultSet rs, CollectionPersister persister, CollectionAliases descriptor, Object owner) throws HibernateException, SQLException {
        Object element = persister.readElement(rs, owner, descriptor.getSuffixedElementAliases(), this.getSession());
        if (element != null) {
            this.bag.add(element);
        }
        return element;
    }

    @Override
    public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
        this.bag = (List)persister.getCollectionType().instantiate(anticipatedSize);
    }

    @Override
    public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
        List<Object> instancesSn;
        List<Object> instancesBag;
        Integer hash;
        Type elementType = persister.getElementType();
        List sn = (List)((Object)this.getSnapshot());
        if (sn.size() != this.bag.size()) {
            return false;
        }
        Map<Integer, List<Object>> hashToInstancesBag = this.groupByEqualityHash(this.bag, elementType);
        Map<Integer, List<Object>> hashToInstancesSn = this.groupByEqualityHash(sn, elementType);
        if (hashToInstancesBag.size() != hashToInstancesSn.size()) {
            return false;
        }
        for (Map.Entry<Integer, List<Object>> hashToInstancesBagEntry : hashToInstancesBag.entrySet()) {
            hash = hashToInstancesBagEntry.getKey();
            instancesBag = hashToInstancesBagEntry.getValue();
            instancesSn = hashToInstancesSn.get(hash);
            if (instancesSn != null && instancesBag.size() == instancesSn.size()) continue;
            return false;
        }
        for (Map.Entry<Integer, List<Object>> hashToInstancesBagEntry : hashToInstancesBag.entrySet()) {
            hash = hashToInstancesBagEntry.getKey();
            instancesBag = hashToInstancesBagEntry.getValue();
            instancesSn = hashToInstancesSn.get(hash);
            for (Object instance : instancesBag) {
                if (this.expectOccurrences(instance, instancesBag, elementType, this.countOccurrences(instance, instancesSn, elementType))) continue;
                return false;
            }
        }
        return true;
    }

    private Map<Integer, List<Object>> groupByEqualityHash(List<Object> searchedBag, Type elementType) {
        if (searchedBag.isEmpty()) {
            return Collections.emptyMap();
        }
        HashMap<Integer, List<Object>> map = new HashMap<Integer, List<Object>>();
        for (Object o : searchedBag) {
            map.computeIfAbsent(this.nullableHashCode(o, elementType), k -> new ArrayList()).add(o);
        }
        return map;
    }

    private Integer nullableHashCode(Object o, Type elementType) {
        if (o == null) {
            return null;
        }
        return elementType.getHashCode(o);
    }

    @Override
    public boolean isSnapshotEmpty(Serializable snapshot) {
        return ((Collection)((Object)snapshot)).isEmpty();
    }

    private int countOccurrences(Object element, List<Object> list, Type elementType) {
        int result = 0;
        for (Object listElement : list) {
            if (!elementType.isSame(element, listElement)) continue;
            ++result;
        }
        return result;
    }

    private boolean expectOccurrences(Object element, List<Object> list, Type elementType, int expected) {
        int result = 0;
        for (Object listElement : list) {
            if (!elementType.isSame(element, listElement) || result++ <= expected) continue;
            return false;
        }
        return result == expected;
    }

    @Override
    public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
        ArrayList<Object> clonedList = new ArrayList<Object>(this.bag.size());
        for (Object item : this.bag) {
            clonedList.add(persister.getElementType().deepCopy(item, persister.getFactory()));
        }
        return clonedList;
    }

    @Override
    public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
        List sn = (List)((Object)snapshot);
        return PersistentBag.getOrphans(sn, this.bag, entityName, this.getSession());
    }

    @Override
    public Serializable disassemble(CollectionPersister persister) throws HibernateException {
        int length = this.bag.size();
        Serializable[] result = new Serializable[length];
        for (int i = 0; i < length; ++i) {
            result[i] = persister.getElementType().disassemble(this.bag.get(i), this.getSession(), null);
        }
        return result;
    }

    @Override
    public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner) throws HibernateException {
        Serializable[] array = (Serializable[])disassembled;
        int size = array.length;
        this.beforeInitialize(persister, size);
        for (Serializable item : array) {
            Object element = persister.getElementType().assemble(item, this.getSession(), owner);
            if (element == null) continue;
            this.bag.add(element);
        }
    }

    @Override
    public boolean needsRecreate(CollectionPersister persister) {
        return !persister.isOneToMany();
    }

    @Override
    public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
        Type elementType = persister.getElementType();
        ArrayList deletes = new ArrayList();
        List sn = (List)((Object)this.getSnapshot());
        Iterator olditer = sn.iterator();
        int i = 0;
        while (olditer.hasNext()) {
            Object old = olditer.next();
            Iterator newiter = this.bag.iterator();
            boolean found = false;
            if (this.bag.size() > i && elementType.isSame(old, this.bag.get(i++))) {
                found = true;
            } else {
                while (newiter.hasNext()) {
                    if (!elementType.isSame(old, newiter.next())) continue;
                    found = true;
                    break;
                }
            }
            if (found) continue;
            deletes.add(old);
        }
        return deletes.iterator();
    }

    @Override
    public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
        List sn = (List)((Object)this.getSnapshot());
        if (sn.size() > i && elemType.isSame(sn.get(i), entry)) {
            return false;
        }
        for (Object old : sn) {
            if (!elemType.isSame(old, entry)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isRowUpdatePossible() {
        return false;
    }

    @Override
    public boolean needsUpdating(Object entry, int i, Type elemType) {
        return false;
    }

    @Override
    public int size() {
        return this.readSize() ? this.getCachedSize() : this.bag.size();
    }

    @Override
    public boolean isEmpty() {
        return this.readSize() ? this.getCachedSize() == 0 : this.bag.isEmpty();
    }

    @Override
    public boolean contains(Object object) {
        Boolean exists = this.readElementExistence(object);
        return exists == null ? this.bag.contains(object) : exists.booleanValue();
    }

    @Override
    public Iterator iterator() {
        this.read();
        return new AbstractPersistentCollection.IteratorProxy(this.bag.iterator());
    }

    @Override
    public Object[] toArray() {
        this.read();
        return this.bag.toArray();
    }

    @Override
    public Object[] toArray(Object[] a) {
        this.read();
        return this.bag.toArray(a);
    }

    @Override
    public boolean add(Object object) {
        if (!this.isOperationQueueEnabled()) {
            this.write();
            return this.bag.add(object);
        }
        this.queueOperation(new SimpleAdd(object));
        return true;
    }

    @Override
    public boolean remove(Object o) {
        this.initialize(true);
        if (this.bag.remove(o)) {
            this.elementRemoved = true;
            this.dirty();
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection c) {
        this.read();
        return this.bag.containsAll(c);
    }

    @Override
    public boolean addAll(Collection values) {
        if (values.size() == 0) {
            return false;
        }
        if (!this.isOperationQueueEnabled()) {
            this.write();
            return this.bag.addAll(values);
        }
        for (Object value : values) {
            this.queueOperation(new SimpleAdd(value));
        }
        return values.size() > 0;
    }

    @Override
    public boolean removeAll(Collection c) {
        if (c.size() > 0) {
            this.initialize(true);
            if (this.bag.removeAll(c)) {
                this.elementRemoved = true;
                this.dirty();
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean retainAll(Collection c) {
        this.initialize(true);
        if (this.bag.retainAll(c)) {
            this.dirty();
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        if (this.isClearQueueEnabled()) {
            this.queueOperation(new Clear());
        } else {
            this.initialize(true);
            if (!this.bag.isEmpty()) {
                this.bag.clear();
                this.dirty();
            }
        }
    }

    @Override
    public Object getIndex(Object entry, int i, CollectionPersister persister) {
        throw new UnsupportedOperationException("Bags don't have indexes");
    }

    @Override
    public Object getElement(Object entry) {
        return entry;
    }

    @Override
    public Object getSnapshotElement(Object entry, int i) {
        List sn = (List)((Object)this.getSnapshot());
        return sn.get(i);
    }

    public int occurrences(Object o) {
        this.read();
        Iterator itr = this.bag.iterator();
        int result = 0;
        while (itr.hasNext()) {
            if (!o.equals(itr.next())) continue;
            ++result;
        }
        return result;
    }

    public void add(int i, Object o) {
        this.write();
        this.bag.add(i, o);
    }

    public boolean addAll(int i, Collection c) {
        if (c.size() > 0) {
            this.write();
            return this.bag.addAll(i, c);
        }
        return false;
    }

    public Object get(int i) {
        this.read();
        return this.bag.get(i);
    }

    @Override
    public int indexOf(Object o) {
        this.read();
        return this.bag.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        this.read();
        return this.bag.lastIndexOf(o);
    }

    public ListIterator listIterator() {
        this.read();
        return new AbstractPersistentCollection.ListIteratorProxy(this.bag.listIterator());
    }

    public ListIterator listIterator(int i) {
        this.read();
        return new AbstractPersistentCollection.ListIteratorProxy(this.bag.listIterator(i));
    }

    public Object remove(int i) {
        this.write();
        return this.bag.remove(i);
    }

    public Object set(int i, Object o) {
        this.write();
        return this.bag.set(i, o);
    }

    public List subList(int start, int end) {
        this.read();
        return new AbstractPersistentCollection.ListProxy(this.bag.subList(start, end));
    }

    @Override
    public boolean entryExists(Object entry, int i) {
        return entry != null;
    }

    public String toString() {
        this.read();
        return this.bag.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    final class SimpleAdd
    extends AbstractPersistentCollection.AbstractValueDelayedOperation {
        public SimpleAdd(Object addedValue) {
            super(addedValue, null);
        }

        @Override
        public void operate() {
            PersistentBag.this.bag.add(this.getAddedInstance());
        }
    }

    final class Clear
    implements AbstractPersistentCollection.DelayedOperation {
        Clear() {
        }

        @Override
        public void operate() {
            PersistentBag.this.bag.clear();
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
}

