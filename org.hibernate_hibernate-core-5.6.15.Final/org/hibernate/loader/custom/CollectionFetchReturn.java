/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.custom;

import org.hibernate.LockMode;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.loader.EntityAliases;
import org.hibernate.loader.custom.FetchReturn;
import org.hibernate.loader.custom.NonScalarReturn;

public class CollectionFetchReturn
extends FetchReturn {
    private final CollectionAliases collectionAliases;
    private final EntityAliases elementEntityAliases;

    public CollectionFetchReturn(String alias, NonScalarReturn owner, String ownerProperty, CollectionAliases collectionAliases, EntityAliases elementEntityAliases, LockMode lockMode) {
        super(owner, ownerProperty, alias, lockMode);
        this.collectionAliases = collectionAliases;
        this.elementEntityAliases = elementEntityAliases;
    }

    public CollectionAliases getCollectionAliases() {
        return this.collectionAliases;
    }

    public EntityAliases getElementEntityAliases() {
        return this.elementEntityAliases;
    }
}

