/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.PersistentObjectException;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.internal.Cascade;
import org.hibernate.engine.internal.CascadePoint;
import org.hibernate.engine.spi.ActionQueue;
import org.hibernate.engine.spi.CascadingActions;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.internal.EvictVisitor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.RefreshEvent;
import org.hibernate.event.spi.RefreshEventListener;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.CollectionType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

public class DefaultRefreshEventListener
implements RefreshEventListener {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DefaultRefreshEventListener.class);

    @Override
    public void onRefresh(RefreshEvent event) throws HibernateException {
        this.onRefresh(event, new IdentityHashMap(10));
    }

    @Override
    public void onRefresh(RefreshEvent event, Map refreshedAlready) {
        Object result;
        LockMode currentLockMode;
        EntityKey key;
        Serializable id;
        EntityPersister persister;
        EventSource source = event.getSession();
        boolean isTransient = event.getEntityName() != null ? !source.contains(event.getEntityName(), event.getObject()) : !source.contains(event.getObject());
        PersistenceContext persistenceContext = source.getPersistenceContextInternal();
        if (persistenceContext.reassociateIfUninitializedProxy(event.getObject())) {
            if (isTransient) {
                source.setReadOnly(event.getObject(), source.isDefaultReadOnly());
            }
            return;
        }
        Object object = persistenceContext.unproxyAndReassociate(event.getObject());
        if (refreshedAlready.containsKey(object)) {
            LOG.trace("Already refreshed");
            return;
        }
        EntityEntry e = persistenceContext.getEntry(object);
        if (e == null) {
            persister = source.getEntityPersister(event.getEntityName(), object);
            id = persister.getIdentifier(object, event.getSession());
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Refreshing transient {0}", MessageHelper.infoString(persister, id, source.getFactory()));
            }
            if (persistenceContext.getEntry(key = source.generateEntityKey(id, persister)) != null) {
                throw new PersistentObjectException("attempted to refresh transient instance when persistent instance was already associated with the Session: " + MessageHelper.infoString(persister, id, source.getFactory()));
            }
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Refreshing ", MessageHelper.infoString(e.getPersister(), e.getId(), source.getFactory()));
            }
            if (!e.isExistsInDatabase()) {
                throw new UnresolvableObjectException(e.getId(), "this instance does not yet exist as a row in the database");
            }
            persister = e.getPersister();
            id = e.getId();
        }
        refreshedAlready.put(object, object);
        Cascade.cascade(CascadingActions.REFRESH, CascadePoint.BEFORE_REFRESH, source, persister, object, refreshedAlready);
        if (e != null) {
            key = source.generateEntityKey(id, persister);
            persistenceContext.removeEntity(key);
            if (persister.hasCollections()) {
                new EvictVisitor(source, object).process(object, persister);
            }
        }
        if (persister.canWriteToCache()) {
            Object previousVersion = null;
            if (persister.isVersionPropertyGenerated()) {
                previousVersion = persister.getVersion(object);
            }
            EntityDataAccess cache = persister.getCacheAccessStrategy();
            Object ck = cache.generateCacheKey(id, persister, source.getFactory(), source.getTenantIdentifier());
            SoftLock lock = cache.lockItem(source, ck, previousVersion);
            cache.remove(source, ck);
            source.getActionQueue().registerProcess((success, session) -> cache.unlockItem(session, ck, lock));
        }
        this.evictCachedCollections(persister, id, source);
        String previousFetchProfile = source.getLoadQueryInfluencers().getInternalFetchProfile();
        source.getLoadQueryInfluencers().setInternalFetchProfile("refresh");
        LockOptions lockOptionsToUse = event.getLockOptions();
        LockMode requestedLockMode = lockOptionsToUse.getLockMode();
        LockMode postRefreshLockMode = null;
        if (e != null && (currentLockMode = e.getLockMode()).greaterThan(requestedLockMode)) {
            lockOptionsToUse = LockOptions.copy(event.getLockOptions(), new LockOptions());
            if (currentLockMode == LockMode.WRITE || currentLockMode == LockMode.PESSIMISTIC_WRITE || currentLockMode == LockMode.PESSIMISTIC_READ) {
                lockOptionsToUse.setLockMode(LockMode.READ);
                postRefreshLockMode = currentLockMode;
            } else {
                lockOptionsToUse.setLockMode(currentLockMode);
            }
        }
        if ((result = persister.load(id, object, lockOptionsToUse, (SharedSessionContractImplementor)source)) != null) {
            if (postRefreshLockMode != null) {
                persistenceContext.getEntry(result).setLockMode(postRefreshLockMode);
            }
            if (!persister.isMutable()) {
                source.setReadOnly(result, true);
            } else {
                source.setReadOnly(result, e == null ? source.isDefaultReadOnly() : e.isReadOnly());
            }
        }
        source.getLoadQueryInfluencers().setInternalFetchProfile(previousFetchProfile);
        UnresolvableObjectException.throwIfNull(result, id, persister.getEntityName());
    }

    private void evictCachedCollections(EntityPersister persister, Serializable id, EventSource source) {
        this.evictCachedCollections(persister.getPropertyTypes(), id, source);
    }

    private void evictCachedCollections(Type[] types, Serializable id, EventSource source) throws HibernateException {
        ActionQueue actionQueue = source.getActionQueue();
        SessionFactoryImplementor factory = source.getFactory();
        MetamodelImplementor metamodel = factory.getMetamodel();
        for (Type type : types) {
            if (type.isCollectionType()) {
                CollectionPersister collectionPersister = metamodel.collectionPersister(((CollectionType)type).getRole());
                if (!collectionPersister.hasCache()) continue;
                CollectionDataAccess cache = collectionPersister.getCacheAccessStrategy();
                Object ck = cache.generateCacheKey(id, collectionPersister, factory, source.getTenantIdentifier());
                SoftLock lock = cache.lockItem(source, ck, null);
                cache.remove(source, ck);
                actionQueue.registerProcess((success, session) -> cache.unlockItem(session, ck, lock));
                continue;
            }
            if (!type.isComponentType()) continue;
            CompositeType actype = (CompositeType)type;
            this.evictCachedCollections(actype.getSubtypes(), id, source);
        }
    }
}

