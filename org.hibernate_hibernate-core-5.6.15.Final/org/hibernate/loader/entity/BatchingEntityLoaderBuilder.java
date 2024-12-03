/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.entity;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.entity.EntityLoader;
import org.hibernate.loader.entity.UniqueEntityLoader;
import org.hibernate.loader.entity.plan.DynamicBatchingEntityLoaderBuilder;
import org.hibernate.loader.entity.plan.LegacyBatchingEntityLoaderBuilder;
import org.hibernate.loader.entity.plan.PaddedBatchingEntityLoaderBuilder;
import org.hibernate.persister.entity.OuterJoinLoadable;

public abstract class BatchingEntityLoaderBuilder {
    public static BatchingEntityLoaderBuilder getBuilder(SessionFactoryImplementor factory) {
        switch (factory.getSessionFactoryOptions().getBatchFetchStyle()) {
            case PADDED: {
                return PaddedBatchingEntityLoaderBuilder.INSTANCE;
            }
            case DYNAMIC: {
                return DynamicBatchingEntityLoaderBuilder.INSTANCE;
            }
        }
        return LegacyBatchingEntityLoaderBuilder.INSTANCE;
    }

    public UniqueEntityLoader buildLoader(OuterJoinLoadable persister, int batchSize, LockMode lockMode, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        if (batchSize <= 1) {
            return this.buildNonBatchingLoader(persister, lockMode, factory, influencers);
        }
        return this.buildBatchingLoader(persister, batchSize, lockMode, factory, influencers);
    }

    protected UniqueEntityLoader buildNonBatchingLoader(OuterJoinLoadable persister, LockMode lockMode, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        return new EntityLoader(persister, lockMode, factory, influencers);
    }

    protected abstract UniqueEntityLoader buildBatchingLoader(OuterJoinLoadable var1, int var2, LockMode var3, SessionFactoryImplementor var4, LoadQueryInfluencers var5);

    public UniqueEntityLoader buildLoader(OuterJoinLoadable persister, int batchSize, LockOptions lockOptions, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        if (batchSize <= 1) {
            return this.buildNonBatchingLoader(persister, lockOptions, factory, influencers);
        }
        return this.buildBatchingLoader(persister, batchSize, lockOptions, factory, influencers);
    }

    protected UniqueEntityLoader buildNonBatchingLoader(OuterJoinLoadable persister, LockOptions lockOptions, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        return new EntityLoader(persister, lockOptions, factory, influencers);
    }

    protected abstract UniqueEntityLoader buildBatchingLoader(OuterJoinLoadable var1, int var2, LockOptions var3, SessionFactoryImplementor var4, LoadQueryInfluencers var5);
}

