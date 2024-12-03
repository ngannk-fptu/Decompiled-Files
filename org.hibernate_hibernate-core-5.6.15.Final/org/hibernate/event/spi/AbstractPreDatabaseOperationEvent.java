/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.EventSource;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.secure.spi.PermissionCheckEntityInformation;

public abstract class AbstractPreDatabaseOperationEvent
extends AbstractEvent
implements PermissionCheckEntityInformation {
    private final Object entity;
    private final Serializable id;
    private final EntityPersister persister;

    public AbstractPreDatabaseOperationEvent(EventSource source, Object entity, Serializable id, EntityPersister persister) {
        super(source);
        this.entity = entity;
        this.id = id;
        this.persister = persister;
    }

    @Override
    public Object getEntity() {
        return this.entity;
    }

    public Serializable getId() {
        return this.id;
    }

    public EntityPersister getPersister() {
        return this.persister;
    }

    @Deprecated
    public EventSource getSource() {
        return this.getSession();
    }

    @Override
    @Deprecated
    public String getEntityName() {
        return this.persister.getEntityName();
    }

    @Override
    @Deprecated
    public Serializable getIdentifier() {
        return this.id;
    }
}

