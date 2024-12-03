/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.SubqueryExpression;
import org.hibernate.engine.spi.TypedValue;

public class SimpleSubqueryExpression
extends SubqueryExpression {
    private Object value;

    protected SimpleSubqueryExpression(Object value, String op, String quantifier, DetachedCriteria dc) {
        super(op, quantifier, dc);
        this.value = value;
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        TypedValue[] subQueryTypedValues = super.getTypedValues(criteria, criteriaQuery);
        TypedValue[] result = new TypedValue[subQueryTypedValues.length + 1];
        System.arraycopy(subQueryTypedValues, 0, result, 1, subQueryTypedValues.length);
        result[0] = new TypedValue(this.getTypes()[0], this.value);
        return result;
    }

    @Override
    protected String toLeftSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
        return "?";
    }
}

