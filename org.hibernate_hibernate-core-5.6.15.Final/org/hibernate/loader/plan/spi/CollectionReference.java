/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import org.hibernate.loader.PropertyPath;
import org.hibernate.loader.plan.spi.CollectionFetchableElement;
import org.hibernate.loader.plan.spi.CollectionFetchableIndex;
import org.hibernate.persister.collection.CollectionPersister;

public interface CollectionReference {
    public String getQuerySpaceUid();

    public CollectionPersister getCollectionPersister();

    public CollectionFetchableIndex getIndexGraph();

    public CollectionFetchableElement getElementGraph();

    public PropertyPath getPropertyPath();

    public boolean allowElementJoin();

    public boolean allowIndexJoin();
}

