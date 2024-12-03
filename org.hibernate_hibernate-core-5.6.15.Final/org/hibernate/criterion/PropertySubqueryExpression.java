/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.SubqueryExpression;

public class PropertySubqueryExpression
extends SubqueryExpression {
    private String propertyName;

    protected PropertySubqueryExpression(String propertyName, String op, String quantifier, DetachedCriteria dc) {
        super(op, quantifier, dc);
        this.propertyName = propertyName;
    }

    @Override
    protected String toLeftSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
        return criteriaQuery.getColumn(criteria, this.propertyName);
    }
}

