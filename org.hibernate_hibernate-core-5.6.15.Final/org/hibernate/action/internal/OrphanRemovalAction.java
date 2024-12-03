/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.internal;

import java.io.Serializable;
import org.hibernate.action.internal.EntityDeleteAction;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;

public final class OrphanRemovalAction
extends EntityDeleteAction {
    public OrphanRemovalAction(Serializable id, Object[] state, Object version, Object instance, EntityPersister persister, boolean isCascadeDeleteEnabled, SessionImplementor session) {
        super(id, state, version, instance, persister, isCascadeDeleteEnabled, session);
    }
}

