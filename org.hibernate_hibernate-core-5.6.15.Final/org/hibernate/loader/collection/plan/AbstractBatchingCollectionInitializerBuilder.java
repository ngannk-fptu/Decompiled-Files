/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.collection.plan;

import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.collection.BatchingCollectionInitializerBuilder;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.loader.collection.plan.CollectionLoader;
import org.hibernate.persister.collection.QueryableCollection;

public abstract class AbstractBatchingCollectionInitializerBuilder
extends BatchingCollectionInitializerBuilder {
    @Override
    protected CollectionInitializer buildNonBatchingLoader(QueryableCollection persister, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        return CollectionLoader.forCollection(persister).withInfluencers(influencers).byKey();
    }
}

