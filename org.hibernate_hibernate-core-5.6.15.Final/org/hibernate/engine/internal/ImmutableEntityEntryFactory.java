/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.internal;

import java.io.Serializable;
import org.hibernate.LockMode;
import org.hibernate.engine.internal.ImmutableEntityEntry;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityEntryFactory;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.Status;
import org.hibernate.persister.entity.EntityPersister;

public class ImmutableEntityEntryFactory
implements EntityEntryFactory {
    public static final ImmutableEntityEntryFactory INSTANCE = new ImmutableEntityEntryFactory();

    private ImmutableEntityEntryFactory() {
    }

    @Override
    public EntityEntry createEntityEntry(Status status, Object[] loadedState, Object rowId, Serializable id, Object version, LockMode lockMode, boolean existsInDatabase, EntityPersister persister, boolean disableVersionIncrement, PersistenceContext persistenceContext) {
        return new ImmutableEntityEntry(status, loadedState, rowId, id, version, lockMode, existsInDatabase, persister, disableVersionIncrement, persistenceContext);
    }
}

