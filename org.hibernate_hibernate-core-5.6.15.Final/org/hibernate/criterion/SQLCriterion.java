/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.type.Type;

public class SQLCriterion
implements Criterion {
    private final String sql;
    private final TypedValue[] typedValues;

    protected SQLCriterion(String sql, Object[] values, Type[] types) {
        this.sql = sql;
        this.typedValues = new TypedValue[values.length];
        for (int i = 0; i < this.typedValues.length; ++i) {
            this.typedValues[i] = new TypedValue(types[i], values[i]);
        }
    }

    protected SQLCriterion(String sql, Object value, Type type) {
        this.sql = sql;
        this.typedValues = new TypedValue[]{new TypedValue(type, value)};
    }

    protected SQLCriterion(String sql) {
        this.sql = sql;
        this.typedValues = new TypedValue[0];
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
        return StringHelper.replace(this.sql, "{alias}", criteriaQuery.getSQLAlias(criteria));
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
        return this.typedValues;
    }

    public String toString() {
        return this.sql;
    }
}

