/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.spi;

import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.walking.spi.CollectionElementDefinition;
import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
import org.hibernate.type.CollectionType;

public interface CollectionDefinition {
    public CollectionPersister getCollectionPersister();

    public CollectionType getCollectionType();

    public CollectionIndexDefinition getIndexDefinition();

    public CollectionElementDefinition getElementDefinition();
}

