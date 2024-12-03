/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;
import org.hibernate.CacheMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.TransientObjectException;
import org.hibernate.action.internal.EntityDeleteAction;
import org.hibernate.action.internal.OrphanRemovalAction;
import org.hibernate.classic.Lifecycle;
import org.hibernate.engine.internal.Cascade;
import org.hibernate.engine.internal.CascadePoint;
import org.hibernate.engine.internal.ForeignKeys;
import org.hibernate.engine.internal.Nullability;
import org.hibernate.engine.spi.CascadingActions;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.internal.OnUpdateVisitor;
import org.hibernate.event.service.spi.JpaBootstrapSensitive;
import org.hibernate.event.spi.DeleteEvent;
import org.hibernate.event.spi.DeleteEventListener;
import org.hibernate.event.spi.EventSource;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.collections.IdentitySet;
import org.hibernate.jpa.event.spi.CallbackRegistry;
import org.hibernate.jpa.event.spi.CallbackRegistryConsumer;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.Type;
import org.hibernate.type.TypeHelper;

public class DefaultDeleteEventListener
implements DeleteEventListener,
CallbackRegistryConsumer,
JpaBootstrapSensitive {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DefaultDeleteEventListener.class);
    private CallbackRegistry callbackRegistry;
    private boolean jpaBootstrap;

    @Override
    public void injectCallbackRegistry(CallbackRegistry callbackRegistry) {
        this.callbackRegistry = callbackRegistry;
    }

    @Override
    public void wasJpaBootstrap(boolean wasJpaBootstrap) {
        this.jpaBootstrap = wasJpaBootstrap;
    }

    @Override
    public void onDelete(DeleteEvent event) throws HibernateException {
        this.onDelete(event, new IdentitySet());
    }

    @Override
    public void onDelete(DeleteEvent event, Set transientEntities) throws HibernateException {
        Object version;
        Serializable id;
        EntityPersister persister;
        Object entity;
        EventSource source = event.getSession();
        PersistenceContext persistenceContext = source.getPersistenceContextInternal();
        EntityEntry entityEntry = persistenceContext.getEntry(entity = persistenceContext.unproxyAndReassociate(event.getObject()));
        if (entityEntry == null) {
            LOG.trace("Entity was not persistent in delete processing");
            persister = source.getEntityPersister(event.getEntityName(), entity);
            if (ForeignKeys.isTransient(persister.getEntityName(), entity, null, source)) {
                this.deleteTransientEntity(source, entity, event.isCascadeDeleteEnabled(), persister, transientEntities);
                return;
            }
            this.performDetachedEntityDeletionCheck(event);
            id = persister.getIdentifier(entity, source);
            if (id == null) {
                throw new TransientObjectException("the detached instance passed to delete() had a null identifier");
            }
            EntityKey key = source.generateEntityKey(id, persister);
            persistenceContext.checkUniqueness(key, entity);
            new OnUpdateVisitor(source, id, entity).process(entity, persister);
            version = persister.getVersion(entity);
            entityEntry = persistenceContext.addEntity(entity, persister.isMutable() ? Status.MANAGED : Status.READ_ONLY, persister.getPropertyValues(entity), key, version, LockMode.NONE, true, persister, false);
            persister.afterReassociate(entity, source);
        } else {
            LOG.trace("Deleting a persistent instance");
            if (entityEntry.getStatus() == Status.DELETED || entityEntry.getStatus() == Status.GONE) {
                LOG.trace("Object was already deleted");
                return;
            }
            persister = entityEntry.getPersister();
            id = entityEntry.getId();
            version = entityEntry.getVersion();
        }
        this.callbackRegistry.preRemove(entity);
        if (this.invokeDeleteLifecycle(source, entity, persister)) {
            return;
        }
        this.deleteEntity(source, entity, entityEntry, event.isCascadeDeleteEnabled(), event.isOrphanRemovalBeforeUpdates(), persister, transientEntities);
        if (source.getFactory().getSettings().isIdentifierRollbackEnabled()) {
            persister.resetIdentifier(entity, id, version, source);
        }
    }

    protected void performDetachedEntityDeletionCheck(DeleteEvent event) {
        if (this.jpaBootstrap) {
            this.disallowDeletionOfDetached(event);
        }
    }

    private void disallowDeletionOfDetached(DeleteEvent event) {
        EventSource source = event.getSession();
        String entityName = event.getEntityName();
        EntityPersister persister = source.getEntityPersister(entityName, event.getObject());
        Serializable id = persister.getIdentifier(event.getObject(), source);
        entityName = entityName == null ? source.guessEntityName(event.getObject()) : entityName;
        throw new IllegalArgumentException("Removing a detached instance " + entityName + "#" + id);
    }

    protected void deleteTransientEntity(EventSource session, Object entity, boolean cascadeDeleteEnabled, EntityPersister persister, Set transientEntities) {
        LOG.handlingTransientEntity();
        if (transientEntities.contains(entity)) {
            LOG.trace("Already handled transient entity; skipping");
            return;
        }
        transientEntities.add(entity);
        this.cascadeBeforeDelete(session, persister, entity, null, transientEntities);
        this.cascadeAfterDelete(session, persister, entity, transientEntities);
    }

    protected final void deleteEntity(EventSource session, Object entity, EntityEntry entityEntry, boolean isCascadeDeleteEnabled, boolean isOrphanRemovalBeforeUpdates, EntityPersister persister, Set transientEntities) {
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Deleting {0}", MessageHelper.infoString(persister, entityEntry.getId(), session.getFactory()));
        }
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        Type[] propTypes = persister.getPropertyTypes();
        Object version = entityEntry.getVersion();
        Object[] currentState = entityEntry.getLoadedState() == null ? persister.getPropertyValues(entity) : entityEntry.getLoadedState();
        Object[] deletedState = this.createDeletedState(persister, currentState, session);
        entityEntry.setDeletedState(deletedState);
        session.getInterceptor().onDelete(entity, entityEntry.getId(), deletedState, persister.getPropertyNames(), propTypes);
        persistenceContext.setEntryStatus(entityEntry, Status.DELETED);
        EntityKey key = session.generateEntityKey(entityEntry.getId(), persister);
        this.cascadeBeforeDelete(session, persister, entity, entityEntry, transientEntities);
        new ForeignKeys.Nullifier(entity, true, false, session, persister).nullifyTransientReferences(entityEntry.getDeletedState());
        new Nullability(session).checkNullability(entityEntry.getDeletedState(), persister, Nullability.NullabilityCheckType.DELETE);
        persistenceContext.registerNullifiableEntityKey(key);
        if (isOrphanRemovalBeforeUpdates) {
            session.getActionQueue().addAction(new OrphanRemovalAction(entityEntry.getId(), deletedState, version, entity, persister, isCascadeDeleteEnabled, session));
        } else {
            session.getActionQueue().addAction(new EntityDeleteAction(entityEntry.getId(), deletedState, version, entity, persister, isCascadeDeleteEnabled, session));
        }
        this.cascadeAfterDelete(session, persister, entity, transientEntities);
    }

    private Object[] createDeletedState(EntityPersister persister, Object[] currentState, EventSource session) {
        Type[] propTypes = persister.getPropertyTypes();
        Object[] deletedState = new Object[propTypes.length];
        boolean[] copyability = new boolean[propTypes.length];
        Arrays.fill(copyability, true);
        TypeHelper.deepCopy(currentState, propTypes, copyability, deletedState, session);
        return deletedState;
    }

    protected boolean invokeDeleteLifecycle(EventSource session, Object entity, EntityPersister persister) {
        if (persister.implementsLifecycle()) {
            LOG.debug("Calling onDelete()");
            if (((Lifecycle)entity).onDelete(session)) {
                LOG.debug("Deletion vetoed by onDelete()");
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void cascadeBeforeDelete(EventSource session, EntityPersister persister, Object entity, EntityEntry entityEntry, Set transientEntities) throws HibernateException {
        CacheMode cacheMode = session.getCacheMode();
        session.setCacheMode(CacheMode.GET);
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        persistenceContext.incrementCascadeLevel();
        try {
            Cascade.cascade(CascadingActions.DELETE, CascadePoint.AFTER_INSERT_BEFORE_DELETE, session, persister, entity, transientEntities);
        }
        finally {
            persistenceContext.decrementCascadeLevel();
            session.setCacheMode(cacheMode);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void cascadeAfterDelete(EventSource session, EntityPersister persister, Object entity, Set transientEntities) throws HibernateException {
        CacheMode cacheMode = session.getCacheMode();
        session.setCacheMode(CacheMode.GET);
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        persistenceContext.incrementCascadeLevel();
        try {
            Cascade.cascade(CascadingActions.DELETE, CascadePoint.BEFORE_INSERT_AFTER_DELETE, session, persister, entity, transientEntities);
        }
        finally {
            persistenceContext.decrementCascadeLevel();
            session.setCacheMode(cacheMode);
        }
    }
}

