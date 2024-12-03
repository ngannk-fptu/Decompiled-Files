/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.search.hibernate.audit;

import com.atlassian.crowd.search.hibernate.HQLQuery;
import com.atlassian.crowd.search.hibernate.audit.Restriction;

public class EmptyRestriction
implements Restriction {
    @Override
    public String getWhere(HQLQuery hqlQuery) {
        return "";
    }
}

