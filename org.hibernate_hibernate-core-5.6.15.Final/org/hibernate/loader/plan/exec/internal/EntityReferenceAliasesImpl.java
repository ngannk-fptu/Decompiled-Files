/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.internal;

import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.EntityAliases;
import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;

public class EntityReferenceAliasesImpl
implements EntityReferenceAliases {
    private final String tableAlias;
    private final EntityAliases columnAliases;

    public EntityReferenceAliasesImpl(String tableAlias, EntityAliases columnAliases) {
        this.tableAlias = StringHelper.safeInterning(tableAlias);
        this.columnAliases = columnAliases;
    }

    @Override
    public String getTableAlias() {
        return this.tableAlias;
    }

    @Override
    public EntityAliases getColumnAliases() {
        return this.columnAliases;
    }
}

