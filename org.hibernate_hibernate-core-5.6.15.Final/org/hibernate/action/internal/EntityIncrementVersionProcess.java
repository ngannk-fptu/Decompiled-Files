/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.internal;

import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;

public class EntityIncrementVersionProcess
implements BeforeTransactionCompletionProcess {
    private final Object object;

    public EntityIncrementVersionProcess(Object object) {
        this.object = object;
    }

    @Override
    public void doBeforeTransactionCompletion(SessionImplementor session) {
        EntityEntry entry = session.getPersistenceContext().getEntry(this.object);
        if (entry == null) {
            return;
        }
        EntityPersister persister = entry.getPersister();
        Object nextVersion = persister.forceVersionIncrement(entry.getId(), entry.getVersion(), session);
        entry.forceLocked(this.object, nextVersion);
    }
}

