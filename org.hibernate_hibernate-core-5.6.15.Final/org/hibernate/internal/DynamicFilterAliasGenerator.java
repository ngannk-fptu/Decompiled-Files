/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import org.hibernate.internal.FilterAliasGenerator;
import org.hibernate.persister.entity.AbstractEntityPersister;

public class DynamicFilterAliasGenerator
implements FilterAliasGenerator {
    private String[] tables;
    private String rootAlias;

    public DynamicFilterAliasGenerator(String[] tables, String rootAlias) {
        this.tables = tables;
        this.rootAlias = rootAlias;
    }

    @Override
    public String getAlias(String table) {
        if (table == null) {
            return this.rootAlias;
        }
        return AbstractEntityPersister.generateTableAlias(this.rootAlias, AbstractEntityPersister.getTableId(table, this.tables));
    }
}

