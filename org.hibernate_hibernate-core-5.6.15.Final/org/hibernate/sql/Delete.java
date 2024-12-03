/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.dialect.Dialect;

public class Delete {
    protected String tableName;
    protected String versionColumnName;
    protected String where;
    protected String comment;
    protected Map<String, String> primaryKeyColumns = new LinkedHashMap<String, String>();

    public Delete setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public Delete setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public String toStatementString() {
        StringBuilder buf = new StringBuilder(this.tableName.length() + 10);
        if (this.comment != null) {
            buf.append("/* ").append(Dialect.escapeComment(this.comment)).append(" */ ");
        }
        buf.append("delete from ").append(this.tableName);
        if (this.where != null || !this.primaryKeyColumns.isEmpty() || this.versionColumnName != null) {
            buf.append(" where ");
        }
        boolean conditionsAppended = false;
        Iterator<Map.Entry<String, String>> iter = this.primaryKeyColumns.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> e = iter.next();
            buf.append(e.getKey()).append('=').append(e.getValue());
            if (iter.hasNext()) {
                buf.append(" and ");
            }
            conditionsAppended = true;
        }
        if (this.where != null) {
            if (conditionsAppended) {
                buf.append(" and ");
            }
            buf.append(this.where);
            conditionsAppended = true;
        }
        if (this.versionColumnName != null) {
            if (conditionsAppended) {
                buf.append(" and ");
            }
            buf.append(this.versionColumnName).append("=?");
        }
        return buf.toString();
    }

    public Delete setWhere(String where) {
        this.where = where;
        return this;
    }

    public Delete addWhereFragment(String fragment) {
        this.where = this.where == null ? fragment : this.where + " and " + fragment;
        return this;
    }

    public Delete setPrimaryKeyColumnNames(String[] columnNames) {
        this.primaryKeyColumns.clear();
        this.addPrimaryKeyColumns(columnNames);
        return this;
    }

    public Delete addPrimaryKeyColumns(String[] columnNames) {
        for (String columnName : columnNames) {
            this.addPrimaryKeyColumn(columnName, "?");
        }
        return this;
    }

    public Delete addPrimaryKeyColumns(String[] columnNames, boolean[] includeColumns, String[] valueExpressions) {
        for (int i = 0; i < columnNames.length; ++i) {
            if (!includeColumns[i]) continue;
            this.addPrimaryKeyColumn(columnNames[i], valueExpressions[i]);
        }
        return this;
    }

    public Delete addPrimaryKeyColumns(String[] columnNames, String[] valueExpressions) {
        for (int i = 0; i < columnNames.length; ++i) {
            this.addPrimaryKeyColumn(columnNames[i], valueExpressions[i]);
        }
        return this;
    }

    public Delete addPrimaryKeyColumn(String columnName, String valueExpression) {
        this.primaryKeyColumns.put(columnName, valueExpression);
        return this;
    }

    public Delete setVersionColumnName(String versionColumnName) {
        this.versionColumnName = versionColumnName;
        return this;
    }
}

