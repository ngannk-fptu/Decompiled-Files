/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import org.hibernate.loader.plan.spi.QuerySpace;
import org.hibernate.persister.collection.CollectionPersister;

public interface CollectionQuerySpace
extends QuerySpace {
    public CollectionPersister getCollectionPersister();
}

