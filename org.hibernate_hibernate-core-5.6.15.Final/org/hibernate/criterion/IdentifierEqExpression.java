/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.util.StringHelper;

public class IdentifierEqExpression
implements Criterion {
    private final Object value;

    protected IdentifierEqExpression(Object value) {
        this.value = value;
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
        String[] columns = criteriaQuery.getIdentifierColumns(criteria);
        String result = String.join((CharSequence)" and ", StringHelper.suffix(columns, " = ?"));
        if (columns.length > 1) {
            result = '(' + result + ')';
        }
        return result;
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
        return new TypedValue[]{criteriaQuery.getTypedIdentifierValue(criteria, this.value)};
    }

    public String toString() {
        return "id = " + this.value;
    }
}

