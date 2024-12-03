/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.ReplicationMode;
import org.hibernate.TransientObjectException;
import org.hibernate.engine.internal.Cascade;
import org.hibernate.engine.internal.CascadePoint;
import org.hibernate.engine.spi.CascadingAction;
import org.hibernate.engine.spi.CascadingActions;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.internal.AbstractSaveEventListener;
import org.hibernate.event.internal.OnReplicateVisitor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.ReplicateEvent;
import org.hibernate.event.spi.ReplicateEventListener;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.Type;

public class DefaultReplicateEventListener
extends AbstractSaveEventListener
implements ReplicateEventListener {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DefaultReplicateEventListener.class);

    @Override
    public void onReplicate(ReplicateEvent event) {
        EventSource source = event.getSession();
        PersistenceContext persistenceContext = source.getPersistenceContextInternal();
        if (persistenceContext.reassociateIfUninitializedProxy(event.getObject())) {
            LOG.trace("Uninitialized proxy passed to replicate()");
            return;
        }
        Object entity = persistenceContext.unproxyAndReassociate(event.getObject());
        if (persistenceContext.isEntryFor(entity)) {
            LOG.trace("Ignoring persistent instance passed to replicate()");
            return;
        }
        EntityPersister persister = source.getEntityPersister(event.getEntityName(), entity);
        Serializable id = persister.getIdentifier(entity, source);
        if (id == null) {
            throw new TransientObjectException("instance with null id passed to replicate()");
        }
        ReplicationMode replicationMode = event.getReplicationMode();
        Object oldVersion = replicationMode == ReplicationMode.EXCEPTION ? null : persister.getCurrentVersion(id, source);
        if (oldVersion != null) {
            Object realOldVersion;
            boolean canReplicate;
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Found existing row for {0}", MessageHelper.infoString(persister, id, source.getFactory()));
            }
            if (canReplicate = replicationMode.shouldOverwriteCurrentVersion(entity, realOldVersion = persister.isVersioned() ? oldVersion : null, persister.getVersion(entity), persister.getVersionType())) {
                this.performReplication(entity, id, realOldVersion, persister, replicationMode, source);
            } else if (LOG.isTraceEnabled()) {
                LOG.trace("No need to replicate");
            }
        } else {
            boolean regenerate;
            if (LOG.isTraceEnabled()) {
                LOG.tracev("No existing row, replicating new instance {0}", MessageHelper.infoString(persister, id, source.getFactory()));
            }
            EntityKey key = (regenerate = persister.isIdentifierAssignedByInsert()) ? null : source.generateEntityKey(id, persister);
            this.performSaveOrReplicate(entity, key, persister, regenerate, (Object)replicationMode, source, true);
        }
    }

    @Override
    protected boolean visitCollectionsBeforeSave(Object entity, Serializable id, Object[] values, Type[] types, EventSource source) {
        OnReplicateVisitor visitor = new OnReplicateVisitor(source, id, entity, false);
        visitor.processEntityPropertyValues(values, types);
        return super.visitCollectionsBeforeSave(entity, id, values, types, source);
    }

    @Override
    protected boolean substituteValuesIfNecessary(Object entity, Serializable id, Object[] values, EntityPersister persister, SessionImplementor source) {
        return false;
    }

    @Override
    protected boolean isVersionIncrementDisabled() {
        return true;
    }

    private void performReplication(Object entity, Serializable id, Object version, EntityPersister persister, ReplicationMode replicationMode, EventSource source) throws HibernateException {
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Replicating changes to {0}", MessageHelper.infoString(persister, id, source.getFactory()));
        }
        new OnReplicateVisitor(source, id, entity, true).process(entity, persister);
        source.getPersistenceContextInternal().addEntity(entity, persister.isMutable() ? Status.MANAGED : Status.READ_ONLY, null, source.generateEntityKey(id, persister), version, LockMode.NONE, true, persister, true);
        this.cascadeAfterReplicate(entity, persister, replicationMode, source);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void cascadeAfterReplicate(Object entity, EntityPersister persister, ReplicationMode replicationMode, EventSource source) {
        PersistenceContext persistenceContext = source.getPersistenceContextInternal();
        persistenceContext.incrementCascadeLevel();
        try {
            Cascade.cascade(CascadingActions.REPLICATE, CascadePoint.AFTER_UPDATE, source, persister, entity, (Object)replicationMode);
        }
        finally {
            persistenceContext.decrementCascadeLevel();
        }
    }

    @Override
    protected CascadingAction getCascadeAction() {
        return CascadingActions.REPLICATE;
    }
}

