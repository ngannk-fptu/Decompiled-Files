/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.PersistentObjectException;
import org.hibernate.TypeMismatchException;
import org.hibernate.action.internal.DelayedPostInsertIdentifier;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.LoadEvent;
import org.hibernate.event.spi.LoadEventListener;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.loader.entity.CacheEntityLoaderHelper;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.tuple.IdentifierProperty;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.type.EmbeddedComponentType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class DefaultLoadEventListener
implements LoadEventListener {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DefaultLoadEventListener.class);

    @Override
    public void onLoad(LoadEvent event, LoadEventListener.LoadType loadType) throws HibernateException {
        EntityPersister persister = this.getPersister(event);
        if (persister == null) {
            throw new HibernateException("Unable to locate persister: " + event.getEntityClassName());
        }
        Class idClass = persister.getIdentifierType().getReturnedClass();
        if (idClass != null && !idClass.isInstance(event.getEntityId()) && !DelayedPostInsertIdentifier.class.isInstance(event.getEntityId())) {
            this.checkIdClass(persister, event, loadType, idClass);
        }
        this.doOnLoad(persister, event, loadType);
    }

    protected EntityPersister getPersister(LoadEvent event) {
        Object instanceToLoad = event.getInstanceToLoad();
        if (instanceToLoad != null) {
            event.setEntityClassName(instanceToLoad.getClass().getName());
            return event.getSession().getEntityPersister(null, instanceToLoad);
        }
        return event.getSession().getFactory().getMetamodel().entityPersister(event.getEntityClassName());
    }

    private void doOnLoad(EntityPersister persister, LoadEvent event, LoadEventListener.LoadType loadType) {
        try {
            EventSource session = event.getSession();
            EntityKey keyToLoad = session.generateEntityKey(event.getEntityId(), persister);
            if (loadType.isNakedEntityReturned()) {
                event.setResult(this.load(event, persister, keyToLoad, loadType));
            } else if (event.getLockMode() == LockMode.NONE) {
                event.setResult(this.proxyOrLoad(event, persister, keyToLoad, loadType));
            } else {
                event.setResult(this.lockAndLoad(event, persister, keyToLoad, loadType, session));
            }
        }
        catch (HibernateException e) {
            LOG.unableToLoadCommand(e);
            throw e;
        }
    }

    private void checkIdClass(EntityPersister persister, LoadEvent event, LoadEventListener.LoadType loadType, Class idClass) {
        SessionFactoryImplementor factory;
        EntityType dependentParentType;
        Type dependentParentIdType;
        Type singleSubType;
        EmbeddedComponentType dependentIdType;
        IdentifierProperty identifierProperty = persister.getEntityMetamodel().getIdentifierProperty();
        if (identifierProperty.isEmbedded() && (dependentIdType = (EmbeddedComponentType)identifierProperty.getType()).getSubtypes().length == 1 && (singleSubType = dependentIdType.getSubtypes()[0]).isEntityType() && (dependentParentIdType = (dependentParentType = (EntityType)singleSubType).getIdentifierOrUniqueKeyType(factory = event.getSession().getFactory())).getReturnedClass().isInstance(event.getEntityId())) {
            this.loadByDerivedIdentitySimplePkValue(event, loadType, persister, dependentIdType, factory.getMetamodel().entityPersister(dependentParentType.getAssociatedEntityName()));
            return;
        }
        throw new TypeMismatchException("Provided id of the wrong type for class " + persister.getEntityName() + ". Expected: " + idClass + ", got " + event.getEntityId().getClass());
    }

    private void loadByDerivedIdentitySimplePkValue(LoadEvent event, LoadEventListener.LoadType options, EntityPersister dependentPersister, EmbeddedComponentType dependentIdType, EntityPersister parentPersister) {
        EventSource session = event.getSession();
        EntityKey parentEntityKey = session.generateEntityKey(event.getEntityId(), parentPersister);
        Object parent = this.doLoad(event, parentPersister, parentEntityKey, options);
        Serializable dependent = (Serializable)dependentIdType.instantiate(parent, session);
        dependentIdType.setPropertyValues(dependent, new Object[]{parent}, dependentPersister.getEntityMode());
        EntityKey dependentEntityKey = session.generateEntityKey(dependent, dependentPersister);
        event.setEntityId(dependent);
        event.setResult(this.doLoad(event, dependentPersister, dependentEntityKey, options));
    }

    private Object load(LoadEvent event, EntityPersister persister, EntityKey keyToLoad, LoadEventListener.LoadType options) {
        boolean isOptionalInstance;
        EventSource session = event.getSession();
        if (event.getInstanceToLoad() != null) {
            if (session.getPersistenceContextInternal().getEntry(event.getInstanceToLoad()) != null) {
                throw new PersistentObjectException("attempted to load into an instance that was already associated with the session: " + MessageHelper.infoString(persister, event.getEntityId(), session.getFactory()));
            }
            persister.setIdentifier(event.getInstanceToLoad(), event.getEntityId(), session);
        }
        Object entity = this.doLoad(event, persister, keyToLoad, options);
        boolean bl = isOptionalInstance = event.getInstanceToLoad() != null;
        if (entity == null && (!options.isAllowNulls() || isOptionalInstance)) {
            session.getFactory().getEntityNotFoundDelegate().handleEntityNotFound(event.getEntityClassName(), event.getEntityId());
        } else if (isOptionalInstance && entity != event.getInstanceToLoad()) {
            throw new NonUniqueObjectException(event.getEntityId(), event.getEntityClassName());
        }
        return entity;
    }

    private Object proxyOrLoad(LoadEvent event, EntityPersister persister, EntityKey keyToLoad, LoadEventListener.LoadType options) {
        boolean entityHasHibernateProxyFactory;
        EventSource session = event.getSession();
        SessionFactoryImplementor factory = session.getFactory();
        boolean traceEnabled = LOG.isTraceEnabled();
        if (traceEnabled) {
            LOG.tracev("Loading entity: {0}", MessageHelper.infoString(persister, event.getEntityId(), factory));
        }
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        EntityMetamodel entityMetamodel = persister.getEntityMetamodel();
        boolean bl = entityHasHibernateProxyFactory = entityMetamodel.getTuplizer().getProxyFactory() != null;
        if (options.isAllowProxyCreation() && entityMetamodel.getBytecodeEnhancementMetadata().isEnhancedForLazyLoading()) {
            Object managed = persistenceContext.getEntity(keyToLoad);
            if (managed != null) {
                EntityEntry entry;
                Status status;
                if (options.isCheckDeleted() && ((status = (entry = persistenceContext.getEntry(managed)).getStatus()) == Status.DELETED || status == Status.GONE)) {
                    return null;
                }
                return managed;
            }
            if (entityHasHibernateProxyFactory) {
                Object proxy = persistenceContext.getProxy(keyToLoad);
                if (proxy != null) {
                    if (traceEnabled) {
                        LOG.trace("Entity proxy found in session cache");
                    }
                    if (LOG.isDebugEnabled() && ((HibernateProxy)proxy).getHibernateLazyInitializer().isUnwrap()) {
                        LOG.debug("Ignoring NO_PROXY to honor laziness");
                    }
                    return persistenceContext.narrowProxy(proxy, persister, keyToLoad, null);
                }
                if (entityMetamodel.hasSubclasses()) {
                    return this.createProxy(event, persister, keyToLoad, persistenceContext);
                }
            }
            if (!entityMetamodel.hasSubclasses()) {
                if (keyToLoad.isBatchLoadable()) {
                    persistenceContext.getBatchFetchQueue().addBatchLoadableEntityKey(keyToLoad);
                }
                return persister.getBytecodeEnhancementMetadata().createEnhancedProxy(keyToLoad, true, session);
            }
        } else if (persister.hasProxy()) {
            Object proxy = persistenceContext.getProxy(keyToLoad);
            if (proxy != null) {
                return this.returnNarrowedProxy(event, persister, keyToLoad, options, persistenceContext, proxy);
            }
            if (options.isAllowProxyCreation()) {
                return this.createProxyIfNecessary(event, persister, keyToLoad, options, persistenceContext);
            }
        }
        return this.load(event, persister, keyToLoad, options);
    }

    private Object returnNarrowedProxy(LoadEvent event, EntityPersister persister, EntityKey keyToLoad, LoadEventListener.LoadType options, PersistenceContext persistenceContext, Object proxy) {
        LazyInitializer li;
        if (LOG.isTraceEnabled()) {
            LOG.trace("Entity proxy found in session cache");
        }
        if ((li = ((HibernateProxy)proxy).getHibernateLazyInitializer()).isUnwrap()) {
            return li.getImplementation();
        }
        Object impl = null;
        if (!options.isAllowProxyCreation() && (impl = this.load(event, persister, keyToLoad, options)) == null) {
            if (options == LoadEventListener.INTERNAL_LOAD_NULLABLE) {
                return null;
            }
            event.getSession().getFactory().getEntityNotFoundDelegate().handleEntityNotFound(persister.getEntityName(), keyToLoad.getIdentifier());
        }
        return persistenceContext.narrowProxy(proxy, persister, keyToLoad, impl);
    }

    private Object createProxyIfNecessary(LoadEvent event, EntityPersister persister, EntityKey keyToLoad, LoadEventListener.LoadType options, PersistenceContext persistenceContext) {
        Object existing = persistenceContext.getEntity(keyToLoad);
        boolean traceEnabled = LOG.isTraceEnabled();
        if (existing != null) {
            EntityEntry entry;
            Status status;
            if (traceEnabled) {
                LOG.trace("Entity found in session cache");
            }
            if (options.isCheckDeleted() && ((status = (entry = persistenceContext.getEntry(existing)).getStatus()) == Status.DELETED || status == Status.GONE)) {
                return null;
            }
            return existing;
        }
        if (traceEnabled) {
            LOG.trace("Creating new proxy for entity");
        }
        return this.createProxy(event, persister, keyToLoad, persistenceContext);
    }

    private Object createProxy(LoadEvent event, EntityPersister persister, EntityKey keyToLoad, PersistenceContext persistenceContext) {
        Object proxy = persister.createProxy(event.getEntityId(), event.getSession());
        persistenceContext.getBatchFetchQueue().addBatchLoadableEntityKey(keyToLoad);
        persistenceContext.addProxy(keyToLoad, proxy);
        return proxy;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Object lockAndLoad(LoadEvent event, EntityPersister persister, EntityKey keyToLoad, LoadEventListener.LoadType options, SessionImplementor source) {
        Object entity;
        Object ck;
        SoftLock lock = null;
        EntityDataAccess cache = persister.getCacheAccessStrategy();
        boolean canWriteToCache = persister.canWriteToCache();
        if (canWriteToCache) {
            ck = cache.generateCacheKey(event.getEntityId(), persister, source.getFactory(), source.getTenantIdentifier());
            lock = cache.lockItem(source, ck, null);
        } else {
            ck = null;
        }
        try {
            entity = this.load(event, persister, keyToLoad, options);
            if (!canWriteToCache) return source.getPersistenceContextInternal().proxyFor(persister, keyToLoad, entity);
        }
        catch (Throwable throwable) {
            if (!canWriteToCache) throw throwable;
            cache.unlockItem(source, ck, lock);
            throw throwable;
        }
        cache.unlockItem(source, ck, lock);
        return source.getPersistenceContextInternal().proxyFor(persister, keyToLoad, entity);
    }

    private Object doLoad(LoadEvent event, EntityPersister persister, EntityKey keyToLoad, LoadEventListener.LoadType options) {
        CacheEntityLoaderHelper.PersistenceContextEntry persistenceContextEntry;
        Object entity;
        EventSource session = event.getSession();
        boolean traceEnabled = LOG.isTraceEnabled();
        if (traceEnabled) {
            LOG.tracev("Attempting to resolve: {0}", MessageHelper.infoString(persister, event.getEntityId(), session.getFactory()));
        }
        if ((entity = (persistenceContextEntry = CacheEntityLoaderHelper.INSTANCE.loadFromSessionCache(event, keyToLoad, options)).getEntity()) != null) {
            return persistenceContextEntry.isManaged() ? entity : null;
        }
        entity = CacheEntityLoaderHelper.INSTANCE.loadFromSecondLevelCache(event, persister, keyToLoad);
        if (entity != null) {
            if (traceEnabled) {
                LOG.tracev("Resolved object in second-level cache: {0}", MessageHelper.infoString(persister, event.getEntityId(), session.getFactory()));
            }
        } else {
            if (traceEnabled) {
                LOG.tracev("Object not resolved in any cache: {0}", MessageHelper.infoString(persister, event.getEntityId(), session.getFactory()));
            }
            entity = this.loadFromDatasource(event, persister);
        }
        if (entity != null && persister.hasNaturalIdentifier()) {
            PersistenceContext persistenceContext = session.getPersistenceContextInternal();
            PersistenceContext.NaturalIdHelper naturalIdHelper = persistenceContext.getNaturalIdHelper();
            naturalIdHelper.cacheNaturalIdCrossReferenceFromLoad(persister, event.getEntityId(), naturalIdHelper.extractNaturalIdValues(entity, persister));
        }
        return entity;
    }

    protected Object loadFromDatasource(LoadEvent event, EntityPersister persister) {
        Object entity = persister.load(event.getEntityId(), event.getInstanceToLoad(), event.getLockOptions(), (SharedSessionContractImplementor)event.getSession(), event.getReadOnly());
        StatisticsImplementor statistics = event.getSession().getFactory().getStatistics();
        if (event.isAssociationFetch() && statistics.isStatisticsEnabled()) {
            statistics.fetchEntity(event.getEntityClassName());
        }
        return entity;
    }
}

