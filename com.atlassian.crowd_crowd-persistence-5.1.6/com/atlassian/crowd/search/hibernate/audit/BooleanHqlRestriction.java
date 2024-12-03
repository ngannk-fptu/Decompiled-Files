/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction$BooleanLogic
 */
package com.atlassian.crowd.search.hibernate.audit;

import com.atlassian.crowd.search.hibernate.HQLQuery;
import com.atlassian.crowd.search.hibernate.audit.Restriction;
import com.atlassian.crowd.search.hibernate.audit.RestrictionCollection;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

class BooleanHqlRestriction
extends RestrictionCollection {
    private final Collector<CharSequence, ?, String> joiningCollector;

    public BooleanHqlRestriction(BooleanRestriction.BooleanLogic booleanLogic, List<Restriction> restrictions) {
        super(restrictions);
        this.joiningCollector = Collectors.joining(" " + booleanLogic.name() + " ");
    }

    protected BooleanHqlRestriction(List<Restriction> restrictions, Collector<CharSequence, ?, String> joiningCollector) {
        super(restrictions);
        this.joiningCollector = joiningCollector;
    }

    @Override
    public String getWhere(HQLQuery hqlQuery) {
        return this.restrictions.stream().map(this.extractWhere(hqlQuery)).filter(where -> !where.isEmpty()).collect(this.joiningCollector);
    }

    protected Function<Restriction, String> extractWhere(HQLQuery hqlQuery) {
        return clause -> clause.getWhere(hqlQuery);
    }
}

