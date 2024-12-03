/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Projection;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.type.Type;

public class SQLProjection
implements Projection {
    private final String sql;
    private final String groupBy;
    private final Type[] types;
    private String[] aliases;
    private String[] columnAliases;
    private boolean grouped;

    protected SQLProjection(String sql, String[] columnAliases, Type[] types) {
        this(sql, null, columnAliases, types);
    }

    protected SQLProjection(String sql, String groupBy, String[] columnAliases, Type[] types) {
        this.sql = sql;
        this.types = types;
        this.aliases = columnAliases;
        this.columnAliases = columnAliases;
        this.grouped = groupBy != null;
        this.groupBy = groupBy;
    }

    @Override
    public String toSqlString(Criteria criteria, int loc, CriteriaQuery criteriaQuery) {
        return StringHelper.replace(this.sql, "{alias}", criteriaQuery.getSQLAlias(criteria));
    }

    @Override
    public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
        return StringHelper.replace(this.groupBy, "{alias}", criteriaQuery.getSQLAlias(criteria));
    }

    @Override
    public Type[] getTypes(Criteria crit, CriteriaQuery criteriaQuery) {
        return this.types;
    }

    public String toString() {
        return this.sql;
    }

    @Override
    public String[] getAliases() {
        return this.aliases;
    }

    @Override
    public String[] getColumnAliases(int loc) {
        return this.columnAliases;
    }

    @Override
    public boolean isGrouped() {
        return this.grouped;
    }

    @Override
    public Type[] getTypes(String alias, Criteria crit, CriteriaQuery criteriaQuery) {
        return null;
    }

    @Override
    public String[] getColumnAliases(String alias, int loc) {
        return null;
    }
}

