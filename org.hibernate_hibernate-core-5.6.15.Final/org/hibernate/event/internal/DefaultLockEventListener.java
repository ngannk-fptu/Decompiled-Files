/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.TransientObjectException;
import org.hibernate.engine.internal.Cascade;
import org.hibernate.engine.internal.CascadePoint;
import org.hibernate.engine.internal.ForeignKeys;
import org.hibernate.engine.spi.CascadingActions;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.event.internal.AbstractLockUpgradeEventListener;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.LockEvent;
import org.hibernate.event.spi.LockEventListener;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.EntityPersister;
import org.jboss.logging.Logger;

public class DefaultLockEventListener
extends AbstractLockUpgradeEventListener
implements LockEventListener {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)DefaultLockEventListener.class.getName());

    @Override
    public void onLock(LockEvent event) throws HibernateException {
        Object entity;
        EventSource source;
        PersistenceContext persistenceContext;
        EntityEntry entry;
        if (event.getObject() == null) {
            throw new NullPointerException("attempted to lock null");
        }
        if (event.getLockMode() == LockMode.WRITE) {
            throw new HibernateException("Invalid lock mode for lock()");
        }
        if (event.getLockMode() == LockMode.UPGRADE_SKIPLOCKED) {
            LOG.explicitSkipLockedLockCombo();
        }
        if ((entry = (persistenceContext = (source = event.getSession()).getPersistenceContextInternal()).getEntry(entity = persistenceContext.unproxyAndReassociate(event.getObject()))) == null) {
            EntityPersister persister = source.getEntityPersister(event.getEntityName(), entity);
            Serializable id = persister.getIdentifier(entity, source);
            if (!ForeignKeys.isNotTransient(event.getEntityName(), entity, Boolean.FALSE, source)) {
                throw new TransientObjectException("cannot lock an unsaved transient instance: " + persister.getEntityName());
            }
            entry = this.reassociate(event, entity, id, persister);
            this.cascadeOnLock(event, persister, entity);
        }
        this.upgradeLock(entity, entry, event.getLockOptions(), event.getSession());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void cascadeOnLock(LockEvent event, EntityPersister persister, Object entity) {
        EventSource source = event.getSession();
        PersistenceContext persistenceContext = source.getPersistenceContextInternal();
        persistenceContext.incrementCascadeLevel();
        try {
            Cascade.cascade(CascadingActions.LOCK, CascadePoint.AFTER_LOCK, source, persister, entity, event.getLockOptions());
        }
        finally {
            persistenceContext.decrementCascadeLevel();
        }
    }
}

