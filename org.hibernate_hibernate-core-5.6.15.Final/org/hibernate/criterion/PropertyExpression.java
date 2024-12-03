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

public class PropertyExpression
implements Criterion {
    private static final TypedValue[] NO_TYPED_VALUES = new TypedValue[0];
    private final String propertyName;
    private final String otherPropertyName;
    private final String op;

    protected PropertyExpression(String propertyName, String otherPropertyName, String op) {
        this.propertyName = propertyName;
        this.otherPropertyName = otherPropertyName;
        this.op = op;
    }

    public String getOp() {
        return this.op;
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        String[] lhsColumns = criteriaQuery.findColumns(this.propertyName, criteria);
        String[] rhsColumns = criteriaQuery.findColumns(this.otherPropertyName, criteria);
        CharSequence[] comparisons = StringHelper.add(lhsColumns, this.getOp(), rhsColumns);
        if (comparisons.length > 1) {
            return '(' + String.join((CharSequence)" and ", comparisons) + ')';
        }
        return comparisons[0];
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
        return NO_TYPED_VALUES;
    }

    public String toString() {
        return this.propertyName + this.getOp() + this.otherPropertyName;
    }
}

