/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.util.StringHelper;

public class ForeignKeyNullExpression
implements Criterion {
    private static final TypedValue[] NO_VALUES = new TypedValue[0];
    private final String associationPropertyName;
    private final boolean negated;

    public ForeignKeyNullExpression(String associationPropertyName) {
        this.associationPropertyName = associationPropertyName;
        this.negated = false;
    }

    public ForeignKeyNullExpression(String associationPropertyName, boolean negated) {
        this.associationPropertyName = associationPropertyName;
        this.negated = negated;
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
        String[] columns = criteriaQuery.getForeignKeyColumns(criteria, this.associationPropertyName);
        String result = String.join((CharSequence)" and ", StringHelper.suffix(columns, this.getSuffix()));
        if (columns.length > 1) {
            result = '(' + result + ')';
        }
        return result;
    }

    private String getSuffix() {
        if (this.negated) {
            return " is not null";
        }
        return " is null";
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
        return NO_VALUES;
    }
}

