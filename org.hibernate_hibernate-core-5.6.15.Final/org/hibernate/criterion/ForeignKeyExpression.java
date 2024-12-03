/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.util.StringHelper;

public class ForeignKeyExpression
implements Criterion {
    private final String associationPropertyName;
    private final Object value;
    private final String operator;

    public ForeignKeyExpression(String associationPropertyName, Object value, String operator) {
        this.associationPropertyName = associationPropertyName;
        this.value = value;
        this.operator = operator;
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
        String[] columns = criteriaQuery.getForeignKeyColumns(criteria, this.associationPropertyName);
        String result = String.join((CharSequence)" and ", StringHelper.suffix(columns, this.operator + "  ?"));
        if (columns.length > 1) {
            result = '(' + result + ')';
        }
        return result;
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
        return new TypedValue[]{criteriaQuery.getForeignKeyTypeValue(criteria, this.associationPropertyName, this.value)};
    }
}

