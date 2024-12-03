/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.search.hibernate.HQLQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ListMultimap
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.user.crowd.hibernate;

import com.atlassian.confluence.impl.user.crowd.hibernate.HQLMembershipQueryTranslator;
import com.atlassian.confluence.impl.user.crowd.hibernate.HibernateSearch;
import com.atlassian.crowd.search.hibernate.HQLQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.Collections;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class GroupingHibernateSearch<TYPE> {
    private static final Logger LOG = LoggerFactory.getLogger(GroupingHibernateSearch.class);
    private final List<HQLQuery> hqlQueries;
    private final int startIndex;
    private final int maxResults;

    public GroupingHibernateSearch(List<HQLQuery> hqlQueries, int startIndex, int maxResults) {
        this.hqlQueries = hqlQueries;
        this.startIndex = startIndex;
        this.maxResults = maxResults;
    }

    public static <T> GroupingHibernateSearch<T> forMembershipsGroupedByName(long directoryId, MembershipQuery<T> query) {
        HQLQuery hqlQuery = new HQLMembershipQueryTranslator().toHQL(directoryId, query, true);
        return new GroupingHibernateSearch(Collections.singletonList(hqlQuery), query.getStartIndex(), query.getMaxResults());
    }

    ListMultimap<String, TYPE> doInHibernate(Session session) throws HibernateException {
        LOG.debug("Running search query: {}", this.hqlQueries);
        ArrayListMultimap results = ArrayListMultimap.create();
        for (HQLQuery hqlQuery : this.hqlQueries) {
            List<Object[]> subQueryResults = this.doInHibernate(session, hqlQuery);
            subQueryResults.forEach(arg_0 -> GroupingHibernateSearch.lambda$doInHibernate$0((ListMultimap)results, arg_0));
        }
        return results;
    }

    private List<Object[]> doInHibernate(Session session, HQLQuery hqlQuery) {
        Query hibernateQuery = session.createQuery(hqlQuery.toString()).setFirstResult(this.startIndex).setCacheable(false);
        HibernateSearch.handle(session, hqlQuery, hibernateQuery, this.maxResults);
        return hibernateQuery.list();
    }

    private static /* synthetic */ void lambda$doInHibernate$0(ListMultimap results, Object[] row) {
        results.put((Object)((String)row[0]), row[1]);
    }
}

