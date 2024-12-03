/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Exportable;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.RelationalModel;
import org.hibernate.mapping.Table;

public class Index
implements RelationalModel,
Exportable,
Serializable {
    private Table table;
    private List<Column> columns = new ArrayList<Column>();
    private Map<Column, String> columnOrderMap = new HashMap<Column, String>();
    private Identifier name;

    @Override
    public String sqlCreateString(Mapping mapping, SqlStringGenerationContext context, String defaultCatalog, String defaultSchema) throws HibernateException {
        Dialect dialect = context.getDialect();
        return Index.buildSqlCreateIndexString(context, this.getQuotedName(dialect), this.getTable(), this.getColumnIterator(), this.columnOrderMap, false, defaultCatalog, defaultSchema);
    }

    public static String buildSqlDropIndexString(SqlStringGenerationContext context, Table table, String name, String defaultCatalog, String defaultSchema) {
        return Index.buildSqlDropIndexString(name, table.getQualifiedName(context));
    }

    public static String buildSqlDropIndexString(String name, String tableName) {
        return "drop index " + StringHelper.qualify(tableName, name);
    }

    public static String buildSqlCreateIndexString(SqlStringGenerationContext context, String name, Table table, Iterator<Column> columns, Map<Column, String> columnOrderMap, boolean unique, String defaultCatalog, String defaultSchema) {
        return Index.buildSqlCreateIndexString(context.getDialect(), name, table.getQualifiedName(context), columns, columnOrderMap, unique);
    }

    public static String buildSqlCreateIndexString(Dialect dialect, String name, String tableName, Iterator<Column> columns, Map<Column, String> columnOrderMap, boolean unique) {
        StringBuilder buf = new StringBuilder("create").append(unique ? " unique" : "").append(" index ").append(dialect.qualifyIndexName() ? name : StringHelper.unqualify(name)).append(" on ").append(tableName).append(" (");
        while (columns.hasNext()) {
            Column column = columns.next();
            buf.append(column.getQuotedName(dialect));
            if (columnOrderMap.containsKey(column)) {
                buf.append(" ").append(columnOrderMap.get(column));
            }
            if (!columns.hasNext()) continue;
            buf.append(", ");
        }
        buf.append(")");
        return buf.toString();
    }

    public static String buildSqlCreateIndexString(SqlStringGenerationContext context, String name, Table table, Iterator<Column> columns, Map<Column, String> columnOrderMap, boolean unique, Metadata metadata) {
        String tableName = context.format(table.getQualifiedTableName());
        return Index.buildSqlCreateIndexString(context.getDialect(), name, tableName, columns, columnOrderMap, unique);
    }

    public String sqlConstraintString(Dialect dialect) {
        StringBuilder buf = new StringBuilder(" index (");
        Iterator<Column> iter = this.getColumnIterator();
        while (iter.hasNext()) {
            buf.append(iter.next().getQuotedName(dialect));
            if (!iter.hasNext()) continue;
            buf.append(", ");
        }
        return buf.append(')').toString();
    }

    @Override
    public String sqlDropString(SqlStringGenerationContext context, String defaultCatalog, String defaultSchema) {
        Dialect dialect = context.getDialect();
        return "drop index " + StringHelper.qualify(this.table.getQualifiedName(context), this.getQuotedName(dialect));
    }

    public Table getTable() {
        return this.table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public int getColumnSpan() {
        return this.columns.size();
    }

    public Iterator<Column> getColumnIterator() {
        return this.columns.iterator();
    }

    public Map<Column, String> getColumnOrderMap() {
        return Collections.unmodifiableMap(this.columnOrderMap);
    }

    public void addColumn(Column column) {
        if (!this.columns.contains(column)) {
            this.columns.add(column);
        }
    }

    public void addColumn(Column column, String order) {
        this.addColumn(column);
        if (StringHelper.isNotEmpty(order)) {
            this.columnOrderMap.put(column, order);
        }
    }

    public void addColumns(Iterator extraColumns) {
        while (extraColumns.hasNext()) {
            this.addColumn((Column)extraColumns.next());
        }
    }

    public boolean containsColumn(Column column) {
        return this.columns.contains(column);
    }

    public String getName() {
        return this.name == null ? null : this.name.getText();
    }

    public void setName(String name) {
        this.name = Identifier.toIdentifier(name);
    }

    public String getQuotedName(Dialect dialect) {
        return this.name == null ? null : this.name.render(dialect);
    }

    public String toString() {
        return this.getClass().getName() + "(" + this.getName() + ")";
    }

    @Override
    public String getExportIdentifier() {
        return StringHelper.qualify(this.getTable().getExportIdentifier(), "IDX-" + this.getName());
    }
}

