/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.collection.internal;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class PersistentArrayHolder
extends AbstractPersistentCollection {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)PersistentArrayHolder.class.getName());
    protected Object array;
    private transient Class elementClass;
    private transient List tempList;

    public PersistentArrayHolder(SharedSessionContractImplementor session, Object array) {
        super(session);
        this.array = array;
        this.setInitialized();
    }

    @Deprecated
    public PersistentArrayHolder(SessionImplementor session, Object array) {
        this((SharedSessionContractImplementor)session, array);
    }

    public PersistentArrayHolder(SharedSessionContractImplementor session, CollectionPersister persister) {
        super(session);
        this.elementClass = persister.getElementClass();
    }

    @Deprecated
    public PersistentArrayHolder(SessionImplementor session, CollectionPersister persister) {
        this((SharedSessionContractImplementor)session, persister);
    }

    @Override
    public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
        int length = Array.getLength(this.array);
        Serializable result = (Serializable)Array.newInstance(persister.getElementClass(), length);
        for (int i = 0; i < length; ++i) {
            Object elt = Array.get(this.array, i);
            try {
                Array.set(result, i, persister.getElementType().deepCopy(elt, persister.getFactory()));
                continue;
            }
            catch (IllegalArgumentException iae) {
                LOG.invalidArrayElementType(iae.getMessage());
                throw new HibernateException("Array element type error", iae);
            }
        }
        return result;
    }

    @Override
    public boolean isSnapshotEmpty(Serializable snapshot) {
        return Array.getLength(snapshot) == 0;
    }

    @Override
    public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
        Object[] sn = (Object[])snapshot;
        Object[] arr = (Object[])this.array;
        ArrayList result = new ArrayList();
        Collections.addAll(result, sn);
        for (int i = 0; i < sn.length; ++i) {
            PersistentArrayHolder.identityRemove(result, arr[i], entityName, this.getSession());
        }
        return result;
    }

    public Object getArray() {
        return this.array;
    }

    @Override
    public boolean isWrapper(Object collection) {
        return this.array == collection;
    }

    @Override
    public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
        Type elementType = persister.getElementType();
        Serializable snapshot = this.getSnapshot();
        int xlen = Array.getLength(snapshot);
        if (xlen != Array.getLength(this.array)) {
            return false;
        }
        for (int i = 0; i < xlen; ++i) {
            if (!elementType.isDirty(Array.get(snapshot, i), Array.get(this.array, i), this.getSession())) continue;
            return false;
        }
        return true;
    }

    public Iterator elements() {
        int length = Array.getLength(this.array);
        ArrayList<Object> list = new ArrayList<Object>(length);
        for (int i = 0; i < length; ++i) {
            list.add(Array.get(this.array, i));
        }
        return list.iterator();
    }

    @Override
    public boolean empty() {
        return false;
    }

    @Override
    public Object readFrom(ResultSet rs, CollectionPersister persister, CollectionAliases descriptor, Object owner) throws HibernateException, SQLException {
        Object element = persister.readElement(rs, owner, descriptor.getSuffixedElementAliases(), this.getSession());
        int index = (Integer)persister.readIndex(rs, descriptor.getSuffixedIndexAliases(), this.getSession());
        for (int i = this.tempList.size(); i <= index; ++i) {
            this.tempList.add(i, null);
        }
        this.tempList.set(index, element);
        return element;
    }

    @Override
    public Iterator entries(CollectionPersister persister) {
        return this.elements();
    }

    @Override
    public void beginRead() {
        super.beginRead();
        this.tempList = new ArrayList();
    }

    @Override
    public boolean endRead() {
        this.setInitialized();
        this.array = Array.newInstance(this.elementClass, this.tempList.size());
        for (int i = 0; i < this.tempList.size(); ++i) {
            Array.set(this.array, i, this.tempList.get(i));
        }
        this.tempList = null;
        return true;
    }

    @Override
    public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
    }

    @Override
    public boolean isDirectlyAccessible() {
        return true;
    }

    @Override
    public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner) throws HibernateException {
        Serializable[] cached = (Serializable[])disassembled;
        this.array = Array.newInstance(persister.getElementClass(), cached.length);
        for (int i = 0; i < cached.length; ++i) {
            Array.set(this.array, i, persister.getElementType().assemble(cached[i], this.getSession(), owner));
        }
    }

    @Override
    public Serializable disassemble(CollectionPersister persister) throws HibernateException {
        int length = Array.getLength(this.array);
        Serializable[] result = new Serializable[length];
        for (int i = 0; i < length; ++i) {
            result[i] = persister.getElementType().disassemble(Array.get(this.array, i), this.getSession(), null);
        }
        return result;
    }

    @Override
    public Object getValue() {
        return this.array;
    }

    @Override
    public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
        int end;
        int i;
        int arraySize;
        ArrayList<Integer> deletes = new ArrayList<Integer>();
        Serializable sn = this.getSnapshot();
        int snSize = Array.getLength(sn);
        if (snSize > (arraySize = Array.getLength(this.array))) {
            for (i = arraySize; i < snSize; ++i) {
                deletes.add(i);
            }
            end = arraySize;
        } else {
            end = snSize;
        }
        for (i = 0; i < end; ++i) {
            if (Array.get(this.array, i) != null || Array.get(sn, i) == null) continue;
            deletes.add(i);
        }
        return deletes.iterator();
    }

    @Override
    public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
        Serializable sn = this.getSnapshot();
        return Array.get(this.array, i) != null && (i >= Array.getLength(sn) || Array.get(sn, i) == null);
    }

    @Override
    public boolean needsUpdating(Object entry, int i, Type elemType) throws HibernateException {
        Serializable sn = this.getSnapshot();
        return i < Array.getLength(sn) && Array.get(sn, i) != null && Array.get(this.array, i) != null && elemType.isDirty(Array.get(this.array, i), Array.get(sn, i), this.getSession());
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
        Serializable sn = this.getSnapshot();
        return Array.get(sn, i);
    }

    @Override
    public boolean entryExists(Object entry, int i) {
        return entry != null;
    }
}

