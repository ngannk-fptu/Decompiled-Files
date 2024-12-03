/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.collection.plan;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.loader.Loader;
import org.hibernate.loader.collection.BasicCollectionLoader;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.loader.collection.OneToManyLoader;
import org.hibernate.loader.collection.plan.AbstractBatchingCollectionInitializerBuilder;
import org.hibernate.loader.collection.plan.BatchingCollectionInitializer;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.type.Type;

public class LegacyBatchingCollectionInitializerBuilder
extends AbstractBatchingCollectionInitializerBuilder {
    public static final LegacyBatchingCollectionInitializerBuilder INSTANCE = new LegacyBatchingCollectionInitializerBuilder();

    @Override
    public CollectionInitializer createRealBatchingCollectionInitializer(QueryableCollection persister, int maxBatchSize, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        int[] batchSizes = ArrayHelper.getBatchSizes(maxBatchSize);
        Loader[] loaders = new Loader[batchSizes.length];
        for (int i = 0; i < batchSizes.length; ++i) {
            loaders[i] = new BasicCollectionLoader(persister, batchSizes[i], factory, loadQueryInfluencers);
        }
        return new LegacyBatchingCollectionInitializer(persister, batchSizes, loaders);
    }

    @Override
    public CollectionInitializer createRealBatchingOneToManyInitializer(QueryableCollection persister, int maxBatchSize, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        int[] batchSizes = ArrayHelper.getBatchSizes(maxBatchSize);
        Loader[] loaders = new Loader[batchSizes.length];
        for (int i = 0; i < batchSizes.length; ++i) {
            loaders[i] = new OneToManyLoader(persister, batchSizes[i], factory, loadQueryInfluencers);
        }
        return new LegacyBatchingCollectionInitializer(persister, batchSizes, loaders);
    }

    public static class LegacyBatchingCollectionInitializer
    extends BatchingCollectionInitializer {
        private final int[] batchSizes;
        private final Loader[] loaders;

        public LegacyBatchingCollectionInitializer(QueryableCollection persister, int[] batchSizes, Loader[] loaders) {
            super(persister);
            this.batchSizes = batchSizes;
            this.loaders = loaders;
        }

        @Override
        public void initialize(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
            CollectionPersister collectionPersister = this.getCollectionPersister();
            Serializable[] batch = session.getPersistenceContext().getBatchFetchQueue().getCollectionBatch(collectionPersister, id, this.batchSizes[0]);
            Type keyType = collectionPersister.getKeyType();
            for (int i = 0; i < this.batchSizes.length - 1; ++i) {
                int smallBatchSize = this.batchSizes[i];
                if (batch[smallBatchSize - 1] == null) continue;
                Serializable[] smallBatch = new Serializable[smallBatchSize];
                System.arraycopy(batch, 0, smallBatch, 0, smallBatchSize);
                this.loaders[i].loadCollectionBatch(session, smallBatch, keyType);
                return;
            }
            this.loaders[this.batchSizes.length - 1].loadCollection(session, id, keyType);
        }
    }
}

