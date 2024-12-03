/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import java.util.Map;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.ObjectDeletedException;
import org.hibernate.StaleObjectStateException;
import org.hibernate.WrongClassException;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementAsProxyLazinessInterceptor;
import org.hibernate.engine.internal.Cascade;
import org.hibernate.engine.internal.CascadePoint;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.engine.spi.CascadingAction;
import org.hibernate.engine.spi.CascadingActions;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.engine.spi.SelfDirtinessTracker;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.internal.AbstractSaveEventListener;
import org.hibernate.event.internal.EntityState;
import org.hibernate.event.internal.EventUtil;
import org.hibernate.event.internal.MergeContext;
import org.hibernate.event.spi.EntityCopyObserver;
import org.hibernate.event.spi.EntityCopyObserverFactory;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.MergeEvent;
import org.hibernate.event.spi.MergeEventListener;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.TypeHelper;

public class DefaultMergeEventListener
extends AbstractSaveEventListener
implements MergeEventListener {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DefaultMergeEventListener.class);

    @Override
    protected Map getMergeMap(Object anything) {
        return ((MergeContext)anything).invertMap();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onMerge(MergeEvent event) throws HibernateException {
        EntityCopyObserver entityCopyObserver = this.createEntityCopyObserver(event.getSession().getFactory());
        MergeContext mergeContext = new MergeContext(event.getSession(), entityCopyObserver);
        try {
            this.onMerge(event, mergeContext);
            entityCopyObserver.topLevelMergeComplete(event.getSession());
        }
        finally {
            entityCopyObserver.clear();
            mergeContext.clear();
        }
    }

    private EntityCopyObserver createEntityCopyObserver(SessionFactoryImplementor sessionFactory) {
        ServiceRegistryImplementor serviceRegistry = sessionFactory.getServiceRegistry();
        EntityCopyObserverFactory configurationService = serviceRegistry.getService(EntityCopyObserverFactory.class);
        return configurationService.createEntityCopyObserver();
    }

    @Override
    public void onMerge(MergeEvent event, Map copiedAlready) throws HibernateException {
        MergeContext copyCache = (MergeContext)copiedAlready;
        EventSource source = event.getSession();
        Object original = event.getOriginal();
        if (original != null) {
            Object entity;
            if (original instanceof HibernateProxy) {
                LazyInitializer li = ((HibernateProxy)original).getHibernateLazyInitializer();
                if (li.isUninitialized()) {
                    LOG.trace("Ignoring uninitialized proxy");
                    event.setResult(source.load(li.getEntityName(), li.getInternalIdentifier()));
                    return;
                }
                entity = li.getImplementation();
            } else if (ManagedTypeHelper.isPersistentAttributeInterceptable(original)) {
                PersistentAttributeInterceptor interceptor = ManagedTypeHelper.asPersistentAttributeInterceptable(original).$$_hibernate_getInterceptor();
                if (interceptor instanceof EnhancementAsProxyLazinessInterceptor) {
                    EnhancementAsProxyLazinessInterceptor proxyInterceptor = (EnhancementAsProxyLazinessInterceptor)interceptor;
                    LOG.trace("Ignoring uninitialized enhanced-proxy");
                    event.setResult(source.load(proxyInterceptor.getEntityName(), (Serializable)proxyInterceptor.getIdentifier()));
                    return;
                }
                entity = original;
            } else {
                entity = original;
            }
            if (copyCache.containsKey(entity) && copyCache.isOperatedOn(entity)) {
                LOG.trace("Already in merge process");
                event.setResult(entity);
            } else {
                EntityKey key;
                Object managedEntity;
                EntityPersister persister;
                Serializable id;
                if (copyCache.containsKey(entity)) {
                    LOG.trace("Already in copyCache; setting in merge process");
                    copyCache.setOperatedOn(entity, true);
                }
                event.setEntity(entity);
                Enum entityState = null;
                PersistenceContext persistenceContext = source.getPersistenceContextInternal();
                EntityEntry entry = persistenceContext.getEntry(entity);
                if (entry == null && (id = (persister = source.getEntityPersister(event.getEntityName(), entity)).getIdentifier(entity, source)) != null && (entry = persistenceContext.getEntry(managedEntity = persistenceContext.getEntity(key = source.generateEntityKey(id, persister)))) != null) {
                    entityState = EntityState.DETACHED;
                }
                if (entityState == null) {
                    entityState = EntityState.getEntityState(entity, event.getEntityName(), entry, source, false);
                }
                switch (1.$SwitchMap$org$hibernate$event$internal$EntityState[entityState.ordinal()]) {
                    case 1: {
                        this.entityIsDetached(event, copyCache);
                        break;
                    }
                    case 2: {
                        this.entityIsTransient(event, copyCache);
                        break;
                    }
                    case 3: {
                        this.entityIsPersistent(event, copyCache);
                        break;
                    }
                    default: {
                        throw new ObjectDeletedException("deleted instance passed to merge", null, EventUtil.getLoggableName(event.getEntityName(), entity));
                    }
                }
            }
        }
    }

    protected void entityIsPersistent(MergeEvent event, Map copyCache) {
        LOG.trace("Ignoring persistent instance");
        Object entity = event.getEntity();
        EventSource source = event.getSession();
        EntityPersister persister = source.getEntityPersister(event.getEntityName(), entity);
        ((MergeContext)copyCache).put(entity, entity, true);
        this.cascadeOnMerge(source, persister, entity, copyCache);
        this.copyValues(persister, entity, entity, source, copyCache);
        event.setResult(entity);
    }

    protected void entityIsTransient(MergeEvent event, Map copyCache) {
        PersistentAttributeInterceptor interceptor;
        Object copy;
        LOG.trace("Merging transient instance");
        Object entity = event.getEntity();
        EventSource session = event.getSession();
        String entityName = event.getEntityName();
        EntityPersister persister = session.getEntityPersister(entityName, entity);
        Serializable id = persister.hasIdentifierProperty() ? persister.getIdentifier(entity, session) : null;
        Object existingCopy = copyCache.get(entity);
        if (existingCopy != null) {
            persister.setIdentifier(copyCache.get(entity), id, session);
            copy = existingCopy;
        } else {
            copy = session.instantiate(persister, id);
            ((MergeContext)copyCache).put(entity, copy, true);
        }
        super.cascadeBeforeSave(session, persister, entity, copyCache);
        this.copyValues(persister, entity, copy, session, copyCache, ForeignKeyDirection.FROM_PARENT);
        this.saveTransientEntity(copy, entityName, event.getRequestedId(), session, copyCache);
        super.cascadeAfterSave(session, persister, entity, copyCache);
        this.copyValues(persister, entity, copy, session, copyCache, ForeignKeyDirection.TO_PARENT);
        event.setResult(copy);
        if (ManagedTypeHelper.isPersistentAttributeInterceptable(copy) && (interceptor = ManagedTypeHelper.asPersistentAttributeInterceptable(copy).$$_hibernate_getInterceptor()) == null) {
            persister.getBytecodeEnhancementMetadata().injectInterceptor(copy, id, (SharedSessionContractImplementor)session);
        }
    }

    private void saveTransientEntity(Object entity, String entityName, Serializable requestedId, EventSource source, Map copyCache) {
        if (requestedId == null) {
            this.saveWithGeneratedId(entity, entityName, copyCache, source, false);
        } else {
            this.saveWithRequestedId(entity, requestedId, entityName, copyCache, source);
        }
    }

    protected void entityIsDetached(MergeEvent event, Map copyCache) {
        LOG.trace("Merging detached instance");
        Object entity = event.getEntity();
        EventSource source = event.getSession();
        EntityPersister persister = source.getEntityPersister(event.getEntityName(), entity);
        String entityName = persister.getEntityName();
        Serializable id = event.getRequestedId();
        if (id == null) {
            id = persister.getIdentifier(entity, source);
        } else {
            Serializable entityId = persister.getIdentifier(entity, source);
            if (!persister.getIdentifierType().isEqual(id, entityId, source.getFactory())) {
                throw new HibernateException("merge requested with id not matching id of passed entity");
            }
        }
        String previousFetchProfile = source.getLoadQueryInfluencers().getInternalFetchProfile();
        source.getLoadQueryInfluencers().setInternalFetchProfile("merge");
        Serializable clonedIdentifier = (Serializable)persister.getIdentifierType().deepCopy(id, source.getFactory());
        Object result = source.get(entityName, clonedIdentifier);
        source.getLoadQueryInfluencers().setInternalFetchProfile(previousFetchProfile);
        if (result == null) {
            this.entityIsTransient(event, copyCache);
        } else {
            ((MergeContext)copyCache).put(entity, result, true);
            Object target = this.unproxyManagedForDetachedMerging(entity, result, persister, source);
            if (target == entity) {
                throw new AssertionFailure("entity was not detached");
            }
            if (!source.getEntityName(target).equals(entityName)) {
                throw new WrongClassException("class of the given object did not match class of persistent copy", event.getRequestedId(), entityName);
            }
            if (this.isVersionChanged(entity, source, persister, target)) {
                StatisticsImplementor statistics = source.getFactory().getStatistics();
                if (statistics.isStatisticsEnabled()) {
                    statistics.optimisticFailure(entityName);
                }
                throw new StaleObjectStateException(entityName, id);
            }
            this.cascadeOnMerge(source, persister, entity, copyCache);
            this.copyValues(persister, entity, target, source, copyCache);
            this.markInterceptorDirty(entity, target, persister);
            event.setResult(result);
        }
    }

    private Object unproxyManagedForDetachedMerging(Object incoming, Object managed, EntityPersister persister, EventSource source) {
        if (managed instanceof HibernateProxy) {
            return source.getPersistenceContextInternal().unproxy(managed);
        }
        if (ManagedTypeHelper.isPersistentAttributeInterceptable(incoming) && persister.getBytecodeEnhancementMetadata().isEnhancedForLazyLoading()) {
            PersistentAttributeInterceptor incomingInterceptor = ManagedTypeHelper.asPersistentAttributeInterceptable(incoming).$$_hibernate_getInterceptor();
            PersistentAttributeInterceptor managedInterceptor = ManagedTypeHelper.asPersistentAttributeInterceptable(managed).$$_hibernate_getInterceptor();
            if (!(managedInterceptor instanceof EnhancementAsProxyLazinessInterceptor)) {
                return managed;
            }
            if (incomingInterceptor instanceof EnhancementAsProxyLazinessInterceptor) {
                return managed;
            }
            persister.initializeEnhancedEntityUsedAsProxy(managed, null, source);
        }
        return managed;
    }

    private void markInterceptorDirty(Object entity, Object target, EntityPersister persister) {
        if (ManagedTypeHelper.isSelfDirtinessTracker(entity) && ManagedTypeHelper.isSelfDirtinessTracker(target)) {
            SelfDirtinessTracker castedTarget = ManagedTypeHelper.asSelfDirtinessTracker(target);
            castedTarget.$$_hibernate_clearDirtyAttributes();
            for (String fieldName : ManagedTypeHelper.asSelfDirtinessTracker(entity).$$_hibernate_getDirtyAttributes()) {
                castedTarget.$$_hibernate_trackChange(fieldName);
            }
        }
    }

    private boolean isVersionChanged(Object entity, EventSource source, EntityPersister persister, Object target) {
        if (!persister.isVersioned()) {
            return false;
        }
        boolean changed = !persister.getVersionType().isSame(persister.getVersion(target), persister.getVersion(entity));
        return changed && this.existsInDatabase(target, source, persister);
    }

    private boolean existsInDatabase(Object entity, EventSource source, EntityPersister persister) {
        Serializable id;
        PersistenceContext persistenceContext = source.getPersistenceContextInternal();
        EntityEntry entry = persistenceContext.getEntry(entity);
        if (entry == null && (id = persister.getIdentifier(entity, source)) != null) {
            EntityKey key = source.generateEntityKey(id, persister);
            Object managedEntity = persistenceContext.getEntity(key);
            entry = persistenceContext.getEntry(managedEntity);
        }
        return entry != null && entry.isExistsInDatabase();
    }

    protected void copyValues(EntityPersister persister, Object entity, Object target, SessionImplementor source, Map copyCache) {
        Object[] copiedValues = TypeHelper.replace(persister.getPropertyValues(entity), persister.getPropertyValues(target), persister.getPropertyTypes(), source, target, copyCache);
        persister.setPropertyValues(target, copiedValues);
    }

    protected void copyValues(EntityPersister persister, Object entity, Object target, SessionImplementor source, Map copyCache, ForeignKeyDirection foreignKeyDirection) {
        Object[] copiedValues = foreignKeyDirection == ForeignKeyDirection.TO_PARENT ? TypeHelper.replaceAssociations(persister.getPropertyValues(entity), persister.getPropertyValues(target), persister.getPropertyTypes(), source, target, copyCache, foreignKeyDirection) : TypeHelper.replace(persister.getPropertyValues(entity), persister.getPropertyValues(target), persister.getPropertyTypes(), source, target, copyCache, foreignKeyDirection);
        persister.setPropertyValues(target, copiedValues);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void cascadeOnMerge(EventSource source, EntityPersister persister, Object entity, Map copyCache) {
        PersistenceContext persistenceContext = source.getPersistenceContextInternal();
        persistenceContext.incrementCascadeLevel();
        try {
            Cascade.cascade(this.getCascadeAction(), CascadePoint.BEFORE_MERGE, source, persister, entity, copyCache);
        }
        finally {
            persistenceContext.decrementCascadeLevel();
        }
    }

    @Override
    protected CascadingAction getCascadeAction() {
        return CascadingActions.MERGE;
    }

    @Override
    protected void cascadeAfterSave(EventSource source, EntityPersister persister, Object entity, Object anything) throws HibernateException {
    }

    @Override
    protected void cascadeBeforeSave(EventSource source, EntityPersister persister, Object entity, Object anything) throws HibernateException {
    }
}

