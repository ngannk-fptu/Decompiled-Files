/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.application.PagedSearcher
 *  com.atlassian.crowd.manager.application.PagingNotSupportedException
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 */
package com.atlassian.crowd.manager.application.search;

import com.atlassian.crowd.manager.application.PagedSearcher;
import com.atlassian.crowd.manager.application.PagingNotSupportedException;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.util.List;

public interface GroupSearchStrategy {
    public <T> List<T> searchGroups(EntityQuery<T> var1);

    public <T> PagedSearcher<T> createPagedGroupSearcher(EntityQuery<T> var1) throws PagingNotSupportedException;
}

