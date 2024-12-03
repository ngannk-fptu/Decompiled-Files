/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory
 *  com.google.common.base.Preconditions
 *  javax.persistence.EntityManager
 *  javax.persistence.Query
 */
package com.atlassian.confluence.plugins.tasklist.importexport;

import com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory;
import com.google.common.base.Preconditions;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class FindAllCurrentAbstractPageIdsHibernateQueryFactory
implements HibernateContentQueryFactory {
    private static final String selectAllCurrentPageAndBlogPostIds = "select page.id from AbstractPage page where page.space.id = :spaceId and page.contentStatus = 'current'";

    public Query getQuery(EntityManager entityManager, Object ... parameters) {
        Long spaceId = (Long)Preconditions.checkNotNull((Object)((Long)parameters[0]));
        Query query = entityManager.createQuery(selectAllCurrentPageAndBlogPostIds);
        query.setParameter("spaceId", (Object)spaceId);
        return query;
    }
}

