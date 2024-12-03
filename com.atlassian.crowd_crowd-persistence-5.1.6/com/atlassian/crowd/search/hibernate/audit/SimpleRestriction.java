/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.search.hibernate.audit;

import com.atlassian.crowd.search.hibernate.HQLQuery;
import com.atlassian.crowd.search.hibernate.audit.Restriction;

class SimpleRestriction
implements Restriction {
    private final String property;
    private final String operator;
    private final Object value;

    public SimpleRestriction(String property, String operator, Object value) {
        this.property = property;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public String getWhere(HQLQuery hqlQuery) {
        return this.property + " " + this.operator + " " + hqlQuery.addParameterPlaceholder(this.value);
    }
}

