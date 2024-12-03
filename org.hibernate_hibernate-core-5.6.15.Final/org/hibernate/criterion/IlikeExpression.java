/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import java.util.Locale;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.engine.spi.TypedValue;

@Deprecated
public class IlikeExpression
implements Criterion {
    private final String propertyName;
    private final Object value;

    protected IlikeExpression(String propertyName, Object value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    protected IlikeExpression(String propertyName, String value, MatchMode matchMode) {
        this(propertyName, matchMode.toMatchString(value));
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
        Dialect dialect = criteriaQuery.getFactory().getDialect();
        String[] columns = criteriaQuery.findColumns(this.propertyName, criteria);
        if (columns.length != 1) {
            throw new HibernateException("ilike may only be used with single-column properties");
        }
        if (dialect instanceof PostgreSQLDialect || dialect instanceof PostgreSQL81Dialect) {
            return columns[0] + " ilike ?";
        }
        return dialect.getLowercaseFunction() + '(' + columns[0] + ") like ?";
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
        return new TypedValue[]{criteriaQuery.getTypedValue(criteria, this.propertyName, this.value.toString().toLowerCase(Locale.ROOT))};
    }

    public String toString() {
        return this.propertyName + " ilike " + this.value;
    }
}

