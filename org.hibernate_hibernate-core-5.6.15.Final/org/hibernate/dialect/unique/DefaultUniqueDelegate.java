/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.unique;

import java.util.Iterator;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.unique.UniqueDelegate;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;

public class DefaultUniqueDelegate
implements UniqueDelegate {
    protected final Dialect dialect;

    public DefaultUniqueDelegate(Dialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public String getColumnDefinitionUniquenessFragment(Column column, SqlStringGenerationContext context) {
        return "";
    }

    @Override
    public String getTableCreationUniqueConstraintsFragment(Table table, SqlStringGenerationContext context) {
        return "";
    }

    @Override
    public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata, SqlStringGenerationContext context) {
        String tableName = context.format(uniqueKey.getTable().getQualifiedTableName());
        String constraintName = this.dialect.quote(uniqueKey.getName());
        return this.dialect.getAlterTableString(tableName) + " add constraint " + constraintName + " " + this.uniqueConstraintSql(uniqueKey);
    }

    protected String uniqueConstraintSql(UniqueKey uniqueKey) {
        StringBuilder sb = new StringBuilder();
        sb.append("unique (");
        Iterator<Column> columnIterator = uniqueKey.columnIterator();
        while (columnIterator.hasNext()) {
            Column column = columnIterator.next();
            sb.append(column.getQuotedName(this.dialect));
            if (uniqueKey.getColumnOrderMap().containsKey(column)) {
                sb.append(" ").append(uniqueKey.getColumnOrderMap().get(column));
            }
            if (!columnIterator.hasNext()) continue;
            sb.append(", ");
        }
        return sb.append(')').toString();
    }

    @Override
    public String getAlterTableToDropUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata, SqlStringGenerationContext context) {
        String tableName = context.format(uniqueKey.getTable().getQualifiedTableName());
        StringBuilder buf = new StringBuilder(this.dialect.getAlterTableString(tableName));
        buf.append(this.getDropUnique());
        if (this.dialect.supportsIfExistsBeforeConstraintName()) {
            buf.append("if exists ");
        }
        buf.append(this.dialect.quote(uniqueKey.getName()));
        if (this.dialect.supportsIfExistsAfterConstraintName()) {
            buf.append(" if exists");
        }
        return buf.toString();
    }

    protected String getDropUnique() {
        return " drop constraint ";
    }
}

