/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.internal;

import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.loader.plan.exec.spi.CollectionReferenceAliases;
import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;

public class CollectionReferenceAliasesImpl
implements CollectionReferenceAliases {
    private final String tableAlias;
    private final String manyToManyAssociationTableAlias;
    private final CollectionAliases collectionAliases;
    private final EntityReferenceAliases entityElementAliases;

    public CollectionReferenceAliasesImpl(String tableAlias, String manyToManyAssociationTableAlias, CollectionAliases collectionAliases, EntityReferenceAliases entityElementAliases) {
        this.tableAlias = tableAlias;
        this.manyToManyAssociationTableAlias = manyToManyAssociationTableAlias;
        this.collectionAliases = collectionAliases;
        this.entityElementAliases = entityElementAliases;
    }

    @Override
    public String getCollectionTableAlias() {
        return StringHelper.isNotEmpty(this.manyToManyAssociationTableAlias) ? this.manyToManyAssociationTableAlias : this.tableAlias;
    }

    @Override
    public String getElementTableAlias() {
        return this.tableAlias;
    }

    @Override
    public CollectionAliases getCollectionColumnAliases() {
        return this.collectionAliases;
    }

    @Override
    public EntityReferenceAliases getEntityElementAliases() {
        return this.entityElementAliases;
    }
}

