/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.search.hibernate.HQLQuery
 *  com.atlassian.crowd.search.hibernate.HQLQueryTranslater
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  org.hibernate.Session
 *  org.hibernate.query.Query
 *  org.hibernate.type.TrueFalseType
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.user.crowd.hibernate;

import com.atlassian.confluence.impl.user.crowd.hibernate.HQLMembershipQueryTranslator;
import com.atlassian.confluence.impl.user.crowd.hibernate.types.TypeMapper;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.search.hibernate.HQLQuery;
import com.atlassian.crowd.search.hibernate.HQLQueryTranslater;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.TrueFalseType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HibernateSearch<TYPE> {
    private static final Logger log = LoggerFactory.getLogger(HibernateSearch.class);
    private static final boolean CACHEABLE_QUERY = false;
    private final List<HQLQuery> hqlQueries;
    private final int startIndex;
    private final int maxResults;
    private final Class<TYPE> clazz;

    private HibernateSearch(List<HQLQuery> hqlQueries, int startIndex, int maxResults, Class<TYPE> clazz) {
        this.hqlQueries = hqlQueries;
        this.startIndex = startIndex;
        this.maxResults = maxResults;
        this.clazz = clazz;
    }

    public static <T> HibernateSearch<T> forEntities(long directoryId, EntityQuery<T> query) {
        List hqlQueries = HibernateSearch.newQueryTranslater().asHQL(directoryId, query);
        return new HibernateSearch(hqlQueries, query.getStartIndex(), query.getMaxResults(), query.getReturnType());
    }

    public static <T> HibernateSearch<T> forEntities(EntityQuery<T> query) {
        HQLQuery hqlQuery = HibernateSearch.newQueryTranslater().asHQL(query);
        return new HibernateSearch(Collections.singletonList(hqlQuery), query.getStartIndex(), query.getMaxResults(), query.getReturnType());
    }

    private static HQLQueryTranslater newQueryTranslater() {
        return new HQLQueryTranslater();
    }

    public static <T> HibernateSearch<T> forMemberships(long directoryId, MembershipQuery<T> query) {
        HQLQuery hqlQuery = new HQLMembershipQueryTranslator().toHQL(directoryId, query);
        return new HibernateSearch(Collections.singletonList(hqlQuery), query.getStartIndex(), query.getMaxResults(), query.getReturnType());
    }

    List<TYPE> doInHibernate(Session session) {
        log.debug("Running search query: {}", this.hqlQueries);
        ArrayList<List<TYPE>> results = new ArrayList<List<TYPE>>();
        for (HQLQuery hqlQuery : this.hqlQueries) {
            results.add(this.doInHibernate(session, hqlQuery));
        }
        return results.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    private List<TYPE> doInHibernate(Session session, HQLQuery hqlQuery) {
        Query hibernateQuery = session.createQuery(hqlQuery.toString()).setFirstResult(this.startIndex).setCacheable(false);
        HibernateSearch.handle(session, hqlQuery, hibernateQuery, this.maxResults);
        Function resultTransform = hqlQuery.getResultTransform();
        if (resultTransform == null || Directory.class.equals(this.clazz) || Application.class.equals(this.clazz)) {
            return HibernateSearch.resultTransformer().apply(hibernateQuery.list());
        }
        return (List)resultTransform.apply(hibernateQuery.list());
    }

    protected static <T> void handle(Session session, HQLQuery hqlQuery, Query<T> hibernateQuery, int maxResults) {
        if (maxResults != -1) {
            hibernateQuery.setMaxResults(maxResults);
        }
        hqlQuery.getParameterMap().forEach((key, value) -> {
            if (value instanceof Enum) {
                Enum enumValue = (Enum)value;
                Type enumType = session.getSessionFactory().getTypeHelper().custom(TypeMapper.enumToCustomTypeClass(enumValue.getClass()));
                hibernateQuery.setParameter(key, value, enumType);
            } else if (value instanceof Boolean) {
                hibernateQuery.setParameter(key, value, (Type)TrueFalseType.INSTANCE);
            } else {
                hibernateQuery.setParameter(key, value);
            }
        });
    }

    private static <T> Function<List<Object[]>, List<?>> resultTransformer() {
        return objects -> {
            if (objects.isEmpty()) {
                return Collections.emptyList();
            }
            ArrayList<Object> results = new ArrayList<Object>(objects.size());
            for (Object object : objects) {
                if (object instanceof Object[] && ((Object[])object).length > 0) {
                    results.add(((Object[])object)[0]);
                    continue;
                }
                results.add(object);
            }
            return results;
        };
    }
}

