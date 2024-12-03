/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.LiteralType;

public class Insert {
    protected String tableName;
    protected String comment;
    protected Map<String, String> columns = new LinkedHashMap<String, String>();
    private Dialect dialect;

    public Insert(Dialect dialect) {
        this.dialect = dialect;
    }

    protected Dialect getDialect() {
        return this.dialect;
    }

    public Insert setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public Insert addColumn(String columnName) {
        return this.addColumn(columnName, "?");
    }

    public Insert addColumns(String[] columnNames) {
        for (String columnName : columnNames) {
            this.addColumn(columnName);
        }
        return this;
    }

    public Insert addColumns(String[] columnNames, boolean[] insertable) {
        for (int i = 0; i < columnNames.length; ++i) {
            if (!insertable[i]) continue;
            this.addColumn(columnNames[i]);
        }
        return this;
    }

    public Insert addColumns(String[] columnNames, boolean[] insertable, String[] valueExpressions) {
        for (int i = 0; i < columnNames.length; ++i) {
            if (!insertable[i]) continue;
            this.addColumn(columnNames[i], valueExpressions[i]);
        }
        return this;
    }

    public Insert addColumn(String columnName, String valueExpression) {
        this.columns.put(columnName, valueExpression);
        return this;
    }

    public Insert addColumn(String columnName, Object value, LiteralType type) throws Exception {
        return this.addColumn(columnName, type.objectToSQLString(value, this.dialect));
    }

    public Insert addIdentityColumn(String columnName) {
        String value = this.dialect.getIdentityColumnSupport().getIdentityInsertString();
        if (value != null) {
            this.addColumn(columnName, value);
        }
        return this;
    }

    public Insert setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public String toStatementString() {
        StringBuilder buf = new StringBuilder(this.columns.size() * 15 + this.tableName.length() + 10);
        if (this.comment != null) {
            buf.append("/* ").append(Dialect.escapeComment(this.comment)).append(" */ ");
        }
        buf.append("insert into ").append(this.tableName);
        if (this.columns.size() == 0) {
            if (!this.dialect.supportsNoColumnsInsert()) throw new MappingException(String.format("The INSERT statement for table [%s] contains no column, and this is not supported by [%s]", this.tableName, this.dialect));
            buf.append(' ').append(this.dialect.getNoColumnsInsertString());
            return buf.toString();
        } else {
            buf.append(" (");
            Iterator<String> iter = this.columns.keySet().iterator();
            while (iter.hasNext()) {
                buf.append(iter.next());
                if (!iter.hasNext()) continue;
                buf.append(", ");
            }
            buf.append(") values (");
            iter = this.columns.values().iterator();
            while (iter.hasNext()) {
                buf.append(iter.next());
                if (!iter.hasNext()) continue;
                buf.append(", ");
            }
            buf.append(')');
        }
        return buf.toString();
    }
}

