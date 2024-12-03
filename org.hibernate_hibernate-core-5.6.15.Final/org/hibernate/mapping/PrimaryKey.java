/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.mapping;

import java.util.Iterator;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Constraint;
import org.hibernate.mapping.Table;
import org.jboss.logging.Logger;

public class PrimaryKey
extends Constraint {
    private static final Logger log = Logger.getLogger(PrimaryKey.class);

    public PrimaryKey(Table table) {
        this.setTable(table);
    }

    @Override
    public void addColumn(Column column) {
        Iterator<Column> columnIterator = this.getTable().getColumnIterator();
        while (columnIterator.hasNext()) {
            Column next = columnIterator.next();
            if (!next.getCanonicalName().equals(column.getCanonicalName())) continue;
            next.setNullable(false);
            log.debugf("Forcing column [%s] to be non-null as it is part of the primary key for table [%s]", (Object)column.getCanonicalName(), (Object)this.getTableNameForLogging(column));
        }
        super.addColumn(column);
    }

    protected String getTableNameForLogging(Column column) {
        if (this.getTable() != null) {
            if (this.getTable().getNameIdentifier() != null) {
                return this.getTable().getNameIdentifier().getCanonicalName();
            }
            return "<unknown>";
        }
        if (column.getValue() != null && column.getValue().getTable() != null) {
            return column.getValue().getTable().getNameIdentifier().getCanonicalName();
        }
        return "<unknown>";
    }

    public String sqlConstraintString(Dialect dialect) {
        StringBuilder buf = new StringBuilder("primary key (");
        Iterator<Column> iter = this.getColumnIterator();
        while (iter.hasNext()) {
            buf.append(iter.next().getQuotedName(dialect));
            if (!iter.hasNext()) continue;
            buf.append(", ");
        }
        return buf.append(')').toString();
    }

    @Override
    public String sqlConstraintString(SqlStringGenerationContext context, String constraintName, String defaultCatalog, String defaultSchema) {
        Dialect dialect = context.getDialect();
        StringBuilder buf = new StringBuilder(dialect.getAddPrimaryKeyConstraintString(constraintName)).append('(');
        Iterator<Column> iter = this.getColumnIterator();
        while (iter.hasNext()) {
            buf.append(iter.next().getQuotedName(dialect));
            if (!iter.hasNext()) continue;
            buf.append(", ");
        }
        return buf.append(')').toString();
    }

    @Override
    public String generatedConstraintNamePrefix() {
        return "PK_";
    }

    @Override
    public String getExportIdentifier() {
        return StringHelper.qualify(this.getTable().getExportIdentifier(), "PK-" + this.getName());
    }
}

