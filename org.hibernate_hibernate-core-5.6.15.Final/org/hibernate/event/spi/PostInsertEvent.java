/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.EventSource;
import org.hibernate.persister.entity.EntityPersister;

public class PostInsertEvent
extends AbstractEvent {
    private Object entity;
    private EntityPersister persister;
    private Object[] state;
    private Serializable id;

    public PostInsertEvent(Object entity, Serializable id, Object[] state, EntityPersister persister, EventSource source) {
        super(source);
        this.entity = entity;
        this.id = id;
        this.state = state;
        this.persister = persister;
    }

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
}

