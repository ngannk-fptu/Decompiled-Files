/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.hibernate.LockMode;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.EntityEntryExtraState;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.persister.entity.EntityPersister;

public interface EntityEntry {
    public LockMode getLockMode();

    public void setLockMode(LockMode var1);

    public Status getStatus();

    public void setStatus(Status var1);

    public Serializable getId();

    public Object[] getLoadedState();

    public Object getLoadedValue(String var1);

    public void overwriteLoadedStateCollectionValue(String var1, PersistentCollection var2);

    public Object[] getDeletedState();

    public void setDeletedState(Object[] var1);

    public boolean isExistsInDatabase();

    public Object getVersion();

    public EntityPersister getPersister();

    public EntityKey getEntityKey();

    public String getEntityName();

    public boolean isBeingReplicated();

    public Object getRowId();

    public void postUpdate(Object var1, Object[] var2, Object var3);

    public void postDelete();

    public void postInsert(Object[] var1);

    public boolean isNullifiable(boolean var1, SharedSessionContractImplementor var2);

    public boolean requiresDirtyCheck(Object var1);

    public boolean isModifiableEntity();

    public void forceLocked(Object var1, Object var2);

    public boolean isReadOnly();

    public void setReadOnly(boolean var1, Object var2);

    public String toString();

    public void serialize(ObjectOutputStream var1) throws IOException;

    public void addExtraState(EntityEntryExtraState var1);

    public <T extends EntityEntryExtraState> T getExtraState(Class<T> var1);
}

