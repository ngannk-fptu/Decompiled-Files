/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.collection.internal;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.hibernate.HibernateException;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.Type;

public class PersistentList
extends AbstractPersistentCollection
implements List {
    protected List list;

    public PersistentList() {
    }

    public PersistentList(SharedSessionContractImplementor session) {
        super(session);
    }

    @Deprecated
    public PersistentList(SessionImplementor session) {
        this((SharedSessionContractImplementor)session);
    }

    public PersistentList(SharedSessionContractImplementor session, List list) {
        super(session);
        this.list = list;
        this.setInitialized();
        this.setDirectlyAccessible(true);
    }

    @Deprecated
    public PersistentList(SessionImplementor session, List list) {
        this((SharedSessionContractImplementor)session, list);
    }

    @Override
    public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
        ArrayList<Object> clonedList = new ArrayList<Object>(this.list.size());
        for (Object element : this.list) {
            Object deepCopy = persister.getElementType().deepCopy(element, persister.getFactory());
            clonedList.add(deepCopy);
        }
        return clonedList;
    }

    @Override
    public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
        List sn = (List)((Object)snapshot);
        return PersistentList.getOrphans(sn, this.list, entityName, this.getSession());
    }

    @Override
    public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
        Type elementType = persister.getElementType();
        List sn = (List)((Object)this.getSnapshot());
        if (sn.size() != this.list.size()) {
            return false;
        }
        Iterator itr = this.list.iterator();
        Iterator snapshotItr = sn.iterator();
        while (itr.hasNext()) {
            if (!elementType.isDirty(itr.next(), snapshotItr.next(), this.getSession())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isSnapshotEmpty(Serializable snapshot) {
        return ((Collection)((Object)snapshot)).isEmpty();
    }

    @Override
    public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
        this.list = (List)persister.getCollectionType().instantiate(anticipatedSize);
    }

    @Override
    public boolean isWrapper(Object collection) {
        return this.list == collection;
    }

    @Override
    public int size() {
        return this.readSize() ? this.getCachedSize() : this.list.size();
    }

    @Override
    public boolean isEmpty() {
        return this.readSize() ? this.getCachedSize() == 0 : this.list.isEmpty();
    }

    @Override
    public boolean contains(Object object) {
        Boolean exists = this.readElementExistence(object);
        return exists == null ? this.list.contains(object) : exists.booleanValue();
    }

    @Override
    public Iterator iterator() {
        this.read();
        return new AbstractPersistentCollection.IteratorProxy(this.list.iterator());
    }

    @Override
    public Object[] toArray() {
        this.read();
        return this.list.toArray();
    }

    @Override
    public Object[] toArray(Object[] array) {
        this.read();
        return this.list.toArray(array);
    }

    @Override
    public boolean add(Object object) {
        if (!this.isOperationQueueEnabled()) {
            this.write();
            return this.list.add(object);
        }
        this.queueOperation(new SimpleAdd(object));
        return true;
    }

    @Override
    public boolean remove(Object value) {
        Boolean exists;
        Boolean bl = exists = this.isPutQueueEnabled() ? this.readElementExistence(value) : null;
        if (exists == null) {
            this.initialize(true);
            if (this.list.remove(value)) {
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
        return this.list.containsAll(coll);
    }

    @Override
    public boolean addAll(Collection values) {
        if (values.size() == 0) {
            return false;
        }
        if (!this.isOperationQueueEnabled()) {
            this.write();
            return this.list.addAll(values);
        }
        for (Object value : values) {
            this.queueOperation(new SimpleAdd(value));
        }
        return values.size() > 0;
    }

    public boolean addAll(int index, Collection coll) {
        if (coll.size() > 0) {
            this.write();
            return this.list.addAll(index, coll);
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection coll) {
        if (coll.size() > 0) {
            this.initialize(true);
            if (this.list.removeAll(coll)) {
                this.elementRemoved = true;
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
        if (this.list.retainAll(coll)) {
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
            if (!this.list.isEmpty()) {
                this.list.clear();
                this.dirty();
            }
        }
    }

    public Object get(int index) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException("negative index");
        }
        Object result = this.readElementByIndex(index);
        return result == UNKNOWN ? this.list.get(index) : result;
    }

    public Object set(int index, Object value) {
        Object old;
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException("negative index");
        }
        Object object = old = this.isPutQueueEnabled() ? this.readElementByIndex(index) : UNKNOWN;
        if (old == UNKNOWN) {
            this.write();
            return this.list.set(index, value);
        }
        this.queueOperation(new Set(index, value, old));
        return old;
    }

    public Object remove(int index) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException("negative index");
        }
        Object old = this.isPutQueueEnabled() ? this.readElementByIndex(index) : UNKNOWN;
        this.elementRemoved = true;
        if (old == UNKNOWN) {
            this.write();
            this.dirty();
            return this.list.remove(index);
        }
        this.queueOperation(new Remove(index, old));
        return old;
    }

    public void add(int index, Object value) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException("negative index");
        }
        this.write();
        this.list.add(index, value);
    }

    @Override
    public int indexOf(Object value) {
        this.read();
        return this.list.indexOf(value);
    }

    @Override
    public int lastIndexOf(Object value) {
        this.read();
        return this.list.lastIndexOf(value);
    }

    public ListIterator listIterator() {
        this.read();
        return new AbstractPersistentCollection.ListIteratorProxy(this.list.listIterator());
    }

    public ListIterator listIterator(int index) {
        this.read();
        return new AbstractPersistentCollection.ListIteratorProxy(this.list.listIterator(index));
    }

    public List subList(int from, int to) {
        this.read();
        return new AbstractPersistentCollection.ListProxy(this.list.subList(from, to));
    }

    @Override
    public boolean empty() {
        return this.list.isEmpty();
    }

    public String toString() {
        this.read();
        return this.list.toString();
    }

    @Override
    public Object readFrom(ResultSet rs, CollectionPersister persister, CollectionAliases descriptor, Object owner) throws HibernateException, SQLException {
        Object element = persister.readElement(rs, owner, descriptor.getSuffixedElementAliases(), this.getSession());
        int index = (Integer)persister.readIndex(rs, descriptor.getSuffixedIndexAliases(), this.getSession());
        for (int i = this.list.size(); i <= index; ++i) {
            this.list.add(i, null);
        }
        this.list.set(index, element);
        return element;
    }

    @Override
    public Iterator entries(CollectionPersister persister) {
        return this.list.iterator();
    }

    @Override
    public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner) throws HibernateException {
        Serializable[] array = (Serializable[])disassembled;
        int size = array.length;
        this.beforeInitialize(persister, size);
        for (Serializable arrayElement : array) {
            this.list.add(persister.getElementType().assemble(arrayElement, this.getSession(), owner));
        }
    }

    @Override
    public Serializable disassemble(CollectionPersister persister) throws HibernateException {
        int length = this.list.size();
        Serializable[] result = new Serializable[length];
        for (int i = 0; i < length; ++i) {
            result[i] = persister.getElementType().disassemble(this.list.get(i), this.getSession(), null);
        }
        return result;
    }

    @Override
    public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
        int end;
        int i;
        ArrayList<Integer> deletes = new ArrayList<Integer>();
        List sn = (List)((Object)this.getSnapshot());
        if (sn.size() > this.list.size()) {
            for (i = this.list.size(); i < sn.size(); ++i) {
                deletes.add((Integer)(indexIsFormula ? sn.get(i) : Integer.valueOf(i)));
            }
            end = this.list.size();
        } else {
            end = sn.size();
        }
        for (i = 0; i < end; ++i) {
            Object item = this.list.get(i);
            Object snapshotItem = sn.get(i);
            if (item != null || snapshotItem == null) continue;
            deletes.add((Integer)(indexIsFormula ? snapshotItem : Integer.valueOf(i)));
        }
        return deletes.iterator();
    }

    @Override
    public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
        List sn = (List)((Object)this.getSnapshot());
        return this.list.get(i) != null && (i >= sn.size() || sn.get(i) == null);
    }

    @Override
    public boolean needsUpdating(Object entry, int i, Type elemType) throws HibernateException {
        List sn = (List)((Object)this.getSnapshot());
        return i < sn.size() && sn.get(i) != null && this.list.get(i) != null && elemType.isDirty(this.list.get(i), sn.get(i), this.getSession());
    }

    @Override
    public Object getIndex(Object entry, int i, CollectionPersister persister) {
        return i;
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

    @Override
    public boolean equals(Object other) {
        this.read();
        return this.list.equals(other);
    }

    @Override
    public int hashCode() {
        this.read();
        return this.list.hashCode();
    }

    @Override
    public boolean entryExists(Object entry, int i) {
        return entry != null;
    }

    final class SimpleRemove
    extends AbstractPersistentCollection.AbstractValueDelayedOperation {
        public SimpleRemove(Object orphan) {
            super(null, orphan);
        }

        @Override
        public void operate() {
            PersistentList.this.list.remove(this.getOrphan());
        }
    }

    final class Remove
    extends AbstractListValueDelayedOperation {
        public Remove(int index, Object orphan) {
            super(index, null, orphan);
        }

        @Override
        public void operate() {
            PersistentList.this.list.remove(this.getIndex());
        }
    }

    final class Set
    extends AbstractListValueDelayedOperation {
        public Set(int index, Object addedValue, Object orphan) {
            super(index, addedValue, orphan);
        }

        @Override
        public void operate() {
            PersistentList.this.list.set(this.getIndex(), this.getAddedInstance());
        }
    }

    final class Add
    extends AbstractListValueDelayedOperation {
        public Add(int index, Object addedValue) {
            super(index, addedValue, null);
        }

        @Override
        public void operate() {
            PersistentList.this.list.add(this.getIndex(), this.getAddedInstance());
        }
    }

    abstract class AbstractListValueDelayedOperation
    extends AbstractPersistentCollection.AbstractValueDelayedOperation {
        private int index;

        AbstractListValueDelayedOperation(Integer index, Object addedValue, Object orphan) {
            super(addedValue, orphan);
            this.index = index;
        }

        protected final int getIndex() {
            return this.index;
        }
    }

    final class SimpleAdd
    extends AbstractPersistentCollection.AbstractValueDelayedOperation {
        public SimpleAdd(Object addedValue) {
            super(addedValue, null);
        }

        @Override
        public void operate() {
            PersistentList.this.list.add(this.getAddedInstance());
        }
    }

    final class Clear
    implements AbstractPersistentCollection.DelayedOperation {
        Clear() {
        }

        @Override
        public void operate() {
            PersistentList.this.list.clear();
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

