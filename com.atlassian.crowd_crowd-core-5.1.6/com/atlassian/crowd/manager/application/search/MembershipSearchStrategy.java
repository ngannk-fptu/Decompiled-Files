/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.google.common.collect.ListMultimap
 */
package com.atlassian.crowd.manager.application.search;

import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.google.common.collect.ListMultimap;
import java.util.List;

public interface MembershipSearchStrategy {
    public <T> List<T> searchDirectGroupRelationships(MembershipQuery<T> var1);

    public <T> List<T> searchNestedGroupRelationships(MembershipQuery<T> var1);

    public <T> ListMultimap<String, T> searchDirectGroupRelationshipsGroupedByName(MembershipQuery<T> var1);
}

