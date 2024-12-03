/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.entity.plan;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.LoadEvent;
import org.hibernate.event.spi.LoadEventListener;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.loader.entity.CacheEntityLoaderHelper;
import org.hibernate.loader.entity.plan.EntityLoader;
import org.hibernate.persister.entity.MultiLoadOptions;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.type.Type;

public class MultiEntityLoadingSupport {
    public static List<?> multiLoad(OuterJoinLoadable persister, Serializable[] ids, SharedSessionContractImplementor session, MultiLoadOptions loadOptions) {
        if (loadOptions.isOrderReturnEnabled()) {
            return MultiEntityLoadingSupport.performOrderedMultiLoad(persister, ids, session, loadOptions);
        }
        return MultiEntityLoadingSupport.performUnorderedMultiLoad(persister, ids, session, loadOptions);
    }

    private static List performOrderedMultiLoad(OuterJoinLoadable persister, Serializable[] ids, SharedSessionContractImplementor session, MultiLoadOptions loadOptions) {
        assert (loadOptions.isOrderReturnEnabled());
        ArrayList result = CollectionHelper.arrayList(ids.length);
        LockOptions lockOptions = loadOptions.getLockOptions() == null ? new LockOptions(LockMode.NONE) : loadOptions.getLockOptions();
        int maxBatchSize = loadOptions.getBatchSize() != null && loadOptions.getBatchSize() > 0 ? loadOptions.getBatchSize().intValue() : session.getJdbcServices().getJdbcEnvironment().getDialect().getDefaultBatchLoadSizingStrategy().determineOptimalBatchLoadSize(persister.getIdentifierType().getColumnSpan(session.getFactory()), ids.length);
        ArrayList<Serializable> idsInBatch = new ArrayList<Serializable>();
        ArrayList<Integer> elementPositionsLoadedByBatch = new ArrayList<Integer>();
        for (int i = 0; i < ids.length; ++i) {
            Serializable id = ids[i];
            EntityKey entityKey = new EntityKey(id, persister);
            if (loadOptions.isSessionCheckingEnabled() || loadOptions.isSecondLevelCacheCheckingEnabled()) {
                CacheEntityLoaderHelper.PersistenceContextEntry persistenceContextEntry;
                LoadEvent loadEvent = new LoadEvent(id, persister.getMappedClass().getName(), lockOptions, (EventSource)session, null);
                Object managedEntity = null;
                if (loadOptions.isSessionCheckingEnabled() && (managedEntity = (persistenceContextEntry = CacheEntityLoaderHelper.INSTANCE.loadFromSessionCache(loadEvent, entityKey, LoadEventListener.GET)).getEntity()) != null && !loadOptions.isReturnOfDeletedEntitiesEnabled() && !persistenceContextEntry.isManaged()) {
                    result.add(i, null);
                    continue;
                }
                if (managedEntity == null && loadOptions.isSecondLevelCacheCheckingEnabled()) {
                    managedEntity = CacheEntityLoaderHelper.INSTANCE.loadFromSecondLevelCache(loadEvent, persister, entityKey);
                }
                if (managedEntity != null) {
                    result.add(i, managedEntity);
                    continue;
                }
            }
            idsInBatch.add(ids[i]);
            if (idsInBatch.size() >= maxBatchSize) {
                MultiEntityLoadingSupport.performOrderedBatchLoad(idsInBatch, lockOptions, persister, session);
            }
            result.add(i, entityKey);
            elementPositionsLoadedByBatch.add(i);
        }
        if (!idsInBatch.isEmpty()) {
            MultiEntityLoadingSupport.performOrderedBatchLoad(idsInBatch, lockOptions, persister, session);
        }
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        for (Integer position : elementPositionsLoadedByBatch) {
            EntityEntry entry;
            EntityKey entityKey = (EntityKey)result.get(position);
            Object entity = persistenceContext.getEntity(entityKey);
            if (!(entity == null || loadOptions.isReturnOfDeletedEntitiesEnabled() || (entry = persistenceContext.getEntry(entity)).getStatus() != Status.DELETED && entry.getStatus() != Status.GONE)) {
                entity = null;
            }
            result.set(position, entity);
        }
        return result;
    }

