/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.collection.plan;

import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.QueryableCollection;

public abstract class BatchingCollectionInitializer
implements CollectionInitializer {
    private final QueryableCollection collectionPersister;

    public BatchingCollectionInitializer(QueryableCollection collectionPersister) {
        this.collectionPersister = collectionPersister;
    }

    public CollectionPersister getCollectionPersister() {
        return this.collectionPersister;
    }
}

