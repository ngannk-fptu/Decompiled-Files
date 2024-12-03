/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.TypedValue;

public class LikeExpression
implements Criterion {
    private final String propertyName;
    private final Object value;
    private final Character escapeChar;
    private final boolean ignoreCase;

    protected LikeExpression(String propertyName, String value, Character escapeChar, boolean ignoreCase) {
        this.propertyName = propertyName;
        this.value = value;
        this.escapeChar = escapeChar;
        this.ignoreCase = ignoreCase;
    }

    protected LikeExpression(String propertyName, String value) {
        this(propertyName, value, null, false);
    }

    protected LikeExpression(String propertyName, String value, MatchMode matchMode) {
        this(propertyName, matchMode.toMatchString(value));
    }

    protected LikeExpression(String propertyName, String value, MatchMode matchMode, Character escapeChar, boolean ignoreCase) {
        this(propertyName, matchMode.toMatchString(value), escapeChar, ignoreCase);
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
        Dialect dialect = criteriaQuery.getFactory().getDialect();
        String[] columns = criteriaQuery.findColumns(this.propertyName, criteria);
        if (columns.length != 1) {
            throw new HibernateException("Like may only be used with single-column properties");
        }
        String escape = this.escapeChar == null ? "" : " escape '" + this.escapeChar + "'";
        String column = columns[0];
        if (this.ignoreCase) {
            if (dialect.supportsCaseInsensitiveLike()) {
                return column + " " + dialect.getCaseInsensitiveLike() + " ?" + escape;
            }
            return dialect.getLowercaseFunction() + '(' + column + ')' + " like ?" + escape;
        }
        return column + " like ?" + escape;
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
        String matchValue = this.ignoreCase ? this.value.toString().toLowerCase() : this.value.toString();
        return new TypedValue[]{criteriaQuery.getTypedValue(criteria, this.propertyName, matchValue)};
    }
}

