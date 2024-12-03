/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.entity.plan;

import java.io.Serializable;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.engine.internal.BatchFetchQueueHelper;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.loader.entity.plan.BatchingEntityLoader;
import org.hibernate.loader.entity.plan.EntityLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;

public class PaddedBatchingEntityLoader
extends BatchingEntityLoader {
    private final int[] batchSizes;
    private final EntityLoader[] loaders;

    public PaddedBatchingEntityLoader(OuterJoinLoadable persister, int maxBatchSize, LockOptions lockOptions, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) {
        super(persister);
        this.batchSizes = ArrayHelper.getBatchSizes(maxBatchSize);
        this.loaders = new EntityLoader[this.batchSizes.length];
        EntityLoader.Builder entityLoaderBuilder = EntityLoader.forEntity(persister).withInfluencers(loadQueryInfluencers).withLockOptions(lockOptions);
        this.loaders[0] = entityLoaderBuilder.withBatchSize(this.batchSizes[0]).byPrimaryKey();
        for (int i = 1; i < this.batchSizes.length; ++i) {
            this.loaders[i] = entityLoaderBuilder.withEntityLoaderTemplate(this.loaders[0]).withBatchSize(this.batchSizes[i]).byPrimaryKey();
        }
        this.validate(maxBatchSize);
    }

    private void validate(int max) {
        if (this.batchSizes[0] != max) {
            throw new HibernateException("Unexpected batch size spread");
        }
        if (this.batchSizes[this.batchSizes.length - 1] != 1) {
            throw new HibernateException("Unexpected batch size spread");
        }
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session) {
        return this.load(id, optionalObject, session, LockOptions.NONE, null);
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions) {
        return this.load(id, optionalObject, session, lockOptions, null);
    }

    @Override
    public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions, Boolean readOnly) {
        Serializable[] batch = session.getPersistenceContextInternal().getBatchFetchQueue().getEntityBatch(this.persister(), id, this.batchSizes[0], this.persister().getEntityMode());
        int numberOfIds = ArrayHelper.countNonNull(batch);
        if (numberOfIds <= 1) {
            Object result = this.loaders[this.batchSizes.length - 1].load(id, optionalObject, session, lockOptions);
            if (result == null) {
                BatchFetchQueueHelper.removeBatchLoadableEntityKey(id, this.persister(), session);
            }
            return result;
        }
        int indexToUse = this.batchSizes.length - 1;
        int i = 0;
        while (i < this.batchSizes.length - 1 && this.batchSizes[i] >= numberOfIds) {
            indexToUse = i++;
        }
        Serializable[] idsToLoad = new Serializable[this.batchSizes[indexToUse]];
        System.arraycopy(batch, 0, idsToLoad, 0, numberOfIds);
        for (int i2 = numberOfIds; i2 < this.batchSizes[indexToUse]; ++i2) {
            idsToLoad[i2] = id;
        }
        List results = this.loaders[indexToUse].loadEntityBatch(session, idsToLoad, this.persister().getIdentifierType(), optionalObject, this.persister().getEntityName(), id, this.persister(), lockOptions, readOnly);
        BatchFetchQueueHelper.removeNotFoundBatchLoadableEntityKeys(idsToLoad, results, this.persister(), session);
        return this.getObjectFromList(results, id, session);
    }
}

