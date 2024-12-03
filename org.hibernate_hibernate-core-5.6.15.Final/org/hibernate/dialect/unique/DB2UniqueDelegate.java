/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.unique;

import java.util.Iterator;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.unique.DefaultUniqueDelegate;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.UniqueKey;

public class DB2UniqueDelegate
extends DefaultUniqueDelegate {
    public DB2UniqueDelegate(Dialect dialect) {
        super(dialect);
    }

    @Override
    public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata, SqlStringGenerationContext context) {
        if (this.hasNullable(uniqueKey)) {
            return Index.buildSqlCreateIndexString(context, uniqueKey.getName(), uniqueKey.getTable(), uniqueKey.columnIterator(), uniqueKey.getColumnOrderMap(), true, metadata);
        }
        return super.getAlterTableToAddUniqueKeyCommand(uniqueKey, metadata, context);
    }

    @Override
    public String getAlterTableToDropUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata, SqlStringGenerationContext context) {
        if (this.hasNullable(uniqueKey)) {
            return Index.buildSqlDropIndexString(uniqueKey.getName(), context.format(uniqueKey.getTable().getQualifiedTableName()));
        }
        return super.getAlterTableToDropUniqueKeyCommand(uniqueKey, metadata, context);
    }

    private boolean hasNullable(UniqueKey uniqueKey) {
        Iterator<Column> iter = uniqueKey.columnIterator();
        while (iter.hasNext()) {
            if (!iter.next().isNullable()) continue;
            return true;
        }
        return false;
    }
}

