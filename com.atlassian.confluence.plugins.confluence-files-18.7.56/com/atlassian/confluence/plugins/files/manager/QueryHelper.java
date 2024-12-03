/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.content.ContentQuery
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.google.common.base.Function
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Iterators
 */
package com.atlassian.confluence.plugins.files.manager;

import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.content.ContentQuery;
import com.atlassian.confluence.content.CustomContentManager;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import java.util.Iterator;

public class QueryHelper {
    public static <T, U> PageResponse<U> doQueryById(CustomContentManager customContentManager, String queryName, PageRequest request, Function<T, U> filterFunction, Object ... args) {
        ContentQuery query = new ContentQuery(queryName, args);
        PageResponse cceos = customContentManager.findByQuery(query, false, LimitedRequestImpl.create((PageRequest)request, (int)request.getLimit()), Predicates.alwaysTrue());
        int limit = request.getLimit();
        Iterable list = Iterables.transform((Iterable)Iterables.limit((Iterable)cceos, (int)limit), filterFunction);
        return PageResponseImpl.from((Iterable)list, (cceos.size() > limit ? 1 : 0) != 0).build();
    }

    public static <T, U> Iterator<U> doQueryById(CustomContentManager customContentManager, String queryName, Function<T, U> transformFunction, int offset, int maxResults, Object ... args) {
        ContentQuery query = new ContentQuery(queryName, args);
        Iterator cceos = customContentManager.findByQuery(query, offset, maxResults);
        return Iterators.transform((Iterator)cceos, transformFunction);
    }
}

