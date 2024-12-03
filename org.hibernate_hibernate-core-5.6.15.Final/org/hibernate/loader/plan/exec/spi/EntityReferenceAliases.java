/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.spi;

import org.hibernate.loader.EntityAliases;

public interface EntityReferenceAliases {
    public String getTableAlias();

    public EntityAliases getColumnAliases();
}

