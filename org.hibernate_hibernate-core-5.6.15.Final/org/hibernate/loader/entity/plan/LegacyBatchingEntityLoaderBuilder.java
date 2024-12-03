/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.entity.plan;

import java.io.Serializable;
import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.engine.internal.BatchFetchQueueHelper;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.loader.entity.UniqueEntityLoader;
import org.hibernate.loader.entity.plan.AbstractBatchingEntityLoaderBuilder;
import org.hibernate.loader.entity.plan.BatchingEntityLoader;
import org.hibernate.loader.entity.plan.EntityLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;

public class LegacyBatchingEntityLoaderBuilder
extends AbstractBatchingEntityLoaderBuilder {
    public static final LegacyBatchingEntityLoaderBuilder INSTANCE = new LegacyBatchingEntityLoaderBuilder();

    @Override
    protected UniqueEntityLoader buildBatchingLoader(OuterJoinLoadable persister, int batchSize, LockMode lockMode, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        return new LegacyBatchingEntityLoader(persister, batchSize, lockMode, factory, influencers);
    }

    @Override
    protected UniqueEntityLoader buildBatchingLoader(OuterJoinLoadable persister, int batchSize, LockOptions lockOptions, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        return new LegacyBatchingEntityLoader(persister, batchSize, lockOptions, factory, influencers);
    }

    public static class LegacyBatchingEntityLoader
    extends BatchingEntityLoader {
        private final int[] batchSizes;
        private final EntityLoader[] loaders;

        public LegacyBatchingEntityLoader(OuterJoinLoadable persister, int maxBatchSize, LockMode lockMode, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) {
            this(persister, maxBatchSize, lockMode, null, factory, loadQueryInfluencers);
        }

        public LegacyBatchingEntityLoader(OuterJoinLoadable persister, int maxBatchSize, LockOptions lockOptions, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) {
            this(persister, maxBatchSize, null, lockOptions, factory, loadQueryInfluencers);
        }

        protected LegacyBatchingEntityLoader(OuterJoinLoadable persister, int maxBatchSize, LockMode lockMode, LockOptions lockOptions, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) {
            super(persister);
            this.batchSizes = ArrayHelper.getBatchSizes(maxBatchSize);
            this.loaders = new EntityLoader[this.batchSizes.length];
            EntityLoader.Builder entityLoaderBuilder = EntityLoader.forEntity(persister).withInfluencers(loadQueryInfluencers).withLockMode(lockMode).withLockOptions(lockOptions);
            this.loaders[0] = entityLoaderBuilder.withBatchSize(this.batchSizes[0]).byPrimaryKey();
            for (int i = 1; i < this.batchSizes.length; ++i) {
                this.loaders[i] = entityLoaderBuilder.withEntityLoaderTemplate(this.loaders[0]).withBatchSize(this.batchSizes[i]).byPrimaryKey();
            }
        }

        @Override
        public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions) {
            return this.load(id, optionalObject, session, lockOptions, null);
        }

        @Override
        public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions, Boolean readOnly) {
            Serializable[] batch = session.getPersistenceContextInternal().getBatchFetchQueue().getEntityBatch(this.persister(), id, this.batchSizes[0], this.persister().getEntityMode());
            for (int i = 0; i < this.batchSizes.length - 1; ++i) {
                int smallBatchSize = this.batchSizes[i];
                if (batch[smallBatchSize - 1] == null) continue;
                Serializable[] smallBatch = new Serializable[smallBatchSize];
                System.arraycopy(batch, 0, smallBatch, 0, smallBatchSize);
                List results = this.loaders[i].loadEntityBatch(session, smallBatch, this.persister().getIdentifierType(), optionalObject, this.persister().getEntityName(), id, this.persister(), lockOptions, readOnly);
                BatchFetchQueueHelper.removeNotFoundBatchLoadableEntityKeys(smallBatch, results, this.persister(), session);
                return this.getObjectFromList(results, id, session);
            }
            Object result = this.loaders[this.batchSizes.length - 1].load(id, optionalObject, session, lockOptions);
            if (result == null) {
                BatchFetchQueueHelper.removeBatchLoadableEntityKey(id, this.persister(), session);
            }
            return result;
        }
    }
}

