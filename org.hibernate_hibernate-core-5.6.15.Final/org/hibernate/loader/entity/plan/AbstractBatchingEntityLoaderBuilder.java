/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.entity.plan;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.entity.BatchingEntityLoaderBuilder;
import org.hibernate.loader.entity.UniqueEntityLoader;
import org.hibernate.loader.entity.plan.EntityLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;

public abstract class AbstractBatchingEntityLoaderBuilder
extends BatchingEntityLoaderBuilder {
    @Override
    protected UniqueEntityLoader buildNonBatchingLoader(OuterJoinLoadable persister, LockMode lockMode, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        return EntityLoader.forEntity(persister).withLockMode(lockMode).withInfluencers(influencers).byPrimaryKey();
    }

    @Override
    protected UniqueEntityLoader buildNonBatchingLoader(OuterJoinLoadable persister, LockOptions lockOptions, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        return EntityLoader.forEntity(persister).withLockOptions(lockOptions).withInfluencers(influencers).byPrimaryKey();
    }
}

