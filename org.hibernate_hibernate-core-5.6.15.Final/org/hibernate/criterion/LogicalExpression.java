/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.spi.TypedValue;

public class LogicalExpression
implements Criterion {
    private final Criterion lhs;
    private final Criterion rhs;
    private final String op;

    protected LogicalExpression(Criterion lhs, Criterion rhs, String op) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = op;
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
        TypedValue[] lhsTypedValues = this.lhs.getTypedValues(criteria, criteriaQuery);
        TypedValue[] rhsTypedValues = this.rhs.getTypedValues(criteria, criteriaQuery);
        TypedValue[] result = new TypedValue[lhsTypedValues.length + rhsTypedValues.length];
        System.arraycopy(lhsTypedValues, 0, result, 0, lhsTypedValues.length);
        System.arraycopy(rhsTypedValues, 0, result, lhsTypedValues.length, rhsTypedValues.length);
        return result;
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
        return '(' + this.lhs.toSqlString(criteria, criteriaQuery) + ' ' + this.getOp() + ' ' + this.rhs.toSqlString(criteria, criteriaQuery) + ')';
    }

    public String getOp() {
        return this.op;
    }

    public String toString() {
        return this.lhs.toString() + ' ' + this.getOp() + ' ' + this.rhs.toString();
    }
}

