/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal;

import java.util.Iterator;
import java.util.Locale;
import org.hibernate.AssertionFailure;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.tool.schema.spi.Exporter;

public class StandardForeignKeyExporter
implements Exporter<ForeignKey> {
    private static final String COLUMN_MISMATCH_MSG = "Number of referencing columns [%s] did not match number of referenced columns [%s] in foreign-key [%s] from [%s] to [%s]";
    private final Dialect dialect;

    public StandardForeignKeyExporter(Dialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public String[] getSqlCreateStrings(ForeignKey foreignKey, Metadata metadata, SqlStringGenerationContext context) {
        Iterator<Object> targetItr;
        if (!this.dialect.hasAlterTable()) {
            return NO_COMMANDS;
        }
        if (!foreignKey.isCreationEnabled()) {
            return NO_COMMANDS;
        }
        if (!foreignKey.isPhysicalConstraint()) {
            return NO_COMMANDS;
        }
        int numberOfColumns = foreignKey.getColumnSpan();
        String[] columnNames = new String[numberOfColumns];
        String[] targetColumnNames = new String[numberOfColumns];
        if (foreignKey.isReferenceToPrimaryKey()) {
            if (numberOfColumns != foreignKey.getReferencedTable().getPrimaryKey().getColumnSpan()) {
                throw new AssertionFailure(String.format(Locale.ENGLISH, COLUMN_MISMATCH_MSG, numberOfColumns, foreignKey.getReferencedTable().getPrimaryKey().getColumnSpan(), foreignKey.getName(), foreignKey.getTable().getName(), foreignKey.getReferencedTable().getName()));
            }
            targetItr = foreignKey.getReferencedTable().getPrimaryKey().getColumnIterator();
        } else {
            if (numberOfColumns != foreignKey.getReferencedColumns().size()) {
                throw new AssertionFailure(String.format(Locale.ENGLISH, COLUMN_MISMATCH_MSG, numberOfColumns, foreignKey.getReferencedColumns().size(), foreignKey.getName(), foreignKey.getTable().getName(), foreignKey.getReferencedTable().getName()));
            }
            targetItr = foreignKey.getReferencedColumns().iterator();
        }
        int i = 0;
        Iterator<Column> itr = foreignKey.getColumnIterator();
        while (itr.hasNext()) {
            columnNames[i] = itr.next().getQuotedName(this.dialect);
            targetColumnNames[i] = ((Column)targetItr.next()).getQuotedName(this.dialect);
            ++i;
        }
        String sourceTableName = context.format(foreignKey.getTable().getQualifiedTableName());
        String targetTableName = context.format(foreignKey.getReferencedTable().getQualifiedTableName());
        StringBuilder buffer = new StringBuilder(this.dialect.getAlterTableString(sourceTableName)).append(foreignKey.getKeyDefinition() != null ? this.dialect.getAddForeignKeyConstraintString(foreignKey.getName(), foreignKey.getKeyDefinition()) : this.dialect.getAddForeignKeyConstraintString(foreignKey.getName(), columnNames, targetTableName, targetColumnNames, foreignKey.isReferenceToPrimaryKey()));
        if (this.dialect.supportsCascadeDelete() && foreignKey.isCascadeDeleteEnabled()) {
            buffer.append(" on delete cascade");
        }
        return new String[]{buffer.toString()};
    }

    @Override
    public String[] getSqlDropStrings(ForeignKey foreignKey, Metadata metadata, SqlStringGenerationContext context) {
        if (!this.dialect.hasAlterTable()) {
            return NO_COMMANDS;
        }
        if (!foreignKey.isCreationEnabled()) {
            return NO_COMMANDS;
        }
        if (!foreignKey.isPhysicalConstraint()) {
            return NO_COMMANDS;
        }
        String sourceTableName = context.format(foreignKey.getTable().getQualifiedTableName());
        return new String[]{this.getSqlDropStrings(sourceTableName, foreignKey, this.dialect)};
    }

    private String getSqlDropStrings(String tableName, ForeignKey foreignKey, Dialect dialect) {
        StringBuilder buf = new StringBuilder(dialect.getAlterTableString(tableName));
        buf.append(dialect.getDropForeignKeyString());
        if (dialect.supportsIfExistsBeforeConstraintName()) {
            buf.append("if exists ");
        }
        buf.append(dialect.quote(foreignKey.getName()));
        if (dialect.supportsIfExistsAfterConstraintName()) {
            buf.append(" if exists");
        }
        return buf.toString();
    }
}

