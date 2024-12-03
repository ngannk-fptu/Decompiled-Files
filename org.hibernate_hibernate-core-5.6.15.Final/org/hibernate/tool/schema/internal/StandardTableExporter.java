/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.InitCommand;
import org.hibernate.boot.model.relational.QualifiedNameParser;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Constraint;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.tool.schema.spi.Exporter;

public class StandardTableExporter
implements Exporter<Table> {
    protected final Dialect dialect;

    public StandardTableExporter(Dialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public String[] getSqlCreateStrings(Table table, Metadata metadata, SqlStringGenerationContext context) {
        QualifiedNameParser.NameParts tableName = new QualifiedNameParser.NameParts(Identifier.toIdentifier(table.getCatalog(), table.isCatalogQuoted()), Identifier.toIdentifier(table.getSchema(), table.isSchemaQuoted()), table.getNameIdentifier());
        String formattedTableName = context.format(tableName);
        StringBuilder buf = new StringBuilder(this.tableCreateString(table.hasPrimaryKey())).append(' ').append(formattedTableName).append(" (");
        boolean isPrimaryKeyIdentity = table.hasPrimaryKey() && table.getIdentifierValue() != null && table.getIdentifierValue().isIdentityColumn(metadata.getIdentifierGeneratorFactory(), this.dialect);
        String pkColName = null;
        if (table.hasPrimaryKey()) {
            Column pkColumn = table.getPrimaryKey().getColumns().iterator().next();
            pkColName = pkColumn.getQuotedName(this.dialect);
        }
        Iterator<Column> columnItr = table.getColumnIterator();
        boolean isFirst = true;
        while (columnItr.hasNext()) {
            String columnComment;
            Column col = columnItr.next();
            if (isFirst) {
                isFirst = false;
            } else {
                buf.append(", ");
            }
            String colName = col.getQuotedName(this.dialect);
            buf.append(colName).append(' ');
            if (isPrimaryKeyIdentity && colName.equals(pkColName)) {
                if (this.dialect.getIdentityColumnSupport().hasDataTypeInIdentityColumn()) {
                    buf.append(col.getSqlType(this.dialect, metadata));
                }
                buf.append(' ').append(this.dialect.getIdentityColumnSupport().getIdentityColumnString(col.getSqlTypeCode(metadata)));
            } else {
                buf.append(col.getSqlType(this.dialect, metadata));
                String defaultValue = col.getDefaultValue();
                if (defaultValue != null) {
                    buf.append(" default ").append(defaultValue);
                }
                if (col.isNullable()) {
                    buf.append(this.dialect.getNullColumnString());
                } else {
                    buf.append(" not null");
                }
            }
            if (col.isUnique()) {
                String keyName = Constraint.generateName("UK_", table, col);
                UniqueKey uk = table.getOrCreateUniqueKey(keyName);
                uk.addColumn(col);
                buf.append(this.dialect.getUniqueDelegate().getColumnDefinitionUniquenessFragment(col, context));
            }
            if (col.getCheckConstraint() != null && this.dialect.supportsColumnCheck()) {
                buf.append(" check (").append(col.getCheckConstraint()).append(")");
            }
            if ((columnComment = col.getComment()) == null) continue;
            buf.append(this.dialect.getColumnComment(columnComment));
        }
        if (table.hasPrimaryKey()) {
            buf.append(", ").append(table.getPrimaryKey().sqlConstraintString(this.dialect));
        }
        buf.append(this.dialect.getUniqueDelegate().getTableCreationUniqueConstraintsFragment(table, context));
        this.applyTableCheck(table, buf);
        buf.append(')');
        if (table.getComment() != null) {
            buf.append(this.dialect.getTableComment(table.getComment()));
        }
        this.applyTableTypeString(buf);
        ArrayList<String> sqlStrings = new ArrayList<String>();
        sqlStrings.add(buf.toString());
        this.applyComments(table, formattedTableName, sqlStrings);
        this.applyInitCommands(table, sqlStrings, context);
        return sqlStrings.toArray(new String[sqlStrings.size()]);
    }

    @Deprecated
    protected void applyComments(Table table, QualifiedTableName tableName, List<String> sqlStrings) {
        this.applyComments(table, tableName.toString(), sqlStrings);
    }

    protected void applyComments(Table table, String formattedTableName, List<String> sqlStrings) {
        if (this.dialect.supportsCommentOn()) {
            if (table.getComment() != null) {
                sqlStrings.add("comment on table " + formattedTableName + " is '" + table.getComment() + "'");
            }
            Iterator<Column> iter = table.getColumnIterator();
            while (iter.hasNext()) {
                Column column = iter.next();
                String columnComment = column.getComment();
                if (columnComment == null) continue;
                sqlStrings.add("comment on column " + formattedTableName + '.' + column.getQuotedName(this.dialect) + " is '" + columnComment + "'");
            }
        }
    }

    protected void applyInitCommands(Table table, List<String> sqlStrings, SqlStringGenerationContext context) {
        for (InitCommand initCommand : table.getInitCommands(context)) {
            Collections.addAll(sqlStrings, initCommand.getInitCommands());
        }
    }

    protected void applyTableTypeString(StringBuilder buf) {
        buf.append(this.dialect.getTableTypeString());
    }

    protected void applyTableCheck(Table table, StringBuilder buf) {
        if (this.dialect.supportsTableCheck()) {
            Iterator<String> checkConstraints = table.getCheckConstraintsIterator();
            while (checkConstraints.hasNext()) {
                buf.append(", check (").append(checkConstraints.next()).append(')');
            }
        }
    }

    protected String tableCreateString(boolean hasPrimaryKey) {
        return hasPrimaryKey ? this.dialect.getCreateTableString() : this.dialect.getCreateMultisetTableString();
    }

    @Override
    public String[] getSqlDropStrings(Table table, Metadata metadata, SqlStringGenerationContext context) {
        StringBuilder buf = new StringBuilder("drop table ");
        if (this.dialect.supportsIfExistsBeforeTableName()) {
            buf.append("if exists ");
        }
        QualifiedNameParser.NameParts tableName = new QualifiedNameParser.NameParts(Identifier.toIdentifier(table.getCatalog(), table.isCatalogQuoted()), Identifier.toIdentifier(table.getSchema(), table.isSchemaQuoted()), table.getNameIdentifier());
        buf.append(context.format(tableName)).append(this.dialect.getCascadeConstraintsString());
        if (this.dialect.supportsIfExistsAfterTableName()) {
            buf.append(" if exists");
        }
        return new String[]{buf.toString()};
    }
}

