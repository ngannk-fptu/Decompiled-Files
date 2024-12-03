/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.AbstractPreDatabaseOperationEvent;
import org.hibernate.event.spi.EventSource;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.secure.spi.PermissionCheckEntityInformation;

public class PreDeleteEvent
extends AbstractPreDatabaseOperationEvent
implements PermissionCheckEntityInformation {
    private Object[] deletedState;

    public PreDeleteEvent(Object entity, Serializable id, Object[] deletedState, EntityPersister persister, EventSource source) {
        super(source, entity, id, persister);
        this.deletedState = deletedState;
    }

    public Object[] getDeletedState() {
        return this.deletedState;
    }
}

