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

public class PersistentSet
extends AbstractPersistentCollection
implements Set {
    protected Set set;
    protected transient List tempList;

    public PersistentSet() {
    }

    public PersistentSet(SharedSessionContractImplementor session) {
        super(session);
    }

    @Deprecated
    public PersistentSet(SessionImplementor session) {
        this((SharedSessionContractImplementor)session);
    }

    public PersistentSet(SharedSessionContractImplementor session, Set set) {
        super(session);
        this.set = set;
        this.setInitialized();
        this.setDirectlyAccessible(true);
    }

    @Deprecated
    public PersistentSet(SessionImplementor session, Set set) {
        this((SharedSessionContractImplementor)session, set);
    }

    @Override
    public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
        HashMap<Object, Object> clonedSet = new HashMap<Object, Object>(this.set.size());
        for (Object aSet : this.set) {
            Object copied = persister.getElementType().deepCopy(aSet, persister.getFactory());
            clonedSet.put(copied, copied);
        }
        return clonedSet;
    }

    @Override
    public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
        Map sn = (Map)((Object)snapshot);
        return PersistentSet.getOrphans(sn.keySet(), this.set, entityName, this.getSession());
    }

    @Override
    public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
        Type elementType = persister.getElementType();
        Map sn = (Map)((Object)this.getSnapshot());
        if (sn.size() != this.set.size()) {
            return false;
        }
        for (Object test : this.set) {
            Object oldValue = sn.get(test);
            if (oldValue != null && !elementType.isDirty(oldValue, test, this.getSession())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isSnapshotEmpty(Serializable snapshot) {
        return ((Map)((Object)snapshot)).isEmpty();
    }

    @Override
    public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
        this.set = (Set)persister.getCollectionType().instantiate(anticipatedSize);
    }

    @Override
    public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner) throws HibernateException {
        Serializable[] array = (Serializable[])disassembled;
        int size = array.length;
        this.beforeInitialize(persister, size);
        for (Serializable arrayElement : array) {
            Object assembledArrayElement = persister.getElementType().assemble(arrayElement, this.getSession(), owner);
            if (assembledArrayElement == null) continue;
            this.set.add(assembledArrayElement);
        }
    }

    @Override
    public boolean empty() {
        return this.set.isEmpty();
    }

    @Override
    public int size() {
        return this.readSize() ? this.getCachedSize() : this.set.size();
    }

    @Override
    public boolean isEmpty() {
        return this.readSize() ? this.getCachedSize() == 0 : this.set.isEmpty();
    }

    @Override
    public boolean contains(Object object) {
        Boolean exists = this.readElementExistence(object);
        return exists == null ? this.set.contains(object) : exists.booleanValue();
    }

    @Override
    public Iterator iterator() {
        this.read();
        return new AbstractPersistentCollection.IteratorProxy(this.set.iterator());
    }

    @Override
    public Object[] toArray() {
        this.read();
        return this.set.toArray();
    }

    @Override
    public Object[] toArray(Object[] array) {
        this.read();
        return this.set.toArray(array);
    }

    @Override
    public boolean add(Object value) {
        Boolean exists;
        Boolean bl = exists = this.isOperationQueueEnabled() ? this.readElementExistence(value) : null;
        if (exists == null) {
            this.initialize(true);
            if (this.set.add(value)) {
                this.dirty();
                return true;
            }
            return false;
        }
        if (exists.booleanValue()) {
            return false;
        }
        this.queueOperation(new SimpleAdd(value));
        return true;
    }

    @Override
    public boolean remove(Object value) {
        Boolean exists;
        Boolean bl = exists = this.isPutQueueEnabled() ? this.readElementExistence(value) : null;
        if (exists == null) {
            this.initialize(true);
            if (this.set.remove(value)) {
                this.elementRemoved = true;
                this.dirty();
                return true;
            }
            return false;
        }
        if (exists.booleanValue()) {
            this.elementRemoved = true;
            this.queueOperation(new SimpleRemove(value));
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection coll) {
        this.read();
        return this.set.containsAll(coll);
    }

    @Override
    public boolean addAll(Collection coll) {
        if (coll.size() > 0) {
            this.initialize(true);
            if (this.set.addAll(coll)) {
                this.dirty();
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean retainAll(Collection coll) {
        this.initialize(true);
        if (this.set.retainAll(coll)) {
            this.dirty();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection coll) {
        if (coll.size() > 0) {
            this.initialize(true);
            if (this.set.removeAll(coll)) {
                this.elementRemoved = true;
                this.dirty();
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public void clear() {
        if (this.isClearQueueEnabled()) {
            this.queueOperation(new Clear());
        } else {
            this.initialize(true);
            if (!this.set.isEmpty()) {
                this.set.clear();
                this.dirty();
            }
        }
    }

    public String toString() {
        this.read();
        return this.set.toString();
    }

    @Override
    public Object readFrom(ResultSet rs, CollectionPersister persister, CollectionAliases descriptor, Object owner) throws HibernateException, SQLException {
        Object element = persister.readElement(rs, owner, descriptor.getSuffixedElementAliases(), this.getSession());
        if (element != null) {
            this.tempList.add(element);
        }
        return element;
    }

    @Override
    public void beginRead() {
        super.beginRead();
        this.tempList = new ArrayList();
    }

    @Override
    public boolean endRead() {
        this.set.addAll(this.tempList);
        this.tempList = null;
        return super.endRead();
    }

    @Override
    public Iterator entries(CollectionPersister persister) {
        return this.set.iterator();
    }

    @Override
    public Serializable disassemble(CollectionPersister persister) throws HibernateException {
        Serializable[] result = new Serializable[this.set.size()];
        Iterator itr = this.set.iterator();
        int i = 0;
        while (itr.hasNext()) {
            result[i++] = persister.getElementType().disassemble(itr.next(), this.getSession(), null);
        }
        return result;
    }

    @Override
    public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
        Type elementType = persister.getElementType();
        Map sn = (Map)((Object)this.getSnapshot());
        ArrayList<Object> deletes = new ArrayList<Object>(sn.size());
        for (Object test : sn.keySet()) {
            if (this.set.contains(test)) continue;
            deletes.add(test);
        }
        for (Object test : this.set) {
            Object oldValue = sn.get(test);
            if (oldValue == null || !elementType.isDirty(test, oldValue, this.getSession())) continue;
            deletes.add(oldValue);
        }
        return deletes.iterator();
    }

    @Override
    public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
        Object oldValue = ((Map)((Object)this.getSnapshot())).get(entry);
        return oldValue == null && entry != null || elemType.isDirty(oldValue, entry, this.getSession());
    }

    @Override
    public boolean needsUpdating(Object entry, int i, Type elemType) {
        return false;
    }

    @Override
    public boolean isRowUpdatePossible() {
        return false;
    }

    @Override
    public Object getIndex(Object entry, int i, CollectionPersister persister) {
        throw new UnsupportedOperationException("Sets don't have indexes");
    }

    @Override
    public Object getElement(Object entry) {
        return entry;
    }

    @Override
    public Object getSnapshotElement(Object entry, int i) {
        throw new UnsupportedOperationException("Sets don't support updating by element");
    }

    @Override
    public boolean equals(Object other) {
        this.read();
        return this.set.equals(other);
    }

    @Override
    public int hashCode() {
        this.read();
        return this.set.hashCode();
    }

    @Override
    public boolean entryExists(Object key, int i) {
        return key != null;
    }

    @Override
    public boolean isWrapper(Object collection) {
        return this.set == collection;
    }

    final class SimpleRemove
    extends AbstractPersistentCollection.AbstractValueDelayedOperation {
        public SimpleRemove(Object orphan) {
            super(null, orphan);
        }

        @Override
        public void operate() {
            PersistentSet.this.set.remove(this.getOrphan());
        }
    }

    final class SimpleAdd
    extends AbstractPersistentCollection.AbstractValueDelayedOperation {
        public SimpleAdd(Object addedValue) {
            super(addedValue, null);
        }

        @Override
        public void operate() {
            PersistentSet.this.set.add(this.getAddedInstance());
        }
    }

    final class Clear
    implements AbstractPersistentCollection.DelayedOperation {
        Clear() {
        }

        @Override
        public void operate() {
            PersistentSet.this.set.clear();
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

