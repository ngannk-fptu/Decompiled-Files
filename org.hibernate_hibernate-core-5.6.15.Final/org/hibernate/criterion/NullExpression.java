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

public class NullExpression
implements Criterion {
    private static final TypedValue[] NO_VALUES = new TypedValue[0];
    private final String propertyName;

    protected NullExpression(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        String[] columns = criteriaQuery.findColumns(this.propertyName, criteria);
        String result = String.join((CharSequence)" and ", StringHelper.suffix(columns, " is null"));
        if (columns.length > 1) {
            result = '(' + result + ')';
        }
        return result;
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return NO_VALUES;
    }

    public String toString() {
        return this.propertyName + " is null";
    }
}

