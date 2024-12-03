/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.collection.spi;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import org.hibernate.HibernateException;
import org.hibernate.collection.spi.LazyInitializable;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.Type;

public interface PersistentCollection
extends LazyInitializable {
    public Object getOwner();

    public void setOwner(Object var1);

    public boolean empty();

    public void setSnapshot(Serializable var1, String var2, Serializable var3);

    public void postAction();

    public Object getValue();

    public void beginRead();

    public boolean endRead();

    public boolean afterInitialize();

    public boolean isDirectlyAccessible();

    public boolean unsetSession(SharedSessionContractImplementor var1);

    public boolean setCurrentSession(SharedSessionContractImplementor var1) throws HibernateException;

    public void initializeFromCache(CollectionPersister var1, Serializable var2, Object var3);

    public Iterator entries(CollectionPersister var1);

    public Object readFrom(ResultSet var1, CollectionPersister var2, CollectionAliases var3, Object var4) throws HibernateException, SQLException;

    public Object getIdentifier(Object var1, int var2);

    public Object getIndex(Object var1, int var2, CollectionPersister var3);

    public Object getElement(Object var1);

    public Object getSnapshotElement(Object var1, int var2);

    public void beforeInitialize(CollectionPersister var1, int var2);

    public boolean equalsSnapshot(CollectionPersister var1);

    public boolean isSnapshotEmpty(Serializable var1);

    public Serializable disassemble(CollectionPersister var1);

    public boolean needsRecreate(CollectionPersister var1);

    public Serializable getSnapshot(CollectionPersister var1);

    public boolean entryExists(Object var1, int var2);

    public boolean needsInserting(Object var1, int var2, Type var3);

    public boolean needsUpdating(Object var1, int var2, Type var3);

    public boolean isRowUpdatePossible();

    public Iterator getDeletes(CollectionPersister var1, boolean var2);

    public boolean isWrapper(Object var1);

    public boolean hasQueuedOperations();

    public Iterator queuedAdditionIterator();

    public Collection getQueuedOrphans(String var1);

    public Serializable getKey();

    public String getRole();

    public boolean isUnreferenced();

    public boolean isDirty();

    default public boolean isElementRemoved() {
        return false;
    }

    default public boolean isDirectlyProvidedCollection(Object collection) {
        return this.isDirectlyAccessible() && this.isWrapper(collection);
    }

    public void clearDirty();

    public Serializable getStoredSnapshot();

    public void dirty();

    public void preInsert(CollectionPersister var1);

    public void afterRowInsert(CollectionPersister var1, Object var2, int var3);

    public Collection getOrphans(Serializable var1, String var2);

    default public boolean isNewlyInstantiated() {
        return this.getKey() == null && !this.isDirty();
    }
}

