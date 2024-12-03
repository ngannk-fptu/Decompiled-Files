/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 */
package com.atlassian.crowd.search.hibernate.audit;

import com.atlassian.crowd.search.hibernate.audit.EmptyRestriction;
import com.atlassian.crowd.search.hibernate.audit.Restriction;
import com.google.common.base.Strings;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RestrictionCollection
implements Restriction {
    protected final List<Restriction> restrictions;

    public RestrictionCollection(List<Restriction> restrictions) {
        this.restrictions = restrictions.stream().filter(clause -> !(clause instanceof EmptyRestriction)).collect(Collectors.toList());
    }

    @Override
    public String getFrom() {
        return this.restrictions.stream().map(Restriction::getFrom).filter(from -> !Strings.isNullOrEmpty((String)from)).collect(Collectors.joining(" "));
    }
}

