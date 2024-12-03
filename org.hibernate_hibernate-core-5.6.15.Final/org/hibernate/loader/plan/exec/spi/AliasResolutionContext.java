/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.spi;

import org.hibernate.loader.plan.exec.spi.CollectionReferenceAliases;
import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;

public interface AliasResolutionContext {
    public String resolveSqlTableAliasFromQuerySpaceUid(String var1);

    public EntityReferenceAliases resolveEntityReferenceAliases(String var1);

    public CollectionReferenceAliases resolveCollectionReferenceAliases(String var1);
}

