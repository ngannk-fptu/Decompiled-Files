/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.sql.Select;

public class InsertSelect {
    protected String tableName;
    protected String comment;
    protected List<String> columnNames = new ArrayList<String>();
    protected Select select;

    public InsertSelect(Dialect dialect) {
    }

    public InsertSelect setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public InsertSelect setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public InsertSelect addColumn(String columnName) {
        this.columnNames.add(columnName);
        return this;
    }

    public InsertSelect addColumns(String[] columnNames) {
        for (String columnName : columnNames) {
            this.columnNames.add(columnName);
        }
        return this;
    }

    public InsertSelect setSelect(Select select) {
        this.select = select;
        return this;
    }

    public String toStatementString() {
        if (this.tableName == null) {
            throw new HibernateException("no table name defined for insert-select");
        }
        if (this.select == null) {
            throw new HibernateException("no select defined for insert-select");
        }
        StringBuilder buf = new StringBuilder(this.columnNames.size() * 15 + this.tableName.length() + 10);
        if (this.comment != null) {
            buf.append("/* ").append(Dialect.escapeComment(this.comment)).append(" */ ");
        }
        buf.append("insert into ").append(this.tableName);
        if (!this.columnNames.isEmpty()) {
            buf.append(" (");
            Iterator<String> itr = this.columnNames.iterator();
            while (itr.hasNext()) {
                buf.append(itr.next());
                if (!itr.hasNext()) continue;
                buf.append(", ");
            }
            buf.append(")");
        }
        buf.append(' ').append(this.select.toStatementString());
        return buf.toString();
    }
}

