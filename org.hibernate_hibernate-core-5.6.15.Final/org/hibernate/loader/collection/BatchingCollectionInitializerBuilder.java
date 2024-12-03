/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.collection;

import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.collection.BasicCollectionLoader;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.loader.collection.DynamicBatchingCollectionInitializerBuilder;
import org.hibernate.loader.collection.OneToManyLoader;
import org.hibernate.loader.collection.PaddedBatchingCollectionInitializerBuilder;
import org.hibernate.loader.collection.plan.LegacyBatchingCollectionInitializerBuilder;
import org.hibernate.persister.collection.QueryableCollection;

public abstract class BatchingCollectionInitializerBuilder {
    public static BatchingCollectionInitializerBuilder getBuilder(SessionFactoryImplementor factory) {
        switch (factory.getSettings().getBatchFetchStyle()) {
            case PADDED: {
                return PaddedBatchingCollectionInitializerBuilder.INSTANCE;
            }
            case DYNAMIC: {
                return DynamicBatchingCollectionInitializerBuilder.INSTANCE;
            }
        }
        return LegacyBatchingCollectionInitializerBuilder.INSTANCE;
    }

    public CollectionInitializer createBatchingCollectionInitializer(QueryableCollection persister, int maxBatchSize, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        if (maxBatchSize <= 1) {
            return this.buildNonBatchingLoader(persister, factory, influencers);
        }
        return this.createRealBatchingCollectionInitializer(persister, maxBatchSize, factory, influencers);
    }

    protected abstract CollectionInitializer createRealBatchingCollectionInitializer(QueryableCollection var1, int var2, SessionFactoryImplementor var3, LoadQueryInfluencers var4);

    public CollectionInitializer createBatchingOneToManyInitializer(QueryableCollection persister, int maxBatchSize, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        if (maxBatchSize <= 1) {
            return this.buildNonBatchingLoader(persister, factory, influencers);
        }
        return this.createRealBatchingOneToManyInitializer(persister, maxBatchSize, factory, influencers);
    }

    protected abstract CollectionInitializer createRealBatchingOneToManyInitializer(QueryableCollection var1, int var2, SessionFactoryImplementor var3, LoadQueryInfluencers var4);

    protected CollectionInitializer buildNonBatchingLoader(QueryableCollection persister, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        return persister.isOneToMany() ? new OneToManyLoader(persister, factory, influencers) : new BasicCollectionLoader(persister, factory, influencers);
    }
}

