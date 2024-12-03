/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.dialect.Dialect;

public class SimpleSelect {
    protected String tableName;
    protected String orderBy;
    protected String comment;
    protected List<String> columns = new ArrayList<String>();
    protected Map<String, String> aliases = new HashMap<String, String>();
    protected List<String> whereTokens = new ArrayList<String>();
    protected LockOptions lockOptions = new LockOptions(LockMode.READ);
    private Dialect dialect;

    public SimpleSelect(Dialect dialect) {
        this.dialect = dialect;
    }

    public SimpleSelect addColumns(String[] columnNames, String[] columnAliases) {
        for (int i = 0; i < columnNames.length; ++i) {
            if (columnNames[i] == null) continue;
            this.addColumn(columnNames[i], columnAliases[i]);
        }
        return this;
    }

    public SimpleSelect addColumns(String[] columns, String[] aliases, boolean[] ignore) {
        for (int i = 0; i < ignore.length; ++i) {
            if (ignore[i] || columns[i] == null) continue;
            this.addColumn(columns[i], aliases[i]);
        }
        return this;
    }

    public SimpleSelect addColumns(String[] columnNames) {
        for (String columnName : columnNames) {
            if (columnName == null) continue;
            this.addColumn(columnName);
        }
        return this;
    }

    public SimpleSelect addColumn(String columnName) {
        this.columns.add(columnName);
        return this;
    }

    public SimpleSelect addColumn(String columnName, String alias) {
        this.columns.add(columnName);
        this.aliases.put(columnName, alias);
        return this;
    }

    public SimpleSelect setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public SimpleSelect setLockOptions(LockOptions lockOptions) {
        LockOptions.copy(lockOptions, this.lockOptions);
        return this;
    }

    public SimpleSelect setLockMode(LockMode lockMode) {
        this.lockOptions.setLockMode(lockMode);
        return this;
    }

    public SimpleSelect addWhereToken(String token) {
        if (token != null) {
            if (!this.whereTokens.isEmpty()) {
                this.and();
            }
            this.whereTokens.add(token);
        }
        return this;
    }

    private void and() {
        if (this.whereTokens.size() > 0) {
            this.whereTokens.add("and");
        }
    }

    public SimpleSelect addCondition(String lhs, String op, String rhs) {
        this.and();
        this.whereTokens.add(lhs + ' ' + op + ' ' + rhs);
        return this;
    }

    public SimpleSelect addCondition(String lhs, String condition) {
        this.and();
        this.whereTokens.add(lhs + ' ' + condition);
        return this;
    }

    public SimpleSelect addCondition(String[] lhs, String op, String[] rhs) {
        for (int i = 0; i < lhs.length; ++i) {
            this.addCondition(lhs[i], op, rhs[i]);
        }
        return this;
    }

    public SimpleSelect addCondition(String[] lhs, String condition) {
        for (String lh : lhs) {
            if (lh == null) continue;
            this.addCondition(lh, condition);
        }
        return this;
    }

    public String toStatementString() {
        StringBuilder buf = new StringBuilder(this.columns.size() * 10 + this.tableName.length() + this.whereTokens.size() * 10 + 10);
        if (this.comment != null) {
            buf.append("/* ").append(Dialect.escapeComment(this.comment)).append(" */ ");
        }
        buf.append("select ");
        HashSet<String> uniqueColumns = new HashSet<String>();
        Iterator<String> iter = this.columns.iterator();
        boolean appendComma = false;
        while (iter.hasNext()) {
            String col = iter.next();
            String alias = this.aliases.get(col);
            if (!uniqueColumns.add(alias == null ? col : alias)) continue;
            if (appendComma) {
                buf.append(", ");
            }
            buf.append(col);
            if (alias != null && !alias.equals(col)) {
                buf.append(" as ").append(alias);
            }
            appendComma = true;
        }
        buf.append(" from ").append(this.dialect.appendLockHint(this.lockOptions, this.tableName));
        if (this.whereTokens.size() > 0) {
            buf.append(" where ").append(this.toWhereClause());
        }
        if (this.orderBy != null) {
            buf.append(this.orderBy);
        }
        if (this.lockOptions != null) {
            buf = new StringBuilder(this.dialect.applyLocksToSql(buf.toString(), this.lockOptions, null));
        }
        return this.dialect.transformSelectString(buf.toString());
    }

    public String toWhereClause() {
        StringBuilder buf = new StringBuilder(this.whereTokens.size() * 5);
        Iterator<String> iter = this.whereTokens.iterator();
        while (iter.hasNext()) {
            buf.append(iter.next());
            if (!iter.hasNext()) continue;
            buf.append(' ');
        }
        return buf.toString();
    }

    public SimpleSelect setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public SimpleSelect setComment(String comment) {
        this.comment = comment;
        return this;
    }
}

