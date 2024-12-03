/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.event.internal;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.ObjectDeletedException;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.internal.AbstractReassociateEventListener;
import org.hibernate.event.spi.EventSource;
import org.hibernate.internal.CoreLogging;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.jboss.logging.Logger;

public abstract class AbstractLockUpgradeEventListener
extends AbstractReassociateEventListener {
    private static final Logger log = CoreLogging.logger(AbstractLockUpgradeEventListener.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected void upgradeLock(Object object, EntityEntry entry, LockOptions lockOptions, EventSource source) {
        LockMode requestedLockMode = lockOptions.getLockMode();
        if (!requestedLockMode.greaterThan(entry.getLockMode())) return;
        if (entry.getStatus() != Status.MANAGED) {
            throw new ObjectDeletedException("attempted to lock a deleted instance", entry.getId(), entry.getPersister().getEntityName());
        }
        EntityPersister persister = entry.getPersister();
        if (log.isTraceEnabled()) {
            log.tracev("Locking {0} in mode: {1}", (Object)MessageHelper.infoString(persister, entry.getId(), source.getFactory()), (Object)requestedLockMode);
        }
        boolean cachingEnabled = persister.canWriteToCache();
        SoftLock lock = null;
        Object ck = null;
        try {
            if (cachingEnabled) {
                EntityDataAccess cache = persister.getCacheAccessStrategy();
                ck = cache.generateCacheKey(entry.getId(), persister, source.getFactory(), source.getTenantIdentifier());
                lock = cache.lockItem(source, ck, entry.getVersion());
            }
            if (persister.isVersioned() && requestedLockMode == LockMode.FORCE) {
                Object nextVersion = persister.forceVersionIncrement(entry.getId(), entry.getVersion(), source);
                entry.forceLocked(object, nextVersion);
            } else {
                persister.lock(entry.getId(), entry.getVersion(), object, lockOptions, (SharedSessionContractImplementor)source);
            }
            entry.setLockMode(requestedLockMode);
            if (!cachingEnabled) return;
        }
        catch (Throwable throwable) {
            if (!cachingEnabled) throw throwable;
            persister.getCacheAccessStrategy().unlockItem(source, ck, lock);
            throw throwable;
        }
        persister.getCacheAccessStrategy().unlockItem(source, ck, lock);
    }
}

