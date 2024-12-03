/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.spi;

import org.hibernate.loader.CollectionAliases;
import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;

public interface CollectionReferenceAliases {
    public String getCollectionTableAlias();

    public String getElementTableAlias();

    public CollectionAliases getCollectionColumnAliases();

    public EntityReferenceAliases getEntityElementAliases();
}

