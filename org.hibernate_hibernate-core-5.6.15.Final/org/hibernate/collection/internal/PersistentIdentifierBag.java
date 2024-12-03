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
import java.util.ListIterator;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.Type;

public class PersistentIdentifierBag
extends AbstractPersistentCollection
implements List {
    protected List<Object> values;
    protected Map<Integer, Object> identifiers;
    private Collection providedValues;

    public PersistentIdentifierBag() {
    }

    public PersistentIdentifierBag(SharedSessionContractImplementor session) {
        super(session);
    }

    @Deprecated
    public PersistentIdentifierBag(SessionImplementor session) {
        this((SharedSessionContractImplementor)session);
    }

    public PersistentIdentifierBag(SharedSessionContractImplementor session, Collection coll) {
        super(session);
        this.providedValues = coll;
        this.values = coll instanceof List ? (List<Object>)coll : new ArrayList<Object>(coll);
        this.setInitialized();
        this.setDirectlyAccessible(true);
        this.identifiers = new HashMap<Integer, Object>();
    }

    @Deprecated
    public PersistentIdentifierBag(SessionImplementor session, Collection coll) {
        this((SharedSessionContractImplementor)session, coll);
    }

    @Override
    public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner) throws HibernateException {
        Serializable[] array = (Serializable[])disassembled;
        int size = array.length;
        this.beforeInitialize(persister, size);
        for (int i = 0; i < size; i += 2) {
            this.identifiers.put(i / 2, persister.getIdentifierType().assemble(array[i], this.getSession(), owner));
            this.values.add(persister.getElementType().assemble(array[i + 1], this.getSession(), owner));
        }
    }

    @Override
    public Object getIdentifier(Object entry, int i) {
        return this.identifiers.get(i);
    }

    @Override
    public boolean isWrapper(Object collection) {
        return this.values == collection;
    }

    @Override
    public boolean isDirectlyProvidedCollection(Object collection) {
        return this.isDirectlyAccessible() && this.providedValues == collection;
    }

    @Override
    public boolean add(Object o) {
        this.write();
        this.values.add(o);
        return true;
    }

    @Override
    public void clear() {
        this.initialize(true);
        if (!this.values.isEmpty() || !this.identifiers.isEmpty()) {
            this.values.clear();
            this.identifiers.clear();
            this.dirty();
        }
    }

    @Override
    public boolean contains(Object o) {
        this.read();
        return this.values.contains(o);
    }

    @Override
    public boolean containsAll(Collection c) {
        this.read();
        return this.values.containsAll(c);
    }

    @Override
    public boolean isEmpty() {
        return this.readSize() ? this.getCachedSize() == 0 : this.values.isEmpty();
    }

    @Override
    public Iterator iterator() {
        this.read();
        return new AbstractPersistentCollection.IteratorProxy(this.values.iterator());
    }

    @Override
    public boolean remove(Object o) {
        this.initialize(true);
        int index = this.values.indexOf(o);
        if (index >= 0) {
            this.beforeRemove(index);
            this.values.remove(index);
            this.elementRemoved = true;
            this.dirty();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection c) {
        if (c.size() > 0) {
            boolean result = false;
            for (Object element : c) {
                if (!this.remove(element)) continue;
                result = true;
            }
            return result;
        }
        return false;
    }

    @Override
    public boolean retainAll(Collection c) {
        this.initialize(true);
        if (this.values.retainAll(c)) {
            this.dirty();
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        return this.readSize() ? this.getCachedSize() : this.values.size();
    }

    @Override
    public Object[] toArray() {
        this.read();
        return this.values.toArray();
    }

    @Override
    public Object[] toArray(Object[] a) {
        this.read();
        return this.values.toArray(a);
    }

    @Override
    public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
        this.identifiers = anticipatedSize <= 0 ? new HashMap<Integer, Object>() : new HashMap(anticipatedSize + 1 + (int)((float)anticipatedSize * 0.75f), 0.75f);
        this.values = anticipatedSize <= 0 ? new ArrayList<Object>() : new ArrayList(anticipatedSize);
    }

    @Override
    public Serializable disassemble(CollectionPersister persister) throws HibernateException {
        Serializable[] result = new Serializable[this.values.size() * 2];
        int i = 0;
        for (int j = 0; j < this.values.size(); ++j) {
            Object value = this.values.get(j);
            result[i++] = persister.getIdentifierType().disassemble(this.identifiers.get(j), this.getSession(), null);
            result[i++] = persister.getElementType().disassemble(value, this.getSession(), null);
        }
        return result;
    }

    @Override
    public boolean empty() {
        return this.values.isEmpty();
    }

    @Override
    public Iterator entries(CollectionPersister persister) {
        return this.values.iterator();
    }

    @Override
    public boolean entryExists(Object entry, int i) {
        return entry != null;
    }

    @Override
    public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
        Type elementType = persister.getElementType();
        Map snap = (Map)((Object)this.getSnapshot());
        if (snap.size() != this.values.size()) {
            return false;
        }
        for (int i = 0; i < this.values.size(); ++i) {
            Object value = this.values.get(i);
            Object id = this.identifiers.get(i);
            if (id == null) {
                return false;
            }
            Object old = snap.get(id);
            if (!elementType.isDirty(old, value, this.getSession())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isSnapshotEmpty(Serializable snapshot) {
        return ((Map)((Object)snapshot)).isEmpty();
    }

    @Override
    public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
        Map snap = (Map)((Object)this.getSnapshot());
        ArrayList deletes = new ArrayList(snap.keySet());
        for (int i = 0; i < this.values.size(); ++i) {
            if (this.values.get(i) == null) continue;
            deletes.remove(this.identifiers.get(i));
        }
        return deletes.iterator();
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
        Map snap = (Map)((Object)this.getSnapshot());
        Object id = this.identifiers.get(i);
        return snap.get(id);
    }

    @Override
    public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
        Map snap = (Map)((Object)this.getSnapshot());
        Object id = this.identifiers.get(i);
        return entry != null && (id == null || snap.get(id) == null);
    }

    @Override
    public boolean needsUpdating(Object entry, int i, Type elemType) throws HibernateException {
        if (entry == null) {
            return false;
        }
        Map snap = (Map)((Object)this.getSnapshot());
        Object id = this.identifiers.get(i);
        if (id == null) {
            return false;
        }
        Object old = snap.get(id);
        return old != null && elemType.isDirty(old, entry, this.getSession());
    }

    @Override
    public Object readFrom(ResultSet rs, CollectionPersister persister, CollectionAliases descriptor, Object owner) throws HibernateException, SQLException {
        Object element = persister.readElement(rs, owner, descriptor.getSuffixedElementAliases(), this.getSession());
        Object old = this.identifiers.put(this.values.size(), persister.readIdentifier(rs, descriptor.getSuffixedIdentifierAlias(), this.getSession()));
        if (old == null) {
            this.values.add(element);
        }
        return element;
    }

    @Override
    public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
        HashMap<Object, Object> map = new HashMap<Object, Object>(this.values.size());
        Iterator<Object> iter = this.values.iterator();
        int i = 0;
        while (iter.hasNext()) {
            Object value = iter.next();
            map.put(this.identifiers.get(i++), persister.getElementType().deepCopy(value, persister.getFactory()));
        }
        return map;
    }

    @Override
    public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
        Map sn = (Map)((Object)snapshot);
        return PersistentIdentifierBag.getOrphans(sn.values(), this.values, entityName, this.getSession());
    }

    @Override
    public void preInsert(CollectionPersister persister) throws HibernateException {
        Iterator<Object> itr = this.values.iterator();
        int i = 0;
        while (itr.hasNext()) {
            Integer loc;
            Object entry = itr.next();
            if (this.identifiers.containsKey(loc = Integer.valueOf(i++))) continue;
            Serializable id = persister.getIdentifierGenerator().generate(this.getSession(), entry);
            this.identifiers.put(loc, id);
        }
    }

    public void add(int index, Object element) {
        this.write();
        this.beforeAdd(index);
        this.values.add(index, element);
    }

    public boolean addAll(int index, Collection c) {
        if (c.size() > 0) {
            for (Object element : c) {
                this.add(index++, element);
            }
            return true;
        }
        return false;
    }

    public Object get(int index) {
        this.read();
        return this.values.get(index);
    }

    @Override
    public int indexOf(Object o) {
        this.read();
        return this.values.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        this.read();
        return this.values.lastIndexOf(o);
    }

    public ListIterator listIterator() {
        this.read();
        return new AbstractPersistentCollection.ListIteratorProxy(this.values.listIterator());
    }

    public ListIterator listIterator(int index) {
        this.read();
        return new AbstractPersistentCollection.ListIteratorProxy(this.values.listIterator(index));
    }

    private void beforeRemove(int index) {
        Object removedId = this.identifiers.get(index);
        int last = this.values.size() - 1;
        for (int i = index; i < last; ++i) {
            Object id = this.identifiers.get(i + 1);
            if (id == null) {
                this.identifiers.remove(i);
                continue;
            }
            this.identifiers.put(i, id);
        }
        this.identifiers.put(last, removedId);
    }

    private void beforeAdd(int index) {
        for (int i = index; i < this.values.size(); ++i) {
            this.identifiers.put(i + 1, this.identifiers.get(i));
        }
        this.identifiers.remove(index);
    }

    public Object remove(int index) {
        this.write();
        this.beforeRemove(index);
        return this.values.remove(index);
    }

    public Object set(int index, Object element) {
        this.write();
        return this.values.set(index, element);
    }

    public List subList(int fromIndex, int toIndex) {
        this.read();
        return new AbstractPersistentCollection.ListProxy(this.values.subList(fromIndex, toIndex));
    }

    @Override
    public boolean addAll(Collection c) {
        if (c.size() > 0) {
            this.write();
            return this.values.addAll(c);
        }
        return false;
    }

    @Override
    public void afterRowInsert(CollectionPersister persister, Object entry, int i) throws HibernateException {
    }
}

