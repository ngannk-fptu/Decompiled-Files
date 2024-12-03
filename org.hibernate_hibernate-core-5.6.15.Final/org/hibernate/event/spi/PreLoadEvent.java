/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.EventSource;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.secure.spi.PermissionCheckEntityInformation;

public class PreLoadEvent
extends AbstractEvent
implements PermissionCheckEntityInformation {
    private Object entity;
    private Object[] state;
    private Serializable id;
    private EntityPersister persister;

    public PreLoadEvent(EventSource session) {
        super(session);
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

    public Object[] getState() {
        return this.state;
    }

    public PreLoadEvent setEntity(Object entity) {
        this.entity = entity;
        return this;
    }

    public PreLoadEvent setId(Serializable id) {
        this.id = id;
        return this;
    }

    public PreLoadEvent setPersister(EntityPersister persister) {
        this.persister = persister;
        return this;
    }

    public PreLoadEvent setState(Object[] state) {
        this.state = state;
        return this;
    }

    @Override
    public String getEntityName() {
        return this.persister.getEntityName();
    }

    @Override
    public Serializable getIdentifier() {
        return this.id;
    }
}

