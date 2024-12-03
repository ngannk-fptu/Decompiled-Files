/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.SubqueryExpression;

public class ExistsSubqueryExpression
extends SubqueryExpression {
    protected ExistsSubqueryExpression(String quantifier, DetachedCriteria dc) {
        super(null, quantifier, dc);
    }

    @Override
    protected String toLeftSqlString(Criteria criteria, CriteriaQuery outerQuery) {
        return "";
    }
}