    private static void performOrderedBatchLoad(List<Serializable> idsInBatch, LockOptions lockOptions, OuterJoinLoadable persister, SharedSessionContractImplementor session) {
        EntityLoader entityLoader = EntityLoader.forEntity(persister).withInfluencers(session.getLoadQueryInfluencers()).withLockOptions(lockOptions).withBatchSize(idsInBatch.size()).byPrimaryKey();
        entityLoader.loadEntityBatch(idsInBatch.toArray(new Serializable[0]), persister, lockOptions, session);
        idsInBatch.clear();
    }

    protected static List performUnorderedMultiLoad(OuterJoinLoadable persister, Serializable[] ids, SharedSessionContractImplementor session, MultiLoadOptions loadOptions) {
        LockOptions lockOptions;
        assert (!loadOptions.isOrderReturnEnabled());
        ArrayList result = CollectionHelper.arrayList(ids.length);
        LockOptions lockOptions2 = lockOptions = loadOptions.getLockOptions() == null ? new LockOptions(LockMode.NONE) : loadOptions.getLockOptions();
        if (loadOptions.isSessionCheckingEnabled() || loadOptions.isSecondLevelCacheCheckingEnabled()) {
            boolean foundAnyManagedEntities = false;
            ArrayList<Serializable> nonManagedIds = new ArrayList<Serializable>();
            for (Serializable id : ids) {
                EntityKey entityKey = new EntityKey(id, persister);
                LoadEvent loadEvent = new LoadEvent(id, persister.getMappedClass().getName(), lockOptions, (EventSource)session, null);
                Object managedEntity = null;
                CacheEntityLoaderHelper.PersistenceContextEntry persistenceContextEntry = CacheEntityLoaderHelper.INSTANCE.loadFromSessionCache(loadEvent, entityKey, LoadEventListener.GET);
                if (loadOptions.isSessionCheckingEnabled() && (managedEntity = persistenceContextEntry.getEntity()) != null && !loadOptions.isReturnOfDeletedEntitiesEnabled() && !persistenceContextEntry.isManaged()) {
                    foundAnyManagedEntities = true;
                    result.add(null);
                    continue;
                }
                if (managedEntity == null && loadOptions.isSecondLevelCacheCheckingEnabled()) {
                    managedEntity = CacheEntityLoaderHelper.INSTANCE.loadFromSecondLevelCache(loadEvent, persister, entityKey);
                }
                if (managedEntity != null) {
                    foundAnyManagedEntities = true;
                    result.add(managedEntity);
                    continue;
                }
                nonManagedIds.add(id);
            }
            if (foundAnyManagedEntities) {
                if (nonManagedIds.isEmpty()) {
                    return result;
                }
                ids = nonManagedIds.toArray((Serializable[])Array.newInstance(ids.getClass().getComponentType(), nonManagedIds.size()));
            }
        }
        int numberOfIdsLeft = ids.length;
        int maxBatchSize = loadOptions.getBatchSize() != null && loadOptions.getBatchSize() > 0 ? loadOptions.getBatchSize().intValue() : session.getJdbcServices().getJdbcEnvironment().getDialect().getDefaultBatchLoadSizingStrategy().determineOptimalBatchLoadSize(persister.getIdentifierType().getColumnSpan(session.getFactory()), numberOfIdsLeft);
        int idPosition = 0;
        while (numberOfIdsLeft > 0) {
            int batchSize = Math.min(numberOfIdsLeft, maxBatchSize);
            EntityLoader entityLoader = EntityLoader.forEntity(persister).withInfluencers(session.getLoadQueryInfluencers()).withLockOptions(lockOptions).withBatchSize(batchSize).byPrimaryKey();
            Serializable[] idsInBatch = new Serializable[batchSize];
            System.arraycopy(ids, idPosition, idsInBatch, 0, batchSize);
            List<?> batchResults = entityLoader.loadEntityBatch(idsInBatch, persister, lockOptions, session);
            result.addAll(batchResults);
            numberOfIdsLeft -= batchSize;
            idPosition += batchSize;
        }
        return result;
    }

    public static QueryParameters buildMultiLoadQueryParameters(OuterJoinLoadable persister, Serializable[] ids, LockOptions lockOptions) {
        Object[] types = new Type[ids.length];
        Arrays.fill(types, persister.getIdentifierType());
        QueryParameters qp = new QueryParameters();
        qp.setOptionalEntityName(persister.getEntityName());
        qp.setPositionalParameterTypes((Type[])types);
        qp.setPositionalParameterValues(ids);
        qp.setLockOptions(lockOptions);
        qp.setOptionalObject(null);
        qp.setOptionalId(null);
        return qp;
    }
}

