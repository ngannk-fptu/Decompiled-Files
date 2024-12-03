/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.search.hibernate.audit;

import com.atlassian.crowd.search.hibernate.HQLQuery;
import com.atlassian.crowd.search.hibernate.audit.BooleanHqlRestriction;
import com.atlassian.crowd.search.hibernate.audit.Restriction;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RestrictionGroup
extends BooleanHqlRestriction {
    public RestrictionGroup(List<Restriction> restrictions) {
        super(restrictions, Collectors.joining(" OR ", "(", ")"));
    }

    @Override
    public String getWhere(HQLQuery hqlQuery) {
        if (this.restrictions.isEmpty()) {
            return "";
        }
        return super.getWhere(hqlQuery);
    }

    @Override
    protected Function<Restriction, String> extractWhere(HQLQuery hqlQuery) {
        return clause -> "(" + clause.getWhere(hqlQuery) + ")";
    }
}

