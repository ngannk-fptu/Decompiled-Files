/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.lock;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.action.internal.EntityIncrementVersionProcess;
import org.hibernate.dialect.lock.LockingStrategy;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.persister.entity.Lockable;

public class OptimisticForceIncrementLockingStrategy
implements LockingStrategy {
    private final Lockable lockable;
    private final LockMode lockMode;

    public OptimisticForceIncrementLockingStrategy(Lockable lockable, LockMode lockMode) {
        this.lockable = lockable;
        this.lockMode = lockMode;
        if (lockMode.lessThan(LockMode.OPTIMISTIC_FORCE_INCREMENT)) {
            throw new HibernateException("[" + (Object)((Object)lockMode) + "] not valid for [" + lockable.getEntityName() + "]");
        }
    }

    @Override
    public void lock(Serializable id, Object version, Object object, int timeout, SharedSessionContractImplementor session) {
        if (!this.lockable.isVersioned()) {
            throw new HibernateException("[" + (Object)((Object)this.lockMode) + "] not supported for non-versioned entities [" + this.lockable.getEntityName() + "]");
        }
        EntityEntry entry = session.getPersistenceContextInternal().getEntry(object);
        ((EventSource)session).getActionQueue().registerProcess(new EntityIncrementVersionProcess(object));
    }

    protected LockMode getLockMode() {
        return this.lockMode;
    }
}

