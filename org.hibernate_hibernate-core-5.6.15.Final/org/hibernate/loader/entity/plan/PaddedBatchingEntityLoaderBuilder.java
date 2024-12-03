/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.entity.plan;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.entity.UniqueEntityLoader;
import org.hibernate.loader.entity.plan.AbstractBatchingEntityLoaderBuilder;
import org.hibernate.loader.entity.plan.PaddedBatchingEntityLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;

public class PaddedBatchingEntityLoaderBuilder
extends AbstractBatchingEntityLoaderBuilder {
    public static final PaddedBatchingEntityLoaderBuilder INSTANCE = new PaddedBatchingEntityLoaderBuilder();

    @Override
    protected UniqueEntityLoader buildBatchingLoader(OuterJoinLoadable persister, int batchSize, LockMode lockMode, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        return this.buildBatchingLoader(persister, batchSize, LockOptions.interpret(lockMode), factory, influencers);
    }

    @Override
    protected UniqueEntityLoader buildBatchingLoader(OuterJoinLoadable persister, int batchSize, LockOptions lockOptions, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        return new PaddedBatchingEntityLoader(persister, batchSize, lockOptions, factory, influencers);
    }
}

