/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.unique;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.unique.DefaultUniqueDelegate;
import org.hibernate.mapping.UniqueKey;

public class InformixUniqueDelegate
extends DefaultUniqueDelegate {
    public InformixUniqueDelegate(Dialect dialect) {
        super(dialect);
    }

    @Override
    public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata, SqlStringGenerationContext context) {
        String tableName = context.format(uniqueKey.getTable().getQualifiedTableName());
        String constraintName = this.dialect.quote(uniqueKey.getName());
        return this.dialect.getAlterTableString(tableName) + " add constraint " + this.uniqueConstraintSql(uniqueKey) + " constraint " + constraintName;
    }
}

