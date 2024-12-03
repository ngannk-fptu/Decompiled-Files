/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.LiteralType;

public class Update {
    protected String tableName;
    protected String versionColumnName;
    protected String where;
    protected String assignments;
    protected String comment;
    protected Map<String, String> primaryKeyColumns = new LinkedHashMap<String, String>();
    protected Map<String, String> columns = new LinkedHashMap<String, String>();
    protected Map<String, String> whereColumns = new LinkedHashMap<String, String>();
    private Dialect dialect;

    public Update(Dialect dialect) {
        this.dialect = dialect;
    }

    public String getTableName() {
        return this.tableName;
    }

    public Update appendAssignmentFragment(String fragment) {
        this.assignments = this.assignments == null ? fragment : this.assignments + ", " + fragment;
        return this;
    }

    public Update setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Update setPrimaryKeyColumnNames(String[] columnNames) {
        this.primaryKeyColumns.clear();
        this.addPrimaryKeyColumns(columnNames);
        return this;
    }

    public Update addPrimaryKeyColumns(String[] columnNames) {
        for (String columnName : columnNames) {
            this.addPrimaryKeyColumn(columnName, "?");
        }
        return this;
    }

    public Update addPrimaryKeyColumns(String[] columnNames, boolean[] includeColumns, String[] valueExpressions) {
        for (int i = 0; i < columnNames.length; ++i) {
            if (!includeColumns[i]) continue;
            this.addPrimaryKeyColumn(columnNames[i], valueExpressions[i]);
        }
        return this;
    }

    public Update addPrimaryKeyColumns(String[] columnNames, String[] valueExpressions) {
        for (int i = 0; i < columnNames.length; ++i) {
            this.addPrimaryKeyColumn(columnNames[i], valueExpressions[i]);
        }
        return this;
    }

    public Update addPrimaryKeyColumn(String columnName, String valueExpression) {
        this.primaryKeyColumns.put(columnName, valueExpression);
        return this;
    }

    public Update setVersionColumnName(String versionColumnName) {
        this.versionColumnName = versionColumnName;
        return this;
    }

    public Update setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public Update addColumns(String[] columnNames) {
        for (String columnName : columnNames) {
            this.addColumn(columnName);
        }
        return this;
    }

    public Update addColumns(String[] columnNames, boolean[] updateable, String[] valueExpressions) {
        for (int i = 0; i < columnNames.length; ++i) {
            if (!updateable[i]) continue;
            this.addColumn(columnNames[i], valueExpressions[i]);
        }
        return this;
    }

    public Update addColumns(String[] columnNames, String valueExpression) {
        for (String columnName : columnNames) {
            this.addColumn(columnName, valueExpression);
        }
        return this;
    }

    public Update addColumn(String columnName) {
        return this.addColumn(columnName, "?");
    }

    public Update addColumn(String columnName, String valueExpression) {
        this.columns.put(columnName, valueExpression);
        return this;
    }

    public Update addColumn(String columnName, Object value, LiteralType type) throws Exception {
        return this.addColumn(columnName, type.objectToSQLString(value, this.dialect));
    }

    public Update addWhereColumns(String[] columnNames) {
        for (String columnName : columnNames) {
            this.addWhereColumn(columnName);
        }
        return this;
    }

    public Update addWhereColumns(String[] columnNames, String valueExpression) {
        for (String columnName : columnNames) {
            this.addWhereColumn(columnName, valueExpression);
        }
        return this;
    }

    public Update addWhereColumn(String columnName) {
        return this.addWhereColumn(columnName, "=?");
    }

    public Update addWhereColumn(String columnName, String valueExpression) {
        this.whereColumns.put(columnName, valueExpression);
        return this;
    }

    public Update setWhere(String where) {
        this.where = where;
        return this;
    }

    public String toStatementString() {
        StringBuilder buf = new StringBuilder(this.columns.size() * 15 + this.tableName.length() + 10);
        if (this.comment != null) {
            buf.append("/* ").append(Dialect.escapeComment(this.comment)).append(" */ ");
        }
        buf.append("update ").append(this.tableName).append(" set ");
        boolean assignmentsAppended = false;
        Iterator<Map.Entry<String, String>> iter = this.columns.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> e = iter.next();
            buf.append(e.getKey()).append('=').append(e.getValue());
            if (iter.hasNext()) {
                buf.append(", ");
            }
            assignmentsAppended = true;
        }
        if (this.assignments != null) {
            if (assignmentsAppended) {
                buf.append(", ");
            }
            buf.append(this.assignments);
        }
        boolean conditionsAppended = false;
        if (!this.primaryKeyColumns.isEmpty() || this.where != null || !this.whereColumns.isEmpty() || this.versionColumnName != null) {
            buf.append(" where ");
        }
        iter = this.primaryKeyColumns.entrySet().iterator();
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
        for (Map.Entry<String, String> e : this.whereColumns.entrySet()) {
            if (conditionsAppended) {
                buf.append(" and ");
            }
            buf.append(e.getKey()).append(e.getValue());
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
}

