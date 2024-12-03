/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.internal;

import org.hibernate.OptimisticLockException;
import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;

public class EntityVerifyVersionProcess
implements BeforeTransactionCompletionProcess {
    private final Object object;

    public EntityVerifyVersionProcess(Object object) {
        this.object = object;
    }

    @Override
    public void doBeforeTransactionCompletion(SessionImplementor session) {
        EntityEntry entry = session.getPersistenceContext().getEntry(this.object);
        if (entry == null) {
            return;
        }
        EntityPersister persister = entry.getPersister();
        Object latestVersion = persister.getCurrentVersion(entry.getId(), session);
        if (!entry.getVersion().equals(latestVersion)) {
            throw new OptimisticLockException(this.object, "Newer version [" + latestVersion + "] of entity [" + MessageHelper.infoString(entry.getEntityName(), entry.getId()) + "] found in database");
        }
    }
}

