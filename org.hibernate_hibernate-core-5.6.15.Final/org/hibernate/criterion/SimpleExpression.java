/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import java.util.Locale;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.type.Type;

public class SimpleExpression
implements Criterion {
    private final String propertyName;
    private final Object value;
    private boolean ignoreCase;
    private final String op;

    protected SimpleExpression(String propertyName, Object value, String op) {
        this.propertyName = propertyName;
        this.value = value;
        this.op = op;
    }

    protected SimpleExpression(String propertyName, Object value, String op, boolean ignoreCase) {
        this.propertyName = propertyName;
        this.value = value;
        this.ignoreCase = ignoreCase;
        this.op = op;
    }

    public final String getOp() {
        return this.op;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public Object getValue() {
        return this.value;
    }

    public SimpleExpression ignoreCase() {
        this.ignoreCase = true;
        return this;
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        String[] columns = criteriaQuery.findColumns(this.propertyName, criteria);
        Type type = criteriaQuery.getTypeUsingProjection(criteria, this.propertyName);
        StringBuilder fragment = new StringBuilder();
        if (columns.length > 1) {
            fragment.append('(');
        }
        SessionFactoryImplementor factory = criteriaQuery.getFactory();
        int[] sqlTypes = type.sqlTypes(factory);
        for (int i = 0; i < columns.length; ++i) {
            boolean lower;
            boolean bl = lower = this.ignoreCase && (sqlTypes[i] == 12 || sqlTypes[i] == 1 || sqlTypes[i] == -9 || sqlTypes[i] == -15);
            if (lower) {
                fragment.append(factory.getDialect().getLowercaseFunction()).append('(');
            }
            fragment.append(columns[i]);
            if (lower) {
                fragment.append(')');
            }
            fragment.append(this.getOp()).append("?");
            if (i >= columns.length - 1) continue;
            fragment.append(" and ");
        }
        if (columns.length > 1) {
            fragment.append(')');
        }
        return fragment.toString();
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        Object casedValue = this.ignoreCase ? this.value.toString().toLowerCase(Locale.ROOT) : this.value;
        return new TypedValue[]{criteriaQuery.getTypedValue(criteria, this.propertyName, casedValue)};
    }

    public String toString() {
        return this.propertyName + this.getOp() + this.value;
    }
}

