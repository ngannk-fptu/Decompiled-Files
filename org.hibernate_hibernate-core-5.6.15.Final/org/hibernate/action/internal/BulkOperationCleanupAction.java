/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.internal;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.action.spi.AfterTransactionCompletionProcess;
import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
import org.hibernate.action.spi.Executable;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Queryable;

public class BulkOperationCleanupAction
implements Executable,
Serializable {
    private final Serializable[] affectedTableSpaces;
    private final Set<EntityCleanup> entityCleanups = new HashSet<EntityCleanup>();
    private final Set<CollectionCleanup> collectionCleanups = new HashSet<CollectionCleanup>();
    private final Set<NaturalIdCleanup> naturalIdCleanups = new HashSet<NaturalIdCleanup>();

    public BulkOperationCleanupAction(SharedSessionContractImplementor session, Queryable ... affectedQueryables) {
        SessionFactoryImplementor factory = session.getFactory();
        LinkedHashSet<String> spacesList = new LinkedHashSet<String>();
        for (Queryable persister : affectedQueryables) {
            Set<String> roles;
            EntityDataAccess entityDataAccess;
            spacesList.addAll(Arrays.asList((String[])persister.getQuerySpaces()));
            if (persister.canWriteToCache() && (entityDataAccess = persister.getCacheAccessStrategy()) != null) {
                this.entityCleanups.add(new EntityCleanup(entityDataAccess, session));
            }
            if (persister.hasNaturalIdentifier() && persister.hasNaturalIdCache()) {
                this.naturalIdCleanups.add(new NaturalIdCleanup(persister.getNaturalIdCacheAccessStrategy(), session));
            }
            if ((roles = factory.getMetamodel().getCollectionRolesByEntityParticipant(persister.getEntityName())) == null) continue;
            for (String role : roles) {
                CollectionPersister collectionPersister = factory.getMetamodel().collectionPersister(role);
                if (!collectionPersister.hasCache()) continue;
                this.collectionCleanups.add(new CollectionCleanup(collectionPersister.getCacheAccessStrategy(), session));
            }
        }
        this.affectedTableSpaces = (Serializable[])spacesList.toArray(new String[0]);
    }

    public BulkOperationCleanupAction(SharedSessionContractImplementor session, Set tableSpaces) {
        LinkedHashSet<String> spacesList = new LinkedHashSet<String>(tableSpaces);
        SessionFactoryImplementor factory = session.getFactory();
        MetamodelImplementor metamodel = factory.getMetamodel();
        for (EntityPersister persister : metamodel.entityPersisters().values()) {
            Set<String> roles;
            String[] entitySpaces = (String[])persister.getQuerySpaces();
            if (!this.affectedEntity(tableSpaces, (Serializable[])entitySpaces)) continue;
            spacesList.addAll(Arrays.asList(entitySpaces));
            if (persister.canWriteToCache()) {
                this.entityCleanups.add(new EntityCleanup(persister.getCacheAccessStrategy(), session));
            }
            if (persister.hasNaturalIdentifier() && persister.hasNaturalIdCache()) {
                this.naturalIdCleanups.add(new NaturalIdCleanup(persister.getNaturalIdCacheAccessStrategy(), session));
            }
            if ((roles = metamodel.getCollectionRolesByEntityParticipant(persister.getEntityName())) == null) continue;
            for (String role : roles) {
                CollectionPersister collectionPersister = metamodel.collectionPersister(role);
                if (!collectionPersister.hasCache()) continue;
                this.collectionCleanups.add(new CollectionCleanup(collectionPersister.getCacheAccessStrategy(), session));
            }
        }
        this.affectedTableSpaces = (Serializable[])spacesList.toArray(new String[0]);
    }

    private boolean affectedEntity(Set<?> affectedTableSpaces, Serializable[] checkTableSpaces) {
        if (affectedTableSpaces == null || affectedTableSpaces.isEmpty()) {
            return true;
        }
        for (Serializable checkTableSpace : checkTableSpaces) {
            if (!affectedTableSpaces.contains(checkTableSpace)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Serializable[] getPropertySpaces() {
        return this.affectedTableSpaces;
    }

    @Override
    public BeforeTransactionCompletionProcess getBeforeTransactionCompletionProcess() {
        return null;
    }

    @Override
    public AfterTransactionCompletionProcess getAfterTransactionCompletionProcess() {
        return (success, session) -> {
            for (EntityCleanup entityCleanup : this.entityCleanups) {
                entityCleanup.release();
            }
            this.entityCleanups.clear();
            for (NaturalIdCleanup naturalIdCleanup : this.naturalIdCleanups) {
                naturalIdCleanup.release();
            }
            this.naturalIdCleanups.clear();
            for (CollectionCleanup collectionCleanup : this.collectionCleanups) {
                collectionCleanup.release();
            }
            this.collectionCleanups.clear();
        };
    }

    @Override
    public void beforeExecutions() throws HibernateException {
    }

    @Override
    public void execute() throws HibernateException {
    }

    @Override
    public void afterDeserialize(SharedSessionContractImplementor session) {
    }

    private static class NaturalIdCleanup
    implements Serializable {
        private final NaturalIdDataAccess naturalIdCacheAccessStrategy;
        private final SoftLock cacheLock;

        public NaturalIdCleanup(NaturalIdDataAccess naturalIdCacheAccessStrategy, SharedSessionContractImplementor session) {
            this.naturalIdCacheAccessStrategy = naturalIdCacheAccessStrategy;
            this.cacheLock = naturalIdCacheAccessStrategy.lockRegion();
            naturalIdCacheAccessStrategy.removeAll(session);
        }

        private void release() {
            this.naturalIdCacheAccessStrategy.unlockRegion(this.cacheLock);
        }
    }

    private static class CollectionCleanup
    implements Serializable {
        private final CollectionDataAccess cacheAccess;
        private final SoftLock cacheLock;

        private CollectionCleanup(CollectionDataAccess cacheAccess, SharedSessionContractImplementor session) {
            this.cacheAccess = cacheAccess;
            this.cacheLock = cacheAccess.lockRegion();
            cacheAccess.removeAll(session);
        }

        private void release() {
            this.cacheAccess.unlockRegion(this.cacheLock);
        }
    }

    private static class EntityCleanup
    implements Serializable {
        private final EntityDataAccess cacheAccess;
        private final SoftLock cacheLock;

        private EntityCleanup(EntityDataAccess cacheAccess, SharedSessionContractImplementor session) {
            this.cacheAccess = cacheAccess;
            this.cacheLock = cacheAccess.lockRegion();
            cacheAccess.removeAll(session);
        }

        private void release() {
            this.cacheAccess.unlockRegion(this.cacheLock);
        }
    }
}

