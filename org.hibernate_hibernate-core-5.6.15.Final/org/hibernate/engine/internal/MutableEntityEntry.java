/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.hibernate.EntityMode;
import org.hibernate.LockMode;
import org.hibernate.engine.internal.AbstractEntityEntry;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.persister.entity.EntityPersister;

public final class MutableEntityEntry
extends AbstractEntityEntry {
    @Deprecated
    public MutableEntityEntry(Status status, Object[] loadedState, Object rowId, Serializable id, Object version, LockMode lockMode, boolean existsInDatabase, EntityPersister persister, EntityMode entityMode, String tenantId, boolean disableVersionIncrement, PersistenceContext persistenceContext) {
        this(status, loadedState, rowId, id, version, lockMode, existsInDatabase, persister, disableVersionIncrement, persistenceContext);
    }

    public MutableEntityEntry(Status status, Object[] loadedState, Object rowId, Serializable id, Object version, LockMode lockMode, boolean existsInDatabase, EntityPersister persister, boolean disableVersionIncrement, PersistenceContext persistenceContext) {
        super(status, loadedState, rowId, id, version, lockMode, existsInDatabase, persister, disableVersionIncrement, persistenceContext);
    }

    private MutableEntityEntry(SessionFactoryImplementor factory, String entityName, Serializable id, Status status, Status previousStatus, Object[] loadedState, Object[] deletedState, Object version, LockMode lockMode, boolean existsInDatabase, boolean isBeingReplicated, PersistenceContext persistenceContext) {
        super(factory, entityName, id, status, previousStatus, loadedState, deletedState, version, lockMode, existsInDatabase, isBeingReplicated, persistenceContext);
    }

    public static EntityEntry deserialize(ObjectInputStream ois, PersistenceContext persistenceContext) throws IOException, ClassNotFoundException {
        String previousStatusString;
        return new MutableEntityEntry(persistenceContext.getSession().getFactory(), (String)ois.readObject(), (Serializable)ois.readObject(), Status.valueOf((String)ois.readObject()), (previousStatusString = (String)ois.readObject()).length() == 0 ? null : Status.valueOf(previousStatusString), (Object[])ois.readObject(), (Object[])ois.readObject(), ois.readObject(), LockMode.valueOf((String)ois.readObject()), ois.readBoolean(), ois.readBoolean(), persistenceContext);
    }
}

