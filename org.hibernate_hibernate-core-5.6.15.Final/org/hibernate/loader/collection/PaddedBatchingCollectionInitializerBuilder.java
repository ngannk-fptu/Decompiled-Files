/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.collection;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.loader.Loader;
import org.hibernate.loader.collection.BasicCollectionLoader;
import org.hibernate.loader.collection.BatchingCollectionInitializer;
import org.hibernate.loader.collection.BatchingCollectionInitializerBuilder;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.loader.collection.OneToManyLoader;
import org.hibernate.persister.collection.QueryableCollection;

public class PaddedBatchingCollectionInitializerBuilder
extends BatchingCollectionInitializerBuilder {
    public static final PaddedBatchingCollectionInitializerBuilder INSTANCE = new PaddedBatchingCollectionInitializerBuilder();

    @Override
    public CollectionInitializer createRealBatchingCollectionInitializer(QueryableCollection persister, int maxBatchSize, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        int[] batchSizes = ArrayHelper.getBatchSizes(maxBatchSize);
        Loader[] loaders = new Loader[batchSizes.length];
        for (int i = 0; i < batchSizes.length; ++i) {
            loaders[i] = new BasicCollectionLoader(persister, batchSizes[i], factory, loadQueryInfluencers);
        }
        return new PaddedBatchingCollectionInitializer(persister, batchSizes, loaders);
    }

    @Override
    public CollectionInitializer createRealBatchingOneToManyInitializer(QueryableCollection persister, int maxBatchSize, SessionFactoryImplementor factory, LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        int[] batchSizes = ArrayHelper.getBatchSizes(maxBatchSize);
        Loader[] loaders = new Loader[batchSizes.length];
        for (int i = 0; i < batchSizes.length; ++i) {
            loaders[i] = new OneToManyLoader(persister, batchSizes[i], factory, loadQueryInfluencers);
        }
        return new PaddedBatchingCollectionInitializer(persister, batchSizes, loaders);
    }

    private static class PaddedBatchingCollectionInitializer
    extends BatchingCollectionInitializer {
        private final int[] batchSizes;
        private final Loader[] loaders;

        public PaddedBatchingCollectionInitializer(QueryableCollection persister, int[] batchSizes, Loader[] loaders) {
            super(persister);
            this.batchSizes = batchSizes;
            this.loaders = loaders;
        }

        @Override
        public void initialize(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
            Serializable[] batch = session.getPersistenceContextInternal().getBatchFetchQueue().getCollectionBatch(this.collectionPersister(), id, this.batchSizes[0]);
            int numberOfIds = ArrayHelper.countNonNull(batch);
            if (numberOfIds <= 1) {
                this.loaders[this.batchSizes.length - 1].loadCollection(session, id, this.collectionPersister().getKeyType());
                return;
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
            this.loaders[indexToUse].loadCollectionBatch(session, idsToLoad, this.collectionPersister().getKeyType());
        }
    }
}

