/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import org.hibernate.Hibernate;
import org.hibernate.PersistentObjectException;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.internal.DefaultSaveOrUpdateEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;

public class DefaultSaveEventListener
extends DefaultSaveOrUpdateEventListener {
    @Override
    protected Serializable performSaveOrUpdate(SaveOrUpdateEvent event) {
        EntityEntry entry = event.getSession().getPersistenceContextInternal().getEntry(event.getEntity());
        if (entry != null && entry.getStatus() != Status.DELETED) {
            return this.entityIsPersistent(event);
        }
        return this.entityIsTransient(event);
    }

    @Override
    protected Serializable saveWithGeneratedOrRequestedId(SaveOrUpdateEvent event) {
        if (event.getRequestedId() == null) {
            return super.saveWithGeneratedOrRequestedId(event);
        }
        return this.saveWithRequestedId(event.getEntity(), event.getRequestedId(), event.getEntityName(), null, event.getSession());
    }

    @Override
    protected boolean reassociateIfUninitializedProxy(Object object, SessionImplementor source) {
        if (!Hibernate.isInitialized(object)) {
            throw new PersistentObjectException("uninitialized proxy passed to save()");
        }
        return false;
    }
}

