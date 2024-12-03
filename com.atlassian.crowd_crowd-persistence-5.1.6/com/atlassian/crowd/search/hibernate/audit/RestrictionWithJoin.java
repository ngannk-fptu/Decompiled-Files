/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.search.hibernate.audit;

import com.atlassian.crowd.search.hibernate.HQLQuery;
import com.atlassian.crowd.search.hibernate.audit.Restriction;

public class RestrictionWithJoin
implements Restriction {
    private final String join;
    private final Restriction restriction;

    public RestrictionWithJoin(String join, Restriction restriction) {
        this.join = join;
        this.restriction = restriction;
    }

    @Override
    public String getWhere(HQLQuery hqlQuery) {
        return this.restriction.getWhere(hqlQuery);
    }

    @Override
    public String getFrom() {
        return " " + this.join;
    }
}

