/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.cache.internal;

import java.io.Serializable;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.action.internal.CollectionAction;
import org.hibernate.action.spi.AfterTransactionCompletionProcess;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.jboss.logging.Logger;

public class CollectionCacheInvalidator
implements Integrator,
PostInsertEventListener,
PostDeleteEventListener,
PostUpdateEventListener {
    private static final Logger LOG = Logger.getLogger((String)CollectionCacheInvalidator.class.getName());
    public static boolean PROPAGATE_EXCEPTION = false;

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        this.integrate(serviceRegistry, sessionFactory);
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        this.evictCache(event.getEntity(), event.getPersister(), event.getSession(), null);
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return true;
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        this.evictCache(event.getEntity(), event.getPersister(), event.getSession(), null);
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        this.evictCache(event.getEntity(), event.getPersister(), event.getSession(), event.getOldState());
    }

    private void integrate(SessionFactoryServiceRegistry serviceRegistry, SessionFactoryImplementor sessionFactory) {
        SessionFactoryOptions sessionFactoryOptions = sessionFactory.getSessionFactoryOptions();
        if (!sessionFactoryOptions.isAutoEvictCollectionCache()) {
            return;
        }
        if (!sessionFactoryOptions.isSecondLevelCacheEnabled()) {
            return;
        }
        EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);
        eventListenerRegistry.appendListeners(EventType.POST_INSERT, this);
        eventListenerRegistry.appendListeners(EventType.POST_DELETE, this);
        eventListenerRegistry.appendListeners(EventType.POST_UPDATE, this);
    }

    private void evictCache(Object entity, EntityPersister persister, EventSource session, Object[] oldState) {
        try {
            SessionFactoryImplementor factory = persister.getFactory();
            MetamodelImplementor metamodel = factory.getMetamodel();
            Set<String> collectionRoles = metamodel.getCollectionRolesByEntityParticipant(persister.getEntityName());
            if (collectionRoles == null || collectionRoles.isEmpty()) {
                return;
            }
            EntityMetamodel entityMetamodel = persister.getEntityMetamodel();
            boolean debugEnabled = LOG.isDebugEnabled();
            for (String role : collectionRoles) {
                CollectionPersister collectionPersister = metamodel.collectionPersister(role);
                if (!collectionPersister.hasCache()) continue;
                String mappedBy = collectionPersister.getMappedByProperty();
                if (!collectionPersister.isManyToMany() && mappedBy != null && !mappedBy.isEmpty()) {
                    Object ref;
                    Serializable id;
                    int i = entityMetamodel.getPropertyIndex(mappedBy);
                    Serializable oldId = null;
                    if (oldState != null) {
                        oldId = this.getIdentifier(session, oldState[i]);
                    }
                    if (((id = this.getIdentifier(session, ref = persister.getPropertyValue(entity, i))) == null || id.equals(oldId)) && (oldId == null || oldId.equals(id))) continue;
                    if (id != null) {
                        this.evict(id, collectionPersister, session);
                    }
                    if (oldId == null) continue;
                    this.evict(oldId, collectionPersister, session);
                    continue;
                }
                if (debugEnabled) {
                    LOG.debug((Object)("Evict CollectionRegion " + role));
                }
                CollectionDataAccess cacheAccessStrategy = collectionPersister.getCacheAccessStrategy();
                SoftLock softLock = cacheAccessStrategy.lockRegion();
                session.getActionQueue().registerProcess((success, session1) -> cacheAccessStrategy.unlockRegion(softLock));
            }
        }
        catch (Exception e) {
            if (PROPAGATE_EXCEPTION) {
                throw new IllegalStateException(e);
            }
            LOG.error((Object)"", (Throwable)e);
        }
    }

    private Serializable getIdentifier(EventSource session, Object obj) {
        Serializable id = null;
        if (obj != null && (id = session.getContextEntityIdentifier(obj)) == null) {
            id = session.getSessionFactory().getMetamodel().entityPersister(obj.getClass()).getIdentifier(obj, session);
        }
        return id;
    }

    private void evict(Serializable id, CollectionPersister collectionPersister, EventSource session) {
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("Evict CollectionRegion " + collectionPersister.getRole() + " for id " + id));
        }
        AfterTransactionCompletionProcess afterTransactionProcess = new CollectionEvictCacheAction(collectionPersister, null, id, session).lockCache();
        session.getActionQueue().registerProcess(afterTransactionProcess);
    }

    private static final class CollectionEvictCacheAction
    extends CollectionAction {
        protected CollectionEvictCacheAction(CollectionPersister persister, PersistentCollection collection, Serializable key, SharedSessionContractImplementor session) {
            super(persister, collection, key, session);
        }

        @Override
        public void execute() throws HibernateException {
        }

        public AfterTransactionCompletionProcess lockCache() {
            this.beforeExecutions();
            return this.getAfterTransactionCompletionProcess();
        }
    }
}

