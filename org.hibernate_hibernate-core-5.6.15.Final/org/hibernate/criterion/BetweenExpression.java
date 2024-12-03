/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.util.StringHelper;

public class BetweenExpression
implements Criterion {
    private final String propertyName;
    private final Object low;
    private final Object high;

    protected BetweenExpression(String propertyName, Object low, Object high) {
        this.propertyName = propertyName;
        this.low = low;
        this.high = high;
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        String[] columns = criteriaQuery.findColumns(this.propertyName, criteria);
        CharSequence[] expressions = StringHelper.suffix(columns, " between ? and ?");
        return String.join((CharSequence)" and ", expressions);
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return new TypedValue[]{criteriaQuery.getTypedValue(criteria, this.propertyName, this.low), criteriaQuery.getTypedValue(criteria, this.propertyName, this.high)};
    }

    public String toString() {
        return this.propertyName + " between " + this.low + " and " + this.high;
    }
}

