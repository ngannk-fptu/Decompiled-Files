/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.custom;

import org.hibernate.LockMode;
import org.hibernate.loader.EntityAliases;
import org.hibernate.loader.custom.FetchReturn;
import org.hibernate.loader.custom.NonScalarReturn;

public class EntityFetchReturn
extends FetchReturn {
    private final EntityAliases entityAliases;

    public EntityFetchReturn(String alias, EntityAliases entityAliases, NonScalarReturn owner, String ownerProperty, LockMode lockMode) {
        super(owner, ownerProperty, alias, lockMode);
        this.entityAliases = entityAliases;
    }

    public EntityAliases getEntityAliases() {
        return this.entityAliases;
    }
}

