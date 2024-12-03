/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.ObjectDeletedException;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.internal.DefaultSaveOrUpdateEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.hibernate.persister.entity.EntityPersister;

public class DefaultUpdateEventListener
extends DefaultSaveOrUpdateEventListener {
    @Override
    protected Serializable performSaveOrUpdate(SaveOrUpdateEvent event) {
        EntityEntry entry = event.getSession().getPersistenceContextInternal().getEntry(event.getEntity());
        if (entry != null) {
            if (entry.getStatus() == Status.DELETED) {
                throw new ObjectDeletedException("deleted instance passed to update()", null, event.getEntityName());
            }
            return this.entityIsPersistent(event);
        }
        this.entityIsDetached(event);
        return null;
    }

    @Override
    protected Serializable getUpdateId(Object entity, EntityPersister persister, Serializable requestedId, SessionImplementor session) throws HibernateException {
        if (requestedId == null) {
            return super.getUpdateId(entity, persister, requestedId, session);
        }
        persister.setIdentifier(entity, requestedId, session);
        return requestedId;
    }
}

