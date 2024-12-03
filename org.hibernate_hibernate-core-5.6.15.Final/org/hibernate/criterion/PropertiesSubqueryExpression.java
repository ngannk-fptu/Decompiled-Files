/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.SubqueryExpression;

public class PropertiesSubqueryExpression
extends SubqueryExpression {
    private final String[] propertyNames;

    protected PropertiesSubqueryExpression(String[] propertyNames, String op, DetachedCriteria dc) {
        super(op, null, dc);
        this.propertyNames = propertyNames;
    }

    @Override
    protected String toLeftSqlString(Criteria criteria, CriteriaQuery outerQuery) {
        StringBuilder left = new StringBuilder("(");
        CharSequence[] sqlColumnNames = new String[this.propertyNames.length];
        for (int i = 0; i < sqlColumnNames.length; ++i) {
            sqlColumnNames[i] = outerQuery.getColumn(criteria, this.propertyNames[i]);
        }
        left.append(String.join((CharSequence)", ", sqlColumnNames));
        return left.append(")").toString();
    }
}

