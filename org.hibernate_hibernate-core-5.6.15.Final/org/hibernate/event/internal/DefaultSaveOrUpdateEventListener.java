/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.PersistentObjectException;
import org.hibernate.TransientObjectException;
import org.hibernate.classic.Lifecycle;
import org.hibernate.engine.internal.Cascade;
import org.hibernate.engine.internal.CascadePoint;
import org.hibernate.engine.spi.CascadingAction;
import org.hibernate.engine.spi.CascadingActions;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.internal.AbstractSaveEventListener;
import org.hibernate.event.internal.EntityState;
import org.hibernate.event.internal.OnUpdateVisitor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.hibernate.event.spi.SaveOrUpdateEventListener;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.proxy.HibernateProxy;

public class DefaultSaveOrUpdateEventListener
extends AbstractSaveEventListener
implements SaveOrUpdateEventListener {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DefaultSaveOrUpdateEventListener.class);

    @Override
    public void onSaveOrUpdate(SaveOrUpdateEvent event) {
        EventSource source = event.getSession();
        Object object = event.getObject();
        Serializable requestedId = event.getRequestedId();
        if (requestedId != null && object instanceof HibernateProxy) {
            ((HibernateProxy)object).getHibernateLazyInitializer().setIdentifier(requestedId);
        }
        if (this.reassociateIfUninitializedProxy(object, source)) {
            LOG.trace("Reassociated uninitialized proxy");
        } else {
            PersistenceContext persistenceContext = source.getPersistenceContextInternal();
            Object entity = persistenceContext.unproxyAndReassociate(object);
            event.setEntity(entity);
            event.setEntry(persistenceContext.getEntry(entity));
            event.setResultId(this.performSaveOrUpdate(event));
        }
    }

    protected boolean reassociateIfUninitializedProxy(Object object, SessionImplementor source) {
        return source.getPersistenceContextInternal().reassociateIfUninitializedProxy(object);
    }

    protected Serializable performSaveOrUpdate(SaveOrUpdateEvent event) {
        EntityState entityState = EntityState.getEntityState(event.getEntity(), event.getEntityName(), event.getEntry(), event.getSession(), null);
        switch (entityState) {
            case DETACHED: {
                this.entityIsDetached(event);
                return null;
            }
            case PERSISTENT: {
                return this.entityIsPersistent(event);
            }
        }
        return this.entityIsTransient(event);
    }

    protected Serializable entityIsPersistent(SaveOrUpdateEvent event) throws HibernateException {
        Serializable savedId;
        EntityEntry entityEntry;
        if (LOG.isTraceEnabled()) {
            LOG.trace("Ignoring persistent instance");
        }
        if ((entityEntry = event.getEntry()) == null) {
            throw new AssertionFailure("entity was transient or detached");
        }
        if (entityEntry.getStatus() == Status.DELETED) {
            throw new AssertionFailure("entity was deleted");
        }
        SessionFactoryImplementor factory = event.getSession().getFactory();
        Serializable requestedId = event.getRequestedId();
        if (requestedId == null) {
            savedId = entityEntry.getId();
        } else {
            boolean isEqual;
            boolean bl = isEqual = !entityEntry.getPersister().getIdentifierType().isEqual(requestedId, entityEntry.getId(), factory);
            if (isEqual) {
                throw new PersistentObjectException("object passed to save() was already persistent: " + MessageHelper.infoString(entityEntry.getPersister(), requestedId, factory));
            }
            savedId = requestedId;
        }
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Object already associated with session: {0}", MessageHelper.infoString(entityEntry.getPersister(), savedId, factory));
        }
        return savedId;
    }

    protected Serializable entityIsTransient(SaveOrUpdateEvent event) {
        LOG.trace("Saving transient instance");
        EventSource source = event.getSession();
        EntityEntry entityEntry = event.getEntry();
        if (entityEntry != null) {
            if (entityEntry.getStatus() == Status.DELETED) {
                source.forceFlush(entityEntry);
            } else {
                throw new AssertionFailure("entity was persistent");
            }
        }
        Serializable id = this.saveWithGeneratedOrRequestedId(event);
        source.getPersistenceContextInternal().reassociateProxy(event.getObject(), id);
        return id;
    }

    protected Serializable saveWithGeneratedOrRequestedId(SaveOrUpdateEvent event) {
        return this.saveWithGeneratedId(event.getEntity(), event.getEntityName(), null, event.getSession(), true);
    }

    protected void entityIsDetached(SaveOrUpdateEvent event) {
        LOG.trace("Updating detached instance");
        EventSource session = event.getSession();
        if (session.getPersistenceContextInternal().isEntryFor(event.getEntity())) {
            throw new AssertionFailure("entity was persistent");
        }
        Object entity = event.getEntity();
        EntityPersister persister = session.getEntityPersister(event.getEntityName(), entity);
        event.setRequestedId(this.getUpdateId(entity, persister, event.getRequestedId(), session));
        this.performUpdate(event, entity, persister);
    }

    protected Serializable getUpdateId(Object entity, EntityPersister persister, Serializable requestedId, SessionImplementor session) {
        Serializable id = persister.getIdentifier(entity, session);
        if (id == null) {
            throw new TransientObjectException("The given object has a null identifier: " + persister.getEntityName());
        }
        return id;
    }

    protected void performUpdate(SaveOrUpdateEvent event, Object entity, EntityPersister persister) throws HibernateException {
        if (LOG.isTraceEnabled() && !persister.isMutable()) {
            LOG.trace("Immutable instance passed to performUpdate()");
        }
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Updating {0}", MessageHelper.infoString(persister, event.getRequestedId(), event.getSession().getFactory()));
        }
        EventSource source = event.getSession();
        EntityKey key = source.generateEntityKey(event.getRequestedId(), persister);
        PersistenceContext persistenceContext = source.getPersistenceContextInternal();
        persistenceContext.checkUniqueness(key, entity);
        if (this.invokeUpdateLifecycle(entity, persister, source)) {
            this.reassociate(event, event.getObject(), event.getRequestedId(), persister);
            return;
        }
        new OnUpdateVisitor(source, event.getRequestedId(), entity).process(entity, persister);
        persistenceContext.addEntity(entity, persister.isMutable() ? Status.MANAGED : Status.READ_ONLY, null, key, persister.getVersion(entity), LockMode.NONE, true, persister, false);
        persister.afterReassociate(entity, source);
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Updating {0}", MessageHelper.infoString(persister, event.getRequestedId(), source.getFactory()));
        }
        this.cascadeOnUpdate(event, persister, entity);
    }

    protected boolean invokeUpdateLifecycle(Object entity, EntityPersister persister, EventSource source) {
        if (persister.implementsLifecycle()) {
            LOG.debug("Calling onUpdate()");
            if (((Lifecycle)entity).onUpdate(source)) {
                LOG.debug("Update vetoed by onUpdate()");
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void cascadeOnUpdate(SaveOrUpdateEvent event, EntityPersister persister, Object entity) {
        EventSource source = event.getSession();
        PersistenceContext persistenceContext = source.getPersistenceContextInternal();
        persistenceContext.incrementCascadeLevel();
        try {
            Cascade.cascade(CascadingActions.SAVE_UPDATE, CascadePoint.AFTER_UPDATE, source, persister, entity);
        }
        finally {
            persistenceContext.decrementCascadeLevel();
        }
    }

    @Override
    protected CascadingAction getCascadeAction() {
        return CascadingActions.SAVE_UPDATE;
    }
}

