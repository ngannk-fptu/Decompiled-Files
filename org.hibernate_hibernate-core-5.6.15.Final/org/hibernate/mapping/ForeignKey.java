/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.MappingException;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Constraint;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Table;

public class ForeignKey
extends Constraint {
    private Table referencedTable;
    private String referencedEntityName;
    private String keyDefinition;
    private boolean cascadeDeleteEnabled;
    private List<Column> referencedColumns = new ArrayList<Column>();
    private boolean creationEnabled = true;

    @Override
    public String getExportIdentifier() {
        return StringHelper.qualify(this.getTable().getExportIdentifier(), "FK-" + this.getName());
    }

    public void disableCreation() {
        this.creationEnabled = false;
    }

    public boolean isCreationEnabled() {
        return this.creationEnabled;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        if ("none".equals(name)) {
            this.disableCreation();
        }
    }

    @Override
    public String sqlConstraintString(SqlStringGenerationContext context, String constraintName, String defaultCatalog, String defaultSchema) {
        Dialect dialect = context.getDialect();
        String[] columnNames = new String[this.getColumnSpan()];
        String[] referencedColumnNames = new String[this.getColumnSpan()];
        Iterator<Column> referencedColumnItr = this.isReferenceToPrimaryKey() ? this.referencedTable.getPrimaryKey().getColumnIterator() : this.referencedColumns.iterator();
        Iterator<Column> columnItr = this.getColumnIterator();
        int i = 0;
        while (columnItr.hasNext()) {
            columnNames[i] = columnItr.next().getQuotedName(dialect);
            referencedColumnNames[i] = referencedColumnItr.next().getQuotedName(dialect);
            ++i;
        }
        String result = this.keyDefinition != null ? dialect.getAddForeignKeyConstraintString(constraintName, this.keyDefinition) : dialect.getAddForeignKeyConstraintString(constraintName, columnNames, this.referencedTable.getQualifiedName(context), referencedColumnNames, this.isReferenceToPrimaryKey());
        return this.cascadeDeleteEnabled && dialect.supportsCascadeDelete() ? result + " on delete cascade" : result;
    }

    public Table getReferencedTable() {
        return this.referencedTable;
    }

    private void appendColumns(StringBuilder buf, Iterator columns) {
        while (columns.hasNext()) {
            Column column = (Column)columns.next();
            buf.append(column.getName());
            if (!columns.hasNext()) continue;
            buf.append(",");
        }
    }

    public void setReferencedTable(Table referencedTable) throws MappingException {
        this.referencedTable = referencedTable;
    }

    public void alignColumns() {
        if (this.isReferenceToPrimaryKey()) {
            this.alignColumns(this.referencedTable);
        }
    }

    private void alignColumns(Table referencedTable) {
        int referencedPkColumnSpan = referencedTable.getPrimaryKey().getColumnSpan();
        if (referencedPkColumnSpan != this.getColumnSpan()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Foreign key (").append(this.getName()).append(":").append(this.getTable().getName()).append(" [");
            this.appendColumns(sb, this.getColumnIterator());
            sb.append("])").append(") must have same number of columns as the referenced primary key (").append(referencedTable.getName()).append(" [");
            this.appendColumns(sb, referencedTable.getPrimaryKey().getColumnIterator());
            sb.append("])");
            throw new MappingException(sb.toString());
        }
        Iterator<Column> fkCols = this.getColumnIterator();
        Iterator<Column> pkCols = referencedTable.getPrimaryKey().getColumnIterator();
        while (pkCols.hasNext()) {
            fkCols.next().setLength(pkCols.next().getLength());
        }
    }

    public String getReferencedEntityName() {
        return this.referencedEntityName;
    }

    public void setReferencedEntityName(String referencedEntityName) {
        this.referencedEntityName = referencedEntityName;
    }

    public String getKeyDefinition() {
        return this.keyDefinition;
    }

    public void setKeyDefinition(String keyDefinition) {
        this.keyDefinition = keyDefinition;
    }

    @Override
    public String sqlDropString(SqlStringGenerationContext context, String defaultCatalog, String defaultSchema) {
        Dialect dialect = context.getDialect();
        String tableName = this.getTable().getQualifiedName(context);
        StringBuilder buf = new StringBuilder(dialect.getAlterTableString(tableName));
        buf.append(dialect.getDropForeignKeyString());
        if (dialect.supportsIfExistsBeforeConstraintName()) {
            buf.append("if exists ");
        }
        buf.append(dialect.quote(this.getName()));
        if (dialect.supportsIfExistsAfterConstraintName()) {
            buf.append(" if exists");
        }
        return buf.toString();
    }

    public boolean isCascadeDeleteEnabled() {
        return this.cascadeDeleteEnabled;
    }

    public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled) {
        this.cascadeDeleteEnabled = cascadeDeleteEnabled;
    }

    public boolean isPhysicalConstraint() {
        return this.referencedTable.isPhysicalTable() && this.getTable().isPhysicalTable() && !this.referencedTable.hasDenormalizedTables();
    }

    public List getReferencedColumns() {
        return this.referencedColumns;
    }

    public boolean isReferenceToPrimaryKey() {
        return this.referencedColumns.isEmpty();
    }

    public void addReferencedColumns(Iterator referencedColumnsIterator) {
        while (referencedColumnsIterator.hasNext()) {
            Selectable col = (Selectable)referencedColumnsIterator.next();
            if (col.isFormula()) continue;
            this.addReferencedColumn((Column)col);
        }
    }

    private void addReferencedColumn(Column column) {
        if (!this.referencedColumns.contains(column)) {
            this.referencedColumns.add(column);
        }
    }

    @Override
    public String toString() {
        if (!this.isReferenceToPrimaryKey()) {
            return this.getClass().getName() + '(' + this.getTable().getName() + this.getColumns() + " ref-columns:" + '(' + this.getReferencedColumns() + ") as " + this.getName() + ")";
        }
        return super.toString();
    }

    @Override
    public String generatedConstraintNamePrefix() {
        return "FK_";
    }
}

