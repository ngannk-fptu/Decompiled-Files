/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal;

import java.util.Iterator;
import java.util.Map;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.QualifiedNameImpl;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.tool.schema.spi.Exporter;

public class StandardIndexExporter
implements Exporter<Index> {
    private final Dialect dialect;

    public StandardIndexExporter(Dialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public String[] getSqlCreateStrings(Index index, Metadata metadata, SqlStringGenerationContext context) {
        JdbcEnvironment jdbcEnvironment = metadata.getDatabase().getJdbcEnvironment();
        String tableName = context.format(index.getTable().getQualifiedTableName());
        String indexNameForCreation = this.dialect.qualifyIndexName() ? context.format(new QualifiedNameImpl(index.getTable().getQualifiedTableName().getCatalogName(), index.getTable().getQualifiedTableName().getSchemaName(), jdbcEnvironment.getIdentifierHelper().toIdentifier(index.getQuotedName(this.dialect)))) : index.getName();
        StringBuilder buf = new StringBuilder().append("create index ").append(indexNameForCreation).append(" on ").append(tableName).append(" (");
        boolean first = true;
        Iterator<Column> columnItr = index.getColumnIterator();
        Map<Column, String> columnOrderMap = index.getColumnOrderMap();
        while (columnItr.hasNext()) {
            Column column = columnItr.next();
            if (first) {
                first = false;
            } else {
                buf.append(", ");
            }
            buf.append(column.getQuotedName(this.dialect));
            if (!columnOrderMap.containsKey(column)) continue;
            buf.append(" ").append(columnOrderMap.get(column));
        }
        buf.append(")");
        return new String[]{buf.toString()};
    }

    @Override
    public String[] getSqlDropStrings(Index index, Metadata metadata, SqlStringGenerationContext context) {
        if (!this.dialect.dropConstraints()) {
            return NO_COMMANDS;
        }
        String tableName = context.format(index.getTable().getQualifiedTableName());
        String indexNameForCreation = this.dialect.qualifyIndexName() ? StringHelper.qualify(tableName, index.getName()) : index.getName();
        return new String[]{"drop index " + indexNameForCreation};
    }
}

