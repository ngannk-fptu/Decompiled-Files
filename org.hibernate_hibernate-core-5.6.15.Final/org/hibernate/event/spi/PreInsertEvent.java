/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.AbstractPreDatabaseOperationEvent;
import org.hibernate.event.spi.EventSource;
import org.hibernate.persister.entity.EntityPersister;

public class PreInsertEvent
extends AbstractPreDatabaseOperationEvent {
    private Object[] state;

    public PreInsertEvent(Object entity, Serializable id, Object[] state, EntityPersister persister, EventSource source) {
        super(source, entity, id, persister);
        this.state = state;
    }

    public Object[] getState() {
        return this.state;
    }
}

