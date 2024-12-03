/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.search.hibernate.audit;

import com.atlassian.crowd.search.hibernate.HQLQuery;

public interface Restriction {
    public String getWhere(HQLQuery var1);

    default public String getFrom() {
        return "";
    }

    default public void visit(HQLQuery hqlQuery) {
        hqlQuery.appendFrom(this.getFrom());
        hqlQuery.safeAppendWhere(this.getWhere(hqlQuery));
    }
}

