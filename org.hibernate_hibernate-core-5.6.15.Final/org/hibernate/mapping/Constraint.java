/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.hibernate.HibernateException;
import org.hibernate.boot.model.relational.Exportable;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.RelationalModel;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Table;

public abstract class Constraint
implements RelationalModel,
Exportable,
Serializable {
    private String name;
    private final ArrayList<Column> columns = new ArrayList();
    private Table table;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String generateName(String prefix, Table table, Column ... columns) {
        StringBuilder sb = new StringBuilder("table`" + table.getName() + "`");
        Column[] alphabeticalColumns = (Column[])columns.clone();
        Arrays.sort(alphabeticalColumns, ColumnComparator.INSTANCE);
        for (Column column : alphabeticalColumns) {
            String columnName = column == null ? "" : column.getName();
            sb.append("column`").append(columnName).append("`");
        }
        return prefix + Constraint.hashedName(sb.toString());
    }

    public static String generateName(String prefix, Table table, List<Column> columns) {
        ArrayList<Column> defensive = new ArrayList<Column>(columns.size());
        for (Column o : columns) {
            if (!(o instanceof Column)) continue;
            defensive.add(o);
        }
        return Constraint.generateName(prefix, table, defensive.toArray(new Column[0]));
    }

    public static String hashedName(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(s.getBytes());
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            return bigInt.toString(35);
        }
        catch (NoSuchAlgorithmException e) {
            throw new HibernateException("Unable to generate a hashed Constraint name!", e);
        }
    }

    public void addColumn(Column column) {
        if (!this.columns.contains(column)) {
            this.columns.add(column);
        }
    }

    public void addColumns(Iterator columnIterator) {
        while (columnIterator.hasNext()) {
            Selectable col = (Selectable)columnIterator.next();
            if (col.isFormula()) continue;
            this.addColumn((Column)col);
        }
    }

    public boolean containsColumn(Column column) {
        return this.columns.contains(column);
    }

    public int getColumnSpan() {
        return this.columns.size();
    }

    public Column getColumn(int i) {
        return this.columns.get(i);
    }

    public Iterator<Column> getColumnIterator() {
        return this.columns.iterator();
    }

    @Deprecated
    public Iterator<Column> columnIterator() {
        return this.columns.iterator();
    }

    public Table getTable() {
        return this.table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public boolean isGenerated(Dialect dialect) {
        return true;
    }

    @Override
    public String sqlDropString(SqlStringGenerationContext context, String defaultCatalog, String defaultSchema) {
        Dialect dialect = context.getDialect();
        if (this.isGenerated(dialect)) {
            String tableName = this.getTable().getQualifiedName(context);
            return String.format(Locale.ROOT, "%s evictData constraint %s", dialect.getAlterTableString(tableName), dialect.quote(this.getName()));
        }
        return null;
    }

    @Override
    public String sqlCreateString(Mapping p, SqlStringGenerationContext context, String defaultCatalog, String defaultSchema) {
        String constraintString;
        Dialect dialect = context.getDialect();
        if (this.isGenerated(dialect) && !StringHelper.isEmpty(constraintString = this.sqlConstraintString(context, this.getName(), defaultCatalog, defaultSchema))) {
            String tableName = this.getTable().getQualifiedName(context);
            return dialect.getAlterTableString(tableName) + " " + constraintString;
        }
        return null;
    }

    public List<Column> getColumns() {
        return this.columns;
    }

    public abstract String sqlConstraintString(SqlStringGenerationContext var1, String var2, String var3, String var4);

    public String toString() {
        return this.getClass().getName() + '(' + this.getTable().getName() + this.getColumns() + ") as " + this.name;
    }

    public abstract String generatedConstraintNamePrefix();

    private static class ColumnComparator
    implements Comparator<Column> {
        public static ColumnComparator INSTANCE = new ColumnComparator();

        private ColumnComparator() {
        }

        @Override
        public int compare(Column col1, Column col2) {
            return col1.getName().compareTo(col2.getName());
        }
    }
}

